package io.github.vvb2060.ndk.boringssl.test;

import static org.junit.Assert.assertEquals;

import android.os.Build;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@RunWith(AndroidJUnit4.class)
public class BoringSSLTest {
    private final static String TAG = BoringSSLTest.class.getSimpleName();
    private final static String ssl = "/libssl_test.so";
    private final static String crypto = "/libcrypto_test.so";
    private final static String separator = "!/lib/";

    private final String apkPath;

    public BoringSSLTest() {
        var instrumentation = InstrumentationRegistry.getInstrumentation();
        apkPath = instrumentation.getTargetContext().getApplicationInfo().sourceDir;
    }

    private static void startProcess(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
        InputStreamReader reader = new InputStreamReader(process.getInputStream());
        try (BufferedReader br = new BufferedReader(reader)) {
            String line = br.readLine();
            while (line != null) {
                Log.v(TAG, line);
                line = br.readLine();
            }
        }
        assertEquals(process.waitFor(), 0);
    }

    @Test
    public void sslTest32Bit() throws Exception {
        if (Build.SUPPORTED_32_BIT_ABIS.length == 0) return;
        var path = apkPath + separator + Build.SUPPORTED_32_BIT_ABIS[0] + ssl;
        startProcess("linker " + path);
    }

    @Test
    public void sslTest64Bit() throws Exception {
        if (Build.SUPPORTED_64_BIT_ABIS.length == 0) return;
        var path = apkPath + separator + Build.SUPPORTED_64_BIT_ABIS[0] + ssl;
        startProcess("linker64 " + path);
    }

    @Test
    public void cryptoTest32Bit() throws Exception {
        if (Build.SUPPORTED_32_BIT_ABIS.length == 0) return;
        var path = apkPath + separator + Build.SUPPORTED_32_BIT_ABIS[0] + crypto;
        startProcess("linker " + path);
    }

    @Test
    public void cryptoTest64Bit() throws Exception {
        if (Build.SUPPORTED_64_BIT_ABIS.length == 0) return;
        var path = apkPath + separator + Build.SUPPORTED_64_BIT_ABIS[0] + crypto;
        startProcess("linker64 " + path);
    }
}
