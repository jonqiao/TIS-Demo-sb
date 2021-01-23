package com.demo.ibmmq.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Controller
public class MsgRecvController {

  @Autowired
  private CachingConnectionFactory cachingConnectionFactory;

  @Value("${demo.queue.local2}") // DEV.QUEUE.2
  private String localQ2;

  @Value("${demo.file.output}")
  private String outputFile;

  @Autowired
  private JmsTemplate jmsTemplate;

  // http://localhost:8080/mqrecv/queue
  @GetMapping("/mqrecv/queue")
  @ResponseBody
  public String recvDefault(){
    // jmsTemplate.setReceiveTimeout(JmsTemplate.RECEIVE_TIMEOUT_NO_WAIT);
    try{
      String msg = "Receive from " + localQ2 + " at " + new Date();
      String resp = Optional.ofNullable(jmsTemplate.receiveAndConvert(localQ2)).map(obj -> obj.toString()).orElse("No msg...");
      log.info(msg + " ==|======> " + resp);
      return msg + " ==|======> " + resp;
    }catch(JmsException e){
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      log.warn(sw.toString());
      return "FAIL to receive msg...";
    }
  }

  // http://localhost:8080/mqrecv/save
  @GetMapping("/mqrecv/save/queue")
  @ResponseBody
  public String recvSaveDefault() {
    try{
      String msg = "Save from " + localQ2 + " at " + new Date();
      String resp = Optional.ofNullable(jmsTemplate.receiveAndConvert(localQ2)).map(obj -> obj.toString()).orElse("No msg...");
      Files.write(Paths.get(outputFile), resp.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
      Files.write(Paths.get(outputFile), System.getProperty("line.separator").getBytes(), StandardOpenOption.APPEND);
      return msg + " ==|======> " + resp;
    }catch(JmsException | IOException e){
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      log.warn(sw.toString());
      return "FAIL to receive msg...";
    }
  }

  // http://localhost:8080/mqrecv/queue/DEV.QUEUE.2
  @GetMapping("/mqrecv/queue/{name}")
  @ResponseBody
  public String recv(@PathVariable String name){
    // jmsTemplate.setReceiveTimeout(JmsTemplate.RECEIVE_TIMEOUT_NO_WAIT);
    try{
      String msg = "Receive from " + name + " at " + new Date();
      String resp = Optional.ofNullable(jmsTemplate.receiveAndConvert(name)).map(obj -> obj.toString()).orElse("No msg...");
      log.info(msg + " ==|======> " + resp);
      return msg + " ==|======> " + resp;
    }catch(JmsException e){
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      log.warn(sw.toString());
      return "FAIL to receive msg...";
    }
  }

}
