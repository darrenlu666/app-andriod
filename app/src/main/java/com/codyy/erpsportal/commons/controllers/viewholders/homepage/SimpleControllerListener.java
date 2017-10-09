package com.codyy.erpsportal.commons.controllers.viewholders.homepage;

import android.graphics.drawable.Animatable;

import com.facebook.drawee.controller.ControllerListener;

/**
 * Created by poe on 17-9-6.
 */

public abstract class SimpleControllerListener implements ControllerListener {

    @Override
    public void onSubmit(String id, Object callerContext) {

    }

    @Override
    public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {

    }

    @Override
    public void onIntermediateImageSet(String id, Object imageInfo) {

    }

    @Override
    public void onIntermediateImageFailed(String id, Throwable throwable) {

    }


    @Override
    public void onRelease(String id) {

    }
}
