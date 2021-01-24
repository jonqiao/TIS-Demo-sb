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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

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
  public String MonitorIndex() {
    return "monitor";
  }

  @GetMapping("/monitor/all")
  @ResponseBody
  public Map monitorAll() {
    long start = System.currentTimeMillis();
    log.info("Monitor All start: " + start);
    Map map = new HashMap<>();
    try {
      MQQueueManager qMgr = new MQComp().connectQmgr(mqProperties);
      if (qMgr != null) {
        Integer q1CurDepth = MQComp.chkQueueDepth(qMgr, localQ1);
        if (q1CurDepth != null) {
          map.put("q1CurDepth", q1CurDepth);
        } else {
          map.put("q1CurDepth", "unknown queue connection");
        }
        Integer q2CurDepth = MQComp.chkQueueDepth(qMgr, localQ2);
        if (q2CurDepth != null) {
          map.put("q2CurDepth", q2CurDepth);
        } else {
          map.put("q2CurDepth", "unknown queue connection");
        }
        Integer q3CurDepth = MQComp.chkQueueDepth(qMgr, localQ3);
        if (q3CurDepth != null) {
          map.put("q3CurDepth", q3CurDepth);
        } else {
          map.put("q3CurDepth", "unknown queue connection");
        }
        qMgr.disconnect();
      } else {
        map.put("q1CurDepth", "unknown qMgr connection");
        map.put("q2CurDepth", "unknown qMgr connection");
        map.put("q3CurDepth", "unknown qMgr connection");
      }
    } catch (MQException e) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      log.warn(sw.toString());
      map.put("q1CurDepth", "Found MQException");
      map.put("q2CurDepth", "Found MQException");
      map.put("q3CurDepth", "Found MQException");
    }
    long end = System.currentTimeMillis();
    log.info("Monitor All end: " + end);
    log.info("Monitor All take: " + (end - start) + " for one time...");
    return map;
  }

  @GetMapping("/monitor/{queuename}")
  @ResponseBody
  public Map monitueue(@PathVariable String queuename) {
    long start = System.currentTimeMillis();
    log.info("Monitor " + queuename + " start: " + start);
    Map map = new HashMap<>();
    String curDepth;
    switch (queuename) {
      case "DEV.QUEUE.1":
        curDepth = "q1CurDepth";
        break;
      case "DEV.QUEUE.2":
        curDepth = "q2CurDepth";
        break;
      case "DEV.QUEUE.3":
        curDepth = "q3CurDepth";
        break;
      default:
        log.info("Only support known queue name for now...");
        curDepth = "qCurDepth";
    }
    try {
      MQQueueManager qMgr = new MQComp().connectQmgr(mqProperties);
      if (qMgr != null) {
        Integer q1CurDepth = MQComp.chkQueueDepth(qMgr, queuename);
        if (q1CurDepth != null) {
          map.put(curDepth, q1CurDepth);
        } else {
          map.put(curDepth, "unknown queue connection");
        }
        qMgr.disconnect();
      } else {
        map.put(curDepth, "unknown qMgr connection");
      }
    } catch (MQException e) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      log.warn(sw.toString());
      map.put(curDepth, "Found MQException");
    }
    long end = System.currentTimeMillis();
    log.info("Monitor " + queuename + " end: " + end);
    log.info("Monitor " + queuename + " take: " + (end - start) + " for one time...");
    return map;
  }

}
