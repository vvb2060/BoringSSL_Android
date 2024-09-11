package io.github.vvb2060.ndk.boringssl.test;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.zip.ZipFile;

public class MainActivity extends Activity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String apkPath;
    private ScrollView scrollView;
    private TextView textView;
    private EditText editText;
    private Future<?> future;

    @SuppressWarnings("SameParameterValue")
    private int dp2px(float dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    private View buildView() {
        var rootView = new LinearLayout(this);
        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.setFitsSystemWindows(true);

        editText = new EditText(this);
        var editParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setHorizontallyScrolling(false);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (event == null) return false;
            if (event.getAction() == KeyEvent.ACTION_DOWN &&
                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                var text = v.getText().toString();
                if (text.isEmpty()) {
                    textView.setText("bssl crypto_test ssl_test");
                    return true;
                }
                textView.setText("");
                future = executor.submit(() -> {
                    var st = new StringTokenizer(text);
                    var cmd = new LinkedList<String>();
                    while (st.hasMoreTokens()) {
                        cmd.add(st.nextToken());
                    }
                    exec(cmd);
                });
                return true;
            }
            return false;
        });
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && future != null) {
                future.cancel(true);
            }
        });
        rootView.addView(editText, editParams);

        scrollView = new ScrollView(this);
        var scrollParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, 1);
        rootView.addView(scrollView, scrollParams);

        textView = new TextView(this);
        var textParams = new ScrollView.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        var dp8 = dp2px(8);
        textView.setPadding(dp8, dp8, dp8, dp8);
        textView.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        textView.setTextIsSelectable(true);
        textView.requestFocus();
        scrollView.addView(textView, textParams);

        return rootView;
    }

    private void append(String string) {
        scrollView.post(() -> {
            textView.append(string);
            textView.append("\n");
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        });
    }

    public static int startProcess(Context context, List<String> command, Consumer<String> out) throws Exception {
        var pb = new ProcessBuilder(command).redirectErrorStream(true);
        var test = new File(context.getCodeCacheDir(), "test");
        pb.environment().put("BORINGSSL_TEST_DATA_ROOT", test.getAbsolutePath());
        pb.environment().put("TMPDIR", context.getCacheDir().getAbsolutePath());
        var process = pb.start();
        var reader = new InputStreamReader(process.getInputStream());
        try (var br = new BufferedReader(reader)) {
            String line = br.readLine();
            while (line != null) {
                out.accept(line);
                if (Thread.interrupted()) {
                    process.destroy();
                    out.accept("[ kill ]");
                    break;
                }
                line = br.readLine();
            }
        }
        var value = process.waitFor();
        out.accept("[ exit " + value + " ]");
        return value;
    }

    private static Pair<String, Boolean> nameToPath(String name) {
        var abi = Build.SUPPORTED_ABIS[0];
        Boolean is64Bit = null;
        if (name.endsWith("32") && Build.SUPPORTED_32_BIT_ABIS.length != 0) {
            abi = Build.SUPPORTED_32_BIT_ABIS[0];
            name = name.substring(0, name.length() - 2);
            is64Bit = false;
        } else if (name.endsWith("64") && Build.SUPPORTED_64_BIT_ABIS.length != 0) {
            abi = Build.SUPPORTED_64_BIT_ABIS[0];
            name = name.substring(0, name.length() - 2);
            is64Bit = true;
        }
        var path = "lib/" + abi + "/lib" + name + ".so";
        return Pair.create(path, is64Bit);
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private void execLinker(List<String> command) {
        var pair = nameToPath(command.get(0));
        var path = apkPath + "!/" + pair.first;
        var is64Bit = pair.second;
        if (is64Bit == null) {
            is64Bit = Process.is64Bit();
        }
        try {
            append("[ exec " + path + " ]");
            command.set(0, path);
            command.add(0, is64Bit ? "linker64" : "linker");
            startProcess(this, command, this::append);
        } catch (Exception e) {
            append(Log.getStackTraceString(e));
        }
    }

    private boolean copyFile(File file) {
        if (file.canExecute()) return true;
        try (var apk = new ZipFile(apkPath)) {
            var so = apk.getEntry(nameToPath(file.getName()).first);
            assert so != null;
            try (var in = apk.getInputStream(so); var out = new FileOutputStream(file)) {
                var buffer = new byte[8192];
                for (var n = in.read(buffer); n >= 0; n = in.read(buffer))
                    out.write(buffer, 0, n);
            }
            return file.setExecutable(true);
        } catch (IOException e) {
            append(Log.getStackTraceString(e));
            return false;
        }
    }

    private void execFile(List<String> command) {
        var file = new File(getCodeCacheDir(), command.get(0));
        if (!copyFile(file)) return;
        try {
            append("[ exec " + file + " ]");
            command.set(0, file.toString());
            startProcess(this, command, this::append);
        } catch (Exception e) {
            append(Log.getStackTraceString(e));
        }
    }

    private void exec(List<String> command) {
        if (command.isEmpty()) return;
        if (command.get(0).startsWith("crypto_test")) {
            try {
                copyTestFiles(this, apkPath);
            } catch (IOException e) {
                append(Log.getStackTraceString(e));
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            execLinker(command);
        } else {
            execFile(command);
        }
    }

    public static void copyTestFiles(Context context, String apkPath) throws IOException {
        var dir = new File(context.getCodeCacheDir(), "test");
        if (dir.isDirectory()) return;
        try (var apk = new ZipFile(apkPath)) {
            for (var e = apk.entries(); e.hasMoreElements(); ) {
                var entry = e.nextElement();
                if (entry.isDirectory()) continue;
                if (!entry.getName().startsWith("assets/")) continue;
                var file = new File(dir, entry.getName().substring(7));
                file.getParentFile().mkdirs();
                try (var in = apk.getInputStream(entry);
                     var out = new FileOutputStream(file)) {
                    var buffer = new byte[8192];
                    for (var n = in.read(buffer); n >= 0; n = in.read(buffer))
                        out.write(buffer, 0, n);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildView());
        apkPath = getApplicationInfo().sourceDir;
        editText.setText("bssl");
        editText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}
