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
        return prefs.getInt(context.getString(R.string.pref_bgr_color_key),
                Integer.parseInt(context.getString(R.string.pref_bgr_color_key)));
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
