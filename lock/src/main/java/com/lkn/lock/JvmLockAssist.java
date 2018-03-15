package com.lkn.lock;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author LiKangning
 * @since 2018/3/2 上午11:23
 */
public abstract class JvmLockAssist {
    private static final Map<String, ReentrantReadWriteLock> keyAndLockMap = Maps.newHashMap();
    private static final Set<String> set = Sets.newConcurrentHashSet();

    public void addWriteLock(String key) {
//        set.con
    }

    protected abstract void doWrite();

}
