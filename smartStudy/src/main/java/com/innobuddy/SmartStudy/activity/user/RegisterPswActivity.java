package com.innobuddy.SmartStudy.activity.user;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.activity.BaseActivity;
import com.innobuddy.SmartStudy.activity.WebViewActivity;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class RegisterPswActivity extends BaseActivity {

	private EditText mEtRegisterPsw;
	private EditText mEtConfirmPsw;
	private Button mBtnRegister;
	private CheckBox mCbSelect;
	private TextView mTvContent;
	private String mobileNumber;
	private String code; 
	private EditText mEtUsername;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1000:
				String json = (String) msg.obj;
				//
				try {
					JSONObject obj = new JSONObject(json);
					String err = obj.getString("err");
					if (!TextUtils.isEmpty(err)) {
						Toast.makeText(RegisterPswActivity.this, err, Toast.LENGTH_LONG).show();
						return;
					}
					boolean succeed = obj.getBoolean("succeed");
					// obj.has(name)
					if (succeed) {
						Toast.makeText(RegisterPswActivity.this, "注册成功", Toast.LENGTH_LONG).show();
						return;
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		mobileNumber = getIntent().getStringExtra("mobileNumber");
		code = getIntent().getStringExtra("code");
		setContentView(R.layout.activity_register_psw);
		mEtRegisterPsw = (EditText) findViewById(R.id.et_register_psw);
		mEtConfirmPsw = (EditText) findViewById(R.id.et_confirm_psw);
		mEtUsername = (EditText) findViewById(R.id.et_username);
		mBtnRegister = (Button) findViewById(R.id.btn_register);
		mCbSelect = (CheckBox) findViewById(R.id.cb_select);
		mTvContent = (TextView) findViewById(R.id.tv_content);
		mBtnRegister.setOnClickListener(this);
		// *****************//
		mTvContent.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
		mTvContent.getPaint().setAntiAlias(true);// 抗锯齿
		mTvContent.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		String registerPsw = mEtRegisterPsw.getText().toString().trim();
		String confirmPsw = mEtConfirmPsw.getText().toString().trim();
		String username = mEtUsername.getText().toString().trim();
		boolean isSelect = mCbSelect.isChecked();
		switch (v.getId()) {
		case R.id.btn_register:
			if (TextUtils.isEmpty(username)) {
				Toast.makeText(this, "用户名不能为空", Toast.LENGTH_LONG).show();
				return;
			}
			if (TextUtils.isEmpty(registerPsw)) {
				Toast.makeText(this, "密码为空", Toast.LENGTH_LONG).show();
				return;
			}
			if (TextUtils.isEmpty(confirmPsw)) {
				Toast.makeText(this, "确认密码为空", Toast.LENGTH_LONG).show();
				return;
			}

			if (!confirmPsw.equals(registerPsw)) {
				Toast.makeText(this, "两次密码不一致", Toast.LENGTH_LONG).show();
				return;
			}
			if (username.length() > 15 || username.length() < 2) {
				Toast.makeText(this, "用户名应2到15位", Toast.LENGTH_LONG).show();
				return;
			}
			if (confirmPsw.length() < 6) {
				Toast.makeText(this, "密码长度不应该小于6位", Toast.LENGTH_LONG).show();
				return;
			}
			if (!isSelect) {
				Toast.makeText(this, "同意条款", Toast.LENGTH_LONG).show();
				return;
			}
			RequestParams params = new RequestParams();
			// params.addHeader("token", "IO193UNVKDF711LFLGIDS81JVMLAFKEI");
			params.addBodyParameter("phone", mobileNumber);
			params.addBodyParameter("email", mobileNumber + "@qq.com");
			params.addBodyParameter("password", registerPsw);
			params.addBodyParameter("username", username);
			params.addBodyParameter("source", "android");
			//params.addBodyParameter("token", "IO193UNVKDF711LFLGIDS81JVMLAFKEI");
			// params.addBodyParameter("token","IO193UNVKDF711LFLGIDS81JVMLAFKEI");
			http.send(HttpMethod.POST, "http://dev.account.smartstudy.com/api/signup", params, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException err, String arg) {
					//
					Toast.makeText(RegisterPswActivity.this,"网络连接失败", Toast.LENGTH_LONG).show();
				}

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					// 关闭当前界面

					//System.out.println(responseInfo.getFirstHeader("Content-Type").getValue());
					//System.out.println(responseInfo.result);// text/html;
															// charset=utf-8
					if (responseInfo.getFirstHeader("Content-Type").getValue().equals("application/json; charset=utf-8")) {
						Message msg = Message.obtain();
						msg.what = 1000;
						msg.obj = responseInfo.result;
						handler.sendMessage(msg);
					} else {
						//当做失败处理
						Toast.makeText(RegisterPswActivity.this, "网络连接失败", Toast.LENGTH_LONG).show();
					}
				}
			});

			// finish();
			break;
		case R.id.tv_content:
			// Toast.makeText(this, "内容", Toast.LENGTH_LONG).show();
			Intent intent = new Intent();
			intent.putExtra("url", "http://www.baidu.com");
			intent.setClass(this, WebViewActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}
}
