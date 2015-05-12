package com.innobuddy.SmartStudy.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.activity.user.LoginActivity;
import com.innobuddy.SmartStudy.utils.VersionUtils;

public class SplashActivity extends Activity {
	private static final int ENTERHOMECODE = 100;
	private TextView mTvVersion;
//	@SuppressLint("HandlerLeak")
//	private Handler handler = new Handler() {
//		public void handleMessage(android.os.Message msg) {
//			switch (msg.what) {
//			case ENTERHOMECODE:
//				Intent intent = new Intent();
//				intent.setClass(SplashActivity.this, MainActivity.class);
//				startActivity(intent);
//				finish();
//				break;
//			default:
//				break;
//			}
//		};
//
//	};
	
	private Button mBtn;
    private Button mBtn2;
    private int screenWidth;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            mBtn.setVisibility(View.VISIBLE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(mBtn, "alpha", 0.1f, 1f);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(mBtn, "translationX", screenWidth / 2 - mBtn.getWidth() / 2);
            animator.setDuration(2000);
            animator2.setDuration(2000);
            animator.setInterpolator(new BounceInterpolator());
            animator2.setInterpolator(new BounceInterpolator());
            animator.start();
            animator2.start();

            mBtn2.setVisibility(View.VISIBLE);
            ObjectAnimator animator3 = ObjectAnimator.ofFloat(mBtn2, "alpha", 0.1f, 1f);
            ObjectAnimator animator4 = ObjectAnimator.ofFloat(mBtn2, "translationX", -screenWidth / 2 + mBtn.getWidth() / 2);
            animator3.setDuration(2000);
            animator4.setDuration(2000);
            animator3.setInterpolator(new BounceInterpolator());
            animator4.setInterpolator(new BounceInterpolator());
            animator3.start();
            animator4.start();
        }
    };


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		mTvVersion = (TextView) findViewById(R.id.tv_version);
		String version = VersionUtils.getAppVersion(this);
		mTvVersion.setText(version);
		handler.sendEmptyMessageDelayed(ENTERHOMECODE, 2000);

		// SystemClock.uptimeMillis();
		// 自从开机所用的时间
		
		
		DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        screenWidth = dm.widthPixels; //测量屏幕宽度
        //  int screenHeigh = dm.heightPixels;

        setContentView(R.layout.activity_splash);
        mBtn = (Button) findViewById(R.id.btn);
        // Button mBtn2;
        mBtn2 = (Button) findViewById(R.id.btn2);
        mBtn.setVisibility(View.INVISIBLE);
        mBtn2.setVisibility(View.INVISIBLE);
        handler.sendEmptyMessageDelayed(100, 2000);
        mBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(SplashActivity.this, LoginActivity.class);
				startActivity(intent);
			}
		});
        
        
	}

}
