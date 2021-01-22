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

@Slf4j
@Component
public class MQUtil {

  public static MQQueueManager initQueueManager(MQProperties mqProperties) throws MQException {
    MQEnvironment.properties.put(MQConstants.APPNAME_PROPERTY, mqProperties.getApplicationName());
    MQEnvironment.hostname = mqProperties.getHostname();
    MQEnvironment.port = mqProperties.getPort();
    MQEnvironment.userID = mqProperties.getUser();
    MQEnvironment.password = mqProperties.getPassword();
    MQEnvironment.channel = mqProperties.getChannel();
    MQQueueManager qMgr = new MQQueueManager(mqProperties.getQMgrName());
    return qMgr;
  }

  public static MQQueue browerQueue(MQQueueManager qMgr, String name) throws MQException {
    int qOption = CMQC.MQOO_BROWSE | CMQC.MQOO_FAIL_IF_QUIESCING | CMQC.MQOO_INQUIRE;
    MQQueue queue = qMgr.accessQueue(name, qOption);
    return queue;
  }

}
