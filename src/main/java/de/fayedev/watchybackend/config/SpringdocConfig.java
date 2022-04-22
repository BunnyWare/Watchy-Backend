package de.fayedev.watchybackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringdocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("Bearer",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("Bearer").bearerFormat("JWT")));
    }

    @Bean
    public OpenApiCustomiser customGlobalHeaderOpenApiCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
            ApiResponses apiResponses = operation.getResponses();
            ApiResponse apiResponseBadRequest = new ApiResponse().description("Bad Request");
            apiResponses.addApiResponse("400", apiResponseBadRequest);
            ApiResponse apiResponseForbidden = new ApiResponse().description("Authentication invalid");
            apiResponses.addApiResponse("403", apiResponseForbidden);
            ApiResponse apiResponseInternalServerError = new ApiResponse().description("Internal server error");
            apiResponses.addApiResponse("500", apiResponseInternalServerError);
            ApiResponse apiResponseDependencyFailed = new ApiResponse().description("Backend service failed");
            apiResponses.addApiResponse("424", apiResponseDependencyFailed);
        }));
    }
}
