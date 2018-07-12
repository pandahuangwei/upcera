package com.upcera.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.List;

/**
 * @author Panda.HuangWei313.
 * @since 2018-07-12 11:19.
 */
@XStreamAlias("RouteResponse")
public class RouteResponse implements Serializable {
    @XStreamAsAttribute
    private String mailno;

    @XStreamImplicit(itemFieldName = "Route")
    private List<Route> routes;


    public String getMailno() {
        return mailno;
    }

    public void setMailno(String mailno) {
        this.mailno = mailno;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
}
