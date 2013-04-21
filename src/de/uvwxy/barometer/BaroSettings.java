package de.uvwxy.barometer;

import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BaroSettings {
	protected static final String BARO_DIFF = "BARO_DIFF";

	private static final float BARO_NO_DIFF = 0;
	private static float baroDiff = BARO_NO_DIFF;

	private static boolean loadedOnce = false;

	public static float getDiffedBaro(Context ctx, float origBaro) {
		if (!loadedOnce) {
			loadedOnce = true;
			loadDiff(ctx);
		}

		return origBaro + baroDiff;
	}

	public static void setDiff(Context ctx, float origBaro, float refBaro) {
		baroDiff = refBaro - origBaro;
		saveDiff(ctx);
	}

	public static boolean loadDiff(Context ctx) {
		SharedPreferences settings = ctx.getSharedPreferences(MainActivity.PREF_ID, 0);
		baroDiff = settings.getFloat(BARO_DIFF, BARO_NO_DIFF);
		return baroDiff == BARO_NO_DIFF;
	}

	public static void saveDiff(Context ctx) {
		SharedPreferences settings = ctx.getSharedPreferences(MainActivity.PREF_ID, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat(BARO_DIFF, baroDiff);
		editor.commit();
	}

	public static void showConfig(final Context ctx, final float origBaro) {
		if (!loadedOnce) {
			loadedOnce = true;
			loadDiff(ctx);
		}

		final Dialog dialog = new Dialog(ctx);

		dialog.setContentView(R.layout.baro_settings);
		dialog.setTitle("Barometer Settings");

		TextView tvBaroCurrent = (TextView) dialog.findViewById(R.id.tvBaroCurrent);
		TextView tvBaroCurrentValue = (TextView) dialog.findViewById(R.id.tvBaroCurrentValue);
		TextView tvBaroRef = (TextView) dialog.findViewById(R.id.tvBaroRef);
		TextView tvBaroDiff = (TextView) dialog.findViewById(R.id.tvBaroDiff);
		Button btnReset = (Button) dialog.findViewById(R.id.btnReset);
		Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
		Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

		final EditText etBaroRefValue = (EditText) dialog.findViewById(R.id.etBaroRefValue);
		final TextView tvBaroDiffValue = (TextView) dialog.findViewById(R.id.tvBaroDiffValue);

		tvBaroCurrentValue.setText(String.format(Locale.US, "%.2f", origBaro));

		if (baroDiff != BARO_NO_DIFF) {
			etBaroRefValue.setText(String.format(Locale.US, "%.2f", (origBaro + baroDiff)));
			tvBaroDiffValue.setText(String.format(Locale.US, "%.2f", baroDiff));
		} else {
			etBaroRefValue.setText("");
			tvBaroDiffValue.setText("");
		}

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();

			}
		});

		btnReset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				baroDiff = 0f;
				saveDiff(ctx);
				etBaroRefValue.setText(String.format(Locale.US, "%.2f", (origBaro + baroDiff)));
				tvBaroDiffValue.setText(String.format(Locale.US, "%.2f", baroDiff));
			}
		});

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					baroDiff = Float.parseFloat(etBaroRefValue.getText().toString()) - origBaro;
					saveDiff(ctx);
					dialog.dismiss();
					Toast.makeText(ctx, R.string.click_to_refresh, Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					Toast.makeText(ctx, R.string.error_reading_reference_value, Toast.LENGTH_SHORT).show();
				}
			}
		});

		etBaroRefValue.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					float x = Float.parseFloat(etBaroRefValue.getText().toString());
					float x_diff = x - origBaro;
					tvBaroDiffValue.setText(String.format(Locale.US, "%.2f", x_diff));
				} catch (Exception e) {
					tvBaroDiffValue.setText("--");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		dialog.show();
	}
}
