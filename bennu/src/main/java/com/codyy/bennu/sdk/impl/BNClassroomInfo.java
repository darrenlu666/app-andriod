package com.codyy.bennu.sdk.impl;


public class BNClassroomInfo {
	private String mAssistClassroomUri1;
	private String mAssistClassroomUri2;
	private String mAssistClassroomUri3;
	private int mAssistClassroomNum;
	
	public void setAssistClassroomUri(int index, String uri) {
		if (index == 0) {
			mAssistClassroomUri1 = uri;
		} else if (index == 1) {
			mAssistClassroomUri2 = uri;
		} else if (index == 2) {
			mAssistClassroomUri3 = uri;
		}
	}
	
	public String getAssistClassroomUri(int index) {
		String uri = "default";
		if (index == 0) {
			uri = mAssistClassroomUri1;
		} else if (index == 1) {
			uri = mAssistClassroomUri2;
		} else if (index == 2) {
			uri = mAssistClassroomUri3;
		}
		
		return uri;
	}
	
	public void setAssistClassroomNum(int num) {
		mAssistClassroomNum = num;
	}
	
	public int getAssistClassroomNum() {
		return mAssistClassroomNum;
	}
	

}
