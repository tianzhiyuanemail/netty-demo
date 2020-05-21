package com.example.service;

import com.example.client.NettyClient;
import com.example.client.handler.ClientInboundHandler;
import com.example.request.Request;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TestService {


    @Resource
    private NettyClient nettyClient;


    public void testa(Request request) {

        System.out.println(request.getBody());
        request.setBody("8888 request");

         nettyClient.getChannel().writeAndFlush(request);
    }

}
