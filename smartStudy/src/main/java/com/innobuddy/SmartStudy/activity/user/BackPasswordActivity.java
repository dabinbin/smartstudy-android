package com.innobuddy.SmartStudy.activity.user;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.activity.BaseActivity;
import com.innobuddy.SmartStudy.utils.ValidateUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class BackPasswordActivity extends BaseActivity {
	private Button mBtnObtionCode;
	private Timer timer;
	private TimerTask task;
	private int time = 60;
	private EditText mEtMobileNumber;
	private Button mBtnRegister;
	private EditText mEtCode;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1000:
				time = time - 1;
				mBtnObtionCode.setText( time + "秒后重发");
				if (time == 0) {
					mBtnObtionCode.setText("获取验证码");
					mBtnObtionCode.setEnabled(true);
					mEtMobileNumber.setEnabled(true);
					mBtnObtionCode.setBackgroundResource(R.drawable.btn_bg_defult);
					cancelCountDownTask();
					time = 60;
				}
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		mBtnObtionCode = (Button) findViewById(R.id.btn_obtain_code);
		mEtMobileNumber = (EditText) findViewById(R.id.et_reg_mobile_number);
		mBtnRegister = (Button) findViewById(R.id.btn_register);
		mEtCode = (EditText) findViewById(R.id.et_code);
		mBtnObtionCode.setOnClickListener(this);
		mBtnRegister.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		String mobileNumber = mEtMobileNumber.getText().toString().trim();
		boolean isNumber = ValidateUtil.isMobile(mobileNumber);
		String code = mEtCode.getText().toString().trim();
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_obtain_code:
			if (TextUtils.isEmpty(mobileNumber)) {
				Toast.makeText(this, "电话号码不能为空", Toast.LENGTH_LONG).show();
				return;
			}
			if (!isNumber) {
				Toast.makeText(this, "电话号码格式不正确", Toast.LENGTH_LONG).show();
				return;
			}
			timer = new Timer();
			task = new TimerTask() {

				@Override
				public void run() {
					// --发送给handler
					Message msg = Message.obtain();
					msg.what = 1000;
					handler.sendMessage(msg);
				}

			};
			timer.schedule(task, 0, 1000);
			// --每一秒钟无延迟运行一次
			mBtnObtionCode.setEnabled(false);// 不可被点击
			mEtMobileNumber.setEnabled(false);
			mBtnObtionCode.setBackgroundResource(R.drawable.btn_bg_enable);
			/***可修改样式***/
			break;
		case R.id.btn_register:
			if (TextUtils.isEmpty(mobileNumber)) {
				Toast.makeText(this, "电话号码不能为空", Toast.LENGTH_LONG).show();
				return;
			}
			if (!isNumber) {
				Toast.makeText(this, "电话号码格式不正确", Toast.LENGTH_LONG).show();
				return;
			}
			if (TextUtils.isEmpty(code)) {
				Toast.makeText(this, "验证码不能为空", Toast.LENGTH_LONG).show();
				return;
			}
			http.send(HttpMethod.POST, "", new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					
				}
			});
			Intent intent=new Intent();
			intent.putExtra("mobileNumber", mobileNumber);
			intent.putExtra("code", code);
			intent.setClass(this, ResetPasswordActivity.class);
			startActivity(intent);
			finish();// 顺便关闭自己
			break;
		default:
			break;
		}
	}

	/**
	 * 取消倒计时任务
	 */
	private void cancelCountDownTask() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (task != null) {
			task.cancel();
			task = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cancelCountDownTask();
	}

	

}
