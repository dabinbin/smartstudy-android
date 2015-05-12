package com.innobuddy.SmartStudy.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.DB.DBHelper;
import com.innobuddy.SmartStudy.adapter.CourseCell2Adapter;
import com.innobuddy.SmartStudy.utils.Utilitys;

public class CollectActivity extends Activity {

	PositionReceiver positionReceiver;
	
	CourseCell2Adapter adapter;
	
	int longClickPostion;
	
	ListView listView;
	TextView emptyTextView;
	
	@Override
	public void finish() {
		
		if (adapter.cursor != null) {
			adapter.cursor.close();
			adapter.cursor = null;
		}
		
		if (positionReceiver != null) {
			unregisterReceiver(positionReceiver);
	        positionReceiver = null;
		}

		super.finish();
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect);
		
		listView = (ListView)findViewById(R.id.listView1);        
        
        emptyTextView = (TextView)findViewById(R.id.emptyTextView);        
		
		Cursor cursor = DBHelper.getInstance(null).queryCollect();
        
		adapter = new CourseCell2Adapter(this, cursor);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				
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
								
				Utilitys.getInstance().playVideo(jsonObject, CollectActivity.this);
				
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				
				longClickPostion = position;
				
				new AlertDialog.Builder(CollectActivity.this).setTitle("提示").setMessage("确定要删除该项吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialoginterface,
											int i) {
										
										adapter.cursor.moveToPosition(longClickPostion);
										int id = adapter.cursor.getInt(adapter.cursor.getColumnIndex("id"));
										DBHelper.getInstance(null).deleteCollect(id);
										
										if (adapter.cursor != null) {
											adapter.cursor.close();
											adapter.cursor = null;
										}

										adapter.cursor = DBHelper.getInstance(null).queryCollect();
								        adapter.notifyDataSetChanged();
										
										checkEmpty();

									}
								})
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialoginterface,
											int i) {
										
									}
								})
								.show();

				return true;
			}
		});

		if (positionReceiver == null) {
			positionReceiver = new PositionReceiver();
	        IntentFilter filter = new IntentFilter();
	        filter.addAction("videoPositionChanged");
	        registerReceiver(positionReceiver, filter);
		}
		
		checkEmpty();
		
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.collect, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_clear) {
			
			new AlertDialog.Builder(CollectActivity.this).setTitle("提示").setMessage("确定要清空我的收藏吗？")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface,
										int i) {
									
									DBHelper.getInstance(null).deleteCollect();
									
									if (adapter.cursor != null) {
										adapter.cursor.close();
										adapter.cursor = null;
									}

									adapter.cursor = DBHelper.getInstance(null).queryCollect();
							        adapter.notifyDataSetChanged();
									
									checkEmpty();

								}
							})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface,
										int i) {
									
								}
							})
							.show();

			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public class PositionReceiver extends BroadcastReceiver {
		
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals("videoPositionChanged")) {
            	
        		if (adapter.cursor != null) {
        			adapter.cursor.close();
        			adapter.cursor = null;
        		}

        		adapter.cursor = DBHelper.getInstance(null).queryCollect();
            	adapter.notifyDataSetChanged();
            	
            	checkEmpty();
            	
            }
        }
		
	}

	
}
