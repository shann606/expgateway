package com.exp.gateway.config;

import java.util.stream.Collectors;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.exp.gateway.customuser.CustomUser;

@Configuration
public class ApiGatewayConfiguration {

	@Bean
	RouteLocator expenseTrackRoute(RouteLocatorBuilder builder) {

		return builder.routes()

				.route("user-actions",
						r -> r.path("/api/v1/users/**", "/api/v1/admin/**").filters(
								f -> f.addRequestHeader("X-Source", "gateway").filter((exchange, chain) -> exchange
										.getPrincipal().cast(Authentication.class).flatMap(authentication -> {

											CustomUser user = (CustomUser) authentication.getPrincipal();

											ServerHttpRequest request = exchange.getRequest().mutate()
													.header("X-Username", user.getUsername()).build();

											return chain.filter(exchange.mutate().request(request).build());
										}).switchIfEmpty(chain.filter(exchange)))

						)

								.uri("http://localhost:8080"))

				.route("category-actions",
						r -> r.path("/api/v1/categories/**").filters(
								f -> f.addRequestHeader("X-Source", "gateway").filter((exchange, chain) -> exchange
										.getPrincipal().cast(Authentication.class).flatMap(authentication -> {

											CustomUser user = (CustomUser) authentication.getPrincipal();

											ServerHttpRequest request = exchange.getRequest().mutate()
													.header("X-Username", user.getUsername()).build();

											return chain.filter(exchange.mutate().request(request).build());
										}).switchIfEmpty(chain.filter(exchange)))

						)

								.uri("http://localhost:8081"))

				.route("login",
						r -> r.path("/login", "/register", "/api/**", "/css/**", "/js/**")
								.filters(f -> f.addRequestHeader("X-Source", "gateway")).uri("http://localhost:4000"))

				.route("login-Ui",
						r -> r.path("/users", "/dashboard", "/users/edit", "/editprofile", "/categories/**", "/",
								"/api/**", "/css/**", "/js/**", "/images/**", "/webjars/**")
								.filters(f -> f.addRequestHeader("X-Source", "gateway").filter((exchange, chain) ->

								exchange.getPrincipal().cast(Authentication.class).flatMap(authentication -> {

									CustomUser user = (CustomUser) authentication.getPrincipal();
									String id = user.getId().toString();

									String username = user.getUsername();

									String roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority)

											.collect(Collectors.joining(","));

									ServerHttpRequest request = exchange.getRequest().mutate()
											.header("X-Username", username).header("X-Roles", roles).header("X-Id", id)
											.build();

									return chain.filter(exchange.mutate().request(request).build());
								}).switchIfEmpty(chain.filter(exchange))

								)).uri("http://localhost:4000"))

				.build();
	}

}
