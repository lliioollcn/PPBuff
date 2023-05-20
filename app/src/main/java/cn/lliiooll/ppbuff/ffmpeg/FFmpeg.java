package cn.lliiooll.ppbuff.ffmpeg;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import java.io.File;

import cn.lliiooll.ppbuff.PPBuff;
import cn.lliiooll.ppbuff.R;
import cn.lliiooll.ppbuff.tracker.PLog;
import cn.lliiooll.ppbuff.utils.Utils;

public class FFmpeg {

    private static FFmpegCallBack callBack;


    public static void runCmd(String command, FFmpegCallBack callBack) {
        Utils.asyncStatic(() -> {
            FFmpeg.callBack = callBack;
            PLog.d("处理ffmpeg指令: " + command);
            String[] cmd = command.split(" ");
            init();
            runCmd(cmd.length, cmd, callBack);
        });

    }

    public static native void runCmd(int argc, String[] argv, FFmpegCallBack callBack);

    public static void init() {
        PLog.d("尝试初始化FFmpeg Native...");
        initNative();
    }

    public static void nativeMsg(String msg) {
        PLog.d("[FFmpeg] " + msg);
    }

    public static void finish() {
        PLog.d("转换完毕");
        if (callBack != null) {
            Utils.syncStatic(() -> callBack.finish());
        }
        updateNotify(0, 0);
        if (notificationManager != null) {
            notificationManager.cancel(114514);
            notificationManager = null;
        }
        notification = null;

    }

    public static void progress(int position, int duration, int state) {
        PLog.d("进度: " + position + " 总时长:" + duration);
        updateNotify(position, duration);
    }

    private static Notification.Builder notification;
    private static NotificationManager notificationManager;

    private static void updateNotify(int position, int duration) {
        if (notificationManager == null) {
            notificationManager = PPBuff.getApplication().getSystemService(NotificationManager.class);
            NotificationChannel notificationChannel = new NotificationChannel("voice_covert", "语音转换", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(false); //关闭闪光灯
            notificationChannel.enableVibration(false); //关闭震动
            notificationChannel.setSound(null, null); //设置静音
            notificationManager.createNotificationChannel(notificationChannel);
        }
        if (notification == null) {
            notification = new Notification.Builder(PPBuff.getApplication(), "voice_covert")
                    .setContentTitle("语音转换中...")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_round_check)
                    .setProgress(duration, position, true);
        }
        if (position > duration) {
            position = duration;
        }
        notification.setProgress(duration, position, false);
        notificationManager.notify(114514, notification.build());
    }

    public static native void initNative();
}
