package com.innobuddy.SmartStudy.application;

import android.app.Application;
/**
 *  启动初始化的使用
 * @author tangyichao
 *
 */
public class SmartStudyApplication extends Application {
	public static SmartStudyApplication mApplication;

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this; 
	}

	/** 获取Application */
	public static SmartStudyApplication getSmartStudyApplication() {
		return mApplication;
	}
}
