package com.valtra.valtraapp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.view.Display;

/**
 * Created by Bilawal on 05-Nov-16.
 */

public class Utils {

    private Context mContext;

    public Utils() {
    }

    public Utils(Context mContext) {
        this.mContext = mContext;
    }


    public Typeface headingTypeFace() {
        return Typeface.createFromAsset(mContext.getAssets(), "fonts/font_poet.ttf");
    }

    public Typeface bodyTypeFace() {
        return Typeface.createFromAsset(mContext.getAssets(), "fonts/font_body.ttf");
    }

    public int[] screenDimension(Activity activity) {

        int[] dimensions = new int[2];

        Display display = activity.getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        dimensions[0] = size.x;
        dimensions[1] = size.y;

        return dimensions;
    }

}
