package com.codyy.erpsportal.commons.controllers.viewholders.interact;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.onlinemeetings.models.entities.VideoMeetingDetailEntity;
import com.codyy.erpsportal.commons.models.entities.interact.RecordPriority;
import com.codyy.erpsportal.onlineteach.models.entities.NetPermission;
import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;
import com.codyy.tpmp.filterlibrary.viewholders.BaseRecyclerViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 互动听课/集体备课/评课议课-详情-录制权限
 * Created by poe on 16-6-23.
 */
public class RecorderPriorityViewHolder extends BaseRecyclerViewHolder<BaseTitleItemBar> {

    @Bind(R.id.tv_resource_name)
    TextView mSchoolTextView;

    public RecorderPriorityViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_resource_prepare_lseeons;
    }

    @Override
    public void setData(int position, BaseTitleItemBar data) {
        mCurrentPosition = position;
        mData = data;
        if (data instanceof RecordPriority) {
            RecordPriority rp = (RecordPriority) data;
            String permission;
            if (rp.isRecorder()) {
                permission = "(有归档权限)";
            } else {
                permission = "(无归档权限)";
            }
            mSchoolTextView.setText("【" + rp.getRecorderSchool() + "】" + rp.getRecorderName() + permission);
        } else if (data instanceof VideoMeetingDetailEntity.PermissionEntity) {//视频会议
            VideoMeetingDetailEntity.PermissionEntity entity = (VideoMeetingDetailEntity.PermissionEntity) data;
            if(entity.isHasPermission()){
                mSchoolTextView.setText("【" + entity.getSchoolName() + "】" + entity.getTeachName() + "(有归档权限)");
            }else{
                mSchoolTextView.setText("【" + entity.getSchoolName() + "】" + entity.getTeachName() + "(无归档权限)");
            }
        } else if (data instanceof NetPermission) {//网络授课　
            NetPermission permission = (NetPermission) data;
            if(permission.isHasPermission()){
                mSchoolTextView.setText("【" + permission.getSchoolName() + "】" + permission.getClassName() + "(有归档权限)");
            }else{
                mSchoolTextView.setText("【" + permission.getSchoolName() + "】" + permission.getClassName() + "(无归档权限)");
            }
        }
    }
}
