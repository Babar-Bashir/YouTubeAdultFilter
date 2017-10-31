package com.babar.youtubefilter;

import android.accessibilityservice.AccessibilityService;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/***********************************
 * Created by Babar on 10/27/2017.  *
 ***********************************/

public class YouTubeFilterService extends AccessibilityService {

    private String YOUTUBE_FULL_SCREEN_BTN_ID = "com.google.android.youtube:id/fullscreen_button";
    private String YOUTUBE_VIDEO_TITLE_ID = "com.google.android.youtube:id/title";
    private static final String TAG = "YouTubeFilterService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            Log.d(TAG, "Event Name : " + AccessibilityEvent.eventTypeToString(event.getEventType()));
            if (getRootInActiveWindow().findAccessibilityNodeInfosByViewId(YOUTUBE_FULL_SCREEN_BTN_ID) != null
                    && getRootInActiveWindow().findAccessibilityNodeInfosByViewId(YOUTUBE_FULL_SCREEN_BTN_ID).size() > 0) {
                List<AccessibilityNodeInfo> listOfResIDs = getRootInActiveWindow().findAccessibilityNodeInfosByViewId(YOUTUBE_VIDEO_TITLE_ID);
                Log.d(TAG, "[MyAccessibilityService] inside onAccessibilityEvent() list size : " + listOfResIDs.size());
                if (listOfResIDs != null && listOfResIDs.size() > 0
                        && listOfResIDs.get(0) != null && listOfResIDs.get(0).getText() != null
                        && listOfResIDs.get(0).getText().length() > 0) {
                    listOfResIDs.get(0).getText().toString().split("\\[\\s]+");
                    String[] splittedTitle = Util.splitParts(listOfResIDs.get(0).getText().toString().toLowerCase());
                    List<String> ngramTitle = Util.generateNgramsUpto(listOfResIDs.get(0).getText().toString().toLowerCase(), splittedTitle.length);

                    if (ngramTitle != null && ngramTitle.size() > 0) {
                        for (int i = 0; i < ngramTitle.size(); i++) {
                            Util.prepareBadKeywords(this);
                            if (Util.ADULT_KEYWORDMAP.containsKey(ngramTitle.get(i))) {
                                String video_path = Util.ADULT_KEYWORDMAP.get(ngramTitle.get(i));
                                Uri uri = Uri.parse(video_path);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                intent.addFlags(PendingIntent.FLAG_CANCEL_CURRENT);
                                startActivity(intent);
                            }
                        }

                    }
                }
            }

        } catch (Exception e) {
            Log.d(TAG, "[MyAccessibilityService] inside onAccessibilityEvent() Exception is :" + e.toString());
        }

    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "OnInterrupt");
    }

    @Override
    public void onServiceConnected() {
        try {
            Log.d(TAG, "on Service Connected");
        } catch (Exception e) {
            Log.d(TAG, "[MyAccessibilityService] inside onServiceConnected() Exception is :" + e.toString());
        }
    }


}
