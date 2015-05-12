package com.innobuddy.SmartStudy.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 正则表达式的工具类
 * @author tangyichao
 *
 */
public class ValidateUtil {

	/**
	 * 验证是否是正确的邮箱格式
	 * 
	 * @param email
	 *            电子邮箱
	 * @return true表示是正确的邮箱格式,false表示不是正确邮箱格式
	 */
	public static boolean isEmail(String email) {
		// 1、\\w+表示@之前至少要输入一个匹配字母或数字或下划线、点、中横线
		String regular = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern pattern = Pattern.compile(regular);
		boolean flag = false;
		if (email != null) {
			Matcher matcher = pattern.matcher(email);
			flag = matcher.matches();
		}
		return flag;
	}

	/**
	 * 验证是否是手机号格式 该方法还不是很严谨,只是可以简单验证
	 * 
	 * @param mobile
	 *            电话号码
	 * @return true表示是正确的手机号格式,false表示不是正确的手机号格式
	 */
	public static boolean isMobile(String mobile) {
		// 当前运营商号段分配
		// 中国移动号段 1340-1348 135 136 137 138 139 150 151 152 157 158 159 187 188
		// 147  移动
		// 中国联通号段 130 131 132 155 156 185 186 145
		// 中国电信号段 133 1349 153 180 189
		// 170 虚拟号段
		String regular = "1[3,4,5,7,8]{1}\\d{9}";
		Pattern pattern = Pattern.compile(regular);
		boolean flag = false;
		if (mobile != null) {
			Matcher matcher = pattern.matcher(mobile);
			flag = matcher.matches();
		}
		return flag;
	}

}
