package com.puchkov.statement.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Statement Api",
                description = "Statement Api from credit-bank", version = "1.0.0",
                contact = @Contact(
                        name = "Puchkov Pavel"
                )
        )
)
public class SwaggerConfig {

}