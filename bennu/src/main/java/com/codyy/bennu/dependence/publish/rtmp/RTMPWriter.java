package com.codyy.bennu.dependence.publish.rtmp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;

public class RTMPWriter 
{
	private static byte flvHeader[] = { 'F', 'L', 'V', 0x01, 0x01, 
		0x00, 0x00, 0x00, 0x09, /* DataOffset(4���ֽ�)���ð汾�̶�Ϊ9 */
		0x00, 0x00, 0x00, 0x00 /* PreviousTagSize0(4���ֽ�)���̶�Ϊ0 */
	};
	
	private boolean hasVideo = false;
	private boolean hasAudio = false;
	String audiopath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/network_audio.flv";
	String videopath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/network_video.flv";
	String path = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/network.flv";
	String logpath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/timestamp.log";
	private File audiofile = new File(audiopath);
	private File videofile = new File(videopath);
	private File file = new File(path);
	private File logfile = new File(logpath);
	private FileOutputStream audioos = null;
	private FileOutputStream videoos = null;
	private FileOutputStream os = null;
	private FileOutputStream logos = null;

	private boolean saveFLV = true;
	private boolean saveInfo = true;

	private int lastVideoTimestamp = 0;
	private int lastAudioTimestamp = 0;
	private int lastTimestamp = 0;
	private long timestampNo = 0;

	private void saveTimestamp(int type, int ts) {
//		if (saveInfo) {
//			long delta = 0;
//			long audioDelta = 0;
//			long vidoeDelta = 0;
//			String str = null;
//			timestampNo++;
//			try {
//				if (logos == null) {
//					logos = new FileOutputStream(logfile);
//				}
//				delta = ts - lastTimestamp;
//				lastTimestamp = ts;
//				if (type == MediaData.MEDIATYPE_AUDIO) {
//					audioDelta = ts - lastAudioTimestamp;
//					lastAudioTimestamp = ts;
//					str = String
//							.format("no.%-8d type = %d ts = %-8d delta = %d audioDelta = %d\n",
//									timestampNo, type, ts, delta, audioDelta);
//				} else if (type == MediaData.MEDIATYPE_VIDEO
//						|| type == MediaData.MEDIATYPE_VIDEOKEY) {
//					vidoeDelta = ts - lastVideoTimestamp;
//					lastVideoTimestamp = ts;
//					str = String
//							.format("no.%-8d type = %d ts = %-8d delta = %d vidoeDelta = %d\n",
//									timestampNo, type, ts, delta, vidoeDelta);
//				}
//				logos.write(str.getBytes());
//				logos.flush();
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}

	private void flvWrite(byte[] tag, int len, int type) {
//		if (!saveFLV)
//			return;
//		try {
//			if (type == MediaData.MEDIATYPE_AUDIO || type == MediaData.MEDIATYPE_AUDIOCONF) {
//				if (audioos == null) {
//					audioos = new FileOutputStream(audiofile);
//					flvHeader[4] = 0x04;
//					audioos.write(flvHeader, 0, 13);
//					audioos.flush();
//				}
//				audioos.write(tag, 0, len);
//				audioos.flush();
//			} else if (type == MediaData.MEDIATYPE_VIDEOCONF || type == MediaData.MEDIATYPE_VIDEO
//					|| type == MediaData.MEDIATYPE_VIDEOKEY) {
//				if (videoos == null) {
//					videoos = new FileOutputStream(videofile);
//					flvHeader[4] = 0x01;
//					videoos.write(flvHeader, 0, 13);
//					videoos.flush();
//				}
//				videoos.write(tag, 0, len);
//				videoos.flush();
//			}
//
//			// ������Ƶд��ͬһ���ļ�
//			if (os == null) {
//				os = new FileOutputStream(file);
//				if (hasAudio)
//					flvHeader[4] |= 0x04;
//				if (hasVideo)
//					flvHeader[4] |= 0x01;
//				os.write(flvHeader, 0, 13);
//				os.flush();
//			}
//			os.write(tag, 0, len);
//			os.flush();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
