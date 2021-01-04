package com.demo.ibmmq;

import com.demo.ibmmq.service.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@SpringBootApplication
public class DemoMqApplication {

	public static void main(String[] args) throws IOException {

		// Path amqclchlFilePath = Paths.get("./AMQCLCHL.TAB");
		// Files.deleteIfExists(amqclchlFilePath);
		// try (InputStream stream = new ClassPathResource("/AMQCLCHL.TAB").getInputStream()) {
		// 	Files.copy(stream, amqclchlFilePath, StandardCopyOption.REPLACE_EXISTING);
		// }
		//
		// Path trustStoreFilePath = Paths.get("./truststore.jks");
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
