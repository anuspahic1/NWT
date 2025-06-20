package com.medicinska.rezervacija.korisnici_doktori.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development server")
                ))
                .info(new Info()
                        .title("Sistem za upravljanje doktorima i pacijentima")
                        .version("1.0")
                        .description("API za upravljanje doktorima, pacijentima i ocjenama")
                        .contact(new Contact()
                                .name("WINX")
                                .email("podrska@medicinska-rezervacija.ba")
                                .url("https://www.medicinska-rezervacija.ba"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}