package com.demo.ibmmq.controller;

import com.sun.corba.se.impl.presentation.rmi.DynamicMethodMarshallerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

@Slf4j
@Controller
public class MsgSendController {

  @Value("${spring.jms.template.default-destination}") // DEV.QUEUE.1
  String defaultDest;

  @Value("${demo.queue.in}") // DEV.QUEUE.3
  String inQueue;

  @Value("${demo.file.default}")
  String defaultFile;

  @Autowired
  private JmsTemplate jmsTemplate;


  // http://localhost:8080/
  @GetMapping
  public String index() {
    // List<String> lines = Files.readAllLines(Paths.get(defaultFile), StandardCharsets.UTF_8);
    // for (String line : lines) {
    //   System.out.println(line);
    // }
    return "index";
  }

  // http://localhost:8080/upload
  @GetMapping("/mqupload")
  public String fileUpload() {
    return "uploadForm";
  }

  // http://localhost:8080/upload/file
  @PostMapping("/mqupload/file")
  public String handleFileUpload(@RequestParam(value="file",required = false) MultipartFile file, Model model) throws IOException {
    if (file == null || file.isEmpty()) {
      System.out.println("No uploadFile, use defaultFile instead...");
      ClassPathResource classPathResource = new ClassPathResource(defaultFile);
      InputStream is = classPathResource.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      while (reader.ready()) {
        String line =  reader.readLine();
        System.out.println("defaultFile: " + line);
        jmsTemplate.convertAndSend(defaultDest, line);
      }
      model.addAttribute("message1", "You successfully send default msg to MQ!");
    } else {
      BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
      while(reader.ready()) {
        String line = reader.readLine();
        System.out.println("uploadFile: " + line);
        jmsTemplate.convertAndSend(inQueue, line);
      }
      model.addAttribute("message2", "You successfully send msg to MQ with " + file.getOriginalFilename() + "!");
    }
    return "uploadForm";
  }

  // http://localhost:8080/mqsend/queue/default
  @GetMapping("/mqsend/queue/default") // DEV.QUEUE.1
  @ResponseBody
  String sendToDefaultQ() {
    try {
      String msg = "Send msg to default Q DEV.QUEUE.1 at " + new Date();
      jmsTemplate.convertAndSend(defaultDest, "Hello World!");
      log.info(msg);
      return msg;
    } catch (JmsException ex) {
      ex.printStackTrace();
      return "FAIL to send msg...";
    }
  }

  // http://localhost:8080/mqsend/queue/DEV.QUEUE.2
  @GetMapping("/mqsend/queue/{name}")
  @ResponseBody
  String sendToNameQ(@PathVariable String name) {
    try {
      String msg = "Send msg to Queue " + name + " at " + new Date();
      jmsTemplate.convertAndSend(name, "Hello World!");
      log.info(msg);
      return msg;
    } catch (JmsException ex) {
      ex.printStackTrace();
      return "FAIL to send msg...";
    }
  }

  // http://localhost:8080/highvolume
  @GetMapping("/highvolume")
  public String highvolumeIndex(Model model) {
    return "highVolume";
  }

  // http://localhost:8080/highvolume
  @PostMapping("/highvolume")
  public String highvolumeSubmit(@RequestParam("reqNum") int reqNum,
                                 @RequestParam(value = "reqQueue", required = false) String reqQueue,
                                 Model model) {
    System.out.println("Custom request count: " + reqNum);
    System.out.println("Custom request queue: " + reqQueue);
    if (reqQueue == null) {
      reqQueue = defaultDest;
    }
    for (int i=0; i<reqNum; i++) {
      jmsTemplate.convertAndSend(reqQueue, "Test message with index " + i);
    }
    model.addAttribute("message", "You successfully send default msg to MQ!");
    return "highVolume";
  }

}
