package com.ishland.fabric.rsls.common;

import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.LoadingModList;

import java.lang.reflect.InvocationTargetException;

public class EarlyMultiVersionUtil {

    public static final LoadingModList loadingModList;

    static {
        loadingModList = getLoadingModList0();
    }

    private static LoadingModList getLoadingModList0() {
        try {
            return FMLLoader.getCurrent().getLoadingModList();
        } catch (NoSuchMethodError e1) {
            try {
                return (LoadingModList) FMLLoader.class.getMethod("getLoadingModList").invoke(null);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
