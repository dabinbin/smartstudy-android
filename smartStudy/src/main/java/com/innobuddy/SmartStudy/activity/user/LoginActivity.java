package com.innobuddy.SmartStudy.activity.user;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.activity.BaseActivity;
import com.innobuddy.SmartStudy.global.ConstantValue;
import com.innobuddy.SmartStudy.global.GlobalParams;
import com.innobuddy.SmartStudy.utils.Md5Utils;
import com.innobuddy.SmartStudy.utils.ValidateUtil;
import com.innobuddy.download.utils.ConfigUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

@SuppressLint("HandlerLeak")
public class LoginActivity extends BaseActivity implements OnFocusChangeListener {
	private EditText mEtLoginMobileNumber;
	private Button mBtnLogin;
	private EditText mEtLoginPsw;
	private TextView mTvRegister;
	private TextView mTvFindPsw;
	private ImageView mIvLoginNumberClear;
	private ImageView mIvLoginPswClear;
	private CheckBox mCbSavePsw;
	private Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1000:
				String json = (String) msg.obj;
				//
				try {
					JSONObject obj = new JSONObject(json);
					String err = obj.getString("err");
					if (!TextUtils.isEmpty(err)) {
						Toast.makeText(LoginActivity.this, err, Toast.LENGTH_LONG).show();
						return;
					}
					String account = obj.getString("account");
					if (!TextUtils.isEmpty(account)) {
						Toast.makeText(LoginActivity.this, account, Toast.LENGTH_LONG).show();
						return;
					}
					String username = obj.getString("username");
					if (!TextUtils.isEmpty(account)) {
						Toast.makeText(LoginActivity.this, username, Toast.LENGTH_LONG).show();
						return;
					}
					boolean succeed = obj.getBoolean("succeed");
					// obj.has(name)
					if (succeed) {
						Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_LONG).show();
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

	// private CheckBox mCbSavePsw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setHomeAsUpIndicator(R.drawable.ic_launcher);
		mEtLoginMobileNumber = (EditText) findViewById(R.id.et_login_mobile_number);
		mEtLoginPsw = (EditText) findViewById(R.id.et_login_psw);
		mTvRegister = (TextView) findViewById(R.id.tv_register);
		mTvFindPsw = (TextView) findViewById(R.id.tv_find_psw);
		mBtnLogin = (Button) findViewById(R.id.btn_login);
		mIvLoginNumberClear = (ImageView) findViewById(R.id.iv_login_number_clear);
		mIvLoginPswClear = (ImageView) findViewById(R.id.iv_login_psw_clear);
		mCbSavePsw = (CheckBox) findViewById(R.id.cb_save_psw);
		mBtnLogin.setOnClickListener(this);
		mTvRegister.setOnClickListener(this);
		mTvFindPsw.setOnClickListener(this);
		mIvLoginNumberClear.setOnClickListener(this);
		mIvLoginPswClear.setOnClickListener(this);
		String mobileNumber = ConfigUtils.getString(this, "userName");
		if (!TextUtils.isEmpty(mobileNumber)) {
			mEtLoginMobileNumber.setText(mobileNumber);
		}
		//
		String password = ConfigUtils.getString(this, "password");

		if (!TextUtils.isEmpty(password)) {
			String passwordEncrypt = com.innobuddy.SmartStudy.utils.ThreeDes.dataEncrypt(password, Md5Utils.encode(ConstantValue.Keys.THERREDS).getBytes());
			// System.out.println(password1);
			mEtLoginPsw.setText(passwordEncrypt);
			mCbSavePsw.setChecked(true);
		} else {
			mCbSavePsw.setChecked(false);
			mEtLoginPsw.setText("");// 密码
		}
		mEtLoginMobileNumber.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.toString().length() == 0) {
					mIvLoginNumberClear.setVisibility(View.GONE);
				} else {
					mIvLoginNumberClear.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				if (s.toString().length() == 0) {
					mIvLoginNumberClear.setVisibility(View.GONE);
				} else {
					mIvLoginNumberClear.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		mEtLoginPsw.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.toString().length() == 0) {
					mIvLoginPswClear.setVisibility(View.GONE);
				} else {
					mIvLoginPswClear.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				if (s.toString().length() == 0) {
					mIvLoginPswClear.setVisibility(View.GONE);
				} else {
					mIvLoginPswClear.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		mEtLoginMobileNumber.setOnFocusChangeListener(this);
		mEtLoginPsw.setOnFocusChangeListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == android.R.id.home) {
			// Toast.makeText(this, "开来吃我啊", Toast.LENGTH_LONG).show();
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		final String mobileNumber = mEtLoginMobileNumber.getText().toString().trim();
		final String password = mEtLoginPsw.getText().toString().trim();
		String pswMd5 = Md5Utils.encode(password);// Md5数据加密
		boolean isNumber = ValidateUtil.isMobile(mobileNumber);
		final boolean isSavePsw = mCbSavePsw.isChecked();
		switch (v.getId()) {
		case R.id.btn_login:
			if (TextUtils.isEmpty(mobileNumber)) {
				Toast.makeText(this, "电话号码不能为空", Toast.LENGTH_LONG).show();
				return;
			}
			if (TextUtils.isEmpty(password)) {
				Toast.makeText(this, "密码不能为空", Toast.LENGTH_LONG).show();
				return;
			}
			if (!isNumber) {
				Toast.makeText(this, "电话号码格式不正确", Toast.LENGTH_LONG).show();
				return;
			}
			if (password.length() < 6) {
				Toast.makeText(this, "密码长度应不小于6位", Toast.LENGTH_LONG).show();
				return;
			}
			// 校验通过
			// 进行网络请求
			// 进行数据加密处理

			RequestParams params = new RequestParams();
			params.addBodyParameter("account", mobileNumber);
			params.addBodyParameter("password", password);
			// params.addBodyParameter("username","tangyichao"+new
			// Random().nextInt(1000));
			params.addBodyParameter("auto", "true");
			params.addBodyParameter("smartRedirect", "http://www.baidu.com");
			params.addBodyParameter("smartCallback", "go");
			// params.addBodyParameter("source","www.baidu");
			http.send(HttpMethod.POST, "http://dev.account.smartstudy.com/signin/handle", params, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException err, String arg) {
					// 登录失败
					Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_LONG).show();
				}

				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					if (responseInfo.getFirstHeader("Content-Type").getValue().equals("application/json; charset=utf-8")) {

						Message msg = Message.obtain();
						msg.what = 1000;
						msg.obj = responseInfo.result;
						handler.sendMessage(msg);
						GlobalParams.isLogin = true;
						ConfigUtils.setString(LoginActivity.this, "userName", mobileNumber);

						if (isSavePsw) {
							String passwordEncrypt = com.innobuddy.SmartStudy.utils.ThreeDes.dataDecrypt(password, Md5Utils.encode(ConstantValue.Keys.THERREDS).getBytes());
							ConfigUtils.setString(LoginActivity.this, "password", passwordEncrypt);
						} else {
							ConfigUtils.setString(LoginActivity.this, "password", "");
						}
						finish();// 关闭当前界面
					} else {
						Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_LONG).show();
					}
				}
			});
			//
			break;
		case R.id.tv_register:
			Toast.makeText(this, "注册", Toast.LENGTH_LONG).show();
			Intent intent = new Intent();
			intent.setClass(this, RegisterActivity.class);
			startActivity(intent);
			// overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
			break;
		case R.id.tv_find_psw:
			// Toast.makeText(this, "找回密码", Toast.LENGTH_LONG).show();
			Intent backPasswordIntent = new Intent();
			backPasswordIntent.setClass(this, BackPasswordActivity.class);
			startActivity(backPasswordIntent);
			break;
		case R.id.iv_login_number_clear:
			mEtLoginMobileNumber.setText("");
			break;
		case R.id.iv_login_psw_clear:
			mEtLoginPsw.setText("");
			break;
		default:
			break;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.et_login_mobile_number:
			String mobileNumber = mEtLoginMobileNumber.getText().toString().trim();
			if (hasFocus && mobileNumber.length() > 0) {
				mIvLoginNumberClear.setVisibility(View.VISIBLE);
			} else {
				mIvLoginNumberClear.setVisibility(View.GONE);
			}
			break;
		case R.id.et_login_psw:
			String password = mEtLoginPsw.getText().toString().trim();
			if (hasFocus && password.length() > 0) {
				mIvLoginPswClear.setVisibility(View.VISIBLE);
			} else {
				mIvLoginPswClear.setVisibility(View.GONE);
			}
			break;

		default:
			break;
		}

	}

}
