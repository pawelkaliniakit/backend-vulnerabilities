package com.backendvulnerabilities.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

@Configuration
public class Config {

    @Bean
    Jaxb2RootElementHttpMessageConverter httpMessageConverter() {
        return new Jaxb2RootElementHttpMessageConverter() {
            @Override
            protected Source processSource(Source source) {
                if (source instanceof StreamSource) {
                    StreamSource streamSource = (StreamSource) source;
                    InputSource inputSource = new InputSource(streamSource.getInputStream());
                    try {
                        XMLReader ex = XMLReaderFactory.createXMLReader();
                        ex.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
                        return new SAXSource(ex, inputSource);
                    } catch (SAXException var6) {
                        this.logger.warn("Processing of external entities could not be disabled", var6);
                        return source;
                    }
                } else {
                    return source;
                }
            }
        };
    }
}
