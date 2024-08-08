package com.puchkov.gateway.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Gateway Api",
                description = "Gateway Api from credit-bank", version = "1.0.0",
                contact = @Contact(
                        name = "Puchkov Pavel"
                )
        )
)
public class SwaggerConfig {

}