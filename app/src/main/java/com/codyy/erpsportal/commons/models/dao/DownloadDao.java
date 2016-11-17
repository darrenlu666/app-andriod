/*
 * Created by FanChao
 */
package com.codyy.erpsportal.commons.models.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.codyy.erpsportal.commons.models.entities.download.BreakPointInfo;
import com.codyy.erpsportal.commons.utils.Cog;

import java.util.ArrayList;
import java.util.List;

public class DownloadDao {
	private static final String TAG = "DownloadDao";
	private static DownloadDao instance;
	private Context context;

	private DownloadDao(Context context) {
		this.context = context;
	}

	public synchronized static DownloadDao instance(Context context) {
		if (instance == null) {
			instance = new DownloadDao(context);
		}
		return instance;
	}

	public SQLiteDatabase getConnection() {
		SQLiteDatabase sqliteDatabase = null;
		try {
			sqliteDatabase = new DownloadOpenHelper(context).getReadableDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sqliteDatabase;
	}

	/**
	 * 检查下载信是否存在  resId是判断标识符
	 * @param resId
	 * @return
	 */
	public synchronized boolean isHasDownloadInfors(String userId, String resId) {
		SQLiteDatabase db = getConnection();
		int count = -1;
		Cursor cursor = null;
		try {
			String sql = "SELECT COUNT(*) FROM download_info WHERE userId=? AND res_id=?";
			cursor = db.rawQuery(sql, new String[] { userId, resId });
			if (cursor.moveToFirst()) {
				count = cursor.getInt(0);
			}
		} catch (Exception e) {
			Cog.e(TAG, "sql execute fail", e);
            e.printStackTrace();
		} finally {
			if (null != cursor) {
				cursor.close();
			}
			if (null != db) {
				db.close();
			}
		}
		return count == 0;
	}

	/**
	 * 保存 下载的具体信息
	 */
	public synchronized void saveDownloadInfos(List<BreakPointInfo> infos) {
		SQLiteDatabase database = getConnection();
		try {
			for (BreakPointInfo info : infos) {
				String sql = "insert into download_info(userId, res_id,thread_id, start_pos, end_pos, compelete_size, total_size, update_time, url) " +
                        "values (?,?,?,?,?,?,?,?,?)";
				Object[] bindArgs = { info.getUserId(), info.getResId(),info.getThreadId(),
                        info.getStartPos(), info.getEndPos(),
                        info.getCompleteSize(), info.getTotal(), info.getUpdateTime(), info.getUrl() };
				database.execSQL(sql, bindArgs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}

	/**
	 * 得到下载具体信息
	 */
	public synchronized List<BreakPointInfo> getDownloadInfos(String userId, String resId) {
		List<BreakPointInfo> list = new ArrayList<>();
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			String sql = "SELECT userId, res_id, thread_id, start_pos, end_pos,compelete_size,total_size, url, update_time " +
                    "FROM download_info " +
                    "WHERE userId=? AND res_id=?";
			cursor = database.rawQuery(sql, new String[] { userId, resId });
			while (cursor.moveToNext()) {
                BreakPointInfo info = new BreakPointInfo(
                        cursor.getString(cursor.getColumnIndex("userId")),
                        cursor.getString(cursor.getColumnIndex("res_id")),
                        cursor.getInt(cursor.getColumnIndex("thread_id")),
                        cursor.getInt(cursor.getColumnIndex("start_pos")),
                        cursor.getInt(cursor.getColumnIndex("end_pos")),
                        cursor.getInt(cursor.getColumnIndex("compelete_size")),
                        cursor.getInt(cursor.getColumnIndex("total_size")),
                        cursor.getString(cursor.getColumnIndex("url")),
                        cursor.getLong(cursor.getColumnIndex("update_time")));
				list.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return list;
	}

	public synchronized int getTotalDownload(String userId, String resId){
		int total = 0;
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			String sql = "SELECT compelete_size FROM download_info WHERE userId=? AND res_id=?";
			cursor = database.rawQuery(sql, new String[] { userId, resId });
			while (cursor.moveToNext()) {
				total += cursor.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return total;
	}

	public synchronized int getFileSize(String userId, String resId){
		int total = 0;
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			String sql = "SELECT total_size FROM download_info WHERE userId=? AND res_id=?";
			cursor = database.rawQuery(sql, new String[] { userId, resId });
			while (cursor.moveToNext()) {
				total = cursor.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return total;
	}

	/**
	 * 更新数据库中的下载信息
     * @param byteSize 新的获取的数据量
	 */
	public synchronized void updateDownloadInfos(String userId, String resId, long byteSize, long updateTime, int threadId) {
		SQLiteDatabase database = getConnection();
		try {
			String sql = "UPDATE download_info SET compelete_size = compelete_size + ?, update_time = ? " +
					"WHERE userId=? AND res_id=? AND thread_id=?";
			Object[] bindArgs = { byteSize, updateTime, userId, resId, threadId};
			database.execSQL(sql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}

    /**
     * 下载完成后删除数据库中的数据
     */
    public synchronized void delete(String userId, String resId) {
        SQLiteDatabase database = getConnection();
        try {
            database.delete("download_info", "userId=? AND res_id=?", new String[] {userId,resId});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }
    }

	/**
	 * 下载异常后、删除数据库中的数据
	 */
	public synchronized void deleteByFileName(String userId, String resId) {
		SQLiteDatabase database = getConnection();
		try {
			database.delete("download_info", "userId=? AND res_id=?", new String[]{userId, resId});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
	}

	public synchronized void batchDeleteByFileName(String userId, List<String> resIds) {
        if (resIds == null || resIds.size() == 0) return;
        SQLiteDatabase database = getConnection();
        database.beginTransaction();
        try {
            for (String resId : resIds) {
                database.delete("download_info", "userId=? AND res_id=?", new String[]{userId, resId});
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            database.close();
        }
    }

	public int getOneFileProgress(String userId, String resId){
		int progress = 0;
		SQLiteDatabase database = getConnection();
		try {
			Cursor cursor=database.query("download_info", null, "userId=? AND res_id=?", new String[]{userId, resId}, null, null, null);
			if(cursor!=null){
				if(cursor.getCount()>0){
					cursor.moveToFirst();
					long bytesReceived =cursor.getInt(cursor.getColumnIndex("compelete_size"));//*100后超过int型数组的界限
					int fileSize = cursor.getInt(cursor.getColumnIndex("total_size"));
                    progress = (int)(bytesReceived * 100 / fileSize);
				}
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
		}
		return progress;
	}
}
