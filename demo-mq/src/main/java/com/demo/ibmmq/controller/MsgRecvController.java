package com.demo.ibmmq.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Controller
public class MsgRecvController {

  @Value("${demo.queue.out}") // DEV.QUEUE.3
  String outQueue;

  @Value("${demo.file.output}")
  String outputFile;

  @Autowired
  private JmsTemplate jmsTemplate;

  // http://localhost:8080/upload
  @GetMapping("/monitor")
  public String monitorIndex() {
    return "monitor";
  }

  // http://localhost:8080/mqrecv/queue
  @GetMapping("/mqrecv/queue")
  @ResponseBody
  String recvDefault(){
    // jmsTemplate.setReceiveTimeout(JmsTemplate.RECEIVE_TIMEOUT_NO_WAIT);
    try{
      String msg = "Receive from " + outQueue + " at " + new Date();
      String resp = Optional.ofNullable(jmsTemplate.receiveAndConvert(outQueue)).map(obj -> obj.toString()).orElse("No msg...");
      log.info(msg + " ==|======> " + resp);
      return msg + " ==|======> " + resp;
    }catch(JmsException ex){
      ex.printStackTrace();
      return "FAIL to receive msg...";
    }
  }

  // http://localhost:8080/mqrecv/save
  @GetMapping("/mqrecv/save/queue")
  @ResponseBody
  String recvSaveDefault(){
    try{
      String msg = "Save from " + outQueue + " at " + new Date();
      String resp = Optional.ofNullable(jmsTemplate.receiveAndConvert(outQueue)).map(obj -> obj.toString()).orElse("No msg...");
      Files.write(Paths.get(outputFile), resp.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
      Files.write(Paths.get(outputFile), System.getProperty("line.separator").getBytes(), StandardOpenOption.APPEND);
      return msg + " ==|======> " + resp;
    }catch(JmsException | IOException ex){
      ex.printStackTrace();
      return "FAIL to receive msg...";
    }
  }

  // http://localhost:8080/mqrecv/queue/DEV.QUEUE.2
  @GetMapping("/mqrecv/queue/{name}")
  @ResponseBody
  String recv(@PathVariable String name){
    // jmsTemplate.setReceiveTimeout(JmsTemplate.RECEIVE_TIMEOUT_NO_WAIT);
    try{
      String msg = "Receive from " + name + " at " + new Date();
      String resp = Optional.ofNullable(jmsTemplate.receiveAndConvert(name)).map(obj -> obj.toString()).orElse("No msg...");
      log.info(msg + " ==|======> " + resp);
      return msg + " ==|======> " + resp;
    }catch(JmsException ex){
      ex.printStackTrace();
      return "FAIL to receive msg...";
    }
  }

}
