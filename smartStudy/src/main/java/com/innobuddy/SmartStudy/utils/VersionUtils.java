package com.innobuddy.SmartStudy.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * 获取版本和版本号的工具类
 * 
 * @author tangyichao
 * 
 */
public class VersionUtils {
	/**
	 *获取应用名和版本
	 * @param context
	 * @return
	 */
	public static String getAppVersion(Context context) {
		// 获取手机的包管理器 程序管理器
		PackageManager pm = context.getPackageManager();
		try {
			// 代表的是某一个应用程序的清单文件 manifest.xml
			PackageInfo packinfo = pm.getPackageInfo(context.getPackageName(), 0);

			return packinfo.applicationInfo.loadLabel(context.getPackageManager()).toString() + "V" + packinfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// can't reach
			return "";
		}
	}
	
	/**
	 *获取当前版本
	 * @param context
	 * @return
	 */
	public static String getAppCurrentVersion(Context context) {
		// 获取手机的包管理器 程序管理器
		PackageManager pm = context.getPackageManager();
		try {
			// 代表的是某一个应用程序的清单文件 manifest.xml
			PackageInfo packinfo = pm.getPackageInfo(context.getPackageName(), 0);

			return  packinfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// can't reach
			return "";
		}
	}


	/**
	 * 获取应用程序的版本号
	 * 
	 * @return 版本号
	 */
	public static int getAppVersionCode(Context context) {
		// 获取手机的包管理器 程序管理器
		PackageManager pm = context.getPackageManager();
		try {
			// 代表的是某一个应用程序的清单文件 manifest.xml
			PackageInfo packinfo = pm.getPackageInfo(context.getPackageName(), 0);

			return packinfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();

			return 0;
		}
	}
}
