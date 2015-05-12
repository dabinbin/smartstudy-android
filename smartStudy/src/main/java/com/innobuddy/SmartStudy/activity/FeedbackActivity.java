package com.innobuddy.SmartStudy.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.innobuddy.SmartStudy.R;

public class FeedbackActivity extends BaseActivity {

	private Button mBtnSubmit;
	private EditText mEtContent;
	private EditText mEtContactWay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		mBtnSubmit = (Button) findViewById(R.id.btn_submit);
		mEtContent = (EditText) findViewById(R.id.et_content);
		mEtContactWay = (EditText) findViewById(R.id.et_contact_way);
		mBtnSubmit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		String content = mEtContent.getText().toString().trim();
		String contactWay = mEtContactWay.getText().toString().trim();
		switch (v.getId()) {
		case R.id.btn_submit:
			if (TextUtils.isEmpty(content)) {
				Toast.makeText(this, "内容不能为空", Toast.LENGTH_LONG).show();
				return;
			}
			if (TextUtils.isEmpty(contactWay)) {
				Toast.makeText(this, "联系方式不能为空", Toast.LENGTH_LONG).show();
				return;
			}
			Toast.makeText(this, "xxxxx", Toast.LENGTH_LONG).show();
			break;

		default:
			break;
		}
	}

}
