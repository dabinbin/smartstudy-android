package com.innobuddy.SmartStudy.activity;



import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.fragment.CourseDetailFragment;
import com.innobuddy.SmartStudy.ui.TabFragmentIndicator;
import com.innobuddy.SmartStudy.ui.TabFragmentIndicator.OnTabClickListener;

public class CourseDetailActivity extends FragmentActivity implements OnTabClickListener {

    ViewPager viewPager;
    TabFragmentIndicator tabFragmentIndicator;
	
	ArrayList<ArrayList<JSONObject>> arrayList;
    
	JSONArray jsonArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_detail);
		
        String jsonString = getIntent().getStringExtra("json");
        
        if (jsonString != null) {
            try {
            	jsonArray = new JSONArray(jsonString);
    		} catch (JSONException e) {
    			e.printStackTrace();
    		}
		}
        
		viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabFragmentIndicator = (TabFragmentIndicator)findViewById(R.id.tabFragmentIndicator);

        if (jsonArray != null) {
        	
            if (jsonArray.length() == 2) {
            	
            	CourseDetailFragment courseDetailFragment1 = (CourseDetailFragment)CourseDetailFragment.instantiate(this, CourseDetailFragment.class.getName());
            	CourseDetailFragment courseDetailFragment2 = (CourseDetailFragment)CourseDetailFragment.instantiate(this, CourseDetailFragment.class.getName());

            	JSONObject jsonObject1 = null;
            	JSONObject jsonObject2 = null;

            	String name1 = null;
            	String name2 = null;
            	
            	try {
            		jsonObject1 = (JSONObject)jsonArray.getJSONObject(0);
            		jsonObject2 = (JSONObject)jsonArray.getJSONObject(1);
            		name1 = jsonObject1.getString("category_name");
            		name2 = jsonObject2.getString("category_name");
				} catch (JSONException e) {
				}
            	
            	courseDetailFragment1.jObject = jsonObject1;
            	courseDetailFragment2.jObject = jsonObject2;
            	
                tabFragmentIndicator.addFragment(0, courseDetailFragment1);
                tabFragmentIndicator.addFragment(1, courseDetailFragment2);
                tabFragmentIndicator.setTabContainerView(R.layout.course_detail_tabindicator);
                
                TextView textView1 = (TextView)findViewById(R.id.tab1_text);
                textView1.setText(name1);
                
                TextView textView2 = (TextView)findViewById(R.id.tab2_text);
                textView2.setText(name2);

                tabFragmentIndicator.setTabSliderView(R.layout.tab_slider);
                tabFragmentIndicator.setOnTabClickListener(this);
                tabFragmentIndicator.setViewPager(viewPager);

            } else if (jsonArray.length() == 1) {
            	
            	JSONObject jsonObject1 = null;

            	String name1 = null;
            	
            	try {
            		jsonObject1 = (JSONObject)jsonArray.getJSONObject(0);
            		name1 = jsonObject1.getString("category_name");
				} catch (JSONException e) {
				}
            	
            	CourseDetailFragment courseDetailFragment1 = (CourseDetailFragment)CourseDetailFragment.instantiate(this, CourseDetailFragment.class.getName());
            	courseDetailFragment1.jObject = jsonObject1;
            
                tabFragmentIndicator.addFragment(0, courseDetailFragment1);
                tabFragmentIndicator.setTabContainerView(R.layout.course_detail_tabindicator);
                
                TextView textView1 = (TextView)findViewById(R.id.tab1_text);
                textView1.setText(name1);
                
                TextView textView2 = (TextView)findViewById(R.id.tab2_text);
                textView2.setVisibility(View.GONE);
                
                tabFragmentIndicator.setOnTabClickListener(this);
                tabFragmentIndicator.setViewPager(viewPager);

    		}
		}
	
        String name = getIntent().getStringExtra("name");
        if (name != null) {
            getActionBar().setTitle(name);
		}
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.course_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onTabClick(View v) {
		
		if ((Integer)v.getTag() == 0) {
			
		} else {

		}
		
	}
	

	
}
