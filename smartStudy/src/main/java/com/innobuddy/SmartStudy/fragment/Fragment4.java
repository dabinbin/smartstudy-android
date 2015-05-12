package com.innobuddy.SmartStudy.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.activity.AboutUsActivity;
import com.innobuddy.SmartStudy.activity.FeedbackActivity;
import com.innobuddy.SmartStudy.utils.Utilitys;
import com.umeng.analytics.MobclickAgent;

public class Fragment4 extends Fragment implements OnClickListener {

		
    View view = null;
	private LinearLayout mLlAboutUs;
	private LinearLayout mLlFeedback;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment4, container, false);
		
		Switch switch1 = (Switch)view.findViewById(R.id.switch1);
		mLlAboutUs = (LinearLayout) view.findViewById(R.id.ll_about_us);
		mLlFeedback = (LinearLayout) view.findViewById(R.id.ll_feedback);
		SharedPreferences settingPreferences = getActivity().getSharedPreferences(Utilitys.SETTING_INFOS, 0);

		boolean mobileHint = settingPreferences.getBoolean(Utilitys.MOBILE_HINT, true);
		
		switch1.setChecked(mobileHint);
		
		switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				SharedPreferences settingPreferences = getActivity().getSharedPreferences(Utilitys.SETTING_INFOS, 0);
				SharedPreferences.Editor editor = settingPreferences.edit();
				editor.putBoolean(Utilitys.MOBILE_HINT, isChecked);
				editor.commit();
				
			}
		});
		mLlAboutUs.setOnClickListener(this);
		mLlFeedback.setOnClickListener(this);
		return view;
		
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("MainScreen"); //统计页面
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("MainScreen"); 
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_about_us:
			//Toast.makeText(getActivity(), "关于我们", Toast.LENGTH_LONG).show();
			Intent aboutUsIntent=new Intent();
			aboutUsIntent.setClass(getActivity(), AboutUsActivity.class);
			startActivity(aboutUsIntent);
			break;
		case R.id.ll_feedback:
			//Toast.makeText(getActivity(), "关于我们", Toast.LENGTH_LONG).show();
			Intent feedbackIntent=new Intent();
			feedbackIntent.setClass(getActivity(),FeedbackActivity.class);
			startActivity(feedbackIntent);
			break;

		default:
			break;
		}
	}

}
