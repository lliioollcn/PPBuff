package cn.lliiooll.ppbuff.utils;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.system.Os;
import android.system.StructUtsname;

import com.tencent.mmkv.MMKV;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import cn.lliiooll.ppbuff.PConfig;
import cn.lliiooll.ppbuff.PPBuff;
import cn.lliiooll.ppbuff.startup.BuffEntrance;
import cn.lliiooll.ppbuff.tracker.PLog;

/**
 * native类
 */
public class Natives {

    /**
     * 初始化native
     */
    public static void init() {
        Application app = PPBuff.getApplication();// 获取宿主application实例
        PLog.d(">>>>>>>>>> 模块类加载器 <<<<<<<<<<");
        /*
        BaseDexClassLoader cLoader = (BaseDexClassLoader) Natives.class.getClassLoader();
        PLog.d("尝试获取lib目录...");
        Object pathList = XposedHelpers.getObjectField(cLoader, "pathList");
        List<File> nativeLibraryDirectories = new ArrayList<>((List<File>) XposedHelpers.getObjectField(pathList, "nativeLibraryDirectories"));
        String path = HookEntry.getPackageName() + "!/lib/arm64-v8a";
        PLog.d("尝试注入lib目录...");
        nativeLibraryDirectories.add(new File(path));
        XposedHelpers.setObjectField(pathList, "nativeLibraryDirectories", nativeLibraryDirectories);
        XposedHelpers.setObjectField(cLoader, "pathList", pathList);
        PLog.d("尝试加载native....");

         */
        //System.loadLibrary("pphelper");
        load(app);
    }

    public static void load(Context ctx) throws LinkageError {
        try {
            test();
            return;
        } catch (UnsatisfiedLinkError ignored) {
        }
        String abi = getAbiForLibrary();
        try {
            Class.forName(Objects.requireNonNull(PPBuffClassLoader.getXposedBridgeClassName()));
            // in host process
            try {
                String modulePath = BuffEntrance.getModulePath();
                String hostPath = getHostPath(ctx);
                PLog.d("宿主app路径: " + hostPath);
                if (modulePath != null) {
                    File mmkvDir = ctx.getExternalFilesDir("buffMMKV");
                    if (mmkvDir.isFile()) {
                        mmkvDir.delete();
                    }
                    if (!mmkvDir.exists()) {
                        mmkvDir.mkdirs();
                    }
                    MMKV.initialize(ctx, mmkvDir.getAbsolutePath(), s -> {
                        PLog.d("尝试加载: " + modulePath + "!/lib/" + abi + "/libppbuff.so");
                        System.load(modulePath + "!/lib/" + abi + "/libppbuff.so");
                        PLog.d("尝试加载: " + modulePath + "!/lib/" + abi + "/libffmpeg.so");
                        System.load(modulePath + "!/lib/" + abi + "/libffmpeg.so");
                    });
                    PConfig.init();
                    PLog.d("dlopen by mmap success");
                }
            } catch (UnsatisfiedLinkError e1) {
                // give enough information to help debug
                // Is this CPU_ABI bad?
                PLog.d("Build.SDK_INT=" + Build.VERSION.SDK_INT);
                PLog.d("Build.CPU_ABI is: " + Build.CPU_ABI);
                PLog.d("Build.CPU_ABI2 is: " + Build.CPU_ABI2);
                PLog.d("Build.SUPPORTED_ABIS is: " + Arrays.toString(Build.SUPPORTED_ABIS));
                PLog.d("Build.SUPPORTED_32_BIT_ABIS is: " + Arrays.toString(Build.SUPPORTED_32_BIT_ABIS));
                PLog.d("Build.SUPPORTED_64_BIT_ABIS is: " + Arrays.toString(Build.SUPPORTED_64_BIT_ABIS));
                // check whether this is a 64-bit ART runtime
                PLog.d("Process.is64bit is: " + Process.is64Bit());
                StructUtsname uts = Os.uname();
                PLog.d("uts.machine is: " + uts.machine);
                PLog.d("uts.version is: " + uts.version);
                PLog.d("uts.sysname is: " + uts.sysname);
                PLog.c(e1);
                PLog.d("加载失败，尝试方案2...");
                load2(ctx);
                // panic, this is a bug
            }
        } catch (ClassNotFoundException e) {
            // not in host process, ignore
            System.loadLibrary("ppbuff");
            System.loadLibrary("ffmpeg");
        }
        try {
            test();
        } catch (UnsatisfiedLinkError e) {
            PLog.c(e);
            PLog.d("加载失败，尝试方案2...");
            load2(ctx);
            return;
        }

    }

    public static native void test();

    private static String getHostPath(Context ctx) {
        return ctx.getClassLoader().getResource("AndroidManifest.xml").getPath().replace("!/AndroidManifest.xml", "").replaceFirst("file:", "");
    }

    private static List<String> libList = new ArrayList<String>() {{
        add("libppbuff");
        add("libffmpeg");
    }};

    public static void load2(Context ctx) {
        String abi = getAbiForLibrary();
        File dir = new File(ctx.getExternalFilesDir("buff_lib"), abi);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        libList.forEach(lib -> {
            File fileLib = new File(dir, lib + ".so");
            if (fileLib.exists()) {
                fileLib.delete();
            }

            try {
                fileLib.createNewFile();
                InputStream is = Natives.class.getClassLoader().getResourceAsStream("/lib/" + abi + "/" + lib + ".so");
                if (is != null) {
                    IOUtils.write(is, fileLib);
                } else {
                    PLog.d("是null: " + "/lib/" + abi + "/" + lib + ".so");
                }

            } catch (Throwable e) {
                PLog.c(e);
            }
        });
        try {
            File mmkvDir = ctx.getExternalFilesDir("buffMMKV");
            if (mmkvDir.isFile()) {
                mmkvDir.delete();
            }
            if (!mmkvDir.exists()) {
                mmkvDir.mkdirs();
            }

            File ppNativeFile = new File(dir, ".\\" + abi + "\\" + libList.get(0) + ".so");
            MMKV.initialize(ctx, mmkvDir.getAbsolutePath(), s -> {
                PLog.d("尝试加载libppbuff.so ......");
                System.load(ppNativeFile.getAbsolutePath());
                PLog.d("加载成功");
            });
            PConfig.init();
        } catch (UnsatisfiedLinkError e) {
            PLog.d(">>>>>>>>>> 又nm得失败了！！！ <<<<<<<<<");
            PLog.c(e);
        }
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
}
