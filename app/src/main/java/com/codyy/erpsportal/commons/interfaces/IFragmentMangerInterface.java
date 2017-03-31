package com.codyy.erpsportal.commons.interfaces;

import android.support.v4.app.FragmentManager;

/**
 * 我们知道DialogFragment在show的时候需要一个FragmentManager
 * 然而因为FragmentManager会在内存不足后屏幕旋转的时候重建,
 * 所以你需要动态获取一个FragmentManager .
 * Created by poe on 28/03/17.
 */

public interface IFragmentMangerInterface {
    /**
     * 动态获取一个FragmentManager
     * @return
     */
    FragmentManager getNewFragmentManager();
}
