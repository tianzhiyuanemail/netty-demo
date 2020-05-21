package com.example.service;

import com.example.request.Request;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    public String testa(Request request){

        return request.getBody()+"9999";
    }

}
