/*
 * Created by FanChao
 */
package com.codyy.erpsportal.commons.models.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.codyy.erpsportal.commons.utils.Constants;


public class DownloadOpenHelper extends SQLiteOpenHelper {

	private final static String DB = Constants.DB_DOWNLOAD;
	private final static int VERSION = 2;

	public DownloadOpenHelper(Context context) {
		super(context, DB, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE download_info(" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId TEXT, " +
				"res_id TEXT, "+
				"thread_id INTEGER, "+
				"start_pos INTEGER," +
				"end_pos INTEGER, " +
				"compelete_size INTEGER, " +
				"total_size INTEGER, " +
				"url CHAR ," +
				"update_time Long)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(newVersion==2){
			db.execSQL("ALTER TABLE download_info add COLUMN userId TEXT");
		}
	}

}
