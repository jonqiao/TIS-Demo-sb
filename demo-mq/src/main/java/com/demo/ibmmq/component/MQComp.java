package com.demo.ibmmq.component;

import com.demo.ibmmq.bean.MQProperties;
import com.demo.ibmmq.util.MQUtil;
import com.ibm.mq.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class MQComp {

  private static final List<Integer> MQRC = Arrays.asList(2161, 2162, 2009, 2059, 2537, 2538);
  private static MQPoolToken defaultToken;
  private MQQueueManager qMgr;

  @PostConstruct
  public void activateConn() {
    log.info("Activates the default connection pool by registering an MQPoolToken object with MQEnvironment.");
    defaultToken = MQEnvironment.addConnectionPoolToken();
  }

  public MQQueueManager connectQmgr(MQProperties mqProperties) {
    MQQueueManager qMgr = null;
    try {
      qMgr = MQUtil.initQueueManager(mqProperties);
    } catch (Exception e) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      log.warn(sw.toString());
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

  @PreDestroy
  public void deactivateConn() {
    log.info("Deactivates the default connection pool, which destroys any queue manager connections stored in the pool.");
    MQEnvironment.removeConnectionPoolToken(defaultToken);
  }
}
