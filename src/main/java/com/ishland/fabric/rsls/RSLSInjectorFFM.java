package com.ishland.fabric.rsls;

import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class RSLSInjectorFFM {

    private static final Logger LOGGER = LoggerFactory.getLogger(RSLSInjectorFFM.class);

    private static final MemorySegment buf;

    static {
        try {
            String name = "rsls-alsoft.conf";
            final InputStream resource = RSLSInjectorFFM.class.getClassLoader().getResourceAsStream(name);
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
                    LOGGER.info("Attempting to invoke putenv(\"{}\")", envDefinition);
                    buf = Arena.global().allocateFrom(envDefinition);

                    final MemorySegment putenv = Linker.nativeLinker().defaultLookup().findOrThrow(PlatformDependent.isWindows() ? "_putenv" : "putenv");
                    MethodHandle putenvHandle = Linker.nativeLinker().downcallHandle(
                            putenv,
                            FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS)
                    );
                    final int result = (int) putenvHandle.invokeExact(buf);
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
