package com.codyy.erpsportal.commons.widgets;


public interface DisplayListener {

    void onLoadSuccess(AwareView awareView, IName name);

    void onLoadFailure(AwareView awareView, IName name);
}
