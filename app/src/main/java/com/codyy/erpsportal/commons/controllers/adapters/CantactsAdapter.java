package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.onlinemeetings.models.entities.ChatMessage;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.widgets.AsyncTextView;
import com.codyy.erpsportal.commons.widgets.AvatarView;

import java.util.LinkedList;

/**
 * Created by yangxinwu on 2015/8/28.
 */
public class CantactsAdapter extends BaseAdapter {
    private Context mContext;
    private LinkedList<ChatMessage> messages;
    public CantactsAdapter(Context mContext, LinkedList<ChatMessage> messages) {
        this.mContext = mContext;
        this.messages = messages;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.v2_list_cell_chat_conversation, null, false);
            viewHolder.name = (AsyncTextView) convertView.findViewById(R.id.tv_name);
            viewHolder.message = (TextView) convertView.findViewById(R.id.tv_message);
            viewHolder.time = (TextView) convertView.findViewById(R.id.tv_start_time);
            viewHolder.unreadCount = (TextView) convertView.findViewById(R.id.tv_unread_msg_number);
            viewHolder.msgState = (ImageView) convertView.findViewById(R.id.iv_msg_state);
            viewHolder.avatar = (AvatarView) convertView.findViewById(R.id.iv_avatar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ChatMessage chatMessage = messages.get(position);
        if (chatMessage.isHasUnReadMsg()){
            viewHolder.unreadCount.setVisibility(View.VISIBLE);
        } else {
            viewHolder.unreadCount.setVisibility(View.INVISIBLE);
        }
        viewHolder.name.setText(chatMessage.getFrom());
        viewHolder.message.setText(chatMessage.getMsg());
        viewHolder.time.setText(DateUtil.stringToDate(chatMessage.getTime()));
        //设置用户头像
        setUserAvatar(chatMessage, viewHolder.name, viewHolder.avatar );
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    /**
     * 显示用户头像
     *
     * @param message
     * @param imageView
     */
    private void setUserAvatar(ChatMessage message, final AsyncTextView displayName,
                               final AvatarView imageView) {

            imageView.setAvatarUrl(message.getHeadUrl());
            if (displayName != null) {
                displayName.setText(message.getName());
            }

    }

    class ViewHolder {
        private TextView message, time, unreadCount;
        private AsyncTextView name;
        private ImageView msgState;
        private AvatarView avatar;
    }

}
