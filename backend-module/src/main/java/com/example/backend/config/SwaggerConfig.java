package com.example.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Desafio BIP — API de Benefícios")
                        .version("1.0.0")
                        .description("""
                                API REST para gerenciamento de benefícios empresariais.
                                
                                Funcionalidades:
                                - CRUD completo de benefícios
                                - Transferência de valores com optimistic locking (via @Version)
                                - Extrato de transações e recibos de transferências
                                
                                Em caso de conflito concorrente na transferência, a API retorna 409 Conflict.
                                """)
                        .contact(new Contact()
                                .name("Desafio BIP")
                                .email("desafio@bip.com")));
    }
}
