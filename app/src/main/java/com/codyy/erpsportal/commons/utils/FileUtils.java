package com.codyy.erpsportal.commons.utils;

import android.content.Context;
import android.os.Environment;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.commons.models.dao.DownloadDao;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

public final class FileUtils {

    private FileUtils() {
    }

    /**
     * @param parentDir
     */
    public static File createFolder(File parentDir, String folderName) {
        File f = new File(parentDir, folderName);
        if (!f.exists() || !f.isDirectory()) {
            if (!f.mkdir()) {
                return null;
            }
        }
        return f;
    }

    public static void deleteFolderRecursively(File folder) {
        if (folder != null && folder.isDirectory() && folder.canWrite()) {
            //delete your contents and recursively delete sub-folders
            File[] listFiles = folder.listFiles();
            
            if (listFiles != null) {
                for (File f : listFiles) {
                    if (f.isFile()) {
                        f.delete();
                    } else if (f.isDirectory()) {
                        deleteFolderRecursively(f);
                    }
                }
                folder.delete();
            }
        }
    }
    
    /**
     * 安全删除（含未下载完的大文件）
     * 如果sd卡空间不足、迭代删除SD卡的下载素材、
     * 直到可用空间200M以上 
     * 对应位置：Documents
     */
    public static void deleteSdData(Context context) throws Exception{
		long sdAvail = MemoryStatus.getSdAvail();
		if(sdAvail < Constants.MAX_AVAILABLE_SPACE) {
            Cog.e("Warn space ", "waring! SD only has free space:" + sdAvail);
            File file=SystemUtils.getCacheDirectory();
			if(file.list().length>0){
				deleteFile(file,context);
				deleteSdData(context);
			}
		}
	}

    /**
     * 清楚所有下载文件
     * @param folder
     * @param context
     */
    public static void deleteFile(File folder, Context context) {
    	if (folder != null && folder.isDirectory() && folder.canWrite()) {
    		//delete your contents and recursively delete sub-folders
    		File[] listFiles = folder.listFiles();
    		if (listFiles != null && listFiles.length > 0) {
    			Arrays.sort(listFiles, new Comparator<File>() {
    				@Override
    				public int compare(File lhs, File rhs) {
    					return Long.valueOf(lhs.lastModified()).compareTo(rhs.lastModified()); 
    				}
    			});

                for(int i=0; i<listFiles.length;i++) {
                    //如果是未下载完成的文件 必须先删除断点信息
                    File file = listFiles[i];
                    if (file.getName().endsWith( Constants.CACHING_SUFFIX)) {
                        if (context != null) {
                            String fileName = listFiles[i].getName();
                            if (fileName.contains(".")) {
                                int dotIndex = fileName.lastIndexOf(".");
                                if (dotIndex == -1) {
                                    continue;
                                }
                                fileName = fileName.substring(i, dotIndex);
                                DownloadDao.instance(context).deleteByFileName(file.getParent(), fileName);
                            }
                            file.delete();
                        }
                    } else {
                        file.delete();
                    }

                }
    		}
    	}
    }
    
    /**
     * remove all file in the folder
     * @param folder
     */
    public static void deleteFileFolder(File folder) {
    	if (folder != null && folder.isDirectory() && folder.canWrite()) {
    		//delete your contents and recursively delete sub-folders
    		File[] listFiles = folder.listFiles();
    		if (listFiles != null && listFiles.length > 0) {
    			for(File child:listFiles){
    				if(child.isDirectory()){
    					deleteFileFolder(child);
    				}else{
                        if (child.getName().endsWith( Constants.CACHING_SUFFIX)) {
                            String fileName = child.getName();
                            if (fileName.contains(".")) {
                                int dotIndex = fileName.lastIndexOf(".");
                                if (dotIndex == -1) {
                                    continue;
                                }
                                fileName = fileName.substring(0, fileName.indexOf("."));
                                DownloadDao.instance(EApplication.instance()).deleteByFileName(child.getParent(), fileName);
                            }
                            TransferManager.instance().cancelDownloadTasks();
                        }
    					child.delete();
    				}
    			}
    		}
    	}
    }

    public static boolean deleteEmptyDirectoryRecursive(File directory) {
        // make sure we only delete canonical children of the parent file we
        // wish to delete. I have a hunch this might be an issue on OSX and
        // Linux under certain circumstances.
        // If anyone can test whether this really happens (possibly related to
        // symlinks), I would much appreciate it.
        String canonicalParent;
        try {
            canonicalParent = directory.getCanonicalPath();
        } catch (IOException ioe) {
            return false;
        }

        if (!directory.isDirectory()) {
            return false;
        }

        boolean canDelete = true;

        File[] files = directory.listFiles();
        for (File file : files) {
            try {
                if (!file.getCanonicalPath().startsWith(canonicalParent))
                    continue;
            } catch (IOException ioe) {
                canDelete = false;
            }

            if (!deleteEmptyDirectoryRecursive(file)) {
                canDelete = false;
            }
        }

        return canDelete && directory.delete();
    }
    
    public static String getValidFileName(String fileName) {
        String newFileName = fileName.replaceAll("[\\\\/:*?\"<>|\\[\\]]+", "_");
        return newFileName;
    }

    /** 保存css样式表
     * @param content
     * @return
     * @throws IOException
     */
    public static boolean saveCSS(String content) throws IOException{
    	
    	boolean result =false;
    	
    	if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){   
	        InputStream is = new ByteArrayInputStream(content.getBytes());
	        File file = new File(Environment.getExternalStorageDirectory().getPath()+"/emj/program/", "css.css");   
	        FileOutputStream fos = new FileOutputStream(file);   
	        BufferedInputStream bis = new BufferedInputStream(is);   
	        byte[] buffer = new byte[1024];   
	        int len ;   
	        int total=0;   
	        
	        while((len =bis.read(buffer))!=-1){   
	            fos.write(buffer, 0, len);   
	            total+= len;   
	            //获取当前下载量   
	        }   
	        
	        fos.close();   
	        bis.close();   
	        is.close();
	        result =true;
	    }
    	return result;
    }
}
