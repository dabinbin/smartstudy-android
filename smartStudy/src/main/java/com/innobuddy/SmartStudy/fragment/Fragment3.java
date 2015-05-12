package com.innobuddy.SmartStudy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.ui.TabFragmentIndicator;
import com.innobuddy.SmartStudy.ui.TabFragmentIndicator.OnTabClickListener;
import com.umeng.analytics.MobclickAgent;

public class Fragment3 extends Fragment implements OnTabClickListener {
	
    ViewPager viewPager;
    TabFragmentIndicator tabFragmentIndicator;
    
    OfflineFragment offlineFragment;
    DownloadFragment downloadFragment;
    
    View rootView;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
//		return super.onCreateView(inflater, container, savedInstanceState);
		
		if (rootView == null) {
			View view = inflater.inflate(R.layout.fragment3, container, false);
			rootView = view;
			viewPager = (ViewPager)view.findViewById(R.id.viewPager);
	        tabFragmentIndicator = (TabFragmentIndicator)view.findViewById(R.id.tabFragmentIndicator);
	        tabFragmentIndicator.setOnTabClickListener(this);
	                
	        if (offlineFragment == null) {
	        	offlineFragment = (OfflineFragment)OfflineFragment.instantiate(getActivity(), OfflineFragment.class.getName());
			}
	        
	        if (downloadFragment == null) {
	        	downloadFragment = (DownloadFragment)DownloadFragment.instantiate(getActivity(), DownloadFragment.class.getName());
			}
	        
	        tabFragmentIndicator.addFragment(0, offlineFragment);
	        tabFragmentIndicator.addFragment(1, downloadFragment);

	        tabFragmentIndicator.setTabContainerView(R.layout.offline_tabindicator);
	        tabFragmentIndicator.setTabSliderView(R.layout.tab_slider);
	        tabFragmentIndicator.setViewPager(viewPager);

		} else {
			
			ViewGroup parent = (ViewGroup)rootView.getParent();
			if (parent != null) {
				parent.removeView(rootView);
			}
			
		}
		
		return rootView;
		
	}
	
	public interface  OnOfflineListener {
		public void onOffline();
	}

	@Override
	public void onTabClick(View v) {
		// TODO Auto-generated method stub
		
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("MainScreen"); //统计页面
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("MainScreen"); 
	}
	
    
}
