package com.upcera.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;

/**
 * @author Panda.HuangWei313.
 * @since 2018-07-12 11:12.
 */
@XStreamAlias("Route")
public class Route implements Serializable {
    private static final  String SPACE = " ";
    @XStreamAsAttribute
    private String remark;
    @XStreamAsAttribute
    private String acceptTime;
    @XStreamAsAttribute
    private String acceptAddress;
    @XStreamAsAttribute
    private String opcode;

    public String getFormat() {
        return this.acceptTime + SPACE + this.acceptAddress + SPACE + this.remark + SPACE + this.opcode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

    public String getAcceptAddress() {
        return acceptAddress;
    }

    public void setAcceptAddress(String acceptAddress) {
        this.acceptAddress = acceptAddress;
    }

    public String getOpcode() {
        return opcode;
    }

    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }

}
