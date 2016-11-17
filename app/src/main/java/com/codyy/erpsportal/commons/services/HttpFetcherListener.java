package com.codyy.erpsportal.commons.services;


import com.codyy.erpsportal.commons.models.entities.download.BreakPointInfo;


public interface HttpFetcherListener {

     void onData(byte[] data, int length, BreakPointInfo info);

     void onSuccess(BreakPointInfo info);

     void onError(Throwable e, int statusCode, String retryAfter);
}
