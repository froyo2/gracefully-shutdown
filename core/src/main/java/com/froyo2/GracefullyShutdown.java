package com.froyo2;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * GracefullyShutdown
 *
 * @author froyo2
 * @since 2019-01-11
 */
@Component
public class GracefullyShutdown {

    private static Logger LOGGER = LoggerFactory.getLogger(GracefullyShutdown.class);

    private GracefullyShutdown() {}

    static {
        LOGGER.info("[GracefullyShutdown] Injecting gracefullyShutdown");
        // add ChiefShutdownHook
        final ShutdownHooksHolder shutdownHooksHolder = ShutdownHooksHolder.getInstance();
        shutdownHooksHolder.put(ChiefShutdownHook.getInstance(), ChiefShutdownHook.getInstance());
        // hold jvm hooks
        String className = "java.lang.ApplicationShutdownHooks";
        try {
            Class<?> clazz = Class.forName(className);
            Field field = clazz.getDeclaredField("hooks");
            field.setAccessible(true);
            synchronized (clazz) {
                IdentityHashMap<Thread, Thread> map = (IdentityHashMap<Thread, Thread>)field.get(clazz);
                for (Thread thread : map.keySet()) {
                    LOGGER.info("[GracefullyShutdown] Hold shutdownHook registered in jvm: " + thread.getName());
                    shutdownHooksHolder.put(thread, thread);
                }
                field.set(clazz, shutdownHooksHolder);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
