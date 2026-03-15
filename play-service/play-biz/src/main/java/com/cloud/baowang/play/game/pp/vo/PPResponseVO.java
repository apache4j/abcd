package com.cloud.baowang.play.game.pp.vo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public class PPResponseVO {

    private String code;
    private String message;
    private String method;
    private String resourceType;
    private String requestId;
    private String hostId;

    @XmlElement(name = "Code")
    public void setCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }

    @XmlElement(name = "Message")
    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    @XmlElement(name = "Method")
    public void setMethod(String method) {
        this.method = method;
    }
    public String getMethod() {
        return method;
    }

    @XmlElement(name = "ResourceType")
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
    public String getResourceType() {
        return resourceType;
    }

    @XmlElement(name = "RequestId")
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    public String getRequestId() {
        return requestId;
    }

    @XmlElement(name = "HostId")
    public void setHostId(String hostId) {
        this.hostId = hostId;
    }
    public String getHostId() {
        return hostId;
    }

    @Override
    public String toString() {
        return "Error: " + code + " - " + message;
    }
}
