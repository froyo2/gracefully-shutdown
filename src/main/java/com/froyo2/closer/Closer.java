package com.froyo2.closer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Closer
 *
 * @author froyo2
 * @since 2019-03-29
 */
public interface Closer {

    Logger LOGGER = LoggerFactory.getLogger(Closer.class);

    void doClose();

}
