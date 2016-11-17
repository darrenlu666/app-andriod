package com.codyy.erpsportal.onlinemeetings.controllers.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.onlinemeetings.models.entities.DocumentDetailEntity;
import com.codyy.erpsportal.onlinemeetings.models.entities.MeetingBase;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by poe on 16-1-4.
 */
public class OnlineDocumentViewHolder extends BaseRecyclerViewHolder<DocumentDetailEntity> {

    @Bind(R.id.tv_documentName) TextView mDocumentName;
    @Bind(R.id.tv_documentAuthor) TextView mDocumentAuthor;
    @Bind(R.id.ib_showDocument)   ImageView mDocumentClickIcon;

    private MeetingBase mMeetingBase ;//当前用户的角色

    public OnlineDocumentViewHolder(View itemView , MeetingBase meetingBase) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mMeetingBase  =   meetingBase ;
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.item_documentmanagement;
    }

    @Override
    public void setData(int position , DocumentDetailEntity data) {
        mCurrentPosition    =   position ;
        this.mData  =   data ;
        mDocumentName.setText(data.getDocName());
        mDocumentAuthor.setText(data.getUploadPerson());
        if(!mMeetingBase.isWhiteBoardManager()){
            mDocumentClickIcon.setVisibility(View.GONE);
        } else{
            mDocumentClickIcon.setVisibility(View.VISIBLE);
        }
    }
}
