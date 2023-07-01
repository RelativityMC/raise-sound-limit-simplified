package com.ishland.fabric.rsls;

import io.netty.util.internal.PlatformDependent;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.SharedLibrary;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
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
                    final Path current = Path.of(".");
                    final Path cacheDir = Files.createDirectories(current.resolve("cache"));
                    final Path tempFile = Files.createTempFile(cacheDir, split[0] + "-", "." + split[1]);
                    Files.copy(resource, tempFile, StandardCopyOption.REPLACE_EXISTING);
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        try {
                            Files.deleteIfExists(tempFile);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }));
                    String envDefinition = "ALSOFT_CONF=" + current.relativize(tempFile);
                    System.out.println(String.format("Attempting to invoke putenv(\"%s\")", envDefinition));
                    buf = MemoryUtil.memASCII(envDefinition);
                    final long address = MemoryUtil.memAddress(buf);

                    SharedLibrary libc = APIUtil.apiCreateLibrary(libName);
                    final long putenv = APIUtil.apiGetFunctionAddress(libc, PlatformDependent.isWindows() ? "_putenv" : "putenv");
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

}
