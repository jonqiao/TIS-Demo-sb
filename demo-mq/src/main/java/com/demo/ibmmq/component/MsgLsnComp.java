package com.demo.ibmmq.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class MsgLsnComp {

  @Value("${spring.jms.template.default-destination}")
  String defaultDest;

  // @JmsListener(destination = "DEV.QUEUE.1")
  // @JmsListener(destination = "#{@destQueue.getDestination()}")
  @JmsListener(containerFactory = "myJmsListenerContainerFactory", destination = "${spring.jms.template.default-destination}")
  public void onMessageReceived(String msg) {
    log.info("JmsListener on " + defaultDest  + " at " + new Date() + " String ==|======> " + msg);
  }

  // @JmsListener(destination = "#{@destQueue.getDestination()}")
  // public void receiveMessage1(Message msg) {
  //   log.info("JmsListener on " + defaultDest  + " at " + new Date() + " Message ==|======> " + msg);
  // }

  @Bean
  public DestQueue destQueue() {
    return new DestQueue();
  }

  public class DestQueue {
    public String getDestination() {
      return defaultDest;
    }
  }

}
