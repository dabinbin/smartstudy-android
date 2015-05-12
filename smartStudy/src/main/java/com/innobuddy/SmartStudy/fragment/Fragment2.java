package com.innobuddy.SmartStudy.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.activity.CollectActivity;
import com.innobuddy.SmartStudy.activity.RecentWatchActivity;
import com.innobuddy.SmartStudy.activity.user.LoginActivity;
import com.innobuddy.SmartStudy.global.GlobalParams;
import com.umeng.analytics.MobclickAgent;

public class Fragment2 extends Fragment {

	private String[] listTitle = { "离线观看", "我的收藏", "近期观看", "登陆注册" };

	private int[] listImage = { R.drawable.mine_offline, R.drawable.mine_connect, R.drawable.mine_recent_watch, R.drawable.mine_recent_watch };

	SimpleAdapter adapter = null;

	ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();

	View view = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// return super.onCreateView(inflater, container, savedInstanceState);

		view = inflater.inflate(R.layout.fragment2, container, false);

		ListView listView = (ListView) view.findViewById(R.id.listView1);

		if (arrayList.size() == 0) {
			for (int i = 0; i < listTitle.length; i++) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("image", listImage[i]);
				item.put("title", listTitle[i]);
				arrayList.add(item);
			}
		}

		adapter = new SimpleAdapter(getActivity(), arrayList, R.layout.mine_cell, new String[] { "image", "title" }, new int[] { R.id.image, R.id.title });
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

				if (position == 0) {
					((MainActivity) getActivity()).mTabHost.setCurrentTab(2);
					((MainActivity) getActivity()).mTabRg.check(R.id.tab_rb_3);
				} else if (position == 1) {

					Intent intent = new Intent();
					intent.setClass(getActivity(), CollectActivity.class);
					startActivity(intent);

				} else if (position == 2) {

					Intent intent = new Intent();
					intent.setClass(getActivity(), RecentWatchActivity.class);
					startActivity(intent);

				} else if (position == 3) {
					if (GlobalParams.isLogin) {
						// Toast.makeText(getActivity(), "已经登陆",
						// Toast.LENGTH_LONG).show();
						// 退出登陆实现 调用接口
						AlertDialog.Builder dialog = new Builder(getActivity());
						dialog.setTitle("提示");
						dialog.setMessage("你确定要退出登陆吗");
						dialog.setPositiveButton("确定", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								GlobalParams.isLogin = false;
								listTitle[3] = "登陆注册";
								arrayList.clear();
								if (arrayList.size() == 0) {
									for (int i = 0; i < listTitle.length; i++) {
										Map<String, Object> item = new HashMap<String, Object>();
										item.put("image", listImage[i]);
										item.put("title", listTitle[i]);
										arrayList.add(item);
									}
								}

								adapter.notifyDataSetChanged();

							}
						});
						dialog.setNegativeButton("取消", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								
							}
						});
						dialog.show();

					} else {
						Intent intent = new Intent();
						intent.setClass(getActivity(), LoginActivity.class);
						startActivity(intent);

					}

				}

			}
		});
		return view;
	}

	public void onResume() {
		super.onResume();
		if (GlobalParams.isLogin) {
			listTitle[3] = "退出登录";
			arrayList.clear();
			if (arrayList.size() == 0) {
				for (int i = 0; i < listTitle.length; i++) {
					Map<String, Object> item = new HashMap<String, Object>();
					item.put("image", listImage[i]);
					item.put("title", listTitle[i]);
					arrayList.add(item);
				}
			}
		}

		adapter.notifyDataSetChanged();
		MobclickAgent.onPageStart("MainScreen"); // 统计页面
	}

}
