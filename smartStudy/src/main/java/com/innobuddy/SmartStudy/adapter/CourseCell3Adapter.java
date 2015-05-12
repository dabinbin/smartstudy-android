package com.innobuddy.SmartStudy.adapter;

import java.io.File;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.utils.Md5Utils;
import com.innobuddy.SmartStudy.utils.Utilitys;
import com.innobuddy.download.utils.ConfigUtils;
import com.innobuddy.download.utils.DStorageUtils;
import com.innobuddy.download.utils.FileUtils;
import com.innobuddy.download.utils.MyIntents;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CourseCell3Adapter extends BaseAdapter {

	LayoutInflater _inflater;
	Context _context;
	public Cursor cursor;
	JSONObject downloadObject;
	HashMap<String, CourseCell3Holder> hashMap;

	public CourseCell3Adapter(Context context, Cursor c, JSONObject d) {
		_inflater = LayoutInflater.from(context);
		_context = context;
		cursor = c;
		downloadObject = d;
		hashMap = new HashMap<String, CourseCell3Adapter.CourseCell3Holder>();
	}

	@Override
	public int getCount() {
		if (cursor != null) {
			return cursor.getCount();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		CourseCell3Holder holder;

		if (convertView == null) {

			convertView = _inflater.inflate(R.layout.course_cell3, null);

			holder = new CourseCell3Holder();
			holder.nameTextView = (TextView) convertView.findViewById(R.id.textView1);
			holder.progressTextView = (TextView) convertView.findViewById(R.id.textView2);
			holder.totalSizeTextView = (TextView) convertView.findViewById(R.id.textView3);
			holder.imageView = (ImageView) convertView.findViewById(R.id.imageView1);
			holder.statusTextView = (TextView) convertView.findViewById(R.id.textView4);
			holder.statusImageView = (ImageView) convertView.findViewById(R.id.imageView2);
			convertView.setTag(holder);

		} else {
			holder = (CourseCell3Holder) convertView.getTag();
		}

		if (cursor != null) {

			cursor.moveToPosition(position);
			ImageLoader.getInstance().displayImage(cursor.getString(cursor.getColumnIndex("poster")), holder.imageView, Utilitys.defaultOptions);
			holder.nameTextView.setText(cursor.getString(cursor.getColumnIndex("name")));

			String url = cursor.getString(cursor.getColumnIndex("url"));
			hashMap.put(url, holder);

			updateStatus(url);

		}

		return convertView;
	}

	public void updateStatus(String url) {
		if (url != null) {
			CourseCell3Holder holder = hashMap.get(url);

			if (holder == null) {
				return;
			}

			long downloadTime = 0;
			long totalTime = 0;
			int status = 0;
			long download = 0;
			try {

				if (downloadObject != null) {

					JSONObject jsonObject = downloadObject.getJSONObject(url);

					if (jsonObject != null) {
						downloadTime = jsonObject.getLong(MyIntents.DOWNLOAD_TIME);
						totalTime = jsonObject.getLong(MyIntents.TOTAL_TIME);
						status = jsonObject.getInt(MyIntents.DOWNLOAD_STATUS);
						download = jsonObject.getLong(MyIntents.LOADING_SIZE);
					}

				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (totalTime > 0) {
				holder.progressTextView.setText((int) Math.ceil(100.0 * downloadTime / totalTime) + "%");
				// holder.totalSizeTextView.setText(DStorageUtils.size(totalSize));
				if (download != 0) {
					holder.totalSizeTextView.setText("下载" + DStorageUtils.size(download));
				}
			} else {
				downloadTime=ConfigUtils.getLong(_context, url+"downloadTime");
				totalTime=ConfigUtils.getLong(_context, url+"totalTime");
				//download=ConfigUtils.getLong(_context, url + "download"); 
				String path = DStorageUtils.FILE_ROOT + Md5Utils.encode(url) + "/";
				File file = new File(path);
				long downloadSize=0L;
				try {
					downloadSize = FileUtils.getFileSize(file);
					//String str = DStorageUtils.size(totelSize);
					//System.out.println(str);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (totalTime > 0) {
					holder.progressTextView.setText((int) Math.ceil(100.0 * downloadTime / totalTime) + "%");
					// holder.totalSizeTextView.setText(DStorageUtils.size(totalSize));
					if (download != 0) {
						holder.totalSizeTextView.setText("下载" + DStorageUtils.size(downloadSize));
					}
				}else{
				holder.progressTextView.setText("0%");
				}
				
			}
			if (holder.progressTextView.getText().toString().trim().equals("0%")) {
				holder.totalSizeTextView.setText("");
			}

			if (status == MyIntents.Status.WAITING) {
				// holder.statusTextView.setText("等待中");
				// holder.statusImageView.setImageResource(R.drawable.download_waiting);
				holder.statusTextView.setText("下载中");
				holder.statusImageView.setImageResource(R.drawable.download_downloading);
			} else if (status == MyIntents.Status.DOWNLOADING) {
				holder.statusTextView.setText("下载中");
				holder.statusImageView.setImageResource(R.drawable.download_downloading);
			} else if (status == MyIntents.Status.PAUSE) {
				holder.statusTextView.setText("暂停");
				holder.statusImageView.setImageResource(R.drawable.download_paused);
			} else if (status == MyIntents.Status.ERROR) {
				holder.statusTextView.setText("失败");
				holder.statusImageView.setImageResource(R.drawable.download_error);
			}

		}
	}

	private class CourseCell3Holder {
		TextView nameTextView;
		TextView progressTextView;
		TextView totalSizeTextView;
		TextView statusTextView;
		ImageView statusImageView;
		ImageView imageView;
	}

}
