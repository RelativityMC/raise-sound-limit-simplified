package com.ishland.fabric.rsls.common;

import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LoadingModList;

import java.lang.reflect.InvocationTargetException;

public class EarlyMultiVersionUtil {

    public static final LoadingModList loadingModList;

    static {
        loadingModList = getLoadingModList0();
    }

    private static LoadingModList getLoadingModList0() {
        try {
            return FMLLoader.getLoadingModList();
        } catch (NoSuchMethodError e1) {
            try {
                return (LoadingModList) FMLLoader.class.getMethod("getLoadingModList").invoke(null);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
