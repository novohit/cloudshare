package com.cloudshare.server.common.parser;

import com.cloudshare.server.file.enums.FileType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author novo
 * @since 2023/11/13
 */
@Component
public class TextParserStrategyFactory implements InitializingBean {

    private final Map<FileType, TextParser> strategyMap = new HashMap<>();

    private final ApplicationContext applicationContext;

    public TextParserStrategyFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public TextParser chooseStrategy(FileType type) {
        TextParser textParser = strategyMap.get(type);
        if (textParser == null) {
            return new NotTextParser();
        }
        return textParser;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 从 IOC 容器中获取 PayStrategy 类型的 Bean 对象
        Map<String, TextParser> beans = applicationContext.getBeansOfType(TextParser.class);
        beans.forEach((beanName, strategy) -> strategyMap.put(strategy.mark(), strategy));
    }
}
