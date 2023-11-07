package com.cloudshare.pay.core;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class PayStrategyFactory implements InitializingBean {

    private final Map<String, PayStrategy> payStrategyMap = new HashMap<>();

    private final ApplicationContext applicationContext;

    public PayStrategyFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public PayStrategy chooseStrategy(String payType) {
        return payStrategyMap.get(payType);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 从 IOC 容器中获取 PayStrategy 类型的 Bean 对象
        Map<String, PayStrategy> beans = applicationContext.getBeansOfType(PayStrategy.class);
        beans.forEach((beanName, strategy) -> payStrategyMap.put(strategy.mark(), strategy));
    }
}
