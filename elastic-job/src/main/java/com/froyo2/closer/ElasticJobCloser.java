package com.froyo2.closer;

import java.util.Collection;

import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.froyo2.SpringContextForShutdown;

/**
 * ElasticJobCloser
 *
 * @author froyo2
 * @since 2019-03-29
 */
public class ElasticJobCloser implements Closer {

    @Override
    public void doClose() {
        LOGGER.info("[GracefullyShutdown] Closing elastic job");
        Collection<SpringJobScheduler> springJobSchedulers =
            SpringContextForShutdown.getBeans(SpringJobScheduler.class);
        springJobSchedulers.forEach(springJobScheduler -> springJobScheduler.getSchedulerFacade().shutdownInstance());
        LOGGER.info("[GracefullyShutdown] Elastic job closed");
    }

}
