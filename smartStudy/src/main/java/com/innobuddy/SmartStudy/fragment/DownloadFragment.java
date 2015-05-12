package com.innobuddy.SmartStudy.fragment;

import java.io.File;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.DB.DBHelper;
import com.innobuddy.SmartStudy.adapter.CourseCell3Adapter;
import com.innobuddy.SmartStudy.utils.Md5Utils;
import com.innobuddy.download.utils.DStorageUtils;
import com.innobuddy.download.utils.FileUtils;
import com.innobuddy.download.utils.MyIntents;

/**
 * A simple {@link Fragment} subclass.
 * 
 */
public class DownloadFragment extends Fragment {
	
	public final static String DOWNLOAD_INFOS = "download_infos";
	public final static String PROGRESS_INFO = "progress_info";
	
	CourseCell3Adapter adapter;
	
    private MyReceiver mReceiver;
    
    JSONObject downloadObject;
	
	int longClickPostion;
    
	ListView listView;
	
	TextView emptyTextView;
	
	ExitReceiver exitReceiver;
	
	public DownloadFragment() {
		
	}
	
	@Override
	public void onDestroy() {
		
		if (adapter.cursor != null) {
			adapter.cursor.close();
			adapter.cursor = null;
		}
		
		if (downloadObject != null) {
						
			SharedPreferences downloadPreferences = getActivity().getSharedPreferences(DOWNLOAD_INFOS, 0);
			SharedPreferences.Editor editor = downloadPreferences.edit();
			editor.putString(PROGRESS_INFO, downloadObject.toString());
			editor.commit();
		}
		
		if (mReceiver != null) {
	        getActivity().unregisterReceiver(mReceiver);
		}
		
		if (exitReceiver != null) {
	        getActivity().unregisterReceiver(exitReceiver);
		}
		super.onDestroy();
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		SharedPreferences downloadPreferences = getActivity().getSharedPreferences(DOWNLOAD_INFOS, Context.MODE_PRIVATE);
		String jsonString = downloadPreferences.getString(PROGRESS_INFO, "");
		
        try {
			
    		if (jsonString == null || jsonString.length() == 0) {
    			downloadObject = new JSONObject();
			} else {
				downloadObject = new JSONObject(jsonString);
				if (downloadObject != null) {
					
					Iterator<?> iterator = downloadObject.keys();
					
					 while(iterator.hasNext()) {
						 String url = (String)iterator.next();
						 try {
							 JSONObject jsonObject = downloadObject.getJSONObject(url);
		                    jsonObject.put(MyIntents.DOWNLOAD_STATUS, MyIntents.Status.PAUSE);
						} catch (Exception e) {
						}
					 }
				}
			}
        	
		} catch (JSONException e) {
			
		}
		
		if (mReceiver == null) {
	        mReceiver = new MyReceiver();
	        IntentFilter filter = new IntentFilter();
	        filter.addAction("com.innobuddy.download.observe");
	        getActivity().registerReceiver(mReceiver, filter);
		}
		
		View view = inflater.inflate(R.layout.fragment_download, container, false);
		
		listView = (ListView)view.findViewById(R.id.listView1);
		
        emptyTextView = (TextView)view.findViewById(R.id.emptyTextView);
		
		Cursor cursor = DBHelper.getInstance(null).queryDownload();

		adapter = new CourseCell3Adapter(getActivity(), cursor, downloadObject);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
								
				adapter.cursor.moveToPosition(position);
				String url = adapter.cursor.getString(adapter.cursor.getColumnIndex("url"));

                JSONObject jsonObject = null;
                try {
                	
                    if (!TextUtils.isEmpty(url)) {
                        jsonObject = downloadObject.getJSONObject(url);
                    }

				} catch (JSONException e) {
				}
                
                if (jsonObject == null) {
                	jsonObject = new JSONObject();
                	try {
                        jsonObject.put(MyIntents.DOWNLOAD_TIME, 0L);
                        jsonObject.put(MyIntents.TOTAL_TIME, 0L);
                        jsonObject.put(MyIntents.LOADING_SIZE, 0L);
                        jsonObject.put(MyIntents.DOWNLOAD_STATUS, MyIntents.Status.WAITING);
                        downloadObject.put(url, jsonObject);
					} catch (JSONException e) {
					}
				}
                
                try {
                	
                	int status = jsonObject.getInt(MyIntents.DOWNLOAD_STATUS);
                    
        			Intent downloadIntent = new Intent("com.innobuddy.download.services.IDownloadService");        			
        			
                    if (status == MyIntents.Status.PAUSE || status == MyIntents.Status.ERROR) {
        				downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.CONTINUE);
        				downloadIntent.putExtra(MyIntents.URL, url);
        				getActivity().getApplicationContext().startService(downloadIntent);
                        jsonObject.put(MyIntents.DOWNLOAD_STATUS, MyIntents.Status.WAITING);
					} else {
						downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.PAUSE);
						downloadIntent.putExtra(MyIntents.URL, url);
						getActivity().getApplicationContext().startService(downloadIntent);
                        jsonObject.put(MyIntents.DOWNLOAD_STATUS, MyIntents.Status.PAUSE);
					}
                    
                    adapter.notifyDataSetChanged();
                    
				} catch (Exception e) {
				}
				
			}
			
		});
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				
				longClickPostion = position;
				
				new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("确定要删除该项吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialoginterface,
											int i) {
										
										adapter.cursor.moveToPosition(longClickPostion);
										
										String url = adapter.cursor.getString(adapter.cursor.getColumnIndex("url"));
										int id = adapter.cursor.getInt(adapter.cursor.getColumnIndex("id"));
										
										Intent downloadIntent = new Intent("com.innobuddy.download.services.IDownloadService");        			
										downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.DELETE);
										downloadIntent.putExtra(MyIntents.URL, url);
										getActivity().getApplicationContext().startService(downloadIntent);
										
//						                File file = new File(DStorageUtils.FILE_ROOT
//						                        + NetworkUtils.getFileNameFromUrl(url) + DownloadTask.TEMP_SUFFIX);
										File file = new File(DStorageUtils.FILE_ROOT + Md5Utils.encode(url));
						    				//删除文件加
//										if (file.exists())
//						                    file.delete();
										FileUtils.deleteDir(file);
										
										DBHelper.getInstance(null).deleteDownload(id);
										
					                    downloadObject.remove(url);
										
										if (adapter.cursor != null) {
											adapter.cursor.close();
											adapter.cursor = null;
										}

										adapter.cursor = DBHelper.getInstance(null).queryDownload();
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
		
		checkEmpty();
		
		if (exitReceiver == null) {
			exitReceiver = new ExitReceiver();
	        IntentFilter filter = new IntentFilter();
	        filter.addAction("applicationExit");
	        getActivity().registerReceiver(exitReceiver, filter);
		}
		
		
		/**
		 * 
		 */
		DownLoadReceiver downLoadReceiver=new DownLoadReceiver();
		IntentFilter downloadFilter=new IntentFilter();
		downloadFilter.addAction("download");
		getActivity().registerReceiver(downLoadReceiver, downloadFilter);
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
	
    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            handleIntent(intent);

        }

        private void handleIntent(Intent intent) {

            if (intent != null && intent.getAction().equals("com.innobuddy.download.observe")) {
            	
                int type = intent.getIntExtra(MyIntents.TYPE, -1);
                String url = intent.getStringExtra(MyIntents.URL);

                JSONObject jsonObject = null;
                try {
                	
                    if (!TextUtils.isEmpty(url)) {
                        jsonObject = downloadObject.getJSONObject(url);
                    }
                    
				} catch (JSONException e) {
				}
                
                try {
                	
                    if (jsonObject == null) {
                    	jsonObject = new JSONObject();
                    	try {
                            jsonObject.put(MyIntents.DOWNLOAD_TIME, 0L);
                            jsonObject.put(MyIntents.TOTAL_TIME, 0L);
                            jsonObject.put(MyIntents.LOADING_SIZE, 0L);
                            jsonObject.put(MyIntents.DOWNLOAD_STATUS, MyIntents.Status.WAITING);
    					} catch (JSONException e) {
    					}
                        downloadObject.put(url, jsonObject);
    				}
                    
				} catch (Exception e) {
					
				}
                
                switch (type) {
                    case MyIntents.Types.ADD:

                    	boolean isPaused = intent.getBooleanExtra(MyIntents.IS_PAUSED, false);
                    	try {
                            if (isPaused) {
                                jsonObject.put(MyIntents.DOWNLOAD_STATUS, MyIntents.Status.PAUSE);
							}
						} catch (JSONException e) {
						}
                        
                		if (adapter.cursor != null) {
                			adapter.cursor.close();
                			adapter.cursor = null;
                		}

                		adapter.cursor = DBHelper.getInstance(null).queryDownload();
                    	adapter.notifyDataSetChanged();
                        
                    	checkEmpty();
                    	
                        break;
                    case MyIntents.Types.COMPLETE:
                    	
                        if (!TextUtils.isEmpty(url)) {
                        	
                        	Cursor cursor = DBHelper.getInstance(null).queryDownload(url);
                        	if (cursor != null) {
                            	DBHelper.getInstance(null).insertOffline(cursor);
                            	cursor.close();
                            	cursor = null;
							}
                        	
                        	DBHelper.getInstance(null).deleteDownload(url);
                        	
		                    downloadObject.remove(url);
                        	
                    		if (adapter.cursor != null) {
                    			adapter.cursor.close();
                    			adapter.cursor = null;
                    		}

                    		adapter.cursor = DBHelper.getInstance(null).queryDownload();
                        	
                        	adapter.notifyDataSetChanged();
                        	
                        	checkEmpty();
                        	
                            Intent nofityIntent = new Intent("downloadFinished");
                            getActivity().sendBroadcast(nofityIntent);
                            
                        }
                        
                        break;
                    case MyIntents.Types.PROCESS:
                        long downloadTime = intent.getLongExtra(MyIntents.DOWNLOAD_TIME, 0L);
                        long totalTime = intent.getLongExtra(MyIntents.TOTAL_TIME, 0L);
                		long download=intent.getLongExtra(MyIntents.LOADING_SIZE, 0L);
                        try {
                    		int status = jsonObject.getInt(MyIntents.DOWNLOAD_STATUS);
                    		if (status != MyIntents.Status.PAUSE) {
                                jsonObject.put(MyIntents.DOWNLOAD_STATUS, MyIntents.Status.DOWNLOADING);
							}
                            jsonObject.put(MyIntents.DOWNLOAD_TIME, downloadTime);
                            jsonObject.put(MyIntents.TOTAL_TIME, totalTime);   
                            jsonObject.put(MyIntents.LOADING_SIZE, download);
						} catch (JSONException e) {
							
						}
                                                
                		adapter.updateStatus(url);//更新进度
                		
                        break;
                               		
     case MyIntents.Types.ERROR:
                        
                    	try {
                   		int status = jsonObject.getInt(MyIntents.DOWNLOAD_STATUS);
                    		if (status == MyIntents.Status.WAITING || status == MyIntents.Status.DOWNLOADING) {
                                jsonObject.put(MyIntents.DOWNLOAD_STATUS, MyIntents.Status.ERROR);
							}
						} catch (JSONException e) {
							
						}

                		adapter.updateStatus(url);
                        
                        break;
                    default:
                        break;
                }
            }
        }
        
    }
    
    
	public class ExitReceiver extends BroadcastReceiver {
		
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals("applicationExit")) {
            	
        		if (downloadObject != null) {
					
        			SharedPreferences downloadPreferences = getActivity().getSharedPreferences(DOWNLOAD_INFOS, 0);
        			SharedPreferences.Editor editor = downloadPreferences.edit();
        			editor.putString(PROGRESS_INFO, downloadObject.toString());
        			editor.commit();
        		}

            }
        }
	}
	public class DownLoadReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			//下载进度接受
			 if (intent != null && intent.getAction().equals("download")) {
				String loading= intent.getStringExtra("loading");
				System.out.println("XXXXXX"+loading);
				//Toast.makeText(DownloadFragment.this.getActivity(), "OK", Toast.LENGTH_LONG).show();
			 }
		}
		
	}
	
//	  private static void doDeleteEmptyDir(String dir) {
//	        boolean success = (new File(dir)).delete();
//	        if (success) {
//	            System.out.println("Successfully deleted empty directory: " + dir);
//	        } else {
//	            System.out.println("Failed to delete empty directory: " + dir);
//	        }
//	    }



}
