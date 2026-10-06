package com.exp.gateway.config;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
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
	
	@Value("${gateway.userservice}")
	private String userServiceEndpoint;
	@Value("${gateway.categoryservice}")
	private String categoryServiceEndpoint;
	@Value("${gateway.expenseservice}")
	private String expenseServiceEndpoint;
	@Value("${gateway.uiservice}")
	private String uiServiceEndpoint;

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

								.uri(userServiceEndpoint))

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

								.uri(categoryServiceEndpoint))

				.route("expense-actions",
						r -> r.path("/api/v1/expenses/**").filters(
								f -> f.addRequestHeader("X-Source", "gateway").filter((exchange, chain) -> exchange
										.getPrincipal().cast(Authentication.class).flatMap(authentication -> {

											CustomUser user = (CustomUser) authentication.getPrincipal();

											ServerHttpRequest request = exchange.getRequest().mutate()
													.header("X-Username", user.getUsername()).build();

											return chain.filter(exchange.mutate().request(request).build());
										}).switchIfEmpty(chain.filter(exchange)))

						)

								.uri(expenseServiceEndpoint))

				.route("login",
						r -> r.path("/login", "/register", "/api/**", "/css/**", "/js/**")
								.filters(f -> f.addRequestHeader("X-Source", "gateway")).uri(uiServiceEndpoint))

				.route("login-Ui",
						r -> r.path("/users", "/dashboard", "/users/edit", "/editprofile", "/categories/**",
								"/expenses/**", "/", "/api/**", "/css/**", "/js/**", "/images/**", "/webjars/**")
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

								)).uri(uiServiceEndpoint))

				.build();
	}

}
