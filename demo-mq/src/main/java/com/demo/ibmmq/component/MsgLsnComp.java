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

  @Value("${spring.jms.template.default-destination}")
  String defaultDest;

  // @JmsListener(destination = "${spring.jms.template.default-destination}")  // "DEV.QUEUE.1"
  // public void onMessageReceivedDefault(String msg) {
  //   log.info("JmsListener on " + defaultDest  + " at " + new Date() + " String ==|======> " + msg);
  // }

  @JmsListener(containerFactory = "myJmsListenerContainerFactory", destination = "${spring.jms.template.default-destination}")
  public void onMessageReceivedCust(String msg) {
    log.info("JmsListener on " + defaultDest  + " at " + new Date() + " String ==|======> " + msg);
  }

  // @JmsListener(containerFactory = "myJmsListenerContainerFactory", destination = "${spring.jms.template.default-destination}")
  // public void onMessageReceivedMQMsg(MQMessage msg) {
  //   // TODO: handle MQMessage
  //   log.info("JmsListener on " + defaultDest  + " at " + new Date() + " String ==|======> " + msg);
  // }

}
