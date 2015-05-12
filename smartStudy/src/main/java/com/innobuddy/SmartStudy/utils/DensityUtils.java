package com.innobuddy.SmartStudy.utils;

import android.content.Context;

/**
 * dip与px之间的相互转化的工具类
 * sp与px之间的相互转换的工具类
 * @author tangyichao
 *
 */

public class DensityUtils {

	/**
	 * dip转化为px
	 * 
	 * @param context
	 *            上下文
	 * @param dpValue
	 *            dip
	 * @return px
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * px转化为dip
	 * 
	 * @param context
	 *            上下文
	 * @param pxValue
	 *            px
	 * @return dip
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	/**
	 * sp转化为px
	 * 
	 * @param context
	 *            上下文
	 * @param spValue
	 *            dip
	 * @return px
	 */
	public static int sp2px(Context context, float spValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (spValue * scale + 0.5f);
	}

	/**
	 * px转化为sp
	 * 
	 * @param context
	 *            上下文
	 * @param pxValue
	 *            px
	 * @return sp
	 */
	public static int px2sp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
