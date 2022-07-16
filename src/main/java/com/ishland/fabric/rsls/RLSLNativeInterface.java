package com.ishland.fabric.rsls;

import io.netty.util.internal.PlatformDependent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class RLSLNativeInterface {

    private RLSLNativeInterface() {
    }

    static  {
        try {
            final String name;
            if (PlatformDependent.isWindows()) {
                name = "rsls-natives.dll";
            } else if (PlatformDependent.isOsx()) {
                name = "rsls-natives.dylib";
            } else {
                name = "librsls-natives.so";
            }
            final InputStream resource = RLSLNativeInterface.class.getClassLoader().getResourceAsStream(name);
            if (resource != null) {
                try {
                    final String[] split = name.split("\\.");
                    final File tempFile = File.createTempFile(split[0] + "-", "." + split[1]);
                    Files.copy(resource, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.load(tempFile.getAbsolutePath());
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

    private native void injectEnvironmentVariable(String path);

    public static void inject() {
        try {
            String name = "rlsl-alsoft.conf";
            final InputStream resource = RLSLNativeInterface.class.getClassLoader().getResourceAsStream(name);
            if (resource != null) {
                try {
                    final String[] split = name.split("\\.");
                    final File tempFile = File.createTempFile(split[0] + "-", "." + split[1]);
                    Files.copy(resource, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("RLSL: Setting up alsoft config %s".formatted(tempFile.getAbsolutePath()));
                    new RLSLNativeInterface().injectEnvironmentVariable(tempFile.getAbsolutePath());
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

}
