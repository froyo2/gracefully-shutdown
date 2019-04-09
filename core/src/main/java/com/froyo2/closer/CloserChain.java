package com.froyo2.closer;

import java.util.LinkedList;

/**
 * CloserChain
 *
 * @author froyo2
 * @since 2019-03-29
 */
public class CloserChain implements Closer {

    private CloserChain(){}

    private static CloserChain closerChain  = new CloserChain();

    public static CloserChain getInstance() {
        return closerChain;
    }

    private LinkedList<Closer> chain = new LinkedList();

    public synchronized CloserChain addCloser(Closer closer) {
        chain.add(closer);
        return this;
    }

    @Override
    public void doClose() {
        LOGGER.info("[GracefullyShutdown] Closing components, no longer accept new request");
        Closer closer;
        while ((closer = chain.poll()) != null) {
            try {
                closer.doClose();
            } catch (Throwable e) {
                LOGGER.info("[GracefullyShutdown] Error occurred while invoke " + closer.getClass() + ", skip");
            }
        }
        LOGGER.info("[GracefullyShutdown] components Closed");
    }

}
