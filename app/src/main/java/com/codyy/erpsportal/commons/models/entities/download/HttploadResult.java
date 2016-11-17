/*
 * Created by FanChao
 */
package com.codyy.erpsportal.commons.models.entities.download;

public class HttploadResult {
	private int fileSize;// 文件大小
	private int complete;// 完成度
	private String url;// 下载器标识

	public HttploadResult(int fileSize, int complete, String url) {
		this.fileSize = fileSize;
		this.complete = complete;
		this.url = url;
	}

	public HttploadResult() {
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public int getComplete() {
		return complete;
	}

	public void setComplete(int complete) {
		this.complete = complete;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
