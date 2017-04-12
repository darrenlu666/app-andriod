package com.codyy.erpsportal.repairs.controllers.viewholders;

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
        mClassroomTv.setText(repairRecord.getClassRoomName());
        mReporterTv.setText(repairRecord.getReporter());
        if (StatusItem.STATUS_NEW.equals(repairRecord.getStatus())) {
            mStatusTv.setText(R.string.status_await_handle);
        } else if (StatusItem.STATUS_PROGRESS.equals(repairRecord.getStatus())) {
            mStatusTv.setText(R.string.status_handling);
        } else if (StatusItem.STATUS_DONE.equals(repairRecord.getStatus())) {
            mStatusTv.setText(R.string.status_handled);
        } else {
            mStatusTv.setText(R.string.status_accepted);
        }
        mReportTimeTv.setText(DateTimeFormat
                .forPattern("yyyy-MM-dd HH:mm")
                .print(repairRecord.getCreateTime()));
        mContentTv.setText(repairRecord.getMalDescription());
    }
}
