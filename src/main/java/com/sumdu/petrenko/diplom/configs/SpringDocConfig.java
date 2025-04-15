package com.sumdu.petrenko.diplom.configs;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Messaging App API")
                        .version("0.1")
                        .description("This is a simple API for a messaging app.")
                        .termsOfService("https://exampleofmywebsite.com/terms")
                        .contact(new Contact()
                                .name("Arsenii Petrenko IN-11")
                                .url("https://exampleofmywebsite.com")
                                .email("arseniipetrenko46@gmail.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                        .summary("This is summary."))
                .externalDocs(new ExternalDocumentation()
                        .description("Find out more")
                        .url("https://exampleofmywebsite.com/docs"))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local server"),
                        new Server().url("http://api.mywebsite.com").description("Production server")
                ))
                .components(new Components()
                        .addSecuritySchemes("basicAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")))
                .security(List.of(new SecurityRequirement().addList("basicAuth"))
        );
    }
}
