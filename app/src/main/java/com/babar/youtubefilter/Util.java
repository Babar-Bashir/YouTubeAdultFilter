package com.babar.youtubefilter;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import custom.com.babar.youtubefilter.R;

/***********************************
 * Created by Babar on 10/27/2017.  *
 ***********************************/

public class Util {
    public static Map<String, String> ADULT_KEYWORDMAP = new HashMap<>();
    private static BufferedReader reader;
    private static InputStream in;
    private static final String TAG = "YouTubeFilterService";

    private static String whiteSpaceRegex = "[\\s]+";


    public static void prepareBadKeywords(Context appContext) {
        try {
            if (ADULT_KEYWORDMAP.size() != 0) {
                return;
            }
            in = appContext.getResources().openRawResource(R.raw.badwords);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(" ");
                if (parts.length == 2
                        && !"localhost".equalsIgnoreCase(parts[1])) {
                    ADULT_KEYWORDMAP.put(parts[1], parts[0]);
                }
            }
        } catch (Exception e) {
            Log.d(TAG,"[Util] inside prepareBadKeywords() Exception : " + e.toString());
        } finally {
            try {
                reader.close();
                in.close();
            } catch (IOException ex) {
            }
        }
    }


    public static String[] splitParts(String title){
        try {
            if(title == null || title.length()<1){
                return null;
            }
            String[] list = title.split(whiteSpaceRegex);
            return list;
        } catch (Exception e) {
            Log.d(TAG,"[Util] inside splitParts() Exception is : "+e.toString());
            return null;
        }
    }


    @SuppressLint("NewApi")
    public static boolean isAccessibilityServiceEnabled(Context appContext) {
        boolean isEnabled = true;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                AccessibilityManager accessibilityManager =
                        (AccessibilityManager) appContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
                if (accessibilityManager.isEnabled()) {
                    List<AccessibilityServiceInfo> list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
                    if (list != null) {
                        if (list.size() != 0) {
                            Log.d(TAG,"AccessibilityManager AccessibilityServiceInfo package name to found " + appContext.getPackageName());
                            for (AccessibilityServiceInfo service : list) {
                                if (service.getId().contains(appContext.getPackageName())) {

                                    Log.d(TAG,"AccessibilityManager AccessibilityServiceInfo service found " + service.getId());
                                    isEnabled = true;
                                    break;
                                } else {
                                    Log.d(TAG,"[Util]AccessibilityManager AccessibilityServiceInfo not required service  " + service.getId());
                                    isEnabled = false;
                                }
                            }
                        } else {
                            Log.d(TAG,"AccessibilityManager nothing in list ");
                            isEnabled = false;
                        }
                    } else {
                        isEnabled = false;
                    }
                } else {
                    Log.d(TAG,"Util :AccessibilityManager  is not enabled");
                    isEnabled = false;
                }
            } else {
                Log.d(TAG,"Util :AccessibilityManager Build version is less than ICE_CREAM_SANDWICH");
            }
            Log.d(TAG,"Util :AccessibilityManager  returning status" + isEnabled);
            return isEnabled;
        } catch (Exception ex) {
            Log.d(TAG,"[Util]:Exception occuered in  isAccessibilityServiceEnabled returning true");
            return isEnabled = true;
        }
    }
}
