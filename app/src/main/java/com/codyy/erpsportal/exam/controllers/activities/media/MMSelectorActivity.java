package com.codyy.erpsportal.exam.controllers.activities.media;

import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.exam.controllers.activities.media.audio.MMAudioAlbumFragment;
import com.codyy.erpsportal.exam.controllers.activities.media.image.MMImageAlbumFragment;
import com.codyy.erpsportal.exam.controllers.activities.media.video.MMVideoAlbumFragment;

import butterknife.Bind;


/**
 * @author eachann
 */
public class MMSelectorActivity extends ToolbarActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mTitle;
    /**
     * 单位M
     */
    private static final int sVideoDefaultSize = 500;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_task_multimedia;
    }

    private static final String EXTRA_TYPE = "EXTRA_TYPE";
    private static final String EXTRA_TYPE_IMAGE = "IMAGE";
    private static final String EXTRA_TYPE_AUDIO = "AUDIO";
    private static final String EXTRA_TYPE_VIDEO = "VIDEO";

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
        if (getIntent().getStringExtra(EXTRA_TYPE) != null) {
            switch (getIntent().getStringExtra(EXTRA_TYPE)) {
                case EXTRA_TYPE_IMAGE:
                    mTitle.setText(getString(R.string.exam_image_select));
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, MMImageAlbumFragment.newInstance(getIntent().getIntExtra("EXTRA_SIZE", 0))).commitAllowingStateLoss();
                    break;
                case EXTRA_TYPE_AUDIO:
                    mTitle.setText(getString(R.string.exam_audio_select));
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, new MMAudioAlbumFragment()).commitAllowingStateLoss();
                    break;
                case EXTRA_TYPE_VIDEO:
                    mTitle.setText(getString(R.string.exam_video_select));
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, MMVideoAlbumFragment.newInstance(getIntent().getIntExtra("EXTRA_SIZE", sVideoDefaultSize) * 1024 * 1024 + 1)).commitAllowingStateLoss();
                    break;
            }

        }
        setViewAnim(false, mTitle);
    }
}
