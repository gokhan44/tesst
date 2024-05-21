package com.aractakip.mailcheck.ws;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement()
public class Response implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "Sonuc")
	private String result = null;

	public Response() {
	}

	public Response(String result) {
		this.result = result;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
