package com.froyo2.closer;

import java.util.Collection;

import org.springframework.util.CollectionUtils;

import com.alibaba.dubbo.remoting.exchange.ExchangeServer;
import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol;

/**
 * DubboProviderCloser
 *
 * @author froyo2
 * @since 2019-05-29
 */
public class DubboProviderCloser implements Closer {

    /**
     * @ 等待正在处理的请求超时时间，默认8秒
     * @ 不是一定等待8秒，假设当前只有一个线程在跑，这个线程在server.close已经执行了5秒，那么只会等待3秒
     */
    private int awaitTimeout = 8000;

    public int getAwaitTimeout() {
        return awaitTimeout;
    }

    public void setAwaitTimeout(int awaitTimeout) {
        this.awaitTimeout = awaitTimeout;
    }

    @Override
    public void doClose() {
        LOGGER.info("[GracefullyShutdown] Closing dubbo provider");
        try {
            Collection<Exporter<?>> exporters = DubboProtocol.getDubboProtocol().getExporters();
            if (!CollectionUtils.isEmpty(exporters)) {
                for (Exporter<?> exporter : exporters) {
                    try {
                        exporter.unexport();
                    } catch (Throwable e) {
                        LOGGER.warn("[GracefullyShutdown] Error while unexport dubbo exporter,skip", e);
                    }
                }
            }
        } catch (Throwable e) {
            LOGGER.warn("[GracefullyShutdown] Error while unexport dubbo exporters,skip", e);
        }
        // server.close等待的过程中还能接收请求，所以要先把所有的exporter unexport掉
        try {
            Collection<ExchangeServer> servers = DubboProtocol.getDubboProtocol().getServers();
            if (!CollectionUtils.isEmpty(servers)) {
                for (ExchangeServer server : servers) {
                    try {
                        server.close(awaitTimeout);
                    } catch (Throwable e) {
                        LOGGER.warn("[GracefullyShutdown] Error while closing dubbo server,skip", e);
                    }
                }
            }
        } catch (Throwable e) {
            LOGGER.warn("[GracefullyShutdown] Error while closing dubbo servers,skip", e);
        }
        LOGGER.info("[GracefullyShutdown] Dubbo provider closed");
    }

}
