package cn.lliiooll.ppbuff;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;

import androidx.compose.ui.graphics.Color;

import cn.lliiooll.ppbuff.app.PPBuffApp;
import cn.lliiooll.ppbuff.tracker.PLog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PPBuff {
    private static String modulePath;
    private static Map<String, String> hostAppClassNames = new HashMap<String, String>() {{
        put(HostInfo.ZuiyouLite.PACKAGE_NAME, "cn.xiaochuankeji.zuiyouLite.app.AppController");// 皮皮搞笑
        put(HostInfo.TieBa.PACKAGE_NAME, "cn.xiaochuankeji.tieba.AppController");// 最右
    }};
    private static Map<String, String> hostAppSettingClassNames = new HashMap<String, String>() {{
        put(HostInfo.ZuiyouLite.PACKAGE_NAME, "cn.xiaochuankeji.zuiyouLite.ui.setting.SettingActivity");// 皮皮搞笑
        put(HostInfo.TieBa.PACKAGE_NAME, "cn.xiaochuankeji.tieba.ui.home.setting.SettingActivity");// 最右
    }};
    private static Application app;


    public static boolean isDebug() {

        return BuildConfig.DEBUG;
    }


    public static void initModulePath(String modulePath) {
        PPBuff.modulePath = modulePath;
    }

    public static String getModulePath() {
        return modulePath;
    }


    public static String getHostApplicationClassName(String packageName) {
        return hostAppClassNames.getOrDefault(packageName, "");
    }

    public static boolean isSupportApp(String packageName) {
        return hostAppClassNames.containsKey(packageName);
    }


    public static String getModulePackName() {
        return "cn.lliiooll.ppbuff";
    }


    public static String getHostApplicationSettingClassName(@NotNull String packageName) {
        return hostAppSettingClassNames.getOrDefault(packageName, "");
    }

    public static void init(@NotNull Application app) {
        PPBuff.app = app;
    }

    public static Application getApplication() {
        return app;
    }


    public static int getHostVersionCode() {
        Application app = getApplication();
        try {
            if (app != null) {
                PackageManager pm = app.getPackageManager();
                PackageInfo info = pm.getPackageInfo(app.getPackageName(), 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    return (int) info.getLongVersionCode();
                } else {
                    return info.versionCode;
                }
            }
        } catch (Throwable e) {
            PLog.Companion.c(e);
        }
        return 0;
    }

    public static String getHostVersionName() {
        Application app = getApplication();
        try {
            if (app != null) {
                PackageManager pm = app.getPackageManager();
                PackageInfo info = pm.getPackageInfo(app.getPackageName(), 0);
                return info.versionName;
            }
        } catch (Throwable e) {
            PLog.Companion.c(e);
        }
        return "null";
    }

    public static String getHostPath() {
        return getApplication().getClassLoader().getResource("AndroidManifest.xml").getPath().replace("!/AndroidManifest.xml", "").replaceFirst("file:", "");
    }

    public static String doReplace(String clazz) {
        String cl = clazz;
        if (cl.startsWith("L")) {
            cl = cl.replaceFirst("L", "");
        }
        if (cl.endsWith(";")) {
            cl = cl.substring(0, cl.length() - 1);
        }
        if (cl.contains("\\")) {
            cl = cl.replace("\\", ".");
        }
        if (cl.contains("/")) {
            cl = cl.replace("/", ".");
        }
        return cl;
    }

    public static String getAbiForLibrary() {
        String[] supported = Process.is64Bit() ? Build.SUPPORTED_64_BIT_ABIS : Build.SUPPORTED_32_BIT_ABIS;
        if (supported == null || supported.length == 0) {
            throw new IllegalStateException("No supported ABI in this device");
        }
        List<String> abis = Arrays.asList("armeabi-v7a", "arm64-v8a");
        for (String abi : supported) {
            if (abis.contains(abi)) {
                return abi;
            }
        }
        throw new IllegalStateException("No supported ABI in " + Arrays.toString(supported));
    }

    public static int getStatusBarHeight(Context ctx) {
        int result = 0;
        //获取状态栏高度的资源id
        if (ctx == null) {
            return result;
        }
        int resourceId = ctx.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = ctx.getResources().getDimensionPixelSize(resourceId);
        }


        return result;
    }

    public static boolean isInHostApp() {
        return app != null && app.getClass() != PPBuffApp.class;
    }

    public static class HostInfo {
        public static class ZuiyouLite {
            public static final String PACKAGE_NAME = "cn.xiaochuankeji.zuiyouLite";
            private static final int PP_2_45_10 = 2451000;
            private static final int PP_2_46_0 = 2460000;
            private static final int PP_2_46_1 = 2460100;
            private static final int PP_2_47_1 = 2470100;
            private static final int PP_2_47_10 = 2471000;
            private static final int PP_2_48_10 = 2481000;
            private static final int PP_2_49_10 = 2491000;
            private static final int PP_2_50_10 = 2501000;
        }

        public static class TieBa {
            public static final String PACKAGE_NAME = "cn.xiaochuankeji.tieba";
            public static final int ZY_5_11_20 = 511200;
        }

    }
}
