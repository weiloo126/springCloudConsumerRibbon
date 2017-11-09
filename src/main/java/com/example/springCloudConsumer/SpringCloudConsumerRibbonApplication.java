package com.example.springCloudConsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author Administrator
 */
@SpringBootApplication
@EnableDiscoveryClient //启用服务注册与发现
@EnableCircuitBreaker //使用该注解开启断路器功能
public class SpringCloudConsumerRibbonApplication {	

	@Bean
	@LoadBalanced //注解开启均衡负载能力,通过Ribbon在客户端实现对服务调用的均衡负载
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudConsumerRibbonApplication.class, args);
	}
}
