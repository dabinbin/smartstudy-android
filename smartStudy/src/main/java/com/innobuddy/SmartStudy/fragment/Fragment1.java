package com.innobuddy.SmartStudy.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.adapter.CourseCellAdapter;
import com.innobuddy.SmartStudy.adapter.SeparatedListAdapter;
import com.innobuddy.SmartStudy.utils.Utilitys;
import com.innobuddy.download.utils.ConfigUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class Fragment1 extends Fragment {

	ListView listView;

	ProgressDialog dialog;

	private List<ImageView> imageViews = new ArrayList<ImageView>();

	private List<View> dots;
	private int lastPosition = 0;

	View rootView;

	JSONArray courseArray;

	Date lastDate;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnArticleSelectedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ImageView imageView;

		imageView = new ImageView(getActivity());
		imageView.setImageResource(R.drawable.course_focus);
		// imageView.setScaleType(ScaleType.CENTER_INSIDE);
		imageViews.add(imageView);

		imageView = new ImageView(getActivity());
		imageView.setImageResource(R.drawable.course_focus);
		// imageView.setScaleType(ScaleType.CENTER_INSIDE);
		imageViews.add(imageView);

		imageView = new ImageView(getActivity());
		imageView.setImageResource(R.drawable.course_focus);
		// imageView.setScaleType(ScaleType.CENTER_INSIDE);
		imageViews.add(imageView);

	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (rootView == null) {

			View view = inflater.inflate(R.layout.fragment1, container, false);
			rootView = view;

			listView = (ListView) view.findViewById(R.id.listView1);
			listView.setDividerHeight(0);

			DisplayMetrics displayMetrics = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay()
					.getMetrics(displayMetrics);
			int width = displayMetrics.widthPixels;
			int height = (int) Math.ceil(width * 7.0 / 16.0);

			View headerView = (View) inflater.inflate(R.layout.course_header,
					null);
			headerView.setLayoutParams(new ListView.LayoutParams(
					ListView.LayoutParams.MATCH_PARENT, height));

			ViewPager viewPager = (ViewPager) headerView
					.findViewById(R.id.viewPager);

			dots = new ArrayList<View>();

			dots.add(headerView.findViewById(R.id.v_dot0));
			dots.add(headerView.findViewById(R.id.v_dot1));
			dots.add(headerView.findViewById(R.id.v_dot2));

			viewPager
					.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

						@Override
						public void onPageSelected(int arg0) {

							dots.get(lastPosition).setBackgroundResource(
									R.drawable.dot_normal);
							dots.get(arg0).setBackgroundResource(
									R.drawable.dot_focused);
							lastPosition = arg0;

						}

						@Override
						public void onPageScrolled(int arg0, float arg1,
								int arg2) {

						}

						@Override
						public void onPageScrollStateChanged(int arg0) {

						}
					});

			viewPager.setAdapter(new HeaderPagerAdapter());

			listView.addHeaderView(headerView, null, false);

		} else {

			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (parent != null) {
				parent.removeView(rootView);
			}

		}

		loadData();

		return rootView;
	}

	public void loadData() {

		Date date = new Date();

		if (courseArray == null || lastDate == null
				|| (date.getTime() - lastDate.getTime() > 1000 * 60 * 60 * 3)) {

		} else {
			return;
		}

		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}

		dialog = new ProgressDialog(getActivity(), 2);
		dialog.setMessage("正在加载中…");
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.show();

		AsyncHttpClient client = new AsyncHttpClient();
		client.get("http://api.smartstudy.com/products?f=json&l=4",
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

						lastDate = new Date();

						String str = new String(arg2);

						Log.v("onSuccess", str);
						ConfigUtils.setString(getActivity(), "homeJSON", str);
						loadingHomeDate(str);

					}

					private void loadingHomeDate(String str) {
						try {

							JSONObject rootObject = new JSONObject(str);
							JSONArray banner = rootObject
									.getJSONArray("banner");

							if (banner != null
									&& banner.length() >= imageViews.size()) {
								for (int i = 0; i < imageViews.size(); i++) {
									ImageLoader.getInstance().displayImage(
											banner.getString(i),
											imageViews.get(i),
											Utilitys.focusOptions);
								}
							}

							courseArray = rootObject.getJSONArray("courses");

							SeparatedListAdapter adapter = new SeparatedListAdapter(
									getActivity());

							for (int i = 0; i < courseArray.length(); i++) {

								JSONObject courseObject = courseArray
										.getJSONObject(i);

								JSONArray videoArray = courseObject
										.getJSONArray("videos");

								ArrayList<ArrayList<JSONObject>> arrayList = new ArrayList<ArrayList<JSONObject>>();

								if (videoArray.length() >= 4) {

									ArrayList<JSONObject> arrayList1 = new ArrayList<JSONObject>();
									arrayList1.add(videoArray.getJSONObject(0));
									arrayList1.add(videoArray.getJSONObject(1));

									arrayList.add(arrayList1);

									ArrayList<JSONObject> arrayList2 = new ArrayList<JSONObject>();
									arrayList2.add(videoArray.getJSONObject(2));
									arrayList2.add(videoArray.getJSONObject(3));

									arrayList.add(arrayList2);

								}

								CourseCellAdapter courseCellAdapter = new CourseCellAdapter(
										getActivity(), arrayList);

								adapter.addSection(
										courseObject.getString("name"),
										courseObject.getInt("id"),
										courseCellAdapter);

							}

							listView.setAdapter(adapter);
							adapter.notifyDataSetChanged();

						} catch (Exception e) {
						}

						if (dialog != null) {
							dialog.dismiss();
							dialog = null;
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						String str = ConfigUtils.getString(getActivity(),
								"homeJSON");
						if (TextUtils.isEmpty(str)) {
							if (arg2 != null) {
								Log.v("onFailure", new String(arg2));
							}

							if (dialog != null) {
								dialog.dismiss();
								dialog = null;
							}
						} else {
							loadingHomeDate(str);
						}

					}

				});

	}

	public class HeaderPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {

			try {

				if (imageViews.get(arg1).getParent() == null) {
					((ViewPager) arg0).addView(imageViews.get(arg1));
				} else {
					((ViewGroup) imageViews.get(arg1).getParent())
							.removeView(imageViews.get(arg1));
					((ViewPager) arg0).addView(imageViews.get(arg1));
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			return imageViews.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("MainScreen"); // 统计页面
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("MainScreen");
	}

}
