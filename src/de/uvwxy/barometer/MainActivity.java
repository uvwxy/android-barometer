package de.uvwxy.barometer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import de.uvwxy.daisy.common.sensors.BarometerReader;
import de.uvwxy.daisy.common.sensors.SensorReader.SensorResultCallback;
import de.uvwxy.daisy.helper.BitmapTools;
import de.uvwxy.daisy.helper.ViewTools;

public class MainActivity extends Activity {
	private final static String PREF_ID = "BARO_SETTINGS";
	private final static String PREF_BARO_OLD = "BARO_SETTINGS";
	private final static String PREF_SHOW_HELP = "SHOW_HELP";

	private Context ctx = this;
	private RelativeLayout rlMain = null;
	private ImageView ivBaro = null;

	Barometer baro = null;

	private SensorResultCallback cb = new SensorResultCallback() {

		@Override
		public void result(float[] f) {
			currentValue = f[0];
			Bitmap temp = baro.drawMillisWithMemory(currentValue, lastValue);
			int w = ivBaro.getWidth();
			int h = ivBaro.getHeight();
			int l = w <= h ? w : h;
			if (l != 0) {
				ivBaro.setImageBitmap(BitmapTools.scaleBitmap(temp, l, l, true));
			}
		}
	};

	BarometerReader baroReader = null;

	private float lastValue;
	private float currentValue;

	private long lastTap = System.currentTimeMillis();
	private long tapSpeed = 500;

	private void initGUI() {
		rlMain = (RelativeLayout) findViewById(R.id.rlMain);
		ivBaro = (ImageView) findViewById(R.id.ivBaro);

		ivBaro.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (System.currentTimeMillis() - lastTap > 1250) {

					updateBaro();

					lastTap = System.currentTimeMillis();
				}
			}
		});

		ivBaro.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				writeValues();
				updateBaro();
				Toast.makeText(ctx, "Saved", Toast.LENGTH_SHORT).show();

				return false;
			}
		});

		rlMain.setBackgroundColor(Color.BLACK);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initGUI();
		baro = new Barometer();
		baro.loadBitmaps(this);
		baroReader = new BarometerReader(this, -2, cb);
		readValues();

		int w = ivBaro.getWidth();
		int h = ivBaro.getHeight();
		int l = w <= h ? w : h;
		if (l != 0) {
			ivBaro.setImageBitmap(BitmapTools.scaleBitmap(baro.drawUnitsMillibar(), l, l, true));
		}

		ViewTools.showHelpOnce(this, PREF_ID, "Usage:", "OK", "Click to refresh\nLongClick to save");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onResume() {
		updateBaro();
		super.onResume();
	}

	private void updateBaro() {
		baroReader.startReading();
	}

	private void readValues() {

		SharedPreferences settings = getSharedPreferences(PREF_ID, 0);
		lastValue = settings.getFloat(PREF_BARO_OLD, -1);
	}

	private void writeValues() {
		SharedPreferences settings = getSharedPreferences(PREF_ID, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat(PREF_BARO_OLD, currentValue);
		editor.commit();
	}

}
