package com.demo.ibmmq.component;

import com.demo.ibmmq.util.MQUtil;
import com.ibm.mq.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MQComp {
  @Value("${demo.mq.applicationName}")
  private String applicationName;
  @Value("${demo.mq.hostname}")
  private String hostname;
  @Value("${demo.mq.port}")
  private int port;

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

  private static List<Integer> MQRC = Arrays.asList(2161, 2162, 2009, 2059, 2537, 2538);
  private static Map map = new HashMap<>();
  private static MQPoolToken defaultToken;
  private MQQueueManager qMgr;

  @Bean
  public void initConfig() {
    map.put("applicationName", applicationName);
    map.put("hostname", hostname);
    map.put("port", port);
    map.put("channel", channel);
    map.put("qMgrName", qMgrName);
    map.put("user", user);
    map.put("password", password);
  }

  @Bean
  public MQQueueManager connQmgr() {
    if (defaultToken == null) {
      log.info("Add MQ ConnectionPool Token...");
      defaultToken = MQEnvironment.addConnectionPoolToken();
    }
    try {
      qMgr = this.createQmgr();
    } catch (InterruptedException | MQException e) {
      MQEnvironment.removeConnectionPoolToken(defaultToken);
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      log.warn(sw.toString());
    }
    return qMgr;
  }

  public synchronized MQQueueManager createQmgr() throws MQException, InterruptedException {
    MQQueueManager qMgr = null;
    while (qMgr == null) {
      try {
        qMgr = MQUtil.initConnection(map);
      } catch (MQException e) {
        if (MQRC.contains(e.reasonCode)) {
          log.warn("Retry due to MQ connection: CC = " + e.completionCode + "; RC = " + e.reasonCode);
          StringWriter sw = new StringWriter();
          e.printStackTrace(new PrintWriter(sw));
          log.warn(sw.toString());
          TimeUnit.SECONDS.sleep(60);
        } else {
          throw e;
        }
      }
    }
    return qMgr;
  }

  public static Integer chkQueueDepth(MQQueueManager qMgr, String name) throws MQException {
    MQQueue queue = null;
    try {
      queue = MQUtil.browerQueue(qMgr, name);
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
