package com.codyy.erpsportal.repairs.controllers.viewholders;

import android.view.View;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.repairs.models.entities.RepairRecord;

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
        mClassroomTv.setText(repairRecord.getClassroomName());
        mReporterTv.setText(repairRecord.getReporterName());
        if (repairRecord.getStatus() == 0) {
            mStatusTv.setText(R.string.status_await_handle);
        } else if (repairRecord.getStatus() == 1) {
            mStatusTv.setText(R.string.status_handling);
        } else if (repairRecord.getStatus() == 2) {
            mStatusTv.setText(R.string.status_handled);
        } else {
            mStatusTv.setText(R.string.status_accepted);
        }
        mReportTimeTv.setText(DateTimeFormat
                .forPattern("yyyy-MM-dd HH:mm")
                .print(repairRecord.getReportTime()));
        mContentTv.setText(repairRecord.getContent());
    }
}
