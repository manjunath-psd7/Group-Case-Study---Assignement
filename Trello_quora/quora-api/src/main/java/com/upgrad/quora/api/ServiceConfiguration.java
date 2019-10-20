package com.upgrad.quora.api;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.upgrad.quora.service")
@EntityScan("com.upgrad.quora.service.entity")
public class ServiceConfiguration {
}
