package com.exp.gateway.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!default")
@EnableDiscoveryClient
@Configuration
public class GatewayDiscoveryClient {

}
