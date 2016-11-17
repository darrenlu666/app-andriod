package com.codyy.erpsportal.onlinemeetings.models.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.codyy.erpsportal.onlinemeetings.models.entities.ChatMessage;
import com.codyy.erpsportal.commons.utils.Cog;

import java.util.ArrayList;
import java.util.List;

/**
 * 保存聊天信息数据库
 */
public class ChatDataHelper {

    public static final String TAG = "ChatDataHelper";
    public static final String TABLE_NAME_CHAT = "chat_table";
    public static final String COLUMN_CHAT_ID = "id";
    public static final String COLUMN_CHAT_FROM = "fromId";
    public static final String COLUMN_CHAT_TO = "toId";
    public static final String COLUMN_CHAT_TIME = "time";
    public static final String COLUMN_CHAT_CONTENT = "content";
    public static final String COLUMN_CHAT_HEADURL = "head_url";
    public static final String COLUMN_CHAT_NAME = "name";
    public static final String COLUMN_CHAT_CACHE_TAYPE = "chat_type";

    private static final String CHAT_TABLE_CREATE = "CREATE TABLE "
            + ChatDataHelper.TABLE_NAME_CHAT + " ("
            + "_id integer not null primary key autoincrement,"
            + ChatDataHelper.COLUMN_CHAT_ID + " TEXT , "
            + ChatDataHelper.COLUMN_CHAT_FROM + " TEXT, "
            + ChatDataHelper.COLUMN_CHAT_TO + " TEXT, "
            + ChatDataHelper.COLUMN_CHAT_TIME + " TEXT, "
            + ChatDataHelper.COLUMN_CHAT_CONTENT + " TEXT, "
            + ChatDataHelper.COLUMN_CHAT_HEADURL + " TEXT, "
            + ChatDataHelper.COLUMN_CHAT_NAME + " TEXT, "
            + ChatDataHelper.COLUMN_CHAT_CACHE_TAYPE + " TEXT);";

    private Context mContext;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;


    public ChatDataHelper(Context context) {
        mContext = context;
        mDbHelper = new DatabaseHelper(context);
    }

    public Context getContext() {
        return mContext;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, "codyy_chat.mDb", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase mDb) {

            mDb.execSQL(CHAT_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase mDb, int oldVersion, int newVersion) {
            mDb.execSQL("DROP TABLE IF EXISTS codyy_chat");
            onCreate(mDb);
        }
    }

    //打开数据库
    public ChatDataHelper open() throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    //关闭数据库
    public void close() {
        mDbHelper.close();
    }


    public void addChat(List<ChatMessage> messages, String id, int index) {
        if (messages != null) {
            for (int i = index; i < messages.size(); i++) {
                ChatMessage chatMessage = messages.get(i);
                if(!isExist(chatMessage.getTime())){
                    ContentValues values = chatToContentValue(chatMessage, id);
                    mDb.insert(TABLE_NAME_CHAT, COLUMN_CHAT_ID + "=" + id, values);
                }
            }
        }
    }

    /**
     * 删除一条数据　
     * @param message
     * @param id
     */
    public void removeData(ChatMessage message ,String id){
        if(null == message) return;
        if(isExist(message.getTime())){
            mDb.delete(TABLE_NAME_CHAT,COLUMN_CHAT_ID + "=?",new String[]{id});
        }
    }
    /**
     *
     * @param timeStamp 时间戳
     * @return
     */
    public boolean isExist(String timeStamp){
        boolean result = false ;

        Cursor cursor = null;
        try {
            cursor = mDb.query(TABLE_NAME_CHAT, null, COLUMN_CHAT_TIME + "=?",
                    new String[]{timeStamp}, null, null, null, "0,30");
            if (cursor != null && cursor.getCount()>0) {
               result = true ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return  result ;
    }


    public void addChat(ChatMessage messages, String id) {
        if (messages != null) {
            ContentValues values = chatToContentValue(messages, id);
            mDb.insert(TABLE_NAME_CHAT, COLUMN_CHAT_ID + "=" + id, values);
        }
    }


    public int deleteAll() {
        return mDb.delete(TABLE_NAME_CHAT, null,
                null);
    }

    public List<ChatMessage> queryAllChat(String id) {
        List<ChatMessage> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDb.query(TABLE_NAME_CHAT, null, COLUMN_CHAT_ID + "=?",
                    new String[]{id}, null, null, null, "0,30");
            if (cursor != null) {
                if (cursor.moveToLast()) {
                    do {
                        ChatMessage chatMessage = cursorToChat(cursor);
                        Cog.d(TAG, "Chat_content --queryAllChat------->" + chatMessage.getMsg());
                        list.add(chatMessage);
                    } while (cursor.moveToPrevious());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        List<ChatMessage> messages = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            messages.add(list.get(i));
        }

        return messages;
    }


    public ChatMessage queryChatById(String id) {
        Cursor cursor = null;
        try {
            cursor = mDb.query(TABLE_NAME_CHAT, null, COLUMN_CHAT_ID + "=?",
                    new String[]{id}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToLast()) {
                    return cursorToChat(cursor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 获取最后一条记录保存的时间
     *
     * @return
     */
    public String getLastTime() {
        Cursor cursor = null;
        try {
            cursor = mDb.query(TABLE_NAME_CHAT, null, null,
                    null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToLast()) {
                    return cursor.getString(cursor.getColumnIndex(COLUMN_CHAT_TIME));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private ContentValues chatToContentValue(ChatMessage chatMessage, String id) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHAT_ID, id);
        values.put(COLUMN_CHAT_FROM, chatMessage.getFrom());
        values.put(COLUMN_CHAT_TO, chatMessage.getTo());
        values.put(COLUMN_CHAT_TIME, chatMessage.getTime());
        values.put(COLUMN_CHAT_CONTENT, chatMessage.getMsg());
        values.put(COLUMN_CHAT_HEADURL, chatMessage.getHeadUrl());
        values.put(COLUMN_CHAT_NAME, chatMessage.getName());
        values.put(COLUMN_CHAT_CACHE_TAYPE, chatMessage.getChatType());


        return values;
    }

    private ChatMessage cursorToChat(Cursor cursor) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(cursor.getString(cursor.getColumnIndex(COLUMN_CHAT_ID)));
        chatMessage.setFrom(cursor.getString(cursor.getColumnIndex(COLUMN_CHAT_FROM)));
        chatMessage.setTo(cursor.getString(cursor.getColumnIndex(COLUMN_CHAT_TO)));
        chatMessage.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_CHAT_TIME)));
        chatMessage.setMsg(cursor.getString(cursor.getColumnIndex(COLUMN_CHAT_CONTENT)));
        chatMessage.setHeadUrl(cursor.getString(cursor.getColumnIndex(COLUMN_CHAT_HEADURL)));
        chatMessage.setName(cursor.getString(cursor.getColumnIndex(COLUMN_CHAT_NAME)));
        chatMessage.setChatType(Integer.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_CHAT_CACHE_TAYPE))));

        return chatMessage;
    }

}
