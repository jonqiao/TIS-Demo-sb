package com.demo.ibmmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ErrorHandler;


@Slf4j
@Configuration
public class myJmsConfig {

  @Autowired
  CachingConnectionFactory cachingConnectionFactory;

  @Bean
  public DefaultJmsListenerContainerFactory myJmsListenerContainerFactory(ExampleErrorHandler errorHandler) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(cachingConnectionFactory);
    factory.setErrorHandler(errorHandler);
    factory.setRecoveryInterval(60000L);
    factory.setExceptionListener(e -> {
      log.error("onException: " + e.getMessage() + " | " + e.getCause().toString());
    });
    return factory;
  }


  @Service
  public class ExampleErrorHandler implements ErrorHandler {
    @Override
    public void handleError(Throwable t) {
      log.error("handleError: " + t.getMessage());
    }
  }

}
