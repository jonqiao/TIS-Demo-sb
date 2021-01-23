package com.demo.ibmmq.controller;

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

import java.io.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
public class MsgSendController {

  @Value("${spring.jms.template.default-destination}") // DEV.QUEUE.1
  private String defaultDest;

  @Value("${demo.queue.local3}") // DEV.QUEUE.3
  private String inQueue;

  @Value("${demo.file.default}")
  private String defaultFile;

  @Autowired
  private JmsTemplate jmsTemplate;


  // http://localhost:8080/upload
  @GetMapping("/mqupload")
  public String fileUpload() {
    return "uploadForm";
  }

  // http://localhost:8080/upload/file
  @PostMapping("/mqupload/file")
  public String handleFileUpload(@RequestParam(value="file",required = false) MultipartFile file, Model model) {
    if (file == null || file.isEmpty()) {
      log.info("No Uploaded File, Use Default File Instead.");
      ClassPathResource classPathResource = new ClassPathResource(defaultFile);
      try {
        InputStream is = classPathResource.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        while (reader.ready()) {
          String line =  reader.readLine();
          log.info("defaultFile: " + line);
          try {
            jmsTemplate.convertAndSend(defaultDest, line);
          } catch (JmsException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            log.warn(sw.toString());
            TimeUnit.SECONDS.sleep(30);
          }
        }
        model.addAttribute("message1", "Success: send default msg to MQ!");
      } catch (Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        log.warn(sw.toString());
        model.addAttribute("message1", "Fail: send default msg to MQ!");
      }
    } else {
      try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        while(reader.ready()) {
          String line = reader.readLine();
          log.info("uploadFile: " + line);
          try {
            jmsTemplate.convertAndSend(inQueue, line);
          } catch (JmsException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            log.warn(sw.toString());
            TimeUnit.SECONDS.sleep(30);
          }
        }
        model.addAttribute("message2", "Success: send msg to MQ with " + file.getOriginalFilename() + "!");
      } catch (Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        log.warn(sw.toString());
        model.addAttribute("message2", "Fail: send msg to MQ with " + file.getOriginalFilename() + "!");
      }
    }
    return "uploadForm";
  }

  // http://localhost:8080/mqsend/queue/default
  @GetMapping("/mqsend/queue/default") // DEV.QUEUE.1
  @ResponseBody
  public String sendToDefaultQ() {
    try {
      String msg = "Send msg to default Q DEV.QUEUE.1 at " + new Date();
      jmsTemplate.convertAndSend(defaultDest, "Hello World!");
      log.info(msg);
      return msg;
    } catch (JmsException e) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      log.warn(sw.toString());
      return "FAIL to send msg...";
    }
  }

  // http://localhost:8080/mqsend/queue/DEV.QUEUE.2
  @GetMapping("/mqsend/queue/{name}")
  @ResponseBody
  public String sendToNameQ(@PathVariable String name) {
    try {
      String msg = "Send msg to Queue " + name + " at " + new Date();
      jmsTemplate.convertAndSend(name, "Hello World!");
      log.info(msg);
      return msg;
    } catch (JmsException e) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      log.warn(sw.toString());
      return "FAIL to send msg...";
    }
  }

  // http://localhost:8080/highvolume
  @GetMapping("/highvolume")
  public String highvolumeIndex(Model model) {
    model.addAttribute("defaultActive", "active");
    return "highVolume";
  }

  // http://localhost:8080/highvolume
  @PostMapping("/highvolume")
  public String highvolumeSubmit(@RequestParam("reqNum") int reqNum,
                                 @RequestParam(value = "reqQueue", required = false) String reqQueue,
                                 Model model) {
    log.info("Custom request count: " + reqNum);
    log.info("Custom request queue: " + reqQueue);
    boolean custReq = false;
    if (reqQueue == null) {
      reqQueue = defaultDest;
    } else {
      custReq = true;
    }
    for (int i=0; i<reqNum; i++) {
      try {
        jmsTemplate.convertAndSend(reqQueue, "Total " + reqNum + " test message with index " + i);
        if (custReq) {
          model.addAttribute("defaultActive", "");
          model.addAttribute("customActive", "active");
          model.addAttribute("message2", "Success: send high volume messages to " + reqQueue + " !");
        } else {
          model.addAttribute("defaultActive", "active");
          model.addAttribute("customActive", "");
          model.addAttribute("message1", "Success: send high volume messages to default Q !");
        }
      } catch (JmsException e) {
        // Exception linkedEx = ex.getLinkedException();
        // if (ex.getLinkedException() != null) {
        //   if (linkedEx instanceof MQException) {
        //     MQException mqException = (MQException) linkedEx;
        //     int reasonCode = mqException.reasonCode;
        //     // Handle the reason code accordingly
        //   }
        // }
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        log.warn(sw.toString());
        try {
          TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException ie) {
          StringWriter iesw = new StringWriter();
          ie.printStackTrace(new PrintWriter(iesw));
          log.warn(iesw.toString());
        }
        if (custReq) {
          model.addAttribute("defaultActive", "");
          model.addAttribute("customActive", "active");
          model.addAttribute("message2", "Fail: send high volume messages to " + reqQueue + " !");
        } else {
          model.addAttribute("defaultActive", "active");
          model.addAttribute("customActive", "");
          model.addAttribute("message1", "Fail: send high volume messages to default Q !");
        }
      }
    }
    return "highVolume";
  }

}
