package com.splawrence.ecommercepro.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

  @Value("${ecommercepro.openapi.dev-url}")
  private String devUrl;

  @Value("${ecommercepro.openapi.prod-url}")
  private String prodUrl;

  @Bean
  public OpenAPI myOpenAPI() {
    final Server developmentServer = new Server();
    developmentServer.setUrl(devUrl);
    developmentServer.setDescription("Development");

    final Server productionServer = new Server();
    productionServer.setUrl(prodUrl);
    productionServer.setDescription("Production");

    final Contact contact = new Contact();
    contact.setEmail("living202@outlook.com");
    contact.setName("Splawrence");
    contact.setUrl("https://www.github.com/splawrence");

    final License apacheLicense = new License().name("Apache License, Version 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html");

    final Info info = new Info()
        .title("E-commerce Pro API")
        .version("1.0")
        .contact(contact)
        .description("This API exposes endpoints for E-commerce platform")
        .license(apacheLicense);

    return new OpenAPI().info(info).servers(List.of(developmentServer, productionServer));
  }
}