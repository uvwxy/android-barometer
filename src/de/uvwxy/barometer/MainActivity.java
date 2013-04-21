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
import android.widget.TextView;
import android.widget.Toast;
import de.uvwxy.daisy.common.sensors.BarometerReader;
import de.uvwxy.daisy.common.sensors.SensorReader.SensorResultCallback;
import de.uvwxy.daisy.helper.BitmapTools;
import de.uvwxy.daisy.helper.ViewTools;

public class MainActivity extends Activity {
	protected final static String PREF_ID = "BARO_SETTINGS";
	private final static String PREF_BARO_OLD = "BARO_SETTINGS";

	private Context ctx = this;
	private RelativeLayout rlMain = null;
	private ImageView ivBaro = null;
	private TextView tvInfo = null;

	private long lastTap = System.currentTimeMillis();
	private long tapSpeed = 500;

	private BarometerReader baroReader = null;
	private Barometer baro = null;
	private float oldValue;
	private float origValue;
	private float currentValue;

	private boolean save = false;

	int x = 0;
	private SensorResultCallback cb = new SensorResultCallback() {

		@Override
		public void result(float[] f) {
			if (f != null && f.length >= 1) {
				currentValue = f[0];
				origValue = currentValue;
				currentValue = BaroSettings.getDiffedBaro(ctx, currentValue);

				// split in if else to avoid values changing after reading
				if (save) {
					oldValue = currentValue;
					writeValues();
					save = false;
					Toast.makeText(ctx, "Saved", Toast.LENGTH_SHORT).show();
				}

				Bitmap temp = baro.drawMillisWithMemory(currentValue, oldValue);

				int w = ivBaro.getWidth();
				int h = ivBaro.getHeight();
				int l = w <= h ? w : h;

				if (l != 0) {
					ivBaro.setImageBitmap(BitmapTools.scaleBitmap(temp, l, l, true));
				}
			}
		}
	};

	private void initGUI() {
		rlMain = (RelativeLayout) findViewById(R.id.rlMain);
		ivBaro = (ImageView) findViewById(R.id.ivBaro);
		tvInfo = (TextView) findViewById(R.id.tvInfo);

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
				save = true;
				updateBaro();
				return true;
			}
		});

		tvInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BaroSettings.showConfig(ctx, origValue);
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
		baroReader = new BarometerReader(this, 50, cb);
		readValues();

		if (ViewTools.isFirstLaunch(this, PREF_ID)) {
			save = true;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onResume() {
		int w = ivBaro.getWidth();
		int h = ivBaro.getHeight();
		int l = w <= h ? w : h;
		if (l != 0) {
			ivBaro.setImageBitmap(BitmapTools.scaleBitmap(baro.drawUnitsMillibar(), l, l, true));
		}

		updateBaro();
		super.onResume();
	}

	private void updateBaro() {
		baroReader.startReading();
	}

	private void readValues() {

		SharedPreferences settings = getSharedPreferences(PREF_ID, 0);
		oldValue = settings.getFloat(PREF_BARO_OLD, -1);
	}

	private void writeValues() {
		SharedPreferences settings = getSharedPreferences(PREF_ID, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat(PREF_BARO_OLD, currentValue);
		editor.commit();
	}

}
