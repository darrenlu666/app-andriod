package com.codyy.erpsportal.commons.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import com.codyy.erpsportal.commons.models.entities.Disk;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;


// 这个用于监控磁盘容量的类(包括内存和SD卡的总空间和可用空间)
public class MemoryStatus {

	private static final String TAG = "MemoryStatus";

	public static Disk updateMemoryStatus(Context context) {
		Disk disk = new Disk();
		String status = Environment.getExternalStorageState();
		// 是否只读
		if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
			status = Environment.MEDIA_MOUNTED;
		}
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				StatFs stat = getSdStat();
				// SD卡总容量
				disk.setSdSize(UIUtils.getBytesInHuman(getSdSize(stat)));
				// SD卡剩余容量
				disk.setSdAvail(UIUtils.getBytesInHuman(getSdAvail(stat)));
				// SD卡已经使用容量
				disk.setSdUsed(UIUtils.getBytesInHuman(getSdUsed(stat)));
			} catch (IllegalArgumentException e) {
				status = Environment.MEDIA_REMOVED;
			}
		}
		// 内存剩余容量
		long memoryAvail = getAvailMemory(context);
		disk.setMemoryAvail(UIUtils.getBytesInHuman(memoryAvail));
		// 内存总容量
		long memorySize = getTotalMemory();
		disk.setMemorySize(UIUtils.getBytesInHuman(memorySize));
		// 内存已使用
		long memoryUsed = memorySize - memoryAvail;
		disk.setMemoryUsed(UIUtils.getBytesInHuman(memoryUsed));
		return disk;
	}
	
	// SD卡总容量
	public static long getSdSize(StatFs stat){
		return getSdTotalBlocks(stat) * getSdBlockSize(stat);
	}

	// SD卡剩余容量
	public static long getSdAvail(StatFs stat){
		return getSdAvailableBlocks(stat) * getSdBlockSize(stat);
	}
	
	public static long getSdAvail(){
		StatFs stat = getSdStat();
		return getSdAvailableBlocks(stat) * getSdBlockSize(stat);
	}
	
	// SD卡已经使用容量 (totalBlocks - availableBlocks) * blockSize
	public static long getSdUsed(StatFs stat){
		return (getSdTotalBlocks(stat) - getSdAvailableBlocks(stat)) * getSdBlockSize(stat);
	}
	
	private static StatFs getSdStat() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		return stat;
	}
	
	private static long getSdAvailableBlocks(StatFs stat){
		return stat.getAvailableBlocks();
	}
	
	private static long getSdTotalBlocks(StatFs stat){
		return stat.getBlockCount();
	}
	
	private static long getSdBlockSize(StatFs stat){
		return stat.getBlockSize();
	}
	

	// 获取android当前可用内存大小
	private static long getAvailMemory(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		manager.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}

	// 获取总内存大小
	private static long getTotalMemory() {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
			// 读取meminfo第一行，系统总内存大小 str2=MemTotal: 833428 kB
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			for (String num : arrayOfString) {
				Log.i(TAG, num + "\t");
			}

			initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
			localBufferedReader.close();
		} catch (IOException e) {
		}
		return initial_memory;
	}

	// 格式化 转化为.MB格式
	public String formatSize(long size) {
		return Long.toString((size / 1024) / 1024);
	}

	// 格式化 转化为.GB格式
	public String formatSize1(long size) {
		BigDecimal b = new BigDecimal((float) ((size / 1024) / 1024) / 1024);
		float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
		return Float.toString(f1);
	}
}
