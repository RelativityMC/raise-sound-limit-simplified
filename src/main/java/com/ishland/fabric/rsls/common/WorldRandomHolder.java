package com.ishland.fabric.rsls.common;

import com.google.common.collect.MapMaker;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.locks.StampedLock;

public class WorldRandomHolder {

    private static final StampedLock lock = new StampedLock();
    private static final Set<Object> randoms = Collections.newSetFromMap(new MapMaker().weakKeys().makeMap());

    public static void putWorldRandom(Object random) {
        randoms.add(random);

    }

    public static boolean isWorldRandom(Object random) {
        return randoms.contains(random);
    }

}
