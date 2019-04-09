package com.froyo2;

/**
 * ShutdownThread
 *
 * @author froyo2
 * @since 2019-02-11
 */
public class ShutdownThread extends Thread implements Comparable {

    public ShutdownThread() {}

    public ShutdownThread(int order) {
        this.order = order;
    }

    public ShutdownThread(int order, String name) {
        this.order = order;
    }

    public ShutdownThread(Runnable runnable) {
        super(runnable);
    }

    public ShutdownThread(Runnable runnable, int order) {
        super(runnable);
        this.order = order;
    }

    public ShutdownThread(Runnable runnable, int order, String name) {
        super(runnable);
        this.order = order;
    }

    private int order;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof ShutdownThread)) {
            throw new RuntimeException(o.getClass().getName() + " is not type of ShutdownThread, cannot compare");
        }
        final int value = Integer.compare(this.order, ((ShutdownThread)o).order);
        return value == 0 ? -1 : value;
    }

}
