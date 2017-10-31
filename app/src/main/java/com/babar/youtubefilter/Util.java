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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
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

    static final Map<String, Integer> ADULT_KEYWORD_FILE_BY_NAME;
    static {
        final Map<String, Integer> valuesByName = new HashMap<>();
        valuesByName.put("PORN_0", R.raw.porn_0);
        valuesByName.put("PORN_1", R.raw.porn_1);
        valuesByName.put("PORN_2", R.raw.porn_2);
        valuesByName.put("PORN_3", R.raw.porn_start_list);
        ADULT_KEYWORD_FILE_BY_NAME = Collections.unmodifiableMap(valuesByName);
    }

    private static String whiteSpaceRegex = "[\\s]+";


    public static void prepareBadKeywords(Context appContext) {
        try {
            if (ADULT_KEYWORDMAP.size() != 0) {
                return;
            }
            for(int i =0;i<ADULT_KEYWORD_FILE_BY_NAME.size();i++) {

                in = appContext.getResources().openRawResource(ADULT_KEYWORD_FILE_BY_NAME.get("PORN_"+i));
                reader = new BufferedReader(new InputStreamReader(in));
                String line = null;

                while ((line = reader.readLine()) != null) {
                    if(line.isEmpty()){
                        continue;
                    }
                    line = line.trim();
                    if (line.startsWith("#")) {
                        continue;
                    }
                    //Using for redirect to specific video
                   /* String[] parts = line.split(" ");
                    if (parts.length == 2
                            && !"localhost".equalsIgnoreCase(parts[1])) {
                        ADULT_KEYWORDMAP.put(parts[1], parts[0]);
                    }*/
                   if(line.startsWith("@")){
                       ADULT_KEYWORDMAP.put(line.substring(1),"https://www.youtube.com/watch?v=L3ePF8idgm4");
                   }
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

    public static List<String> ngrams(String str,int n) {
        List<String> ngrams = new ArrayList<String>();
        String[] words = str.split(" ");
        for (int i = 0; i < words.length - n + 1; i++)
            ngrams.add(concat(words, i, i+n));
        return ngrams;
    }

    private static String concat(String[] words, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++)
            sb.append((i > start ? " " : "") + words[i]);
        return sb.toString();
    }

    /**
     *
     * @param str should has at least one string
     * @param maxGramSize should be 1 at least
     * @return set of continuous word n-grams up to maxGramSize from the sentence
     */
    public static List<String> generateNgramsUpto(String str, int maxGramSize) {

        List<String> sentence = Arrays.asList(str.split("[\\s]+"));

        List<String> ngrams = new ArrayList<String>();
        int ngramSize = 0;
        StringBuilder sb = null;

        //sentence becomes ngrams
        for (ListIterator<String> it = sentence.listIterator(); it.hasNext();) {
            String word = (String) it.next();

            //1- add the word itself
            sb = new StringBuilder(word);
            ngrams.add(word);
            ngramSize=1;
            it.previous();

            //2- insert prevs of the word and add those too
            while(it.hasPrevious() && ngramSize<maxGramSize){
                sb.insert(0,' ');
                sb.insert(0,it.previous());
                ngrams.add(sb.toString());
                ngramSize++;
            }

            //go back to initial position
            while(ngramSize>0){
                ngramSize--;
                it.next();
            }
        }
        return ngrams;
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
