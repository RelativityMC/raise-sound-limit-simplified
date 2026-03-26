package com.ishland.fabric.rsls.common;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

import java.util.concurrent.locks.StampedLock;

public class WorldRandomHolder {

    private static final StampedLock lock = new StampedLock();
    private static final ReferenceOpenHashSet<Object> randoms = new ReferenceOpenHashSet<>(16, 0.5F);

    public static void putWorldRandom(Object random) {
        long stamp = lock.writeLock();
        try {
            randoms.add(random);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public static boolean isWorldRandom(Object random) {
        long stamp = lock.readLock();
        try {
            return randoms.contains(random);
        } finally {
            lock.unlockRead(stamp);
        }
    }

}
