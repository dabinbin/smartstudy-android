package com.innobuddy.SmartStudy.activity.user;



import org.json.JSONException;
import org.json.JSONObject;

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
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class ResetPasswordActivity extends BaseActivity {

	protected static final int RESETSUCCESS = 1000;
	private EditText mEtResetPsw;
	private EditText mEtConfirmPsw;
	private Button mBtnRegister;
	private String mobileNumber;
	private String code;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case RESETSUCCESS:
				String json = (String) msg.obj;
				//
				try {
					JSONObject obj = new JSONObject(json);
					String err = obj.getString("err");
					if (!TextUtils.isEmpty(err)) {
						Toast.makeText(ResetPasswordActivity.this, err, Toast.LENGTH_LONG).show();
						return;
					}
					boolean succeed = obj.getBoolean("succeed");
					if (succeed) {
						Toast.makeText(ResetPasswordActivity.this, "重置成功", Toast.LENGTH_LONG).show();
						return;
					}

				} catch (JSONException e) {
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
		setContentView(R.layout.activity_reset_psw);
		mEtResetPsw = (EditText) findViewById(R.id.et_reset_psw);
		mEtConfirmPsw = (EditText) findViewById(R.id.et_confirm_psw);
		mBtnRegister = (Button) findViewById(R.id.btn_register);
		mBtnRegister.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		String resetPsw = mEtResetPsw.getText().toString().trim();
		String confirmPsw = mEtConfirmPsw.getText().toString().trim();
		switch (v.getId()) {
		case R.id.btn_register:
			if (TextUtils.isEmpty(resetPsw)) {
				Toast.makeText(this, "密码为空", Toast.LENGTH_LONG).show();
				return;
			}
			if (TextUtils.isEmpty(confirmPsw)) {
				Toast.makeText(this, "确认密码为空", Toast.LENGTH_LONG).show();
				return;
			}

			if (!confirmPsw.equals(resetPsw)) {
				Toast.makeText(this, "两次密码不一致", Toast.LENGTH_LONG).show();
				return;
			}
			
			if (confirmPsw.length() < 6) {
				Toast.makeText(this, "密码长度不应该小于6位", Toast.LENGTH_LONG).show();
				return;
			}

			RequestParams params = new RequestParams();
			// params.addHeader("token", "IO193UNVKDF711LFLGIDS81JVMLAFKEI");
			params.addBodyParameter("phone", mobileNumber);
			params.addBodyParameter("email", mobileNumber + "@qq.com");
			params.addBodyParameter("password", resetPsw);
			params.addBodyParameter("source", "android");
			//params.addBodyParameter("token", "IO193UNVKDF711LFLGIDS81JVMLAFKEI");
			// params.addBodyParameter("token","IO193UNVKDF711LFLGIDS81JVMLAFKEI");
			http.send(HttpMethod.POST, "http://dev.account.smartstudy.com/api/signup", params, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException err, String arg) {
					//
					Toast.makeText(ResetPasswordActivity.this,"网络连接失败", Toast.LENGTH_LONG).show();
				}
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					// 关闭当前界面
					if (responseInfo.getFirstHeader("Content-Type").getValue().equals("application/json; charset=utf-8")) {
						
						Message msg = Message.obtain();
						msg.what = RESETSUCCESS;
						msg.obj = responseInfo.result;
						handler.sendMessage(msg);
					} else {
						//当做失败处理
						Toast.makeText(ResetPasswordActivity.this, "网络连接失败", Toast.LENGTH_LONG).show();
					}
				}
			});

			break;
	
		default:
			break;
		}

	}
}

