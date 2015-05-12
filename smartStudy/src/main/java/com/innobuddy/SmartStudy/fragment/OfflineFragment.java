package com.innobuddy.SmartStudy.fragment;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.DB.DBHelper;
import com.innobuddy.SmartStudy.adapter.CourseCell2Adapter;
import com.innobuddy.SmartStudy.utils.Md5Utils;
import com.innobuddy.SmartStudy.utils.Utilitys;
import com.innobuddy.download.utils.DStorageUtils;
import com.innobuddy.download.utils.FileUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * A simple {@link Fragment} subclass.
 * 
 */
public class OfflineFragment extends Fragment {

	FinishReceiver mFinishReceiver;

	PositionReceiver positionReceiver;

	CourseCell2Adapter adapter;

	int longClickPostion;

	ListView listView;
	TextView emptyTextView;

	public OfflineFragment() {
		// Required empty public constructor
	}

	@Override
	public void onDestroy() {

		if (adapter.cursor != null) {
			adapter.cursor.close();
			adapter.cursor = null;
		}

		if (mFinishReceiver != null) {
			getActivity().unregisterReceiver(mFinishReceiver);
			mFinishReceiver = null;
		}

		if (positionReceiver != null) {
			getActivity().unregisterReceiver(positionReceiver);
			positionReceiver = null;
		}

		super.onDestroy();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		View view = inflater.inflate(R.layout.fragment_offline, container, false);

		listView = (ListView) view.findViewById(R.id.listView1);

		emptyTextView = (TextView) view.findViewById(R.id.emptyTextView);

		Cursor cursor = DBHelper.getInstance(null).queryOffline();

		adapter = new CourseCell2Adapter(getActivity(), cursor);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

				Cursor cursor = adapter.cursor;
				cursor.moveToPosition(position);

				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("id", cursor.getInt(cursor.getColumnIndex("id")));
					jsonObject.put("name", cursor.getString(cursor.getColumnIndex("name")));
					jsonObject.put("poster", cursor.getString(cursor.getColumnIndex("poster")));
					jsonObject.put("url", cursor.getString(cursor.getColumnIndex("url")));
					jsonObject.put("cache_url", cursor.getString(cursor.getColumnIndex("cache_url")));
					jsonObject.put("hot", cursor.getInt(cursor.getColumnIndex("hot")));

				} catch (JSONException e) {
					e.printStackTrace();
				}

				Utilitys.getInstance().playVideo(jsonObject, getActivity());

			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				longClickPostion = position;

				new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("确定要删除该项吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {

						adapter.cursor.moveToPosition(longClickPostion);

						int id = adapter.cursor.getInt(adapter.cursor.getColumnIndex("id"));
						String url = adapter.cursor.getString(adapter.cursor.getColumnIndex("url"));

						DBHelper.getInstance(null).deleteOffline(id);

						File file = new File(DStorageUtils.FILE_ROOT + Md5Utils.encode(url));

						FileUtils.deleteDir(file);

						if (adapter.cursor != null) {
							adapter.cursor.close();
							adapter.cursor = null;
						}

						adapter.cursor = DBHelper.getInstance(null).queryOffline();
						adapter.notifyDataSetChanged();

						checkEmpty();

					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {

					}
				}).setNeutralButton("清空", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(getActivity(), "清空", Toast.LENGTH_LONG).show();

						//
						adapter.cursor = DBHelper.getInstance(null).queryOffline();
						adapter.notifyDataSetChanged();

						if (adapter.cursor != null) {
							if (adapter.cursor.moveToFirst()) {
								String url = adapter.cursor.getString(adapter.cursor.getColumnIndex("url"));
								System.out.println(url);
								File file = new File(DStorageUtils.FILE_ROOT + Md5Utils.encode(url));
								FileUtils.deleteDir(file);

							}
						}
						DBHelper.getInstance(null).deleteAlloffline();
						adapter.cursor = DBHelper.getInstance(null).queryOffline();
						adapter.notifyDataSetChanged();
						checkEmpty();
						if (adapter.cursor != null) {
							adapter.cursor.close();
							adapter.cursor = null;
						}

					}
				}).show();

				return true;
			}
		});

		if (mFinishReceiver == null) {
			mFinishReceiver = new FinishReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction("downloadFinished");
			getActivity().registerReceiver(mFinishReceiver, filter);
		}

		if (positionReceiver == null) {
			positionReceiver = new PositionReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction("videoPositionChanged");
			getActivity().registerReceiver(positionReceiver, filter);
		}

		checkEmpty();

		return view;
	}

	public void checkEmpty() {
		if (adapter.cursor.getCount() > 0) {
			emptyTextView.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		} else {
			emptyTextView.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		}
	}

	public class FinishReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null && intent.getAction().equals("downloadFinished")) {
				if (adapter.cursor != null) {
					adapter.cursor.close();
					adapter.cursor = null;
				}

				adapter.cursor = DBHelper.getInstance(null).queryOffline();
				adapter.notifyDataSetChanged();

				checkEmpty();

			}
		}

	}

	public class PositionReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null && intent.getAction().equals("videoPositionChanged")) {

				if (adapter.cursor != null) {
					adapter.cursor.close();
					adapter.cursor = null;
				}

				adapter.cursor = DBHelper.getInstance(null).queryOffline();
				adapter.notifyDataSetChanged();

				checkEmpty();

			}
		}

	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("MainScreen"); // 统计页面
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("MainScreen"); // 统计界面
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_clear) {

			new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("确定要清空最近观看吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialoginterface, int i) {

					DBHelper.getInstance(null).deleteRecentWatch();

					if (adapter.cursor != null) {
						adapter.cursor.close();
						adapter.cursor = null;
					}

					adapter.cursor = DBHelper.getInstance(null).queryRecentWatch();
					adapter.notifyDataSetChanged();

					checkEmpty();

				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialoginterface, int i) {

				}
			}).show();

			return true;
		} else if (id == R.id.homeAsUp) {
			// finish();
			return true;
		} else if (id == R.id.home) {
			// finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
