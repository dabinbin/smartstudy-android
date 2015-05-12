package com.innobuddy.SmartStudy.Video;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.innobuddy.SmartStudy.R;

public class VolumeController {
	private Toast t;
	private VolumeView tv;

	private Context context;

	public VolumeController(Context context) {
		this.context = context;
	}

	public void show(float progress) {
		if (t == null) {
			t = new Toast(context);
			View layout = LayoutInflater.from(context).inflate(R.layout.vv, null);
			tv = (VolumeView) layout.findViewById(R.id.volumeView);
			t.setView(layout);
			t.setGravity(Gravity.BOTTOM, 0, 100);
			t.setDuration(Toast.LENGTH_SHORT);
		}
		tv.setProgress(progress);
		t.show();
	}
}
