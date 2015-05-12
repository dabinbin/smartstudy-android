package com.innobuddy.SmartStudy.adapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.Header;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.activity.CourseDetailActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
  
public class SeparatedListAdapter extends BaseAdapter {  
  
    public final Map<String, Adapter> sections = new LinkedHashMap<String, Adapter>();  
    public final ArrayAdapter<String> headers;  
    public final static int TYPE_SECTION_HEADER = 0;  
	Context _context;
	ArrayList<Integer> ids;
	ProgressDialog dialog;
	
	String tempName;

    public SeparatedListAdapter(Context context) {
    	_context = context;
        headers = new ArrayAdapter<String>(context, R.layout.course_cell_header, R.id.textView2);
        ids = new ArrayList<Integer>();
    }  
  
    public void addSection(String section, int id, Adapter adapter) {  
        this.headers.add(section);  
        this.sections.put(section, adapter);  
        ids.add(id);
    }  
  
    public Object getItem(int position) {  
        for (Object section : this.sections.keySet()) {  
            Adapter adapter = sections.get(section);  
            int size = adapter.getCount() + 1;  
  
            // check if position inside this section  
            if (position == 0)  
                return section;  
            if (position < size)  
                return adapter.getItem(position - 1);  
  
            // otherwise jump into next section  
            position -= size;  
        }  
        return null;  
    }  
  
    public int getCount() {  
        // total together all sections, plus one for each section header  
        int total = 0;  
        for (Adapter adapter : this.sections.values())  
            total += adapter.getCount() + 1;  
        return total;  
    }  
  
    public int getViewTypeCount() {  
        // assume that headers count as one, then total all sections  
        int total = 1;  
        for (Adapter adapter : this.sections.values())  
            total += adapter.getViewTypeCount();  
        return total;  
    }  
  
    public int getItemViewType(int position) {  
        int type = 1;  
        for (Object section : this.sections.keySet()) {  
            Adapter adapter = sections.get(section);  
            int size = adapter.getCount() + 1;  
  
            // check if position inside this section  
            if (position == 0)  
                return TYPE_SECTION_HEADER;  
            if (position < size)  
                return type + adapter.getItemViewType(position - 1);  
  
            // otherwise jump into next section  
            position -= size;  
            type += adapter.getViewTypeCount();  
        }  
        return -1;  
    }  
  
    public boolean areAllItemsSelectable() {  
        return false;  
    }  
  
    public boolean isEnabled(int position) {
    	return false;
//        return (getItemViewType(position) != TYPE_SECTION_HEADER);
    }
  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        int sectionnum = 0;  
        for (Object section : this.sections.keySet()) {  
            Adapter adapter = sections.get(section);  
            int size = adapter.getCount() + 1;  
  
            // check if position inside this section  
            if (position == 0) {
            	View view = headers.getView(sectionnum, convertView, parent);
            	
            	TextView courseTextView = (TextView)view.findViewById(R.id.textView2);
            	courseTextView.setTag(sectionnum);
            	courseTextView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {

						goDetail(ids.get((Integer)v.getTag()), headers.getItem((Integer)v.getTag()));
						
					}
				});

            	TextView moreTextView = (TextView)view.findViewById(R.id.textView3);
            	moreTextView.setTag(sectionnum);
            	moreTextView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {

						goDetail(ids.get((Integer)v.getTag()), headers.getItem((Integer)v.getTag()));
						
					}
				});
            	
                return view;  
            }
            else if (position < size)  
                return adapter.getView(position - 1, convertView, parent);  
  
            // otherwise jump into next section  
            position -= size;  
            sectionnum++;  
        }  
        return null;  
    }  
  
    public void goDetail(int id, String name) {
    	
    	tempName = name;
    	
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
        
        dialog = new ProgressDialog(_context, 2);
		dialog.setMessage("正在加载中…");
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.show();
        
	AsyncHttpClient client = new AsyncHttpClient();
	client.get("http://api.smartstudy.com/products?f=json&c=" + id, new AsyncHttpResponseHandler() {
		
		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			
			if (dialog != null) {
				dialog.dismiss();
				dialog = null;
			}
			
			String string = new String(arg2);
						
			Intent intent = new Intent();
			intent.putExtra("json", string);
			intent.putExtra("name", tempName);
			intent.setClass(_context, CourseDetailActivity.class);
			_context.startActivity(intent);
			
		}
		
		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
			
			if (dialog != null) {
				dialog.dismiss();
				dialog = null;
			}
			
		}
		
	});
    	
    }
    
    @Override  
    public long getItemId(int position) {  
        return position;  
    }
    
	public interface  OnMoreListener {
		public void onMore(int i);
	}
  
}  
