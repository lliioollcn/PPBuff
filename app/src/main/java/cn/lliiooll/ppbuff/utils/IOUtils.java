package cn.lliiooll.ppbuff.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.lliiooll.ppbuff.tracker.PLog;

/**
 * I/O工具类
 */
public class IOUtils {
    /**
     * 从流写入到文件
     *
     * @param is   输入流
     * @param file 目标文件
     */
    public static void write(InputStream is, File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            copy(is, fos);
        } catch (Throwable e) {
            PLog.Companion.c(e);
        }
    }

    public static void write(String str, File file) {
        ByteArrayInputStream bis = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        write(bis, file);
    }

    public static void copy(InputStream is, OutputStream os) {
        try {
            int read = 0;
            byte[] buf = new byte[2048];
            while ((read = is.read(buf)) != -1) {
                os.write(buf, 0, read);
            }
            is.close();
            os.close();
        } catch (Throwable e) {
            PLog.Companion.c(e);
        }
    }


    public static String read(File file) {
        StringBuilder sb = new StringBuilder();
        try {
            if (!file.exists()) {
                file.createNewFile();
                return "";
            }
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            copy(fis, bos);
            sb.append(bos.toString("UTF-8"));
        } catch (Throwable e) {
            PLog.Companion.c(e);
        }
        return sb.toString();
    }


    public static Map<String, Object> toMap(Object postBean) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (postBean != null) {
                for (Field f : postBean.getClass().getDeclaredFields()) {
                    f.setAccessible(true);
                    Object ins = f.get(postBean);
                    if (ins == null) {
                        map.put(f.getName(), null);
                    } else {
                        if (ins.getClass().isPrimitive() || ins.getClass().getName().startsWith("java.lang")) {
                            map.put(f.getName(), ins);
                        } else {
                            PLog.Companion.d(f.getName() + "是其他类: " + ins.getClass().getName());

                            if (ins.getClass().getName().startsWith("cn.xiaochuankeji")) {
                                map.put(f.getName(), toMap(ins));
                            } else if (ins.getClass().getName().startsWith("java.util")) {
                                map.put(f.getName(), ins);
                            } else {
                                map.put(f.getName(), ins.getClass().getName());
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {
            PLog.Companion.c(e);
        }
        return map;
    }

    public static void append(String m, File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, true);
            fw.append(m);
            fw.close();
        } catch (Throwable e) {
            PLog.Companion.c(e);
        }
    }

    public static void copy(File file, File sFile) {
        try {
            PLog.Companion.d("复制文件: " + file.getAbsolutePath());
            PLog.Companion.d("到文件: " + sFile.getAbsolutePath());
            FileInputStream fis = new FileInputStream(file);
            BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(sFile));
            copy(fis, fos);
        } catch (Throwable e) {
            PLog.Companion.c(e);
        }
    }

    public static String read(File file, int line) {
        StringBuilder sb = new StringBuilder();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            for (int i = 0; i < line; i++) {
                String ct = br.readLine();
                if (ct != null) {
                    sb.append(ct).append("\n");
                }
            }
            br.close();
            fr.close();
        } catch (Throwable e) {
            PLog.Companion.c(e);
        }
        return sb.toString();
    }

    public static void copy(Context ctx, Uri uri, File file) {
        try {
            ContentResolver resolver = ctx.getContentResolver();
            InputStream is = resolver.openInputStream(uri);

            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            copy(is, fos);
        } catch (Throwable e) {
            PLog.Companion.c(e);
        }
    }

    public static void copy(Context ctx, File file, Uri uri) {
        try {
            ContentResolver resolver = ctx.getContentResolver();
            OutputStream fos = resolver.openOutputStream(uri);

            if (!file.exists()) {
                file.createNewFile();
            }
            FileInputStream fis = new FileInputStream(file);
            copy(fis, fos);
        } catch (Throwable e) {
            PLog.Companion.c(e);
        }

    }

    public static void copy(@Nullable InputStream byteStream, @NotNull File tempFile) {
        try {
            copy(byteStream, new FileOutputStream(tempFile));
        } catch (Throwable e) {
            PLog.Companion.c(e);
        }
    }
}
