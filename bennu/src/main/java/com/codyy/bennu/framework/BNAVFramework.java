package com.codyy.bennu.framework;

import android.content.Context;
import android.util.Log;

public class BNAVFramework {
	
    private static native void nativeInit(Context context) throws Exception;
    private static native void nativeUnInit();
    private static boolean isInited;

    public static void init(Context context) throws Exception {
        isInited = false;
    	nativeInit(context);
    }

    public static void release() {
    	nativeUnInit();
    }
    
    public static boolean isInited() {
    	return isInited;
    }
    
    public static void CFJ_Initialized () {
        Log.d("FAD","CFJ_Initialized!!!!!!!!!!!!!!!");
        isInited = true;
    }
    
    static {
        System.loadLibrary("bennu_avframework");
    }

}
