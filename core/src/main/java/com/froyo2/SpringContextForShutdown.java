package com.froyo2;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * SpringContextForShutdown
 *
 * @author froyo2
 * @since 2019-02-11
 */
@Component
public class SpringContextForShutdown implements ApplicationContextAware {
    /**
     * 上下文对象实例
     */
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextForShutdown.applicationContext = applicationContext;
    }

    public static boolean isReady() {
        return applicationContext != null;
    }

    /**
     * 获取applicationContext
     * 
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 通过name获取 Bean.
     * 
     * @param name
     * @return
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * 通过class获取Bean.
     * 
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 通过class获取Beans.
     * 
     * @param <T>
     * @param clazz
     * @return
     */
    public static <T> Collection<T> getBeans(Class<T> clazz) {
        Map<String, T> beansMap = applicationContext.getBeansOfType(clazz);
        return beansMap.values();
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     * 
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }
}
