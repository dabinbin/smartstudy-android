package com.innobuddy.SmartStudy.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.DB.DBHelper;
import com.innobuddy.SmartStudy.utils.Utilitys;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint({ "SimpleDateFormat", "InflateParams" }) 
public class CourseCell2Adapter extends BaseAdapter {

	LayoutInflater _inflater;
	Context _context;
	public Cursor cursor;
	
	public CourseCell2Adapter(Context context, Cursor c) {
		_inflater = LayoutInflater.from(context);
		_context = context;
		cursor = c;
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
		CourseCell2Holder holder;
		
		  if(convertView == null)
		    {
		      convertView = _inflater.inflate(R.layout.course_cell2, null);
		      holder = new CourseCell2Holder();
		      holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
		      holder.textView2 = (TextView) convertView.findViewById(R.id.textView2);
		      holder.imageView1 = (ImageView) convertView.findViewById(R.id.imageView1);
		      convertView.setTag(holder);
		    }
		  else {
		      holder = (CourseCell2Holder) convertView.getTag();
		  }
		
		  if (cursor != null && holder != null) {
			  cursor.moveToPosition(position);
			  ImageLoader.getInstance().displayImage(cursor.getString(cursor.getColumnIndex("poster")), holder.imageView1, Utilitys.defaultOptions);
			  holder.textView1.setText(cursor.getString(cursor.getColumnIndex("name")));
			  
			  int columnIndex = cursor.getColumnIndex("postion");
			  
			  if (columnIndex >= 0) {
				  DateFormat formatter = new SimpleDateFormat("mm:ss");
				  holder.textView2.setText(formatter.format(new Date(cursor.getInt(columnIndex))));
			  } else {
				  int id = cursor.getInt(cursor.getColumnIndex("id"));
				  Cursor c = DBHelper.getInstance(null).queryRecentWatch(id);
				  if (c != null && c.getCount() > 0) {
					  DateFormat formatter = new SimpleDateFormat("mm:ss");
					  holder.textView2.setText(formatter.format(new Date(c.getInt(c.getColumnIndex("postion")))));
				}
				  
				  if (c!= null) {
					c.close();
				}
			  }
			  
		}
		  
		return convertView;
	}
	
	  private class CourseCell2Holder
	  {
		    TextView textView1;
		    TextView textView2;
		    ImageView imageView1;
	  }

}
