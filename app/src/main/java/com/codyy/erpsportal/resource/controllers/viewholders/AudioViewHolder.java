package com.codyy.erpsportal.resource.controllers.viewholders;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.EasyVhrCreator.LayoutId;
import com.codyy.erpsportal.resource.models.entities.Audio;
import com.codyy.erpsportal.resource.models.entities.AudioEvent;

import org.joda.time.format.DateTimeFormat;

import java.util.List;

import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * 音频资源ViewHolder
 * Created by gujiajia on 2016/6/14.
 */
@LayoutId(R.layout.item_resource_audio)
public class AudioViewHolder extends BindingRvHolder<Audio> {

    @Bind(R.id.ib_play)
    ImageButton mPlayIb;

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.tv_view_count)
    TextView mViewCountTv;

    @Bind(R.id.tv_download_count)
    TextView mDownloadCountTv;

    @Bind(R.id.tv_duration)
    TextView mDurationTv;

    public AudioViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataToView(final List<Audio> audioList, final int position) {
        final Audio audio = audioList.get(position);
        mTitleTv.setText(audio.getName());
        final AudioEvent audioEvent = new AudioEvent(audio, position);
        mPlayIb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(audioEvent);
            }
        });
        if (audio.isPlaying()) {
            mPlayIb.setImageResource(R.drawable.btn_pause);
        } else {
            mPlayIb.setImageResource(R.drawable.btn_play);
        }
        mViewCountTv.setText(audio.getPlayCount() + "");
        mDownloadCountTv.setText(audio.getDownloadCount() + "");
        mDurationTv.setText(DateTimeFormat.forPattern("HH:mm:ss").withZoneUTC().print(audio.getDuration()));
//        itemView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AudioDetailsActivity.start(v.getContext(), audioList, position);
//            }
//        });
    }
}
