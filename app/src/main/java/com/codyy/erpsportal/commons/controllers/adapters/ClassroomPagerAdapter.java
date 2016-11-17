package com.codyy.erpsportal.commons.controllers.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codyy.erpsportal.commons.controllers.fragments.ClassroomVideoFragment;
import com.codyy.erpsportal.commons.models.entities.Classroom;
import com.codyy.erpsportal.commons.utils.Cog;

import java.util.ArrayList;
import java.util.List;

/**
 * 主辅课堂切换pagerAdapter
 * @author caixingming
 */
public class ClassroomPagerAdapter extends FragmentPagerAdapter {

    private List<Classroom> mClassroomList;
    private List<ClassroomVideoFragment> mFragments = new ArrayList<>();

    public ClassroomPagerAdapter(FragmentManager fm, List<Classroom> classroomList) {
        super(fm);
        this.mClassroomList = classroomList;
        initFrags(classroomList);
    }

    public void update(List<Classroom> videos){
        this.mClassroomList =   videos;
        this.notifyDataSetChanged();
        initFrags(videos);
    }

    private void initFrags(List<Classroom> videos) {
        mFragments.clear();
        for(int i=0;i<videos.size();i++){
            mFragments.add(null);
        }
    }

    public void playAll(){
        for(ClassroomVideoFragment f:mFragments){
            if(null != f)
            f.check3GWifi();
        }
    }

    @Override
    public int getCount() {
        return mClassroomList.size();
    }

    @Override
    public Fragment getItem(int position) {
        Cog.d("BNVideoPlayFragment", "position :" + position);
        ClassroomVideoFragment f = mFragments.get(position);
        if(f == null){
            f = ClassroomVideoFragment.newInstance(mClassroomList.get(position));
            mFragments.add(position,f);
        }
        return f;
    }
}