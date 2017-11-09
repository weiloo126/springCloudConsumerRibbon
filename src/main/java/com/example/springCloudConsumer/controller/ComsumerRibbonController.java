package com.example.springCloudConsumer.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

/**
 * Ribbon是一个基于HTTP和TCP客户端的负载均衡器。Feign中也使用Ribbon，后续会介绍Feign的使用。
 * Ribbon可以在通过客户端中配置的ribbonServerList服务端列表去轮询访问以达到均衡负载的作用。
 * 
 * 当Ribbon与Eureka联合使用时，ribbonServerList会被DiscoveryEnabledNIWSServerList重写，扩展成从Eureka注册中心中获取服务端列表。
 * 同时它也会用NIWSDiscoveryPing来取代IPing，它将职责委托给Eureka来确定服务端是否已经启动。
 * 
 * 下面我们通过实例看看如何使用Ribbon来调用服务，并实现客户端的均衡负载。
 * 
 * 对于RestTemplate的使用，我们的第一个url参数有一些特别。这里请求的host位置并没有使用一个具体的IP地址和端口的形式，而是采用了服务名的方式组成。
 * Spring Cloud Ribbon有一个拦截器，它能够在这里进行实际调用的时候，自动的去选取服务实例，并将实际要请求的IP地址和端口替换这里的服务名，从而完成服务接口的调用。
 * @author LW  
 * @date 2017年10月9日
 */
@RestController
@RequestMapping("/ribbon")
public class ComsumerRibbonController {

    @Autowired
    RestTemplate restTemplate;
    
    private static final ThreadLocal<Long> startTimeLocal = new ThreadLocal<>();
	
	@RequestMapping("/hello/{name}")
	@HystrixCommand(fallbackMethod = "helloFallback") //在使用ribbon消费服务的函数上增加@HystrixCommand注解来指定熔断时的回调方法
	public String hello(@PathVariable("name") String name){
		startTimeLocal.set(System.currentTimeMillis());
		return restTemplate.getForEntity("http://SPRING-CLOUD-PRODUCER/hello?name="+name, String.class).getBody();
	}
	
	public String helloFallback(String name) {
		System.out.println("hello retry timeout: " + (System.currentTimeMillis() - startTimeLocal.get()) + " ms.");
        return "helloError " + name;
    }
    
    @RequestMapping("/add/{a}/{b}")
	@HystrixCommand(fallbackMethod = "addFallback")
	public String add(@PathVariable("a") Integer a, @PathVariable("b") Integer b) {
		startTimeLocal.set(System.currentTimeMillis());
        return restTemplate.getForEntity("http://SPRING-CLOUD-PRODUCER/add?a="+a+"&b="+b, String.class).getBody();
    }
	
	public String addFallback(Integer a, Integer b) {//必须要有和原方法一样的参数和参数类型
		System.out.println("add retry timeout: " + (System.currentTimeMillis() - startTimeLocal.get()) + " ms.");
        return "addError：a=" + a + ", b=" + b;
    }
	
}
