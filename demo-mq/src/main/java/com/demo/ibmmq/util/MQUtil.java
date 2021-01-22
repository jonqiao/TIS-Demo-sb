package com.demo.ibmmq.util;

import com.demo.ibmmq.bean.MQProperties;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;

@Slf4j
@Component
public class MQUtil {

  public static MQQueueManager initQueueManager(MQProperties mqProperties) throws MQException, MalformedURLException {
    MQEnvironment.properties.put(MQConstants.APPNAME_PROPERTY, mqProperties.getApplicationName());
    // MQEnvironment.sslCipherSuite = mqProperties.getSslCipherSuite();
    MQEnvironment.CCSID = 1208;
    MQEnvironment.userID = mqProperties.getUser();
    MQEnvironment.password = mqProperties.getPassword();
    MQEnvironment.hostname = mqProperties.getHostname();
    MQEnvironment.port = mqProperties.getPort();
    MQEnvironment.channel = mqProperties.getChannel();
    MQQueueManager qMgr = new MQQueueManager(mqProperties.getQMgrName());

    // URL chanTab = new URL(mqProperties.getCcdtUrl());
    // MQQueueManager qMgr = new MQQueueManager(mqProperties.getQMgrName(), chanTab);
    return qMgr;
  }

  public static MQQueue browerQueue(MQQueueManager qMgr, String name) throws MQException {
    int qOption = CMQC.MQOO_BROWSE | CMQC.MQOO_FAIL_IF_QUIESCING | CMQC.MQOO_INQUIRE;
    MQQueue queue = qMgr.accessQueue(name, qOption);
    return queue;
  }

}
