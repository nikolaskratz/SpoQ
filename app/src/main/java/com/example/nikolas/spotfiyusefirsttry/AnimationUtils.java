package com.example.nikolas.spotfiyusefirsttry;

import android.content.Context;
import android.util.TypedValue;

public class AnimationUtils {

    private static final AnimationUtils ourInstance = new AnimationUtils();

    public static AnimationUtils getInstance() {
        return ourInstance;
    }

    private AnimationUtils() {
    }

    public static int dpToPx(float dpValue, Context context){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }
}
