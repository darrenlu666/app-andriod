package com.codyy.erpsportal.resource.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;

/**
 * Created by gujiajia on 2016/6/15.
 */
public class DrawableUtils {

    public static Drawable obtainAttrDrawable(Context themedContext, @AttrRes int attrResId) {
        // Create an array of the attributes we want to resolve
        // using values from a theme
        int[] attrs = new int[] { attrResId /* index 0 */};

        // Obtain the styled attributes. 'themedContext' is a context with a
        // theme, typically the current Activity (i.e. 'this')
        TypedArray ta = themedContext.obtainStyledAttributes(attrs);

        // To get the value of the 'listItemBackground' attribute that was
        // set in the theme used in 'themedContext'. The parameter is the index
        // of the attribute in the 'attrs' array. The returned Drawable
        // is what you are after
        Drawable drawableFromTheme = ta.getDrawable(0 /* index */);

        // Finally, free the resources used by TypedArray
        ta.recycle();
        return drawableFromTheme;
    }

}
