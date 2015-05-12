package com.innobuddy.SmartStudy.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.adapter.CourseCellAdapter;
import com.innobuddy.SmartStudy.utils.Utilitys;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link CourseDetailFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link CourseDetailFragment#newInstance} factory method to create an instance
 * of this fragment.
 * 
 */
public class CourseDetailFragment extends Fragment {

	public JSONObject jObject;
	
	public CourseDetailFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		View view = inflater.inflate(R.layout.fragment_course_detail, container,
				false);
		
		ListView listView = (ListView)view.findViewById(R.id.listView1);
		listView.setDividerHeight(0);

		try {
			
			JSONObject guideObject = jObject.getJSONObject("guide");

			if (guideObject != null) {
				ImageView imageView = new ImageView(getActivity());
				imageView.setImageResource(R.drawable.course_focus);
				
		        DisplayMetrics displayMetrics = new DisplayMetrics();
		        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		        int width = displayMetrics.widthPixels;
		        int height = (int)Math.ceil(width * 7.0 / 16.0);
				
				imageView.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, height));
				ImageLoader.getInstance().displayImage(guideObject.getString("poster"), imageView, Utilitys.focusOptions);
		        listView.addHeaderView(imageView, null, false);
		        
			}
			
		} catch (JSONException e) {
		}
		
		try {
			
			JSONArray videosArray = jObject.getJSONArray("videos");
			int row = videosArray.length() / 2;

			ArrayList<ArrayList<JSONObject>> arrayList = new ArrayList<ArrayList<JSONObject>>();
			
			for (int i = 0; i < row; i++) {
				ArrayList<JSONObject> arrayList2 = new ArrayList<JSONObject>();
				arrayList2.add(videosArray.getJSONObject(2 * i));
				arrayList2.add(videosArray.getJSONObject(2 * i + 1));
				arrayList.add(arrayList2);
			}
			
			CourseCellAdapter courseCellAdapter = new CourseCellAdapter(getActivity(), arrayList);
			
			listView.setAdapter(courseCellAdapter);
			
		} catch (JSONException e) {
			
		}
		
		
		return view;
	}


}
