package com.codyy.erpsportal.repairs.controllers.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.repairs.models.entities.RepairRecord;
import com.codyy.erpsportal.repairs.models.entities.StatusItem;

import org.joda.time.format.DateTimeFormat;

import butterknife.Bind;

/**
 * 报修记录组持者
 * Created by gujiajia on 2017/3/22.
 */
@LayoutId(R.layout.item_repair_record)
public class RepairRecordVh extends BindingRvHolder<RepairRecord> {

    @Bind(R.id.tv_classroom)
    TextView mClassroomTv;

    @Bind(R.id.tv_status)
    TextView mStatusTv;

    @Bind(R.id.tv_reporter)
    TextView mReporterTv;

    @Bind(R.id.tv_report_time)
    TextView mReportTimeTv;

    @Bind(R.id.tv_content)
    TextView mContentTv;

    public RepairRecordVh(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataToView(RepairRecord repairRecord) {
        Context context = itemView.getContext();
        mClassroomTv.setText(context.getString(R.string.classroom_role_format,
                repairRecord.getSkey(),
                repairRecord.getClassRoomName()));
        mReporterTv.setText(repairRecord.getReporter());
        if (StatusItem.STATUS_NEW.equals(repairRecord.getStatus())) {
            mStatusTv.setText(R.string.status_await_handle);
            mStatusTv.setTextColor(0xffe86153);
        } else if (StatusItem.STATUS_PROGRESS.equals(repairRecord.getStatus())) {
            mStatusTv.setText(R.string.status_handling);
            mStatusTv.setTextColor(0xfffd9b01);
        } else if (StatusItem.STATUS_DONE.equals(repairRecord.getStatus())) {
            mStatusTv.setText(R.string.status_handled);
            mStatusTv.setTextColor(0xff19ab20);
        } else {
            mStatusTv.setText(R.string.status_accepted);
            mStatusTv.setTextColor(0xff999999);
        }
        mReportTimeTv.setText(DateTimeFormat
                .forPattern("yyyy-MM-dd HH:mm")
                .print(repairRecord.getCreateTime()));
        mContentTv.setText(repairRecord.getMalDescription());
    }
}
