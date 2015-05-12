package com.innobuddy.download.domain;

import java.io.Serializable;

public class TsFile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//ts片段连接
	private String tsUrl;
	//ts片段时间
	private float time;
	public String getTsUrl() {
		return tsUrl;
	}
	public void setTsUrl(String tsUrl) {
		this.tsUrl = tsUrl;
	}
	public float getTime() {
		return time;
	}
	public void setTime(float time) {
		this.time = time;
	}
	
	
	

}
