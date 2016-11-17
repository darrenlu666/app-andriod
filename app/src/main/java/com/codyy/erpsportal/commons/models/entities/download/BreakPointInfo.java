/*
 * Created by FanChao
 */
package com.codyy.erpsportal.commons.models.entities.download;

public class BreakPointInfo {
	private String userId;
	private String resId;//资源id 唯一不重复
	private int threadId;// 下载器id
	private int startPos;// 开始点
	private int endPos;// 结束点
	private int completeSize;// 已下载长度
	private String url;// 下载器网络标识
	private int totalSize;
	private long updateTime;

	public BreakPointInfo(String userId, String resId, int threadId, int startPos, int endPos,
			int completeSize, int totalSize, String url, long updateTime) {
        this.userId = userId;
		this.resId	=	resId;
		this.threadId = threadId;
		this.startPos = startPos;
		this.endPos = endPos;
		this.completeSize = completeSize;
		this.totalSize = totalSize;
		this.updateTime = updateTime;
		this.url = url;
	}

	public BreakPointInfo() {
	}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}

	public int getTotal() {
		return totalSize;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public void setTotal(int totalSize) {
		this.totalSize = totalSize;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public int getStartPos() {
		return startPos;
	}

	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	public int getEndPos() {
		return endPos;
	}

	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}

	public int getCompleteSize() {
		return completeSize;
	}

	public void setCompleteSize(int completeSize) {
		this.completeSize = completeSize;
	}

	@Override
	public String toString() {
		return "BreakPointInfo [" +
				"resId="+resId+
				" ,threadId=" + threadId + ", startPos="
				+ startPos + ", endPos=" + endPos + ", completeSize="
				+ completeSize + ", url=" + url + ", totalSize=" + totalSize
				+ "]";
	}

}
