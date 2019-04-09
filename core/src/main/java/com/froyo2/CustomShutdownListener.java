package com.froyo2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CustomShutdownListener
 *
 * @author froyo2
 * @since 2019-02-11
 */
public interface CustomShutdownListener extends Comparable {

    Logger LOGGER = LoggerFactory.getLogger(CustomShutdownListener.class);

    void onShutdown();

    default int getOrder() {
        return 0;
    }

    @Override
    default int compareTo(Object o) {
        if (!(o instanceof CustomShutdownListener)) {
            throw new RuntimeException(
                o.getClass().getName() + " is not type of CustomShutdownListener, cannot compare");
        }
        return Integer.compare(getOrder(), ((CustomShutdownListener)o).getOrder());
    }
}
