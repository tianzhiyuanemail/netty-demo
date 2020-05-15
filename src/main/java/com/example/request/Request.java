package com.example.request;

import lombok.Data;
import org.msgpack.annotation.Message;

import java.io.Serializable;

@Message
@Data
public class Request implements Serializable {

    private Long requestId;

    //类型
    private Integer type;

    //内容
    private String body;


}
