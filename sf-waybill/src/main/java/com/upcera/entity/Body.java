package com.upcera.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.Serializable;

/**
 * @author Panda.HuangWei313.
 * @since 2018-07-12 15:34.
 */
@XStreamAlias("Body")
public class Body implements Serializable {

    @XStreamAlias("RouteResponse")
    private RouteResponse routeResponse;

    public RouteResponse getRouteResponse() {
        return routeResponse;
    }

    public void setRouteResponse(RouteResponse routeResponse) {
        this.routeResponse = routeResponse;
    }
}
