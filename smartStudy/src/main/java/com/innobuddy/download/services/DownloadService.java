package com.innobuddy.download.services;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.widget.Toast;

import com.innobuddy.SmartStudy.global.GlobalParams;
import com.innobuddy.SmartStudy.utils.Md5Utils;
import com.innobuddy.download.domain.TsFile;
import com.innobuddy.download.utils.ConfigUtils;
import com.innobuddy.download.utils.DStorageUtils;
import com.innobuddy.download.utils.FileUtils;
import com.innobuddy.download.utils.MyIntents;
import com.innobuddy.download.utils.NetworkUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

@SuppressLint("HandlerLeak")
public class DownloadService extends Service {

	private final static int SUCCESSCODE = 100;
	private final static int DOWNLOADCODE = 101;
	private DownloadManager mDownloadManager;
	private HttpUtils http;
	private String url;
	private ArrayList<TsFile> list;
	private HttpHandler<File> httpHandler;
	private DownloadTaskListener listener;
	private String target;
	// private static final String TAG = "DownloadService";
	private boolean flag = true;// 如果删除 文件 关闭
	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESSCODE:
				String result = (String) msg.obj;
				ConfigUtils.setString(DownloadService.this, url + "m3u8", result);
				list = m3u8Parser(result, url);
				ConfigUtils.setString(DownloadService.this, url + "end", "N");// 正在缓存
				for (int i = 0; i < GlobalParams.list.size(); i++) {
					if (!GlobalParams.list.get(i).equals(url)) {
						if (!TextUtils.isEmpty(GlobalParams.list.get(i))) {// &&
							mDownloadManager.pauseTask(GlobalParams.list.get(i));   
						}
					}
				}
				Message downloadMsg = obtainMessage();
				downloadMsg.what = DOWNLOADCODE;
				handler.sendMessage(downloadMsg);
				break;
			case DOWNLOADCODE:
				int j = ConfigUtils.getInt(DownloadService.this, url + "count");
				if (list == null || list.size() == 0 || j >= list.size() || list.get(j) == null)
					return;
				final String tag = target + list.get(j).getTsUrl().substring(list.get(j).getTsUrl().lastIndexOf("/") + 1, list.get(j).getTsUrl().length());
				http.configCurrentHttpCacheExpiry(0); // 请求缓存
				httpHandler = http.download(list.get(j).getTsUrl(), tag, true, false, new RequestCallBack<File>() {
					@Override
					public void onSuccess(ResponseInfo<File> response) {
						if (flag) {
							if (new File(tag).exists()) {
								ConfigUtils.setInt(DownloadService.this, url + "count", ConfigUtils.getInt(DownloadService.this, url + "count") + 1);
							}
							if (ConfigUtils.getInt(DownloadService.this, url + "count") < list.size() - 1) {
								Message msg = obtainMessage();
								msg.what = DOWNLOADCODE;
								handler.sendMessage(msg);
							} else {
								completeTask(url);// 如果下载完成的处理//Y代表已经完成 N代表没有完成
								nextDownload(url);
							}
						}
						httpHandler.cancel();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {// xUtils
						arg0.printStackTrace();
						if (arg1.contains("ConnectionPoolTimeoutException")) {
							Message msg = obtainMessage();
							msg.what = DOWNLOADCODE;
							handler.sendMessage(msg);
						} else if ("maybe the file has downloaded completely".equals(arg1)) {
							if (new File(tag).exists()) {
								ConfigUtils.setInt(DownloadService.this, url + "count", ConfigUtils.getInt(DownloadService.this, url + "count") + 1);
							}
							Message msg = obtainMessage();
							msg.what = DOWNLOADCODE;
							handler.sendMessage(msg);
						} else {
							downloadError(url);// 下载错误
							nextDownload(url);
						}
					}

					public void onLoading(long total, long current, boolean isUploading) {
						//计算下载量算法
						long download = current - ConfigUtils.getLong(DownloadService.this, url + "current" + ConfigUtils.getInt(DownloadService.this, url + "count"));
						ConfigUtils.setLong(DownloadService.this, url + "current" + ConfigUtils.getInt(DownloadService.this, url + "count"), current);
						ConfigUtils.setLong(DownloadService.this, url + "download", ConfigUtils.getLong(DownloadService.this, url + "download") + download);
						// 算法
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
						
						float downloadTime = 0f;
						float totalTime = 0f;
						for (int i = 0; i < list.size(); i++) {
							if (i < ConfigUtils.getInt(DownloadService.this, url + "count")) {
								downloadTime = downloadTime + list.get(i).getTime();
							} else if (i == ConfigUtils.getInt(DownloadService.this, url + "count")) {
								downloadTime = downloadTime + current * list.get(i).getTime() / total;
							}
							totalTime = totalTime + list.get(i).getTime();
						}
						ConfigUtils.setLong(DownloadService.this, url+"downloadTime", (long)downloadTime);
						ConfigUtils.setLong(DownloadService.this, url+"totalTime", (long)totalTime);
						Intent updateIntent = new Intent("com.innobuddy.download.observe");
						updateIntent.putExtra(MyIntents.TYPE, MyIntents.Types.PROCESS);
						updateIntent.putExtra(MyIntents.PROCESS_SPEED, "");
						updateIntent.putExtra(MyIntents.PROCESS_PROGRESS, 0 + "");
						updateIntent.putExtra(MyIntents.LOADING_SIZE, downloadSize);
						updateIntent.putExtra(MyIntents.DOWNLOAD_TIME, (long) downloadTime);
						updateIntent.putExtra(MyIntents.TOTAL_TIME, (long) totalTime);
						updateIntent.putExtra(MyIntents.URL, url);
						sendBroadcast(updateIntent);
						//读取文件夹大小
						
						/*******************/
					}

					public void onCancelled() {
						// bei'qu'xi
					}
				});
				break;
			case 1000:
				url = (String) msg.obj;
				if (!TextUtils.isEmpty(url)) {
					target = DStorageUtils.FILE_ROOT + Md5Utils.encode(url) + "/";
					http.send(HttpMethod.GET, url, new RequestCallBack<String>() {
						@Override
						public void onFailure(HttpException arg0, String arg1) {
							Toast.makeText(DownloadService.this, "获取数据失败", Toast.LENGTH_LONG).show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							if (list != null) {
								list = null;
							}
							Message msg = Message.obtain();
							msg.what = SUCCESSCODE;
							msg.obj = responseInfo.result;
							handler.sendMessage(msg);
							
						}
					});
				}

				break;
			}
		};
	};

	@Override
	public IBinder onBind(Intent intent) {

		return new DownloadServiceImpl();
	}

	@Override
	public void onCreate() {

		super.onCreate();
		mDownloadManager = new DownloadManager(this, handler, listener);

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);
		http = new HttpUtils();
		if (mDownloadManager == null) {
			mDownloadManager = new DownloadManager(this, handler, listener);
		}
		if (intent != null && intent.getAction() != null) {
			if (intent.getAction().equals("com.innobuddy.download.services.IDownloadService")) {
				int type = intent.getIntExtra(MyIntents.TYPE, -1);
				switch (type) {
				case MyIntents.Types.START:
					if (!mDownloadManager.isRunning()) {
						mDownloadManager.startManage();
					} else {
						mDownloadManager.reBroadcastAddAllTask();
					}
					flag = true;
					break;
				case MyIntents.Types.ADD:
					url = intent.getStringExtra(MyIntents.URL);
					if (!TextUtils.isEmpty(url)) {// && !mDownloadManager.hasTask(url)
						mDownloadManager.addTask(url);
						GlobalParams.list.add(url);// 如果任务当中有就不加
						flag = true;
					}
					break;
				case MyIntents.Types.CONTINUE:
					url = intent.getStringExtra(MyIntents.URL);
					if (!TextUtils.isEmpty(url)) {
						target = DStorageUtils.FILE_ROOT + Md5Utils.encode(url) + "/";
						String result = ConfigUtils.getString(DownloadService.this, url + "m3u8");
						list = m3u8Parser(result, url);//
						Message msg = Message.obtain();
						msg.what = DOWNLOADCODE;
						handler.sendMessage(msg);
						flag = true;
					}
					break;
				case MyIntents.Types.DELETE:
					url = intent.getStringExtra(MyIntents.URL);
					if (!TextUtils.isEmpty(url)) {
						mDownloadManager.deleteTask(url);
					}
					ConfigUtils.setLong(DownloadService.this, url + "download", 0);
					ConfigUtils.setInt(DownloadService.this, url + "count", 0);
					ConfigUtils.setString(DownloadService.this, url + "m3u8", "");
					ConfigUtils.setString(DownloadService.this, url + "end", "");// 设置service
					// if (httpHandler != null)
					// httpHandler.cancel();
					GlobalParams.list.remove(url);// 集合里面删除
					flag = false;
					for (int i = 0; i < GlobalParams.list.size(); i++) {
						if (!GlobalParams.list.get(i).equals(url)) {
							if (!TextUtils.isEmpty(GlobalParams.list.get(i))) {// &&
								mDownloadManager.addTask(GlobalParams.list.get(i));
								flag = true;
							}
						}
					}
					break;
				case MyIntents.Types.PAUSE:
					url = intent.getStringExtra(MyIntents.URL);
					if (!TextUtils.isEmpty(url)) {
						target = DStorageUtils.FILE_ROOT + Md5Utils.encode(url) + "/";
						mDownloadManager.pauseTask(url);
						// PAUSE的暂停
						if (httpHandler != null)
							httpHandler.cancel();
						flag = false;
						for (int i = 0; i < GlobalParams.list.size(); i++) {
							if (!GlobalParams.list.get(i).equals(url)) {
								if (!TextUtils.isEmpty(GlobalParams.list.get(i))) {// &&
									mDownloadManager.addTask(GlobalParams.list.get(i));
									flag = true;
								}
							}
						}
					}
					break;
				case MyIntents.Types.STOP:
					mDownloadManager.close();// 关闭服务下载
					if (httpHandler != null)
						httpHandler.cancel();//
					break;
				default:
					break;
				}
			}
		}

	}

	private class DownloadServiceImpl extends IDownloadService.Stub {

		@Override
		public void startManage() throws RemoteException {

			mDownloadManager.startManage();
		}

		@Override
		public void addTask(String url) throws RemoteException {

			mDownloadManager.addTask(url);
		}

		@Override
		public void pauseTask(String url) throws RemoteException {
		}

		@Override
		public void deleteTask(String url) throws RemoteException {
		}

		@Override
		public void continueTask(String url) throws RemoteException {
		}

	}

	/*
	 * @return返回ts_url集合
	 */
	public ArrayList<TsFile> m3u8Parser(String result, String m3u8_url) {
		// 写入本机文件当中
		try {
			File file = new File(DStorageUtils.FILE_ROOT + Md5Utils.encode(url) + "/" + NetworkUtils.getFileNameFromUrl(url));
			GlobalParams.path = Md5Utils.encode(url) + "/";
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(file);
			writer.write(result);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();//  
		}
		// 解析文件
		InputStream in = new ByteArrayInputStream(result.getBytes());
		ArrayList<TsFile> list_ts = new ArrayList<TsFile>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			TsFile tsFile = null;
			while ((line = reader.readLine()) != null) {
				// tsFile=new TsFile();

				if (line.startsWith("#")) {
					// 这里是Metadata信息
					if (line.startsWith("#EXTINF:")) {
						tsFile = new TsFile();
						tsFile.setTime(Float.valueOf(line.substring("#EXTINF:".length(), line.length() - 1)));
						System.out.println(Float.valueOf(line.substring("#EXTINF:".length(), line.length() - 1)));
					}
				} else if (line.length() > 0) {
					// 这里是一个指向的视频流路径 ,可能是绝对地址，也可能是相对地址
					if (line.startsWith("http")) {// 如果以http开头，一定是绝对地址了
						tsFile.setTsUrl(line);
					} else {// 不以http开头，是相对地址，需要进行拼接。
						String ts_url;
						String m3u8_postfixname = m3u8_url.substring(m3u8_url.lastIndexOf("/") + 1, m3u8_url.length());
						if (m3u8_postfixname.equals("dest.m3u8")) {
							ts_url = m3u8_url.replace("dest.m3u8", line);
						} else {
							ts_url = m3u8_url.replace("dest.m3u8", line);
						}
						tsFile.setTsUrl(ts_url);
						list_ts.add(tsFile);
					}

				}
				//

			}
			in.close();
			return list_ts;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void broadcastAddTask(String url) {

		broadcastAddTask(url, false);
	}

	private void broadcastAddTask(String url, boolean isInterrupt) {

		Intent nofityIntent = new Intent("com.innobuddy.download.observe");
		nofityIntent.putExtra(MyIntents.TYPE, MyIntents.Types.ADD);
		nofityIntent.putExtra(MyIntents.URL, url);
		nofityIntent.putExtra(MyIntents.IS_PAUSED, isInterrupt);
		this.sendBroadcast(nofityIntent);
	}

	public void completeTask(String url) {

		// if (mDownloadingTasks.contains(task)) {
		// if(TextUtils.isEmpty(url)){
		// ConfigUtils.clearURL(this, );
		// }
		// mDownloadingTasks.remove(task);

		// notify list changed
		// ConfigUtils.clearURL(this,0);
		Intent nofityIntent = new Intent("com.innobuddy.download.observe");
		nofityIntent.putExtra(MyIntents.TYPE, MyIntents.Types.COMPLETE);
		nofityIntent.putExtra(MyIntents.URL, url);
		sendBroadcast(nofityIntent);
		// 判断文件是够完整
		target = DStorageUtils.FILE_ROOT + Md5Utils.encode(url) + "/";
		boolean isExist = false;
		File file = new File(target);
		File[] listFiles = file.listFiles();
		for (int i = 0; i < listFiles.length; i++) {
			if (listFiles[i].getName().contains("dest0.ts")) {
				isExist = true;
			}
		}
		if (!isExist) {
			final String tag = target + "dest0.ts";
			http.configCurrentHttpCacheExpiry(0); // 请求缓存
			http.download(url.replace("dest.m3u8", "dest0.ts"), tag, true, false, new RequestCallBack<File>() {
				@Override
				public void onSuccess(ResponseInfo<File> response) {
				}

				@Override
				public void onFailure(HttpException arg0, String arg1) {// xUtils
				}

				public void onLoading(long total, long current, boolean isUploading) {
				}

				public void onCancelled() {
				}
			});
		}

	}

	public void downloadError(String url) {
		Intent errorIntent = new Intent("com.innobuddy.download.observe");
		errorIntent.putExtra(MyIntents.TYPE, MyIntents.Types.ERROR);
		errorIntent.putExtra(MyIntents.ERROR_CODE, "");
		errorIntent.putExtra(MyIntents.URL, url);
		sendBroadcast(errorIntent);
	}

	public void nextDownload(String url) {
		ConfigUtils.setString(DownloadService.this, url + "end", "Y");
		ConfigUtils.setLong(DownloadService.this, url + "current", 0L);
		ConfigUtils.setLong(DownloadService.this, url + "download", 0L);
		ConfigUtils.setInt(DownloadService.this, url + "count", 0);
		GlobalParams.list.remove(url);
		if (GlobalParams.list.size() != 0) {
			url = GlobalParams.list.get(0);
			if (!TextUtils.isEmpty(url)) {
				mDownloadManager.addTask(url);
				broadcastAddTask(url);
				flag = true;
			}
		}
	}


}
