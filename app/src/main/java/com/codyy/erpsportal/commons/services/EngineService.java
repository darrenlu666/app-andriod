package com.codyy.erpsportal.commons.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.SystemUtils;
import com.codyy.erpsportal.commons.utils.ThreadPool;

import java.io.File;

/**
 * this service is used for download
 * @author poe.Cai
 * 2013-10-8
 */
public class EngineService extends Service implements IEngineService {

	private static final String TAG = "EngineService";

	private final IBinder binder;

	private ThreadPool threadPool;

	private final static long[] VENEZUELAN_VIBE = buildVenezuelanVibe(); // 震动

	public EngineService() {
		binder = new EngineServiceBinder();
		threadPool = new ThreadPool("Engine");
	}

	@Override
	public void onCreate() {
		startServices();
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();
	}

	public synchronized void startServices() {
		Cog.d(TAG, "Engine started");
	}

	/**
	 * 通知前台文件下载完毕了~
	 * @param displayName
	 * @param file
	 */
	public void notifyDownloadFinished(String displayName, File file) {
		/*try {
			Context context = getApplicationContext();
			Intent i = new Intent(context, DisplayActivity.class);

			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			i.putExtra(Constants.EXTRA_DOWNLOAD_COMPLETE_NOTIFICATION, true);
			i.putExtra(Constants.EXTRA_DOWNLOAD_COMPLETE_PATH, file.getAbsolutePath());

			PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

			NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification(R.drawable.frostwire_notification, getString(R.string.download_finished), System.currentTimeMillis());
			notification.vibrate = ConfigurationManager.instance().vibrateOnFinishedDownload() ? VENEZUELAN_VIBE : null;
			notification.number = TransferManager.instance().getDownloadsToReview();
			notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_AUTO_CANCEL;
			notification.setLatestEventInfo(context, getString(R.string.download_finished), displayName, pi);
			manager.notify(Constants.NOTIFICATION_DOWNLOAD_TRANSFER_FINISHED, notification);
		} catch (Throwable e) {
			LOG.error("Error creating notification for download finished", e);
		}*/
		Cog.i(TAG,"donwload file 100% :" + file.getAbsolutePath());
	}

	@Override
	public boolean moveTempFile(File savePath) {
		File finalFile = getFinalFile(savePath);
		return savePath.renameTo(finalFile);
	}

	@Override
	public boolean renameFile(File tempPath, String filename) {
		File savePath = new File(tempPath.getParent(), filename);
		return tempPath.renameTo(savePath);
	}

	protected static File getFinalFile(File savePath) {
		File path = SystemUtils.getCacheDirectory();
		File finalFile = new File(path, savePath.getName());
		return finalFile;
	}

	// 震动方式
	private static long[] buildVenezuelanVibe() {

		long shortVibration = 80;
		long mediumVibration = 100;
		long shortPause = 100;
		long mediumPause = 150;
		long longPause = 180;

		return new long[] { 0, shortVibration, longPause, shortVibration, shortPause, shortVibration, shortPause, shortVibration, mediumPause, mediumVibration };
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public byte getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isStarted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStarting() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStopped() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStopping() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDisconnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void stopServices(boolean disconnected) {
		// TODO Auto-generated method stub

	}

	@Override
	public ThreadPool getThreadPool() {
		return threadPool;
	}

	public class EngineServiceBinder extends Binder {
		public IEngineService getService() {
			return EngineService.this;
		}
	}

}
