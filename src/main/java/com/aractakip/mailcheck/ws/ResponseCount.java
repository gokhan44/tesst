package com.aractakip.mailcheck.ws;

public class ResponseCount {

	public String result;
	public String err_code;
	public String err_desc;

	public ResponseCount() {

	}

	public ResponseCount(String result, String err_code,
			String err_desc) {
		this.result = result;
		this.err_code = err_code;
		this.err_desc = err_desc;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getErr_code() {
		return err_code;
	}

	public void setErr_code(String err_code) {
		this.err_code = err_code;
	}

	public String getErr_desc() {
		return err_desc;
	}

	public void setErr_desc(String err_desc) {
		this.err_desc = err_desc;
	}



}
