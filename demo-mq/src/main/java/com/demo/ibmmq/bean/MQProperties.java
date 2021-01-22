package com.demo.ibmmq.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = "demo.mq")
public class MQProperties {
  private String applicationName;
  private String qMgrName;
  private String user;
  private String password;
  private String channel;
  private String hostname;
  private int port;
  private String ccdtUrl;
  private String sslCipherSuite;
}
