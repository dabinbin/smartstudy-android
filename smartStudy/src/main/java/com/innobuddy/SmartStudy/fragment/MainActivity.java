package com.innobuddy.SmartStudy.fragment;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.DB.DBHelper;
import com.innobuddy.download.utils.DStorageUtils;
import com.innobuddy.download.utils.MyIntents;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.umeng.analytics.MobclickAgent;
//import android.support.v13.app.FragmentTabHost;

public class MainActivity extends FragmentActivity {

	FragmentTabHost mTabHost;
	RadioGroup mTabRg;

	private static final Class<?>[] fragments = { Fragment1.class,
			Fragment2.class, Fragment3.class, Fragment4.class };

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);

		File cacheDir = StorageUtils.getOwnCacheDirectory(
				getApplicationContext(), "ImageLoader/Cache");

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.memoryCacheExtraOptions(720, 1280)
				// default = device screen dimensions
				.diskCacheExtraOptions(720, 1280, null)
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024)
				.memoryCacheSizePercentage(13)
				// default
				.diskCache(new UnlimitedDiscCache(cacheDir))
				// default
				.diskCacheSize(50 * 1024 * 1024)
				.diskCacheFileCount(100)
				.diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
				// default
				.imageDownloader(
						new BaseImageDownloader(getApplicationContext())) // default
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
				.build();
		ImageLoader.getInstance().init(config);

		setContentView(R.layout.activity_main);
		initView();

		// getActionBar().setBackgroundDrawable(new ColorDrawable(0xff54c4ff));

		DBHelper.getInstance(getApplicationContext());

		if (!DStorageUtils.isSDCardPresent()) {
			Toast.makeText(this, "未发现SD卡", Toast.LENGTH_LONG).show();
			return;
		}

		if (!DStorageUtils.isSdCardWrittenable()) {
			Toast.makeText(this, "SD卡不能读写", Toast.LENGTH_LONG).show();
			return;
		}

		try {
			DStorageUtils.mkdir();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Intent downloadIntent = new Intent(
				"com.innobuddy.download.services.IDownloadService");
		downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.START);
		getApplicationContext().startService(downloadIntent);

		// downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.STOP);
		// getApplicationContext().startService(downloadIntent);

	}

	private void initView() {

		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		// 得到fragment的个数
		int count = fragments.length;
		for (int i = 0; i < count; i++) {
			// 为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(i + "").setIndicator(i + "");
			// 将Tab按钮添加进Tab选项卡中
			mTabHost.addTab(tabSpec, fragments[i], null);
		}

		mTabRg = (RadioGroup) findViewById(R.id.tab_rg_menu);
		mTabRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.tab_rb_1:
					mTabHost.setCurrentTab(0);
					break;
				case R.id.tab_rb_2:
					mTabHost.setCurrentTab(1);
					break;
				case R.id.tab_rb_3:
					mTabHost.setCurrentTab(2);
					break;
				case R.id.tab_rb_4:
					mTabHost.setCurrentTab(3);
					break;
				default:
					break;
				}
			}
		});

		mTabHost.setCurrentTab(0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void finish() {

		Intent downloadIntent = new Intent(
				"com.innobuddy.download.services.IDownloadService");
		downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.STOP);
		getApplicationContext().startService(downloadIntent);

		super.finish();
	}

	@Override
	public void onDestroy() {

		Intent downloadIntent = new Intent(
				"com.innobuddy.download.services.IDownloadService");
		downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.STOP);
		getApplicationContext().startService(downloadIntent);

		super.onDestroy();

	}

	/**
	 * 菜单、返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click(); // 调用双击退出函数
		}
		return false;
	}

	/**
	 * 双击退出函数
	 */
	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {

			// Intent nofityIntent = new Intent("applicationExit");
			// sendBroadcast(nofityIntent);

			finish();
			 System.exit(0);
		}
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);//友盟统计
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this); // 友盟统计
	}

}
