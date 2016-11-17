package com.codyy.erpsportal.commons.services;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.Constants;
import com.codyy.erpsportal.commons.utils.FileUtils;
import com.codyy.erpsportal.commons.utils.SystemUtils;
import com.codyy.erpsportal.commons.utils.TransferManager;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.models.dao.CacheDao;
import com.codyy.erpsportal.commons.models.dao.DownloadDao;
import com.codyy.erpsportal.commons.models.entities.download.BreakPointInfo;
import com.codyy.erpsportal.commons.models.entities.download.Transfer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HttpLargeDownload implements Transfer {
	private static final String TAG = "HttpLargeDownload";
    private static final int SPEED_AVERAGE_CALCULATION_INTERVAL_MILLISECONDS = 1000;
    static final int STATUS_DOWNLOADING = 1;

    static final int STATUS_COMPLETE = 2;

    static final int STATUS_ERROR = 3;

    /**
     * 下载的地址
     */
    private String url;

    /**
     * 下载的名称
     */
    private String fileName;

    private String dirPath;

    /**
     * 中间文件路径
     */
    private File savePath;

    /**
     * 线程数
     */
    private int threadCount;

    /**
     * 所要下载的文件的大小
     */
    private int fileSize;

    private Context context;

	private long bytesReceived;

    private String userId;

    /**
     * 资源id
     */
    private String resId;

    /**
     * 存放下载信息类的集合
     */
    private List<BreakPointInfo> infos;

    public int state;

	public long averageSpeed; // in bytes
    // variables to keep the download rate of file transfer
	private long speedMarkTimestamp;

	private long totalReceivedSinceLastSpeedStamp;

    private HttpDownloadListener listener;

    private volatile boolean isOngoing;

    /**
	 * 
	 * @param dirPath
	 *            download url
	 * @param fileName
	 *            name
	 * @param threadCount
	 * @param context
	 */
	public HttpLargeDownload(List<HttpLargeDownload> largeDownloads , String url, String fileName,
							 String dirPath, int threadCount, Context context, String userId, String resId) {
		this.url = url;
        this.dirPath = dirPath;
		this.fileName = fileName;
		this.savePath = new File(dirPath, fileName + Constants.CACHING_SUFFIX);
		this.threadCount = threadCount;
		this.context = context;
        this.userId = userId;
		this.resId = resId;

		this.bytesReceived = DownloadDao.instance(context).getTotalDownload(userId, resId);
		this.fileSize = DownloadDao.instance(context).getFileSize(userId, resId);
        Cog.d(TAG, "HttpLargeDownload:bytesReceived=", bytesReceived,
                ",fileSize=", fileSize);
	}

	public HttpDownloadListener getListener() {
		return listener;
	}

	public void setListener(HttpDownloadListener listener) {
		this.listener = listener;
	}

	public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}

	public String getUrl() {
		return url;
	}

	public String getFileName() {
		return fileName;
	}

	public int getState() {
		return state;
	}

	public File getSavePath() {
		return savePath;
	}

	private static void removeFcFile(String userId, String resId) {
		String filename = resId + Constants.CACHING_SUFFIX;//StringUtils.interceptionLastString(url, "/");
        String cacheDirPath = SystemUtils.getCachePath();
        if (cacheDirPath == null) return;

		File file = new File(cacheDirPath + File.separator + userId, filename);
		if (file != null && file.exists()) {
			file.delete();
		}
	}

	/*线程下载文件的打开按钮*/
	public void stopDownLoad(){
		isOngoing = false;
	}
	public boolean isOngoing(){
		return isOngoing;
	}

    /**
     * 检查是否能下载
     *
     * @return
     * @throws Throwable
     */
	public boolean checkCanDownload() throws Throwable{
		// 检查大小是否足够
		if (isFirstDownload()) {
			Cog.d(TAG, "checkCanDownload::is first");
			// 如果documents里有此文件+.cxm!,删除此文件;
			removeFcFile(userId, resId);
			// 初始化网络
			try {
                Cog.d(TAG, "checkCanDownload::url=", url);
                if (!checkDirectory()){
                    return false;
                }

                if (!savePath.exists() && !savePath.createNewFile()) {
                    return false;
                }
                URL url = new URL(this.url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                fileSize = conn.getContentLength();
                Cog.d(TAG, "checkCanDownload:fileSize=", fileName, ",fileSize=", fileSize);
                conn.disconnect();
                if(fileSize < 0){
                    Cog.e(TAG, "网络异常！获取下载信息：文件大小=》失败url："+ this.url);
					CacheDao cacheDao = new CacheDao(EApplication.instance());
					cacheDao.updateResourceSize(0, resId, userId);
                    state = STATUS_ERROR;
                    return false;
                } else {
                    // 本地访问文件
                    RandomAccessFile accessFile = new RandomAccessFile(savePath, "rwd");
                    accessFile.setLength(fileSize);
                    accessFile.close();
                    //更新本地文件大小
                    CacheDao cacheDao = new CacheDao(EApplication.instance());
                    Cog.e(TAG, "fileSize=", fileSize);
                    cacheDao.updateResourceSize(fileSize, resId, userId);
                }
            } catch (Throwable e) {
                state = STATUS_ERROR;
                Cog.e(TAG,"net work fail：", e);
                return false;
            }
			FileUtils.deleteSdData(context);
			int range = fileSize / threadCount;
			infos = new ArrayList<>(threadCount);

			for (int i = 0; i <= threadCount - 1; i++) {
				BreakPointInfo info = new BreakPointInfo(userId, resId,i, i * range, (i + 1) * range - 1, 0, fileSize, url, System.currentTimeMillis());
				infos.add(info);
			}

			if(fileSize<=0){
				return false;
			}
			
			// 第一次保存
			DownloadDao.instance(context).saveDownloadInfos(infos);
			// 创建保存记录的信息
			return true;
		} else {
			Cog.d(TAG, "checkCanDownload::not the first time");
			//检查sd卡的空间是否足够
			FileUtils.deleteSdData(context);
			infos = DownloadDao.instance(context).getDownloadInfos(userId, resId);
			
			int size = 0;
			int completeSize = 0;
			for (BreakPointInfo info : infos) {
				completeSize += info.getCompleteSize();
				size += info.getEndPos() - info.getStartPos() + 1;
			}
			
			if(size<=0){
				//删除记录
				DownloadDao.instance(context).delete(userId, resId);
				//删除fc文件
				removeFcFile(userId, url);
				return false;
			}
			
			//下载超过100%的异常处理
			if(completeSize>size){
				infos = null;
				//删除记录
				DownloadDao.instance(context).delete(userId, resId);
				//删除fc文件
				removeFcFile(userId, resId);
				return false;
			}
			return true;
		}
	}

    /**
     * 检查文件夹是否可用
     * @return
     */
    private boolean checkDirectory() {
        File dir = new File(dirPath);
        return dir.mkdirs() || dir.isDirectory();
    }

	/**
	 * 是否存在断点信息了
	 * @return
	 */
	private boolean isFirstDownload() {
		return DownloadDao.instance(context).isHasDownloadInfors(userId, resId);
	}

	public void start() {
		Engine.instance().getThreadPool().execute(new Runnable() {
			@Override
			public void run() {
                state = STATUS_DOWNLOADING;
                doDownload(0, 0);
			}
		});
	}

    private void doDownload(int delay, int retry) {
        try {
            SystemClock.sleep(delay * 1000);
            if (checkCanDownload()) {
                isOngoing = true;
                for (BreakPointInfo info : infos) {
                    Cog.d(TAG,"info:"+info.toString());
                    // 断点下载各个信息
                    saveNew(url, 6000, HttpLargeDownload.this, info, new DownloadListener(retry));
                }
            } else {
                error(null);
            }
        } catch (Throwable e) {
            error(e);
        }
    }

    public static boolean saveNew(String url, int timeout, HttpLargeDownload downloader, BreakPointInfo info, HttpFetcherListener listener) {
        Cog.d(TAG, "saveNew: info=", info);
        boolean result = true;
        RandomAccessFile randomAccessFile = null;
        InputStream inStream = null;
		HttpURLConnection urlConnection = null;
        int statusCode = -1;
        String retryAfter = null;
        try {
            URL downloadUrl = new URL(url);
            urlConnection = (HttpURLConnection) downloadUrl.openConnection();
            urlConnection.setConnectTimeout(timeout);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
            urlConnection.setRequestProperty("Accept-Language", "zh-CN");
            urlConnection.setRequestProperty("Referer", downloadUrl.toString());
            urlConnection.setRequestProperty("Charset", "UTF-8");
            String range = "bytes=" + (info.getStartPos() + info.getCompleteSize()) + "-" + info.getEndPos();
            Cog.d(TAG, "saveNew: range:", range);
            urlConnection.setRequestProperty("Range", range);//设置获取实体数据的范围
            urlConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
            urlConnection.setRequestProperty("Connection", "Keep-Alive");

            randomAccessFile = new RandomAccessFile(downloader.getSavePath(), "rwd");
            randomAccessFile.seek(info.getStartPos() + info.getCompleteSize());

            statusCode = urlConnection.getResponseCode();// response.getStatusLine().getStatusCode();

            if (statusCode < 200 || statusCode >= 300) {
                retryAfter = urlConnection.getHeaderField("Retry-After");
                throw new IOException("bad status code, downloading file " + statusCode);
            }
			/*if (response.getEntity() != null) {
				writeEntity(downloader, response.getEntity(), info, randomAccessFile, listener);
			}*/
            Cog.d(TAG, "saveNew: contentLength=", urlConnection.getContentLength(), ",info.getTotal()=", info.getTotal());
            inStream = urlConnection.getInputStream();
            byte[] data = new byte[4096];
            int byteCount = 0;

            while ((byteCount = inStream.read(data)) != -1) {
                if(downloader.isOngoing()) {
                    randomAccessFile.write(data, 0, byteCount);
                    if (listener != null) {
                        listener.onData(data, byteCount, info); // updata database or show
                    }
                } else {//暂停时应该跳出循环结束下载线程
                    break;
                }
            }
            if (downloader.isOngoing()) {//说明不是暂停情况，真的下载完成了
                if (listener != null) {
                    listener.onSuccess(info);
                }
            }
        } catch (IOException e) {
            if (listener != null) {
                listener.onError(e, statusCode, retryAfter);
            }
            Cog.e(TAG,"Error downloading from: " + url, e);
            result = false;
        } finally {
            if(randomAccessFile!=null){
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

	private final class DownloadListener implements HttpFetcherListener {
		private final int retry;

		private long totalReceived;

		public DownloadListener(int retry) {
			this.retry = retry;
		}

        @Override
		public void onData(byte[] data, int byteSize, BreakPointInfo info) {
			bytesReceived += byteSize;
            totalReceived += byteSize;
			Cog.d(TAG,this,"onData bytesReceived:",bytesReceived,",speed:",averageSpeed,"/s, totalSize:", info.getTotal());
            Cog.d(TAG,this,"onData length:" + totalReceived);
            DownloadDao.instance(context).updateDownloadInfos(info.getUserId(), info.getResId(), byteSize, System.currentTimeMillis(), info.getThreadId());
			updateAverageDownloadSpeed();
		}

        @Override
		public synchronized void onSuccess(BreakPointInfo info) {
			int total = DownloadDao.instance(context).getTotalDownload(info.getUserId(), info.getResId());
			Cog.d(TAG,"onSuccess 第" + info.getThreadId() + "下载成功，totalSize：" + info.getTotal()
					+ ",completed size:" + total + " ,progress::"+ DownloadDao.instance(context).getOneFileProgress(userId, resId));
			if (info.getTotal() == total) {
				complete(info.getUrl());
			}
		}

		@Override
		public void onError(Throwable e, int statusCode, String retryAfter) {
			if (statusCode == 503 && !TextUtils.isEmpty(retryAfter)
					&& retry < Constants.MAX_PEER_HTTP_DOWNLOAD_RETRIES) {
				int delay = Integer.parseInt(retryAfter);
				if (delay > 0) {
                    doDownload(delay, retry + 1);
				} else {
					error(e);
				}
			} else {
				error(e);
			}
		}
	}

	private void updateAverageDownloadSpeed() {
		long now = System.currentTimeMillis();
		if (isComplete()) {
			averageSpeed = 0;
			speedMarkTimestamp = now;
			totalReceivedSinceLastSpeedStamp = 0;
		} else if (now - speedMarkTimestamp > SPEED_AVERAGE_CALCULATION_INTERVAL_MILLISECONDS) {
			averageSpeed = ((bytesReceived - totalReceivedSinceLastSpeedStamp) * 1000) / (now - speedMarkTimestamp);
			speedMarkTimestamp = now;
			totalReceivedSinceLastSpeedStamp = bytesReceived;

			if(listener != null){
				listener.onProgress(getProgress(), UIUtils.getBytesInHuman(getDownloadSpeed()) + "/s");
			}
		}
	}

	private void complete(String url) {
		Cog.d(TAG, "complete:fileName:" + fileName);
		Cog.d(TAG, "complete:url:" + url);
        state = STATUS_COMPLETE;

        DownloadDao.instance(context).delete(userId, resId);
        File tempFile = getSavePath();
        if (tempFile != null && tempFile.exists()) {
            Engine.instance().renameFile(tempFile, fileName);
        }
        TransferManager.removeDownload(this);
        if (listener != null) {
            Cog.d(TAG,"complete:onComplete");
			listener.onComplete(this, url, fileName);
		}
	}

	public interface HttpDownloadListener {
		void onComplete(HttpLargeDownload download, String url, String fileName);
		void onProgress(int progress, String speed);
        void onError(String url);
	}

	private void error(Throwable e) {
		Cog.e(TAG,String.format("Error downloading url: %s", url), e);
		state = STATUS_ERROR;
		cleanup();
        if (listener != null) {
            listener.onError(url);
        }
	}

	private void cleanup() {
		try {
			if (DownloadDao.instance(context).getDownloadInfos(userId, resId).size() == 0) {
				savePath.delete();
			}
		} catch (Throwable tr) {
			Cog.d(TAG, tr.toString());
		}
	}

	@Override
	public String getDisplayName() {
		return fileName;
	}

	@Override
	public String getStatus() {
		return null;
	}

	@Override
	public int getProgress() {
		if (isComplete()) {
			return 100;
		}
		if(fileSize <= 0){
			return 0;
		}
		int progress = (int) (bytesReceived * 100 / fileSize);
        Cog.d(TAG,"getProgress: bytesReceived:" , bytesReceived,
                ",fileSize:",fileSize,",progress:" ,progress);

		if (progress < 0){
			progress = 0;
		}
		//下载异常、结束从新下载
		if(progress>100){
            Cog.d(TAG, "getProgress: progress>100");
			stopDownLoad();
			TransferManager.removeDownload(this);
			DownloadDao.instance(EApplication.instance()).delete(userId, resId);
			removeFcFile(userId, resId);
		}
		
		return progress;
	}

	@Override
	public long getSize() {
		return fileSize;
	}

    @Override
	public long getDownloadSpeed() {
		return (!isDownloading()) ? 0 : averageSpeed;
	}

    @Override
	public boolean isComplete() {
		return state == STATUS_COMPLETE;
	}

	public boolean isError() {
		return state == STATUS_ERROR;
	}

    @Override
	public void cancel() { }

    @Override
	public boolean isDownloading() {
		return state == STATUS_DOWNLOADING;
	}

	@Override
	public void cancel(boolean deleteData) { }

}
