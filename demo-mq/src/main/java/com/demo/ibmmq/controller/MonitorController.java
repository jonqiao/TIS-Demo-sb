package com.demo.ibmmq.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jms.JMSException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Controller
public class MonitorController {

  @Value("ibm.mq.queueManager")
  private String queueManager;
  @Value("ibm.mq.channel")
  private String channel;
  @Value("ibm.mq.connName")
  private String connName;
  // @Value("ibm.mq.ccdtUrl")
  // private String ccdtUrl;
  @Value("ibm.mq.user")
  private String user;
  @Value("ibm.mq.password")
  private String password;
  @Value("${spring.jms.template.default-destination}") // DEV.QUEUE.1
  private String defaultQ;
  @Value("${demo.queue.local2}") // DEV.QUEUE.2
  private String localQ2;
  @Value("${demo.queue.local3}") // DEV.QUEUE.3
  private String localQ3;
  @Value("${demo.file.output}")
  private String outputFile;

  @Autowired
  private JmsTemplate jmsTemplate;

  // http://localhost:8080/upload
  @GetMapping("/monitor")
  public String monitorIndex() throws JMSException {
    return "monitor";
  }









}
