package com.example.nikolas.spotfiyusefirsttry;

import android.app.Activity;
import android.content.pm.ActivityInfo;

public class ActivityHelper {
    public static void initialize(Activity activity) {

        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
