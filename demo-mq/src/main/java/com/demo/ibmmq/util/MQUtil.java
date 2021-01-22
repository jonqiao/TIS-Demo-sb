package com.demo.ibmmq.util;

import com.ibm.mq.*;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class MQUtil {

  public static MQQueueManager initConnection(Map map) throws MQException {
    MQEnvironment.properties.put(MQConstants.APPNAME_PROPERTY, map.get("applicationName"));
    MQEnvironment.hostname = (String) map.get("hostname");
    MQEnvironment.port = (int) map.get("port");
    MQEnvironment.userID = (String) map.get("user");
    MQEnvironment.password = (String) map.get("password");
    MQEnvironment.channel = (String) map.get("channel");
    MQQueueManager qMgr = new MQQueueManager((String) map.get("qMgrName"));
    return qMgr;
  }

  public static MQQueue browerQueue(MQQueueManager qMgr, String name) throws MQException {
    int qOption = CMQC.MQOO_BROWSE | CMQC.MQOO_FAIL_IF_QUIESCING | CMQC.MQOO_INQUIRE;
    MQQueue queue = qMgr.accessQueue(name, qOption);
    return queue;
  }

}
