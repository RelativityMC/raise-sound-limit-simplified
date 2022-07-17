package com.ishland.fabric.rsls;

import io.netty.util.internal.PlatformDependent;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.SharedLibrary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class RSLSInjectorLWJGL {

    private static final String libName;
    private static final ByteBuffer buf;

    static {
        if (PlatformDependent.isWindows()) {
            libName = "msvcrt.dll";
        } else if (PlatformDependent.isOsx()) {
            libName = "libSystem.dylib";
        } else {
            libName = "libc.so.6";
        }

        try {
            String name = "rsls-alsoft.conf";
            final InputStream resource = RSLSInjectorLWJGL.class.getClassLoader().getResourceAsStream(name);
            if (resource != null) {
                try {
                    final String[] split = name.split("\\.");
                    final File tempFile = File.createTempFile(split[0] + "-", "." + split[1]);
                    Files.copy(resource, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    String envDefinition = "ALSOFT_CONF=" + tempFile.getAbsolutePath();
                    buf = MemoryUtil.memASCII(envDefinition);
                    final long address = MemoryUtil.memAddress(buf);

                    SharedLibrary libc = APIUtil.apiCreateLibrary(libName);
                    final long putenv = APIUtil.apiGetFunctionAddress(libc, "putenv");
                    final int result = JNI.invokePI(address, putenv);
                    if (result != 0) throw new RuntimeException("Error %d when setting env".formatted(result));
                } finally {
                    resource.close();
                }
            } else {
                throw new FileNotFoundException(name);
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static void init() {
    }

    public static String getEnv() {
        SharedLibrary libc = APIUtil.apiCreateLibrary(libName);
        final long putenv = APIUtil.apiGetFunctionAddress(libc, "getenv");
        ByteBuffer buf = MemoryUtil.memASCII("ALSOFT_CONF");
        final long resultPointer = JNI.invokePP(MemoryUtil.memAddress(buf), putenv);
        String result = MemoryUtil.memASCIISafe(resultPointer);
        MemoryUtil.memFree(buf);
        return result;
    }

}
