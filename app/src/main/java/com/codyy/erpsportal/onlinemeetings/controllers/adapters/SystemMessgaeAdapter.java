package com.codyy.erpsportal.onlinemeetings.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.SystemMessage;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.StringUtils;
import com.codyy.erpsportal.commons.widgets.AsyncTextView;
import com.codyy.erpsportal.commons.widgets.AvatarView;

import java.util.List;

/**
 * Created by yangxinwu on 2015/8/28.
 */
public class SystemMessgaeAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<SystemMessage> mMessages;

    public SystemMessgaeAdapter(Context context, List<SystemMessage> mMessages) {
        inflater = LayoutInflater.from(context);
        this.mMessages = mMessages;
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row_received_message, null);
            holder.ivAvatar = (AvatarView) convertView.findViewById(R.id.iv_userhead);
            holder.timestamp = (TextView) convertView.findViewById(R.id.timestamp);
            // 这里是文字内容
            holder.tvChatMsg = (TextView) convertView.findViewById(R.id.tv_chatcontent);
            holder.tvUserName = (AsyncTextView) convertView.findViewById(R.id.tv_userid);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SystemMessage systemMessage = mMessages.get(position);
        holder.tvUserName.setText(systemMessage.getNick());
        holder.tvUserName.setVisibility(View.GONE);
        holder.tvChatMsg.setText(systemMessage.getNick()+systemMessage.getMsg());
//        if(TextUtils.isEmpty(systemMessage.getNick())){
//            holder.tvChatMsg.setText(systemMessage.getNick()+systemMessage.getMsg());
//        }else{
//            holder.tvChatMsg.setText(systemMessage.getNick()+systemMessage.getMsg());
//        }
        setUserAvatar(systemMessage,holder.ivAvatar);
        if (position == 0) {
            holder.timestamp.setText(DateUtil.stringToDate(systemMessage.getTime()));
            holder.timestamp.setVisibility(View.VISIBLE);
        } else {
            // 两条消息时间离得如果稍长，显示时间
            SystemMessage systemMessage2 = mMessages.get(position - 1);
            if (systemMessage2 != null && DateUtil.isCloseEnough(Long.parseLong(systemMessage.getTime()), Long.parseLong(systemMessage2.getTime()))) {
                holder.timestamp.setVisibility(View.GONE);
            } else {
                holder.timestamp.setText(DateUtil.stringToDate(systemMessage.getTime()));
                holder.timestamp.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }

    /**
     * 显示用户头像
     *
     * @param message
     * @param imageView
     */
    private void setUserAvatar(SystemMessage message,
                               final AvatarView imageView) {
        if(message.getHeadUrl()!=null) {
            imageView.setAvatarUrl(StringUtils.replaceSmallPic(message.getHeadUrl()));
        }
    }
    public static class ViewHolder {
        TextView tvChatMsg;
        AvatarView ivAvatar;
        AsyncTextView tvUserName;
        TextView timestamp;
    }
}