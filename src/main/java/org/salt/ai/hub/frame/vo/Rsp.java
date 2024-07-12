package org.salt.ai.hub.frame.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Rsp<T> {

    protected Integer code;

    protected String msg;

    protected T data;
}
