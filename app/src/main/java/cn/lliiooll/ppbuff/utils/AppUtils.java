package cn.lliiooll.ppbuff.utils;

import android.content.Context;

/**
 * Application工具类
 */
public class AppUtils {


    public static float dp2px(Context context, int dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }
}
