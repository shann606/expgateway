package com.exp.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {

	@Bean
	RouteLocator expenseTrackRoute(RouteLocatorBuilder builder) {

		return builder.routes()

				.route("user-actions",
						r -> r.path("/api/v1/users/**").filters(f -> f.addRequestHeader("x-source", "gateway"))
								.uri("http://localhost:8080"))

				.route("login-Ui",
						r -> r.path("/login", "/register", "/dashboard", "/", "/api/**", "/css/**", "/js/**")
								.filters(f -> f.addRequestHeader("x-source", "gateway")).uri("http://localhost:4000"))

				

				.build();

	}

}
