package cn.lliiooll.ppbuff.view;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.lliiooll.ppbuff.PConfig;
import cn.lliiooll.ppbuff.R;
import cn.lliiooll.ppbuff.aio.AudioBuilder;
import cn.lliiooll.ppbuff.ffmpeg.FFmpeg;
import cn.lliiooll.ppbuff.ffmpeg.FFmpegCallBack;
import cn.lliiooll.ppbuff.tracker.PLog;
import cn.lliiooll.ppbuff.utils.AppUtils;
import cn.lliiooll.ppbuff.utils.IOUtils;
import cn.lliiooll.ppbuff.utils.PDownload;
import cn.lliiooll.ppbuff.utils.Utils;
import de.robv.android.xposed.XposedHelpers;

public class PDialogVoice extends Dialog {

    private final Activity activity;
    private Uri uri;

    public PDialogVoice(@NonNull Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pp_dialog_voice);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        //设置宽
        switch (WindowManager.LayoutParams.MATCH_PARENT) {
            case WindowManager.LayoutParams.MATCH_PARENT:
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                break;
            case WindowManager.LayoutParams.WRAP_CONTENT:
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                break;
            default:
                layoutParams.width = (int) AppUtils.dp2px(getContext(), WindowManager.LayoutParams.MATCH_PARENT);
                break;
        }


        //设置显示位置
        layoutParams.gravity = Gravity.CENTER;
        //设置是否屏蔽返回键与点击空白区域不关闭Dialog
        setCancelable(true);
        //设置属性
        getWindow().setAttributes(layoutParams);
        ImageView close = findViewById(R.id.pp_voice_close);
        ImageView music = findViewById(R.id.pp_voice_music);
        EditText search = findViewById(R.id.pp_dialog_search);
        close.setOnClickListener(v -> dismiss());


        LinearLayout list = findViewById(R.id.pp_voice_list);

        //http://music.163.com/song/media/outer/url?id=400162138.mp3
        music.setOnClickListener(v -> {
            if (StrUtil.isBlank(search.getText().toString().trim())) {
                Utils.toastShort("请在搜索框输入你要搜索的歌曲名称后继续");
            } else {
                Utils.toastShort("搜索中，请稍后...");
                Utils.asyncStatic(() -> {
                    addSongs(list, "https://music.lliiooll.cn", search.getText().toString().trim());
                });

            }
        });
        addFiles(list, uri, "");
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                list.removeAllViews();
                addFiles(list, uri, s.toString().trim());
            }
        });

    }

    private void addSongs(LinearLayout list, String rootUrl, String keyWord) {
        String jstr = HttpUtil.get(rootUrl + "/cloudsearch?keywords=" + keyWord);
        if (StrUtil.isNotBlank(jstr)) {
            JSONObject json = JSONUtil.parseObj(jstr);
            if (json.getInt("code") == 200) {
                JSONArray songs = json.getJSONObject("result").getJSONArray("songs");
                songs.forEach(songJson -> {
                    if (songJson instanceof JSONObject) {
                        JSONObject song = (JSONObject) songJson;
                        StringBuilder name = new StringBuilder(song.getStr("name"));
                        JSONArray ars = song.getJSONArray("ar");
                        ars.forEach(arJson -> {
                            if (arJson instanceof JSONObject) {
                                JSONObject ar = (JSONObject) arJson;
                                name.append("-").append(ar.getStr("name"));
                            }
                        });
                        name.append("-").append(song.getJSONObject("al").getStr("name"));
                        Utils.syncStatic(() -> {
                            TextView text = new TextView(getContext());
                            text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            text.setText(name.toString());
                            text.setTextColor(Color.BLACK);
                            text.setOnClickListener(v -> {
                                Utils.toastShort("下载中，请稍后...");
                                Utils.asyncStatic(() -> {
                                    File temp = PDownload.INSTANCE.downloadTemp("http://music.163.com/song/media/outer/url?id=" + song.getInt("id") + ".mp3");
                                    try {
                                        if (FileUtil.readLine(new RandomAccessFile(temp, "r"), StandardCharsets.UTF_8).contains("html")) {
                                            Utils.syncStatic(() -> {
                                                Utils.toastShort("下载失败: 资源不存在");
                                            });
                                        } else {
                                            sendVoice(DocumentFile.fromFile(temp));
                                        }
                                    } catch (FileNotFoundException e) {
                                        PLog.c(e);
                                    }

                                });

                            });
                            text.setPadding(0, 10, 0, 10);
                            list.addView(text);
                        });
                    }
                });
            }
        }
    }

    private void addFiles(LinearLayout list, Uri uri, String keyWord) {
        Utils.asyncStatic(() -> {
            DocumentFile dir = DocumentFile.fromTreeUri(getContext(), uri);
            if (dir == null) return;
            PLog.d("开始搜索文件夹: " + dir.getName());
            DocumentFile[] files = dir.listFiles();
            PLog.d("文件个数: " + files.length + " @" + dir.getUri());
            for (DocumentFile file : files) {
                PLog.d("文件路径@" + file.canRead() + ": " + file.getUri());
                if (!file.getName().contains(".") && !file.isDirectory()) {
                    PLog.d("无效文件: " + file.getName());
                    continue;
                }
                if (StrUtil.isNotEmpty(keyWord) && !file.getName().contains(keyWord)) {
                    if (file.isDirectory()) {
                        addFiles(list, file.getUri(), keyWord);
                    } else {
                        PLog.d("不是要搜索的文件: " + file.getName());
                    }
                    continue;
                }
                Utils.syncStatic(() -> {
                    TextView text = new TextView(getContext());
                    text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    text.setText(file.getName());
                    text.setTextColor(Color.BLACK);
                    text.setOnClickListener(v -> {
                        if (file.isDirectory()) {
                            list.removeAllViews();
                            addBack(list, file.getParentFile(), keyWord);
                            addFiles(list, file.getUri(), "");
                        } else {
                            sendVoice(file);

                        }
                    });
                    text.setPadding(0, 10, 0, 10);
                    list.addView(text);
                });
                if (file.isDirectory() && StrUtil.isNotBlank(keyWord)) {
                    addFiles(list, file.getUri(), keyWord);
                }
            }
        });
    }

    private void sendVoice(DocumentFile file) {
        dismiss();
        File tempDir = activity.getExternalFilesDir("helperVoiceTemp");
        File covertDir = activity.getExternalFilesDir("helperVoiceCovert");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        if (!covertDir.exists()) {
            covertDir.mkdirs();
        }
        String fn = System.currentTimeMillis() + "." + file.getName().split("\\.")[1];
        File tempFile = new File(tempDir, fn);
        File covertile = new File(covertDir, fn + ".mp3");
        PLog.d("复制到文件: " + tempFile.getAbsolutePath());
        IOUtils.copy(activity, file.getUri(), tempFile);
        PLog.d("复制完毕: " + tempFile.length());
        FFmpegCallBack callBack = () -> {
            Class<?> clazz = activity.getClass();
            for (Field f : clazz.getDeclaredFields()) {
                if (f.getType().getName().contains("AudioBean")) {
                    if (PConfig.bool("voiceAutoCovert", true)) {
                        XposedHelpers.setObjectField(activity, f.getName(), AudioBuilder.build(covertile.getAbsolutePath(), null, PConfig.number("voiceTime", 5201314)));
                    } else {
                        XposedHelpers.setObjectField(activity, f.getName(), AudioBuilder.build(tempFile.getAbsolutePath(), null, PConfig.number("voiceTime", 5201314)));
                    }
                    PLog.d("语音设置成功，准备发送");
                    XposedHelpers.callMethod(activity, "M1");
                    PLog.d("语音发送成功: M1");
                    Utils.toastShort("发送成功");
                    PLog.d("开始清除缓存...");
                    if (tempDir.listFiles() != null) {
                        for (File f1 : tempDir.listFiles()) {
                            PLog.d("删除缓存文件: " + f1.getName() + "@" + f1.delete());
                        }
                    }
                    if (covertDir.listFiles() != null) {
                        for (File f1 : covertDir.listFiles()) {
                            if (!f1.getName().equalsIgnoreCase(covertile.getName())) {
                                PLog.d("删除转换文件: " + f1.getName() + "@" + f1.delete());
                            }
                        }
                    }

                                /*
                                for (Method m : clazz.getDeclaredMethods()) {
                                    if (m.getReturnType() == void.class && !Modifier.isStatic(m.getModifiers()) && m.getParameterTypes().length == 0) {
                                        XposedHelpers.callMethod(activity, m.getName());
                                        PLog.d("语音发送成功: " + m.getName());
                                        dismiss();
                                    }
                                }

                                 */
                    break;
                }
            }
        };
        if (PConfig.bool("voiceAutoCovert", true)) {
            PLog.d("开始转换格式...");
            Utils.toastShort("语音转换中,请查看通知以获取转换进度...");
            FFmpeg.runCmd("ffmpeg.exe -i " + tempFile.getAbsolutePath() + " -map_metadata -1 " + covertile.getAbsolutePath(), callBack);
        } else {
            PLog.d("未启用自动转换，直接发送");
            Utils.syncStatic(callBack::finish);

        }
    }

    private void addBack(LinearLayout list, DocumentFile parentFile, String keyWord) {
        if (parentFile == null) return;
        TextView backTree = new TextView(getContext());
        backTree.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        backTree.setText("<- 返回上一级");
        backTree.setTextColor(Color.BLACK);
        backTree.setOnClickListener((bv -> {
            list.removeAllViews();
            addBack(list, parentFile.getParentFile(), keyWord);
            addFiles(list, parentFile.getUri(), "");
        }));
        backTree.setPadding(0, 10, 0, 10);
        list.addView(backTree);
    }

    public PDialogVoice uri(Uri uri) {
        this.uri = uri;
        return this;
    }
}
