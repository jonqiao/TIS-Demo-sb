package com.demo.ibmmq.controller;

import com.ibm.mq.*;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@Controller
public class MonitorController {
  @Value("${demo.mq.applicationName}")
  private String applicationName;
  @Value("${demo.mq.hostname}")
  private String hostname;
  @Value("${demo.mq.port}")
  private String port;

  @Value("${ibm.mq.queueManager}")
  private String qMgrName;
  @Value("${ibm.mq.channel}")
  private String channel;
  // @Value("${ibm.mq.ccdtUrl}")
  // private String ccdtUrl;
  @Value("${ibm.mq.user}")
  private String user;
  @Value("${ibm.mq.password}")
  private String password;

  @Value("${demo.queue.local1}") // DEV.QUEUE.1
  private String localQ1;
  @Value("${demo.queue.local2}") // DEV.QUEUE.2
  private String localQ2;
  @Value("${demo.queue.local3}") // DEV.QUEUE.3
  private String localQ3;

  @Autowired
  private JmsTemplate jmsTemplate;

  private static MQQueueManager qMgr;

  @GetMapping("/monitor")
  public String index() {
    return "monitor";
  }

  // http://localhost:8080/monitor
  @PostMapping("/monitor/all")
  public String monitorIndex(Model model) {
    MQPoolToken defaultToken = null;
    try {
      if (qMgr == null || !qMgr.isConnected()) {
        defaultToken = this.initConnection();
      }
      if (qMgr != null) {
        Integer q1CurDepth = this.chkQueueDepth(localQ1);
        if (q1CurDepth != null) {
          model.addAttribute("q1CurDepth", q1CurDepth);
        } else {
          model.addAttribute("q1CurDepth", "unknown");
        }
        Integer q2CurDepth = this.chkQueueDepth(localQ2);
        if (q2CurDepth != null) {
          model.addAttribute("q2CurDepth", q2CurDepth);
        } else {
          model.addAttribute("q2CurDepth", "unknown");
        }
        Integer q3CurDepth = this.chkQueueDepth(localQ3);
        if (q3CurDepth != null) {
          model.addAttribute("q3CurDepth", q3CurDepth);
        } else {
          model.addAttribute("q3CurDepth", "unknown");
        }
        qMgr.disconnect();
      } else {
        model.addAttribute("q1CurDepth", "unknown");
        model.addAttribute("q2CurDepth", "unknown");
        model.addAttribute("q3CurDepth", "unknown");
      }
    } catch (Exception e) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      log.warn(sw.toString());
    } finally {
      if (defaultToken != null) {
        MQEnvironment.removeConnectionPoolToken(defaultToken);
      }
    }
    return "monitor";
  }

  public MQPoolToken initConnection() {
    try {
      MQPoolToken defaultToken = MQEnvironment.addConnectionPoolToken();
      MQEnvironment.properties.put(MQConstants.APPNAME_PROPERTY, applicationName);
      MQEnvironment.hostname = hostname;
      MQEnvironment.port = Integer.parseInt(port);
      MQEnvironment.userID = user;
      MQEnvironment.password = password;
      MQEnvironment.channel = channel;
      qMgr = new MQQueueManager(qMgrName);
      return defaultToken;
    } catch (MQException e) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      log.warn(sw.toString());
    }
    return null;
  }

  public Integer chkQueueDepth(String name) throws MQException {
    MQQueue queue = null;
    try {
      int qOption = CMQC.MQOO_BROWSE | CMQC.MQOO_FAIL_IF_QUIESCING | CMQC.MQOO_INQUIRE;
      queue = qMgr.accessQueue(name, qOption);
      return queue.getCurrentDepth();
    } catch (MQException e) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      log.warn(sw.toString());
    } finally {
      if (queue != null) {
        queue.close();
      }
    }
    return null;
  }

}
