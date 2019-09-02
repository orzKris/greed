package com.kris.greed.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 上下文
 * <p>
 * 实现BeanFactoryAware接口的Bean实例将会拥有对容器的访问能力。BeanFactoryAware接口仅有如下一个方法：
 * SetBeanFactory(BeanFactory beanFactory)：该方法有一个参数beanFactory，该参数指向创建它的BeanFactory。
 * 该方法将由Spring调动，当Spring调用该方法时会将Spring容器作为参数传入该方法。
 * ApplicationContextRegister类实现了ApplicationContextAware接口，并实现了该接口提供的setApplicationContextAware()方法，这就使得该Bean实例可以直接访问到创建她的Spring容器。
 * 将该Bean部署在Spring容器中。
 *
 * @author Kris
 * @date 2019/1/24
 */
@Component
@Lazy(false)
public class ApplicationContextRegister implements ApplicationContextAware {

    private ApplicationContext applicationContextRegister;

    /**
     * 设置spring上下文
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        applicationContextRegister = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContextRegister;
    }
}
