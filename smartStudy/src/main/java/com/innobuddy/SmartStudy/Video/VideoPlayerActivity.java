package com.innobuddy.SmartStudy.Video;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.widget.VideoView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.DB.DBHelper;
import com.innobuddy.SmartStudy.utils.Md5Utils;
import com.innobuddy.SmartStudy.utils.Utilitys;
import com.innobuddy.download.utils.ConfigUtils;
import com.innobuddy.download.utils.DStorageUtils;
import com.innobuddy.download.utils.MyIntents;
import com.innobuddy.download.utils.NetworkUtils;

public class VideoPlayerActivity extends Activity implements OnClickListener {

	// 自定义VideoView
	private VideoView mVideo;

	// 头部View
	private View mTopView;

	// 底部View
	private View mBottomView;

	// 视频播放控制
	private View mControlView;

	// 功能View
	private View mActionView;

	// 视频播放拖动条
	private SeekBar mSeekBar;
	private ImageView mPlay;
	private ImageView mBackward;
	private ImageView mForward;
	private TextView mPlayTime;
	private TextView mDurationTime;
	private ImageView mCollect;
	private ImageView mOffline;

	private TextView titleView;

	// 音频管理器
	private AudioManager mAudioManager;

	// 屏幕宽高
	private float width;
	private float height;

	// 视频播放时间
	private int playTime;

	private String videoUrl = "";

	//private String videoCacheUrl = "";

	private String videoPath = "";

	// 自动隐藏顶部和底部View的时间
	private static final int HIDE_TIME = 5000;

	// 声音调节Toast
	private VolumeController volumeController;

	// 原始屏幕亮度
	private int orginalLight;

	JSONObject jsonObject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);

		String jsonString = getIntent().getStringExtra("json");

		String name = "";

		if (jsonString != null) {
			try {
				jsonObject = new JSONObject(jsonString);
			} catch (JSONException e) {

			}
		}

		if (jsonObject != null) {

			DBHelper.getInstance(null).insertRecentWatch(jsonObject);

			try {

				int id = jsonObject.getInt(DBHelper.VIDEO_ID);

				videoUrl = jsonObject.getString(DBHelper.VIDEO_URL);

				//videoCacheUrl = jsonObject.getString(DBHelper.VIDEO_CACHE_URL);

				name = jsonObject.getString(DBHelper.VIDEO_NAME);

				Cursor c = DBHelper.getInstance(null).queryRecentWatch(id);
				if (c != null && c.getCount() > 0) {
					c.moveToFirst();
					int position = c.getInt(c.getColumnIndex(DBHelper.VIDEO_POSTION));
					if (position > 0) {
						playTime = position;
					}
				}

				if (c != null) {
					c.close();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		setContentView(R.layout.activity_video_player);
		volumeController = new VolumeController(this);
		mVideo = (VideoView) findViewById(R.id.videoview);
		mPlayTime = (TextView) findViewById(R.id.play_time);
		mDurationTime = (TextView) findViewById(R.id.total_time);
		mSeekBar = (SeekBar) findViewById(R.id.seekbar);
		mTopView = findViewById(R.id.top_layout);
		mBottomView = findViewById(R.id.bottom_layout);
		mControlView = findViewById(R.id.bottom_control);
		mActionView = findViewById(R.id.right_action);
		mBackward = (ImageView) findViewById(R.id.imageView4);
		mPlay = (ImageView) findViewById(R.id.imageView5);
		mForward = (ImageView) findViewById(R.id.imageView6);
		mCollect = (ImageView) findViewById(R.id.imageView2);
		mOffline = (ImageView) findViewById(R.id.imageView3);
		titleView = (TextView) findViewById(R.id.titleView);

		titleView.setText(name);

		mCollect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				collect();
			}
		});

		mOffline.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				offline();
			}
		});

		mBackward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backward();
			}
		});

		mForward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				forward();
			}
		});

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		width = DensityUtil.getWidthInPx(this);
		height = DensityUtil.getHeightInPx(this);
		threshold = DensityUtil.dip2px(this, 18);

		orginalLight = LightnessController.getLightness(this);

		mPlay.setOnClickListener(this);
		mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);

		ImageView imageView = (ImageView) findViewById(R.id.imageView1);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goBack();
			}
		});

		playVideo();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			height = DensityUtil.getWidthInPx(this);
			width = DensityUtil.getHeightInPx(this);
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			width = DensityUtil.getWidthInPx(this);
			height = DensityUtil.getHeightInPx(this);
		}
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPause() {
		super.onPause();
		LightnessController.setLightness(this, orginalLight);
	}

	private OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			mHandler.postDelayed(hideRunnable, HIDE_TIME);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			mHandler.removeCallbacks(hideRunnable);
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (fromUser) {
				long time = progress * mVideo.getDuration() / 100;
				mVideo.seekTo(time);
			}
		}
	};

	@Override
	public void finish() {

		if (jsonObject != null) {
			try {

				DBHelper.getInstance(null).updateRecentWatch(jsonObject.getInt("id"), (int) mVideo.getCurrentPosition());

				Intent nofityIntent = new Intent("videoPositionChanged");
				sendBroadcast(nofityIntent);

			} catch (JSONException e) {

			}
		}

		super.finish();
	}

	private void goBack() {

		finish();

	}

	private void collect() {
		if (jsonObject != null) {
			Toast.makeText(VideoPlayerActivity.this, "已收藏。", Toast.LENGTH_SHORT).show();
			DBHelper.getInstance(null).insertCollect(jsonObject);
		}
	}

	private void offline() {

		try {

			if (jsonObject != null) {

				int id = jsonObject.getInt(DBHelper.VIDEO_ID);
				// String url = jsonObject.getString(DBHelper.VIDEO_CACHE_URL);
				String url = jsonObject.getString(DBHelper.VIDEO_URL);
				File file = new File(DStorageUtils.FILE_ROOT + Md5Utils.encode(url) + "/" + NetworkUtils.getFileNameFromUrl(url));
				File file2 = new File(DStorageUtils.FILE_ROOT + Md5Utils.encode(url));
				// chulis
				String str = ConfigUtils.getString(this, url + "end");
				System.out.println(str);
				if (!file2.exists()) {
					file2.mkdirs();// 创建文件夹
				}
				if (file.exists() && str.equals("N")) {
					Toast.makeText(VideoPlayerActivity.this, "正在缓存。", Toast.LENGTH_SHORT).show();
					return;
				}
				if (file.exists() && str.equals("Y")) {
					Toast.makeText(VideoPlayerActivity.this, "已缓存。", Toast.LENGTH_SHORT).show();
					return;
				}
				Cursor c1 = DBHelper.getInstance(null).queryDownload(id);
				Cursor c2 = DBHelper.getInstance(null).queryOffline(id);

				if ((c1 != null && c1.getCount() > 0) || c2 != null && c2.getCount() > 0) {
					Toast.makeText(VideoPlayerActivity.this, "已加入缓存中。", Toast.LENGTH_SHORT).show();
					Editor editor = sp.edit();
					Set<String> values = new TreeSet<String>();
					values.add(url);
					editor.putStringSet("downloadVideo", values);
					editor.commit();
				} else {

					SharedPreferences settingPreferences = getSharedPreferences(Utilitys.SETTING_INFOS, 0);

					boolean mobileHint = settingPreferences.getBoolean(Utilitys.MOBILE_HINT, true);

					if (Utilitys.isMobileNetwork(this) && mobileHint) {

						new AlertDialog.Builder(VideoPlayerActivity.this).setTitle("提示").setMessage("您正在使用蜂窝移动网络。").setPositiveButton("确定", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {

								Toast.makeText(VideoPlayerActivity.this, "已加入缓存中。", Toast.LENGTH_SHORT).show();

								DBHelper.getInstance(null).insertDownload(jsonObject);
								try {
									Intent downloadIntent = new Intent("com.innobuddy.download.services.IDownloadService");
									downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.ADD);
									downloadIntent.putExtra(MyIntents.URL, jsonObject.getString(DBHelper.VIDEO_URL));
									getApplicationContext().startService(downloadIntent);

									Editor editor = sp.edit();
									Set<String> values = new TreeSet<String>();
									values.add(jsonObject.getString(DBHelper.VIDEO_URL));
									editor.putStringSet("downloadVideo", values);
									editor.commit();
								} catch (JSONException e) {
								}

							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i) {

							}
						}).show();

					} else {

						Toast.makeText(VideoPlayerActivity.this, "已加入缓存中。", Toast.LENGTH_SHORT).show();

						DBHelper.getInstance(null).insertDownload(jsonObject);
						try {
							Intent downloadIntent = new Intent("com.innobuddy.download.services.IDownloadService");
							downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.ADD);
							downloadIntent.putExtra(MyIntents.URL, jsonObject.getString(DBHelper.VIDEO_URL));
							getApplicationContext().startService(downloadIntent);
						} catch (JSONException e) {
						}

					}

				}

				if (c1 != null) {
					c1.close();
				}

				if (c2 != null) {
					c2.close();
				}

			}

		} catch (Exception e) {

		}

	}

	private void backward() {
		int current = (int) mVideo.getCurrentPosition();
		if (current > 0 && current < mVideo.getDuration()) {
			current -= 15 * 1000;
			if (current < 0) {
				current = 0;
			}
			mVideo.seekTo(current);
			mSeekBar.setProgress(current * 100 / (int) mVideo.getDuration());
			mPlayTime.setText(formatTime(current));
		}
	}

	private void forward() {
		int current = (int) mVideo.getCurrentPosition();
		if (current >= 0 && current < mVideo.getDuration()) {
			current += 15 * 1000;
			if (current > mVideo.getDuration()) {
				current = (int) mVideo.getDuration();
			}
			mVideo.seekTo(current);
			mSeekBar.setProgress(current * 100 / (int) mVideo.getDuration());
			mPlayTime.setText(formatTime(current));
		}
	}

	private void backward(float delataX) {
		int current = (int) mVideo.getCurrentPosition();
		int backwardTime = (int) (delataX / width * mVideo.getDuration());
		int currentTime = current - backwardTime;
		if (currentTime < 0) {
			currentTime = 0;
		}
		mVideo.seekTo(currentTime);
		mSeekBar.setProgress(currentTime * 100 / (int) mVideo.getDuration());
		mPlayTime.setText(formatTime(currentTime));
	}

	private void forward(float delataX) {

		int duration = (int) mVideo.getDuration();
		if (duration > 0) {
			int current = (int) mVideo.getCurrentPosition();
			int forwardTime = (int) (delataX / width * mVideo.getDuration());
			int currentTime = current + forwardTime;
			if (currentTime > mVideo.getDuration()) {
				currentTime = (int) mVideo.getDuration();
			}
			mVideo.seekTo(currentTime);
			mSeekBar.setProgress(currentTime * 100 / (int) mVideo.getDuration());
			mPlayTime.setText(formatTime(currentTime));
		}
	}

	private void volumeDown(float delatY) {
		int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		int down = (int) (delatY / height * max * 3);
		int volume = Math.max(current - down, 0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
		int transformatVolume = volume * 100 / max;
		volumeController.show(transformatVolume);
	}

	private void volumeUp(float delatY) {
		int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		int up = (int) ((delatY / height) * max * 3);
		int volume = Math.min(current + up, max);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
		int transformatVolume = volume * 100 / max;
		volumeController.show(transformatVolume);
	}

	private void lightDown(float delatY) {
		int down = (int) (delatY / height * 255 * 3);
		int transformatLight = LightnessController.getLightness(this) - down;
		LightnessController.setLightness(this, transformatLight);
	}

	private void lightUp(float delatY) {
		int up = (int) (delatY / height * 255 * 3);
		int transformatLight = LightnessController.getLightness(this) + up;
		LightnessController.setLightness(this, transformatLight);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(0);
		mHandler.removeCallbacksAndMessages(null);
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				if (mVideo.getCurrentPosition() > 0) {
					mPlayTime.setText(formatTime(mVideo.getCurrentPosition()));
					int progress = (int) mVideo.getCurrentPosition() * 100 / (int) mVideo.getDuration();
					mSeekBar.setProgress(progress);
					if (mVideo.getCurrentPosition() > mVideo.getDuration() - 100) {
						mPlayTime.setText("00:00");
						mSeekBar.setProgress(0);
					}
					mSeekBar.setSecondaryProgress(mVideo.getBufferPercentage());
				} else {
					mPlayTime.setText("00:00");
					mSeekBar.setProgress(0);
				}

				break;
			case 2:
				showOrHide();
				break;

			default:
				break;
			}
		}
	};

	private void playVideo() {

		videoPath = DStorageUtils.FILE_ROOT + Md5Utils.encode(videoUrl) + "/" + NetworkUtils.getFileNameFromUrl(videoUrl);
		File file = new File(videoPath);
		// System.out.println(file.getAbsolutePath());
		//m3u8存在播放本地文件 并且已经缓存完成
		if (file.exists() && ConfigUtils.getString(VideoPlayerActivity.this, videoUrl + "end").equals("Y")) {
			mVideo.setVideoPath(videoPath);
		} else {
			mVideo.setVideoPath(videoUrl);
		}

		mVideo.requestFocus();
		mVideo.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				// mVideo.setVideoWidth(mp.getVideoWidth());
				// mVideo.setVideoHeight(mp.getVideoHeight());

				mVideo.start();
				int duration = (int) mVideo.getDuration();
				if (playTime != 0 && (duration - playTime > 1000)) {
					mVideo.seekTo(playTime);
				} else {
					playTime = 0;
				}

				mHandler.removeCallbacks(hideRunnable);
				mHandler.postDelayed(hideRunnable, HIDE_TIME);
				mDurationTime.setText(formatTime(mVideo.getDuration()));
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						mHandler.sendEmptyMessage(1);
					}
				}, 0, 1000);
			}
		});
		mVideo.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				goBack();
			}
		});
		mVideo.setOnTouchListener(mTouchListener);
	}

	private Runnable hideRunnable = new Runnable() {

		@Override
		public void run() {
			showOrHide();
		}
	};

	@SuppressLint("SimpleDateFormat")
	private String formatTime(long time) {
		DateFormat formatter = new SimpleDateFormat("mm:ss");
		return formatter.format(new Date(time));
	}

	private float mLastMotionX;
	private float mLastMotionY;
	private int startX;
	private int startY;
	private int threshold;
	private boolean isClick = true;

	private OnTouchListener mTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			final float x = event.getX();
			final float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mLastMotionX = x;
				mLastMotionY = y;
				startX = (int) x;
				startY = (int) y;
				break;
			case MotionEvent.ACTION_MOVE:
				float deltaX = x - mLastMotionX;
				float deltaY = y - mLastMotionY;
				float absDeltaX = Math.abs(deltaX);
				float absDeltaY = Math.abs(deltaY);
				// 声音调节标识
				boolean isAdjustAudio = false;
				if (absDeltaX > threshold && absDeltaY > threshold) {
					if (absDeltaX < absDeltaY) {
						isAdjustAudio = true;
					} else {
						isAdjustAudio = false;
					}
				} else if (absDeltaX < threshold && absDeltaY > threshold) {
					isAdjustAudio = true;
				} else if (absDeltaX > threshold && absDeltaY < threshold) {
					isAdjustAudio = false;
				} else {
					return true;
				}
				if (isAdjustAudio) {
					if (x < width / 2) {
						if (deltaY > 0) {
							lightDown(absDeltaY);
						} else if (deltaY < 0) {
							lightUp(absDeltaY);
						}
					} else {
						if (deltaY > 0) {
							volumeDown(absDeltaY);
						} else if (deltaY < 0) {
							volumeUp(absDeltaY);
						}
					}

				} else {
					if (deltaX > 0) {
						forward(absDeltaX);
					} else if (deltaX < 0) {
						backward(absDeltaX);
					}
				}
				mLastMotionX = x;
				mLastMotionY = y;
				break;
			case MotionEvent.ACTION_UP:
				if (Math.abs(x - startX) > threshold || Math.abs(y - startY) > threshold) {
					isClick = false;
				}
				mLastMotionX = 0;
				mLastMotionY = 0;
				startX = (int) 0;
				if (isClick) {
					showOrHide();
				}
				isClick = true;
				break;

			default:
				break;
			}
			return true;
		}

	};

	private SharedPreferences sp;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageView5:
			if (mVideo.isPlaying()) {
				// //if (mVideo.canPause()) {
				mPlay.setImageResource(R.drawable.video_play);
				mVideo.pause();
				// }
			} else {
				mPlay.setImageResource(R.drawable.video_pause);
				mVideo.start();
			}
			break;
		default:
			break;
		}
	}

	private void showOrHide() {
		if (mTopView.getVisibility() == View.VISIBLE) {
			mTopView.clearAnimation();
			Animation animation = AnimationUtils.loadAnimation(this, R.anim.option_leave_from_top);
			animation.setAnimationListener(new AnimationImp() {
				@Override
				public void onAnimationEnd(Animation animation) {
					super.onAnimationEnd(animation);
					mTopView.setVisibility(View.GONE);
				}
			});
			mTopView.startAnimation(animation);

			mBottomView.clearAnimation();
			Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.option_leave_from_bottom);
			animation1.setAnimationListener(new AnimationImp() {
				@Override
				public void onAnimationEnd(Animation animation) {
					super.onAnimationEnd(animation);
					mBottomView.setVisibility(View.GONE);
				}
			});
			mBottomView.startAnimation(animation1);

			mControlView.clearAnimation();
			Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.option_leave_from_bottom);
			animation2.setAnimationListener(new AnimationImp() {
				@Override
				public void onAnimationEnd(Animation animation) {
					super.onAnimationEnd(animation);
					mControlView.setVisibility(View.GONE);
				}
			});
			mControlView.startAnimation(animation2);

			mActionView.clearAnimation();
			Animation animation3 = AnimationUtils.loadAnimation(this, R.anim.option_leave_from_right);
			animation3.setAnimationListener(new AnimationImp() {
				@Override
				public void onAnimationEnd(Animation animation) {
					super.onAnimationEnd(animation);
					mActionView.setVisibility(View.GONE);
				}
			});
			mActionView.startAnimation(animation3);

		} else {

			mTopView.setVisibility(View.VISIBLE);
			mTopView.clearAnimation();
			Animation animation = AnimationUtils.loadAnimation(this, R.anim.option_entry_from_top);
			mTopView.startAnimation(animation);

			mBottomView.setVisibility(View.VISIBLE);
			mBottomView.clearAnimation();
			Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.option_entry_from_bottom);
			mBottomView.startAnimation(animation1);

			mControlView.setVisibility(View.VISIBLE);
			mControlView.clearAnimation();
			Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.option_entry_from_bottom);
			mControlView.startAnimation(animation2);

			mActionView.setVisibility(View.VISIBLE);
			mActionView.clearAnimation();
			Animation animation3 = AnimationUtils.loadAnimation(this, R.anim.option_entry_from_right);
			mActionView.startAnimation(animation3);

			mHandler.removeCallbacks(hideRunnable);
			mHandler.postDelayed(hideRunnable, HIDE_TIME);

		}
	}

	private class AnimationImp implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {

		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}

	}

}
