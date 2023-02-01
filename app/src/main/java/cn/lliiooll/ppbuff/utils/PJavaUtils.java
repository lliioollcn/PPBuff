package cn.lliiooll.ppbuff.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.lliiooll.ppbuff.tracker.PLog;

public class PJavaUtils {

    public static String commentTime(long j2) {
        j2 = j2 * 1000;
        long currentTimeMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        long timeInMillis = calendar.getTimeInMillis();
        if (j2 < timeInMillis || currentTimeMillis - j2 > 60000) {
            if (j2 >= timeInMillis) {
                long j3 = currentTimeMillis - j2;
                if (j3 < 3600000) {
                    long j4 = j3 / 60000;
                    long j5 = j4 != 0 ? j4 : 1L;
                    return j5 + "分钟前";
                }
            }
            if (j2 >= timeInMillis) {
                long j6 = (currentTimeMillis - j2) / 3600000;
                return j6 + "小时前";

            }
            calendar.setTimeInMillis(j2);
            if (j2 > timeInMillis - 2505600000L) {

                return (((timeInMillis - j2) / 86400000) + 1) + "天前";
            }
            calendar.setTimeInMillis(currentTimeMillis);
            int i2 = calendar.get(1);
            calendar.setTimeInMillis(j2);
            int i3 = calendar.get(1);
            if (i3 < i2) {
                return i3 + "/" + (calendar.get(2) + 1) + "/" + calendar.get(5);
            }
            return (calendar.get(2) + 1) + "/" + calendar.get(5);
        }
        return "刚刚";
    }

    public static String commentDetailTime(String format, long time) {
        return new SimpleDateFormat(format).format(new Date(time * 1000));
    }

    public static boolean isConnected(String s) {
        try {
            URL u = new URL(s);
            AtomicBoolean ok = new AtomicBoolean(true);
            AtomicBoolean success = new AtomicBoolean(false);
            new Thread(() -> {
                try {
                    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                    conn.setReadTimeout(2000);
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(2000);
                    PLog.Companion.d("链接: " + s + " 返回值: " + conn.getResponseCode());
                    success.set(conn.getResponseCode() == HttpURLConnection.HTTP_OK);
                    ok.set(false);
                } catch (IOException e) {
                    PLog.Companion.c(e);
                    success.set(false);
                    ok.set(false);
                }
            }).start();
            while (ok.get()) {
                Thread.sleep(10L);
            }
            return success.get();
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

}
