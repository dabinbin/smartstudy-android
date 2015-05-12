package com.innobuddy.SmartStudy.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.innobuddy.SmartStudy.R;

public class WebViewActivity extends BaseActivity {
	private String url;
	private WebView mWv;
	private ProgressBar mPb;
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		url = getIntent().getStringExtra("url");
		setContentView(R.layout.activity_webview);
		mWv = (WebView) findViewById(R.id.wv);
		mPb=(ProgressBar) findViewById(R.id.pb);
		mWv.setWebViewClient(new WebViewClient() {
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				handler.proceed();// 接受证书
			}
		});
		mWv.getSettings().setJavaScriptEnabled(true);
		mWv.getSettings().setDefaultTextEncodingName("gb2312");
		mWv.setWebChromeClient(new WebChromeClient());
		mWv.getSettings().setDomStorageEnabled(true);
		// myWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		// add h5 use database
		mWv.getSettings().setDatabaseEnabled(true);
		if (!TextUtils.isEmpty(url)) {
			mWv.loadUrl(url);
		}
		
	}

	@Override
	protected void onResume() {

		/**
		 * WebView加载过程
		 */
		mWv.setDownloadListener(new DownloadListener() {
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});

		mWv.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {

				super.onPageFinished(view, url);
				mPb.setVisibility(View.GONE);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);

				return true;
			}

		});
		WebSettings webSettings = mWv.getSettings(); // webView:
		// 类WebView的实例

		webSettings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		super.onResume();
	}

}
