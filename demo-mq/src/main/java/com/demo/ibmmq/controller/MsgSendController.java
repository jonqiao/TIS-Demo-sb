package com.demo.ibmmq.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

@Slf4j
@Controller
@RequestMapping("mqsend")
public class MsgSendController {

  @Value("${spring.jms.template.default-destination}") // DEV.QUEUE.1
  String defaultDest;

  @Value("${demo.queue.in}") // DEV.QUEUE.3
  String inQueue;

  @Value("${demo.file.in}")
  String inFile;

  @Autowired
  private JmsTemplate jmsTemplate;


  // http://localhost:8080/mqsend
  @GetMapping
  public String listUploadedFiles() throws IOException {
    return "uploadForm";
  }

  // http://localhost:8080/mqsend/queue/DEV.QUEUE.2
  @GetMapping("/queue/{name}")
  @ResponseBody
  String send(@PathVariable String name) {
    try {
      String msg = "Send msg to IBM MQ at " + new Date();
      jmsTemplate.convertAndSend(defaultDest, "Hello World!");
      jmsTemplate.convertAndSend(inQueue, "Hello World!");
      jmsTemplate.convertAndSend(name, "Hello World!");
      log.info(msg);
      return msg;
    } catch (JmsException ex) {
      ex.printStackTrace();
      return "FAIL to send msg...";
    }
  }

  // http://localhost:8080/mqsend/queue
  @GetMapping("/queue")
  @ResponseBody
  String sendDefault() {
    try {
      String msg = "Send msg to IBM MQ at " + new Date();
      jmsTemplate.convertAndSend(defaultDest, "Hello World!");
      jmsTemplate.convertAndSend(inQueue, "Hello World!");
      log.info(msg);
      return msg;
    } catch (JmsException ex) {
      ex.printStackTrace();
      return "FAIL to send msg...";
    }
  }

  // http://localhost:8080/mqsend
  @PostMapping("/queue")
  public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
    while(reader.ready()) {
      String line = reader.readLine();
      jmsTemplate.convertAndSend(defaultDest, line);
      jmsTemplate.convertAndSend(inQueue, line);
    }

    // List<String> lines = Files.readAllLines(Paths.get(inFile), StandardCharsets.UTF_8);
    // for (String line : lines) {
    //   jmsTemplate.convertAndSend(defaultDest, line);
    //   jmsTemplate.convertAndSend(inQueue, line);
    // }

    model.addAttribute("message", "You successfully send msg to MQ with " + file.getOriginalFilename() + "!");
    return "uploadForm";
  }

}
