package com.demo.ibmmq;

import com.demo.ibmmq.service.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;

import java.io.IOException;

@EnableJms
@SpringBootApplication
public class DemoMqApplication {

	public static void main(String[] args) throws IOException {

		// Files.createDirectories(Paths.get("./logs/"));
		// Path amqclchlFilePath = Paths.get("./logs/AMQCLCHL.TAB");
		// Files.deleteIfExists(amqclchlFilePath);
		// try (InputStream stream = new ClassPathResource("/AMQCLCHL.TAB").getInputStream()) {
		// 	Files.copy(stream, amqclchlFilePath, StandardCopyOption.REPLACE_EXISTING);
		// }
		//
		// Path trustStoreFilePath = Paths.get("./logs/truststore.jks");
		// Files.deleteIfExists(trustStoreFilePath);
		// try (InputStream stream = new ClassPathResource("/truststore.jks").getInputStream()) {
		// 	Files.copy(stream, trustStoreFilePath, StandardCopyOption.REPLACE_EXISTING);
		// }
		//
		// System.setProperty("com.ibm.mq.cfg.useIBMCipherMappings", "false");
		// System.setProperty("javax.net.ssl.trustStore", trustStoreFilePath.toString());
		// System.setProperty("javax.net.ssl.trustStorePassword", "passw0rd");

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
