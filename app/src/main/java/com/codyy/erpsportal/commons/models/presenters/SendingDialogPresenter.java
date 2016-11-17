package com.codyy.erpsportal.commons.models.presenters;


import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.dialogs.LoadingDialog;

/**
 * Created by gujiajia on 2016/2/3.
 */
public class SendingDialogPresenter {

    private LoadingDialog mLoadingDialog;

    private IFragmentManagerProvider mFragmentManagerProvider;

    public SendingDialogPresenter(IFragmentManagerProvider fragmentManagerProvider) {
        mFragmentManagerProvider = fragmentManagerProvider;
    }

    public void show() {
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialog.newInstance(R.string.sending);
        }
        mLoadingDialog.show(mFragmentManagerProvider.obtainFragmentManager(), "sending");
    }

    public void dismiss() {
        mLoadingDialog.dismiss();
    }
}
