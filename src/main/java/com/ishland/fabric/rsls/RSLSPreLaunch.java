package com.ishland.fabric.rsls;

import com.ishland.fabric.rsls.common.RSLSConfig;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class RSLSPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        RSLSConfig.init();
        RSLSInjectorFFM.init();
    }
}
