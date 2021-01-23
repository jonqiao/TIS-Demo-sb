package com.demo.ibmmq.controller;

import com.demo.ibmmq.bean.MQProperties;
import com.demo.ibmmq.component.MQComp;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@Controller
public class MonitorController {

  @Value("${demo.queue.local1}") // DEV.QUEUE.1
  private String localQ1;
  @Value("${demo.queue.local2}") // DEV.QUEUE.2
  private String localQ2;
  @Value("${demo.queue.local3}") // DEV.QUEUE.3
  private String localQ3;

  @Autowired
  private JmsTemplate jmsTemplate;
  @Autowired
  private MQProperties mqProperties;

  // http://localhost:8080/monitor
  @GetMapping("/monitor")
  public String index() {
    return "monitor";
  }

  @PostMapping("/monitor/all")
  public String monitorIndex(Model model) {
    long start = System.currentTimeMillis();
    log.info("Monitor start: " + start);
    try {
      MQQueueManager qMgr = new MQComp().connectQmgr(mqProperties);
      if (qMgr != null) {
        Integer q1CurDepth = MQComp.chkQueueDepth(qMgr, localQ1);
        if (q1CurDepth != null) {
          model.addAttribute("q1CurDepth", q1CurDepth);
        } else {
          model.addAttribute("q1CurDepth", "unknown queue connection");
        }
        Integer q2CurDepth = MQComp.chkQueueDepth(qMgr, localQ2);
        if (q2CurDepth != null) {
          model.addAttribute("q2CurDepth", q2CurDepth);
        } else {
          model.addAttribute("q2CurDepth", "unknown queue connection");
        }
        Integer q3CurDepth = MQComp.chkQueueDepth(qMgr, localQ3);
        if (q3CurDepth != null) {
          model.addAttribute("q3CurDepth", q3CurDepth);
        } else {
          model.addAttribute("q3CurDepth", "unknown queue connection");
        }
        qMgr.disconnect();
      } else {
        model.addAttribute("q1CurDepth", "unknown qMgr connection");
        model.addAttribute("q2CurDepth", "unknown qMgr connection");
        model.addAttribute("q3CurDepth", "unknown qMgr connection");
      }
    } catch (MQException e) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      log.warn(sw.toString());
      model.addAttribute("q1CurDepth", "Found MQException");
      model.addAttribute("q2CurDepth", "Found MQException");
      model.addAttribute("q3CurDepth", "Found MQException");
    }
    long end = System.currentTimeMillis();
    log.info("Monitor end: " + end);
    log.info("Monitor take: " + (end - start) + " for one time...");
    return "monitor";
  }

}
