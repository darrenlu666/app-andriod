package com.codyy.erpsportal.commons.models.engine;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.mainpage.MainResClassroom;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.ref.WeakReference;

/**
 * 直播数据填充器
 * Created by gujiajia on 2016/8/19.
 */
@ItemLayoutId(R.layout.item_main_live_course)
public class LiveClassroomViewStuffer implements ViewStuffer<MainResClassroom> {

    private WeakReference<Activity> mActivityRef;

    private OnLiveClassroomClickListener mClassroomClickListener;

    public LiveClassroomViewStuffer(Activity activity) {
        this.mActivityRef = new WeakReference<>(activity);
    }

    public LiveClassroomViewStuffer(Activity activity,
                                    OnLiveClassroomClickListener liveClassroomClickListener) {
        this.mActivityRef = new WeakReference<>(activity);
        this.mClassroomClickListener = liveClassroomClickListener;
    }

    @Override
    public void onStuffView(View view, final MainResClassroom liveClassroom) {
        TextView titleTv = (TextView) view.findViewById(R.id.tv_title);
        TextView scopeTv = (TextView) view.findViewById(R.id.tv_scope);
        TextView timeTv = (TextView) view.findViewById(R.id.tv_time);
        titleTv.setText(liveClassroom.getSchoolName());
        String scopeStr = liveClassroom.getGradeName() + "/" +
                liveClassroom.getSubjectName() + "/" +
                liveClassroom.getTeacherName();
        scopeTv.setText(scopeStr);
        if ("liveAppointment".equals(liveClassroom.getType())) {//名校网络课堂 节次形式
            int classSeq = liveClassroom.getClassSeq();
            String[] numbers = view.getContext().getResources().getStringArray(R.array.numbers);
            timeTv.setText(numbers[classSeq]);
        } else {//专递课堂9:10
            long startTime = liveClassroom.getRealBeginTime();
            DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
            timeTv.setText(dtf.print(startTime));
        }
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClassroomClickListener != null)
                    mClassroomClickListener.onLiveClassroomClick(liveClassroom);
//                Activity activity = mActivityRef.get();
//                if (activity == null) return;
//                FirstPageLiveActivity.startActivity(activity,
//                        liveClassroom.getId(),
//                        liveClassroom.getType());
            }
        });
    }

    public interface OnLiveClassroomClickListener{
        void onLiveClassroomClick(MainResClassroom liveClassroom);
    }
}
