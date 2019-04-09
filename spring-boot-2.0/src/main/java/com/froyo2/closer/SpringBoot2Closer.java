package com.froyo2.closer;

import com.froyo2.SpringContextForShutdown;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.server.WebServer;

/**
 * SpringBoot1TomcatCloser
 *
 * @author froyo2
 * @since 2019-03-29
 */
public class SpringBoot2Closer implements Closer {

    public void doClose() {
        LOGGER.info("[GracefullyShutdown] Closing spring boot server ");
        try {
            WebServerApplicationContext applicationContext =
                (WebServerApplicationContext)SpringContextForShutdown.getApplicationContext();
            WebServer webServer = applicationContext.getWebServer();
            webServer.stop();
            LOGGER.info("[GracefullyShutdown] Spring boot server closed");
        } catch (Throwable e) {
            LOGGER.info("[GracefullyShutdown] Error occurred while closing spring boot, skip");
        }
    }

}
