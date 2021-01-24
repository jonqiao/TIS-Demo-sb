package com.demo.ibmmq.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class IndexController {

  // http://localhost:8080/
  @GetMapping
  public String index() {
    // List<String> lines = Files.readAllLines(Paths.get(defaultFile), StandardCharsets.UTF_8);
    // for (String line : lines) {
    //   log.info(line);
    // }
    return "index";
  }

  // http://localhost:8080/help
  @GetMapping("/help")
  public String help(@RequestParam String name) {
    log.info("Request URL: /help?name=" + name);
    return "help";
  }

  // http://localhost:8080/help/readme
  @GetMapping("/help/{name}")
  @ResponseBody
  public String readme(@PathVariable String name) {
    // String fileContent = new String(Files.readAllBytes(Paths.get("classpath:static/markdown/README.md")));
    ClassPathResource classPathResource = new ClassPathResource("static/markdown/README.md");
    if ("readme".equalsIgnoreCase(name)) {
      // classPathResource = new ClassPathResource("static/markdown/README.md");
    }
    if ("admin".equalsIgnoreCase(name)) {
      classPathResource = new ClassPathResource("static/markdown/EnvSetUp.md");
    }
    try {
      log.info("Send file: " + classPathResource.getFilename());
      InputStream is = classPathResource.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      String fileContent = reader.lines().collect(Collectors.joining(System.lineSeparator()));
      // fileContent.replace("|", "&brvbar;");
      return fileContent;
    } catch (Exception e) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      log.warn(sw.toString());
    }
    return "Cannot find README!";
  }

}
