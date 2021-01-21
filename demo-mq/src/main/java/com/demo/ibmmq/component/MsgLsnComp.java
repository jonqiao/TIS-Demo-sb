package com.demo.ibmmq.component;

import com.ibm.mq.MQMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class MsgLsnComp {

  @Value("${spring.jms.template.default-destination}") // DEV.QUEUE.1
  String defaultDest;

  @Value("${demo.queue.local2}") // DEV.QUEUE.2
  private String localQ2;
  @Value("${demo.queue.local3}") // DEV.QUEUE.3
  private String localQ3;

  // @JmsListener(destination = "${spring.jms.template.default-destination}")  // "DEV.QUEUE.1"
  // public void onMessageReceivedDefault(String msg) {
  //   log.info("JmsListener on " + defaultDest  + " at " + new Date() + " String ==|======> " + msg);
  // }

  @JmsListener(containerFactory = "myJmsListenerContainerFactory", destination = "${spring.jms.template.default-destination}")
  public void onMessageReceivedDefault(String msg) {
    log.info("JmsListener on " + defaultDest  + " at " + new Date() + " String ==|======> " + msg);
  }

  @JmsListener(containerFactory = "myJmsListenerContainerFactory", destination = "${demo.queue.local2}")
  public void onMessageReceivedLocalQ2(String msg) {
    log.info("JmsListener on " + localQ2  + " at " + new Date() + " String ==|======> " + msg);
  }

  @JmsListener(containerFactory = "myJmsListenerContainerFactory", destination = "${demo.queue.local3}")
  public void onMessageReceivedLocalQ3(String msg) {
    log.info("JmsListener on " + localQ3  + " at " + new Date() + " String ==|======> " + msg);
  }

  // @JmsListener(containerFactory = "myJmsListenerContainerFactory", destination = "${spring.jms.template.default-destination}")
  // public void onMessageReceivedMQMsg(MQMessage msg) {
  //   // TODO: handle MQMessage
  //   log.info("JmsListener on " + defaultDest  + " at " + new Date() + " String ==|======> " + msg);
  // }

}
