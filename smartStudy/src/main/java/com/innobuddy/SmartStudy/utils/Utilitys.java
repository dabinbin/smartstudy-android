package com.innobuddy.SmartStudy.utils;

import java.io.File;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.DB.DBHelper;
import com.innobuddy.SmartStudy.Video.VideoPlayerActivity;
import com.innobuddy.download.utils.DStorageUtils;
import com.innobuddy.download.utils.NetworkUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class Utilitys {
	
	public final static String SETTING_INFOS = "settings";
	public final static String MOBILE_HINT = "mobile_hint";
	
	Context _context;
	
	JSONObject _jsonObject;
	
	public JSONObject downloadObject = null;
	
	private static Utilitys instance = null;
	
	public static Utilitys getInstance() {
		if (instance == null) {
			instance = new Utilitys();
		}
		return instance;
	}

	public Utilitys() {
		
	}


	public static final DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()  
    .showImageOnLoading(R.drawable.course_default) //设置图片在下载期间显示的图片  
    .showImageForEmptyUri(R.drawable.course_default)//设置图片Uri为空或是错误的时候显示的图片  
   .showImageOnFail(R.drawable.course_default)  //设置图片加载/解码过程中错误时候显示的图片
   .cacheInMemory(true)//设置下载的图片是否缓存在内存中  
   .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中  
   .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
   //.delayBeforeLoading(int delayInMillis)//int delayInMillis为你设置的下载前的延迟时间
   //设置图片加入缓存前，对bitmap进行设置  
   //.preProcessor(BitmapProcessor preProcessor)  
   .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位  
//   .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少  
//   .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间  
   .build();//构建完成
	
	public static final DisplayImageOptions focusOptions = new DisplayImageOptions.Builder()  
    .showImageOnLoading(R.drawable.course_focus) //设置图片在下载期间显示的图片  
    .showImageForEmptyUri(R.drawable.course_focus)//设置图片Uri为空或是错误的时候显示的图片  
   .showImageOnFail(R.drawable.course_focus)  //设置图片加载/解码过程中错误时候显示的图片
   .cacheInMemory(true)//设置下载的图片是否缓存在内存中  
   .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中  
   .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
   //.delayBeforeLoading(int delayInMillis)//int delayInMillis为你设置的下载前的延迟时间
   //设置图片加入缓存前，对bitmap进行设置  
   //.preProcessor(BitmapProcessor preProcessor)  
   .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位  
//   .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少  
//   .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间  
   .build();//构建完成

	public static boolean isMobileNetwork(Context context) {
		 ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		 if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				return true;
			}
		}
		 return false;
	}
		
	public void playVideo(JSONObject jsonObject, Context context) {
		
try {
	
	if (jsonObject != null) {
		
		_jsonObject = jsonObject;
		
		_context = context;
		
		String url = jsonObject.getString(DBHelper.VIDEO_URL);
        File file = new File(DStorageUtils.FILE_ROOT
                + NetworkUtils.getFileNameFromUrl(url));
        
		SharedPreferences settingPreferences = context.getSharedPreferences(SETTING_INFOS, 0);
		
		boolean mobileHint = settingPreferences.getBoolean(MOBILE_HINT, true);
        
        if (!file.exists() && Utilitys.isMobileNetwork(context) && mobileHint) {
			
			new AlertDialog.Builder(context).setTitle("提示").setMessage("您正在使用蜂窝移动网络。")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface,
										int i) {
									
									Intent intent = new Intent();
									intent.putExtra("json", _jsonObject.toString());
									intent.setClass(_context, VideoPlayerActivity.class);
									_context.startActivity(intent);

								}
							})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface,
										int i) {
									
								}
							})
							.show();
			
		} else {
			Intent intent = new Intent();
			intent.putExtra("json", jsonObject.toString());
			intent.setClass(context, VideoPlayerActivity.class);
			context.startActivity(intent);

        }
		
	}

} catch (Exception e) {
	
}

}
	
	
}
