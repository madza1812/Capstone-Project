package com.example.android.danga.noteyouplus;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.example.android.danga.noteyouplus.data.NoteService;

/**
 * Created by An on 6/12/2016.
 */
public class Util {

    public static String getPreferredSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_order_key),
                context.getString(R.string.pref_sort_order_default));
    }

    public static int getPreferredDefaultColor(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(prefs.getString(context.getString(R.string.pref_bgr_color_key),
                context.getString(R.string.pref_bgr_color_default)));
    }

    public static String getPreferenceTitleColor(String value){
        String retTitle;
        switch (value) {
            case "900": {
                retTitle = "White";
                break;
            }
            case "901": {
                retTitle = "Red";
                break;
            }
            case "902": {
                retTitle = "Yellow";
                break;
            }
            case "903": {
                retTitle = "Green";
                break;
            }
            case "904": {
                retTitle = "Blue";
                break;
            }
            case "905": {
                retTitle = "Orange";
                break;
            }
            case "906": {
                retTitle = "Purple";
                break;
            }
            case "907": {
                retTitle = "Light Blue";
                break;
            }
            case "908": {
                retTitle = "Light Yellow";
                break;
            }
            default: {
                retTitle = value;
            }
        }
        return retTitle;
    }

    public static int getBgrColor(int bgrColorCode) {
        String retColor;
        switch (bgrColorCode) {
            case NoteService.BLUE_BGR_COLOR: {
                retColor = "#80D8FF";
                break;
            }
            case NoteService.DEFAULT_BGR_COLOR: {
                retColor = "#FFFFFF";
                break;
            }
            case NoteService.GREEN_BGR_COLOR: {
                retColor = "#CCFF90";
                break;
            }
            case NoteService.LIGHT_BLUE_BGR_COLOR:{
                retColor = "#84FFFF";
                break;
            }
            case NoteService.LIGHT_YELLOW_BGR_COLOR:{
                retColor = "#FFFF8D";
                break;
            }
            case NoteService.ORANGE_BGR_COLOR: {
                retColor = "#FFD180";
                break;
            }
            case NoteService.PURPLE_BGR_COLOR:{
                retColor = "#EA80FC";
                break;
            }
            case NoteService.RED_BGR_COLOR: {
                retColor = "#FF8A80";
                break;
            }
            case NoteService.YELLOW_BGR_COLOR: {
                retColor = "#F4FF81";
                break;
            }
            default:
                retColor = "#FFFFFF";
                break;
        }

        return Color.parseColor(retColor);
    }

    public static int getDrawable(int color) {
        int retDrawable = R.drawable.white_bgr;
        switch (color) {
            case NoteService.BLUE_BGR_COLOR: {
                retDrawable = R.drawable.blue_bgr;
                break;
            }
            case NoteService.DEFAULT_BGR_COLOR: {
                retDrawable = R.drawable.white_bgr;
                break;
            }
            case NoteService.GREEN_BGR_COLOR: {
                retDrawable = R.drawable.green_bgr;
                break;
            }
            case NoteService.LIGHT_BLUE_BGR_COLOR: {
                retDrawable = R.drawable.light_blue_bgr;
                break;
            }
            case NoteService.LIGHT_YELLOW_BGR_COLOR: {
                retDrawable = R.drawable.light_yellow_bgr;
                break;
            }
            case NoteService.ORANGE_BGR_COLOR: {
                retDrawable = R.drawable.orange_bgr;
                break;
            }
            case NoteService.PURPLE_BGR_COLOR: {
                retDrawable = R.drawable.purple_bgr;
                break;
            }
            case NoteService.RED_BGR_COLOR: {
                retDrawable = R.drawable.red_bgr;
                break;
            }
            case NoteService.YELLOW_BGR_COLOR: {
                retDrawable = R.drawable.yellow_bgr;
                break;
            }
            default:
                retDrawable = R.drawable.white_bgr;
                break;
        }
        return retDrawable;
    }
}
