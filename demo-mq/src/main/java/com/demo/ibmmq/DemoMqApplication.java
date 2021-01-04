package com.demo.ibmmq;

import com.demo.ibmmq.service.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

// https://www.ibm.com/support/knowledgecenter/zh/SSFKSJ_9.1.0/com.ibm.mq.dev.doc/q031500_.htm
// https://developer.ibm.com/languages/spring/tutorials/mq-jms-application-development-with-spring-boot/
// https://developer.ibm.com/zh/tutorials/mq-jms-application-development-with-spring-boot/
// https://github.com/ibm-messaging/mq-jms-spring

@SpringBootApplication
public class DemoMqApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoMqApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}

}
