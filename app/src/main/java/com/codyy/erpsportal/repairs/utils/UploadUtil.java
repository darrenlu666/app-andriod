package com.codyy.erpsportal.repairs.utils;

import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.repairs.models.entities.UploadingImage;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 上传工具
 * Created by gujiajia on 2017/4/6.
 */

public class UploadUtil {

    private final static String TAG = "UploadUtil";

    public static String doUpload(UploadingImage image, String urlStr) {
        String result = null;
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setChunkedStreamingMode(1024);
            conn.setReadTimeout(10 * 1000);
            conn.setConnectTimeout(10 * 1000);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", "utf-8"); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            File file = new File(image.getPath());
            if (file != null) {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuilder sb = new StringBuilder();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意： name里面的值为服务端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=utf-8" + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len;
//                int count = 0;
//                int total = is.available();
                while ((len = is.read(bytes)) != -1 && image.getStatus() == UploadingImage.STATUS_UPLOADING) {
                    dos.write(bytes, 0, len);
//                    count += len;
//                    publishProgress((int) ((count / (float) total) * 100));
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                dos.close();
                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */
                InputStream input = conn.getInputStream();
                StringBuilder sb1 = new StringBuilder();
                int ss;
                while ((ss = input.read()) != -1) {
                    sb1.append((char) ss);
                }
                result = sb1.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Disposable uploadImages(final String uploadUrl, List<UploadingImage> imageList, final OnEachUploadedCompleteListener listener) {
        return Observable.fromIterable(imageList)
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<UploadingImage>() {
                    @Override
                    public void accept(UploadingImage uploadingImage) throws Exception {
                        Cog.d(TAG, "doOnNext accept imageDetail=", uploadingImage.getPath());
                        String result = UploadUtil.doUpload(uploadingImage, uploadUrl);
                        JSONObject jsonObject = new JSONObject(result);
                        uploadingImage.setId( jsonObject.optString("message"));
                        uploadingImage.setName( jsonObject.optString("realname"));
                        Cog.d(TAG, "doOnNext upload result=", result);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UploadingImage>() {
                    @Override
                    public void accept(UploadingImage image) throws Exception {
                        Cog.d(TAG, "onNext value=", image.getPath());
                        image.setStatus(UploadingImage.STATUS_FINISHED);
                        if (listener != null) {
                            listener.onEachUploadComplete(image);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Cog.d(TAG, "onError e=", throwable);
                        throwable.printStackTrace();
                    }
                });
    }

    public interface OnEachUploadedCompleteListener {
        void onEachUploadComplete(UploadingImage image);
    }
}
