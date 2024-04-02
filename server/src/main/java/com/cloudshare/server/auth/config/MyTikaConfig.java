package com.cloudshare.server.auth.config;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author novo
 * @since 2024/3/31
 */
@Configuration
public class MyTikaConfig {

    @Autowired
    private ResourceLoader resourceLoader;

    @Bean
    public Tika tika() throws TikaException, IOException, SAXException {

        Resource resource = resourceLoader.getResource("classpath:tika-config.xml");
        InputStream inputStream = resource.getInputStream();

        TikaConfig config = new TikaConfig(inputStream);
        Detector detector = config.getDetector();
        Parser autoDetectParser = new AutoDetectParser(config);

        return new Tika(detector, autoDetectParser);
    }
}
