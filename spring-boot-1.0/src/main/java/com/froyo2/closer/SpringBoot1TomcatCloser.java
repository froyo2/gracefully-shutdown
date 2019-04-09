package com.froyo2.closer;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;

import com.froyo2.SpringContextForShutdown;

/**
 * SpringBoot1TomcatCloser
 *
 * @author froyo2
 * @since 2019-03-29
 */
public class SpringBoot1TomcatCloser implements Closer {

    public void doClose() {
        LOGGER.info("[GracefullyShutdown] Closing embedded tomcat server");
        EmbeddedWebApplicationContext embeddedWebApplicationContext =
            (EmbeddedWebApplicationContext)SpringContextForShutdown.getApplicationContext();
        EmbeddedServletContainer embeddedServletContainer = embeddedWebApplicationContext.getEmbeddedServletContainer();
        if (embeddedServletContainer instanceof TomcatEmbeddedServletContainer) {
            TomcatEmbeddedServletContainer tomcatEmbeddedServletContainer =
                (org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer)embeddedServletContainer;
            Connector[] connectors = tomcatEmbeddedServletContainer.getTomcat().getService().findConnectors();
            for (Connector connector : connectors) {
                connector.pause();
            }
            for (Connector connector : connectors) {
                Executor executor = connector.getProtocolHandler().getExecutor();
                if (executor instanceof ThreadPoolExecutor) {
                    try {
                        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)executor;
                        threadPoolExecutor.shutdown();
                        if (!threadPoolExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                            LOGGER.warn(
                                "[GracefullyShutdown] Tomcat thread pool did not shutdown gracefully within 10 seconds. Proceeding with forceful shutdown");
                        }
                    } catch (InterruptedException e) {
                        LOGGER.warn("[GracefullyShutdown] Error occurred while close tomcat server");
                    }
                }
            }
        }
        LOGGER.info("[GracefullyShutdown] Embedded tomcat server closed");
    }

}
