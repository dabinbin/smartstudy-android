package com.innobuddy.SmartStudy.adapter;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.utils.Utilitys;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CourseCellAdapter extends BaseAdapter {
	LayoutInflater _inflater;
	ArrayList<ArrayList<JSONObject>> _list;
	Context _context;
		
	  public CourseCellAdapter(Context context, ArrayList<ArrayList<JSONObject>> list) {
		  _inflater = LayoutInflater.from(context);
		  _list = list;
		  _context = context;
		  }
	
	@Override
	public int getCount() {
		return _list.size();
	}

	@Override
	public Object getItem(int position) {
		return _list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@SuppressLint("InflateParams") 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		CourseCellHolder holder;
	    
	    if(convertView == null)
	    {
	      convertView = _inflater.inflate(R.layout.course_cell1, null);
	      holder = new CourseCellHolder();
	     // holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
	      holder.imageView1 = (ImageView) convertView.findViewById(R.id.imageView1);
	      //holder.textView2 = (TextView) convertView.findViewById(R.id.textView2);
	      holder.imageView2 = (ImageView) convertView.findViewById(R.id.imageView2);
	      
	        DisplayMetrics displayMetrics = new DisplayMetrics();
	        ((Activity)_context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
	        float density = displayMetrics.density;
	        if (density <= 0) {
	        	density = 1.5f;
			}
	        int width = (displayMetrics.widthPixels - (int)(24 * density)) / 2;

	      ViewGroup.LayoutParams layoutParams = holder.imageView1.getLayoutParams();
	      layoutParams.height = (int)Math.ceil(width * 9.0 / 16.0);
	      holder.imageView1.setLayoutParams(layoutParams);
	      holder.imageView2.setLayoutParams(layoutParams);
	      
	      convertView.setTag(holder);
	      
	    }
	    else
	    {
	      holder = (CourseCellHolder) convertView.getTag();
	    }
	
	    ArrayList<JSONObject> arrayList = _list.get(position);
	    
	    JSONObject object1 = arrayList.get(0);
	    JSONObject object2 = arrayList.get(1);
	    
	    holder.imageView1.setTag(object1);
	    holder.imageView2.setTag(object2);

	    try {
	    	
//	    	holder.textView1.setText(object1.getString("name"));
			ImageLoader.getInstance().displayImage(object1.getString("poster"), holder.imageView1, Utilitys.defaultOptions);
//	    	holder.textView2.setText(object2.getString("name"));
		    ImageLoader.getInstance().displayImage(object2.getString("poster"), holder.imageView2, Utilitys.defaultOptions);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	    	    
	    holder.imageView1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Utilitys.getInstance().playVideo((JSONObject)v.getTag(), _context);
				
			}
			
		});
	    
	    holder.imageView2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Utilitys.getInstance().playVideo((JSONObject)v.getTag(), _context);

			}
			
		});
	    
		return convertView;
	}
	
	  private class CourseCellHolder
	  {
	   // TextView textView1;
	    ImageView imageView1;
	   // TextView textView2;
	    ImageView imageView2;
	  }

}
