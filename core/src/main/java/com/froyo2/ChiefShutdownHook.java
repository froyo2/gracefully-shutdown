package com.froyo2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.froyo2.closer.CloserChain;

/**
 * ChiefShutdownHook
 *
 * @author froyo2
 * @since 2019-01-24
 */
public class ChiefShutdownHook extends ShutdownThread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChiefShutdownHook.class);

    /**
     * Has it already been destroyed or not?
     */
    private final AtomicBoolean destroyed;

    private ChiefShutdownHook(int order) {
        this.destroyed = new AtomicBoolean(false);
        super.setOrder(order);
    }

    private static final ChiefShutdownHook instance = new ChiefShutdownHook(100);

    public static ChiefShutdownHook getInstance() {
        return instance;
    }

    @Override
    public void run() {
        LOGGER.info("[GracefullyShutdown] Run ChiefShutdownHook now.");
        if (!destroyed.compareAndSet(false, true)) {
            return;
        }
        // 1.关闭组件，停止接收新请求，尽可能完成处理中的请求，这个操作可以与2并行，所以起一个单独的线程来做
        Thread closerThread = null;
        try {
            closerThread = new Thread(() -> CloserChain.getInstance().doClose());
            closerThread.start();
        } catch (Throwable e) {
            LOGGER.warn("[GracefullyShutdown] Error occurred while run closing components, skip", e);
        }
        // 2.回调业务方自定义函数
        try {
            runCustomShutdown();
        } catch (Throwable e) {
            LOGGER.warn("[GracefullyShutdown] Error occurred while run custom shutdown listeners, skip", e);
        }
        // 3.执行拦截到的jvm的shutdown hooks
        try {
            //保证第一步执行完
            if (closerThread != null) {
                closerThread.join();
            }
            runJvmHooks();
        } catch (Throwable e) {
            LOGGER.warn("[GracefullyShutdown] Error occurred while run jvm shutdown hooks, skip", e);
        }
        LOGGER.info("[GracefullyShutdown] Exit Gracefully! Bye~~");
    }

    private void runCustomShutdown() {
        LOGGER.info("[GracefullyShutdown] Run custom shutdown listeners");
        try {
            final Collection<CustomShutdownListener> beans =
                SpringContextForShutdown.getBeans(CustomShutdownListener.class);
            final ArrayList<CustomShutdownListener> arrayList = new ArrayList<>();
            arrayList.addAll(beans);
            Collections.sort(arrayList);
            arrayList.forEach(shutdown -> {
                try {
                    shutdown.onShutdown();
                } catch (Throwable e) {
                    LOGGER.error("[GracefullyShutdown] Error occurred while run custom shutdown listener["
                        + shutdown.getClass().getName() + "], skip", e);
                }
            });
            LOGGER.info("[GracefullyShutdown] Custom shutdown listeners all completed");
        } catch (Throwable e) {
            LOGGER.error("[GracefullyShutdown] Error occurred while run custom shutdown listeners, skip", e);
        }
    }

    private void runJvmHooks() {
        LOGGER.info("[GracefullyShutdown] Run jvm shutdown hooks");
        final Set<Thread> jvmHooks = ShutdownHooksHolder.getJvmHooks().keySet();
        jvmHooks.forEach(jvmHook -> {
            LOGGER.info("[GracefullyShutdown] Run shutdownHook registered in jvm: " + jvmHook.getName());
            jvmHook.start();
        });
        jvmHooks.forEach(jvmHook -> {
            while (true) {
                try {
                    jvmHook.join();
                    break;
                } catch (InterruptedException ignored) {
                    // ignore this exception
                }
            }
        });
        LOGGER.info("[GracefullyShutdown] Jvm shutdown hooks all completed.");
    }

}
