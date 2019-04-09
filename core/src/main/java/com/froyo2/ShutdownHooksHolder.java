package com.froyo2;

import java.util.IdentityHashMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * ShutdownHooksHolder
 *
 * @author froyo2
 * @since 2019-01-24
 */
public class ShutdownHooksHolder extends IdentityHashMap<Thread, Thread> {

    private static final ShutdownHooksHolder instance = new ShutdownHooksHolder();

    private static final IdentityHashMap<Thread, Thread> jvmHooks = new IdentityHashMap();

    private ShutdownHooksHolder() {}

    public static ShutdownHooksHolder getInstance() {
        return instance;
    }

    public static IdentityHashMap<Thread, Thread> getJvmHooks() {
        return jvmHooks;
    }

    @Override
    public Thread put(Thread key, Thread value) {
        if (key instanceof ShutdownThread) {
            return super.put(key, value);
        }
        return jvmHooks.put(key, value);
    }

    @Override
    public Thread remove(Object key) {
        if (!(key instanceof Thread)) {
            return null;
        }
        if (key instanceof ShutdownThread) {
            return super.remove(key);
        }
        return jvmHooks.remove(key);
    }

    @Override
    public Set<Thread> keySet() {
        Set<Thread> threads = super.keySet();
        Set<Thread> set = new TreeSet<>();
        set.addAll(threads);
        return set;
    }

}
