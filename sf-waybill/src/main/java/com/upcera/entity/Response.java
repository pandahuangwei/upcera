package com.upcera.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Panda.HuangWei313.
 * @since 2018-07-12 11:25.
 */
@XStreamAlias("Response")
public class Response implements Serializable {
    @XStreamAsAttribute
    private String service;

    @XStreamAlias("Head")
    private String head;

    @XStreamAlias("Body")
    private Body body;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
