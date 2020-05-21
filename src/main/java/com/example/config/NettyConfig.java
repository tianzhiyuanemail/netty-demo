package com.example.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "netty")
public class NettyConfig {

    private Integer localPort;

    private String  remoteAddress;

    private Integer remotePort;

}
