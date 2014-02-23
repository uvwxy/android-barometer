package de.uvwxy.barometer;

import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import de.uvwxy.helper.IntentTools;

public class FragmentSettings extends Fragment {

	private static final String SETTINGS = null;
	private static final String SETTINGS_LENGHT_TYPE = null;
	private static final String SETTINGS_PRESSURE_TYPE = null;
	private static final int SETTINGS_METRES = 0;
	private static final int SETTINGS_KILOMETRES = 0;
	private static final int SETTINGS_FOOT = 0;
	private static final int SETTINGS_MILES = 0;
	private static final int SETTINGS_YARDS = 0;
	private static final int SETTINGS_PRESSURE_MBAR = 0;
	private static final int SETTINGS_PRESSURE_BAR = 0;
	private static final int SETTINGS_PRESSURE_PA = 0;
	private static final int SETTINGS_PRESSURE_AT = 0;
	private static final int SETTINGS_PRESSURE_ATM = 0;
	private static final int SETTINGS_PRESSURE_TORR = 0;
	private static final int SETTINGS_PRESSURE_PSI = 0;
	private RadioGroup rbgLengths = null;
	private RadioButton rbMetres = null;
	private RadioButton rbKilometres = null;
	private RadioButton rbFoot = null;
	private RadioButton rbMiles = null;
	private RadioButton rbYards = null;

	private RadioGroup rbgPressure = null;
	private RadioButton rbMBar = null;
	private RadioButton rbBar = null;
	private RadioButton rbPa = null;
	private RadioButton rbAt = null;
	private RadioButton rbAtm = null;
	private RadioButton rbTorr = null;
	private RadioButton rbPsi = null;
	private float origBaro;

	private void initGUI(View r) {

		rbgLengths = (RadioGroup) r.findViewById(R.id.rbgLengths);
		rbMetres = (RadioButton) r.findViewById(R.id.rbMetres);
		rbKilometres = (RadioButton) r.findViewById(R.id.rbKilometres);
		rbFoot = (RadioButton) r.findViewById(R.id.rbFoot);
		rbMiles = (RadioButton) r.findViewById(R.id.rbMiles);
		rbYards = (RadioButton) r.findViewById(R.id.rbYards);


		rbgPressure = (RadioGroup) r.findViewById(R.id.rbgPressure);
		rbMBar = (RadioButton) r.findViewById(R.id.rbMBar);
		rbBar = (RadioButton) r.findViewById(R.id.rbBar);
		rbPa = (RadioButton) r.findViewById(R.id.rbPa);
		rbAt = (RadioButton) r.findViewById(R.id.rbAt);
		rbAtm = (RadioButton) r.findViewById(R.id.rbAtm);
		rbTorr = (RadioButton) r.findViewById(R.id.rbTorr);
		rbPsi = (RadioButton) r.findViewById(R.id.rbPsi);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
		initGUI(rootView);

		

		final Activity act = getActivity();
		String set = SETTINGS;
		String setL = SETTINGS_LENGHT_TYPE;
		String setP = SETTINGS_PRESSURE_TYPE;

		IntentTools.switchSettings(act, rbMetres, set, setL, SETTINGS_METRES);
		IntentTools.switchSettings(act, rbKilometres, set, setL, SETTINGS_KILOMETRES);
		IntentTools.switchSettings(act, rbFoot, set, setL, SETTINGS_FOOT);
		IntentTools.switchSettings(act, rbMiles, set, setL, SETTINGS_MILES);
		IntentTools.switchSettings(act, rbYards, set, setL, SETTINGS_YARDS);

		IntentTools.switchSettings(act, rbMBar, set, setP, SETTINGS_PRESSURE_MBAR);
		IntentTools.switchSettings(act, rbBar, set, setP, SETTINGS_PRESSURE_BAR);
		IntentTools.switchSettings(act, rbPa, set, setP, SETTINGS_PRESSURE_PA);
		IntentTools.switchSettings(act, rbAt, set, setP, SETTINGS_PRESSURE_AT);
		IntentTools.switchSettings(act, rbAtm, set, setP, SETTINGS_PRESSURE_ATM);
		IntentTools.switchSettings(act, rbTorr, set, setP, SETTINGS_PRESSURE_TORR);
		IntentTools.switchSettings(act, rbPsi, set, setP, SETTINGS_PRESSURE_PSI);

		rbMetres.setOnClickListener(updateUnits);
		rbKilometres.setOnClickListener(updateUnits);
		rbFoot.setOnClickListener(updateUnits);
		rbMiles.setOnClickListener(updateUnits);
		rbYards.setOnClickListener(updateUnits);
		
		rbMBar.setOnClickListener(updateUnits);
		rbBar.setOnClickListener(updateUnits);
		rbPa.setOnClickListener(updateUnits);
		rbAt.setOnClickListener(updateUnits);
		rbAtm.setOnClickListener(updateUnits);
		rbTorr.setOnClickListener(updateUnits);
		rbPsi.setOnClickListener(updateUnits);

		
		if (!loadedOnce) {
			loadedOnce = true;
			loadDiff(act);
		}


		TextView tvBaroCurrent = (TextView) rootView.findViewById(R.id.tvBaroCurrent);
		TextView tvBaroCurrentValue = (TextView) rootView.findViewById(R.id.tvBaroCurrentValue);
		TextView tvBaroRef = (TextView) rootView.findViewById(R.id.tvBaroRef);
		TextView tvBaroDiff = (TextView) rootView.findViewById(R.id.tvBaroDiff);
		Button btnReset = (Button) rootView.findViewById(R.id.btnReset);
		Button btnSave = (Button) rootView.findViewById(R.id.btnSave);

		final EditText etBaroRefValue = (EditText) rootView.findViewById(R.id.etBaroRefValue);
		final TextView tvBaroDiffValue = (TextView) rootView.findViewById(R.id.tvBaroDiffValue);

		tvBaroCurrentValue.setText(String.format(Locale.US, "%.2f", origBaro));

		if (baroDiff != BARO_NO_DIFF) {
			etBaroRefValue.setText(String.format(Locale.US, "%.2f", (origBaro + baroDiff)));
			tvBaroDiffValue.setText(String.format(Locale.US, "%.2f", baroDiff));
		} else {
			etBaroRefValue.setText("");
			tvBaroDiffValue.setText("");
		}

	

		btnReset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				baroDiff = 0f;
				saveDiff(act);
				etBaroRefValue.setText(String.format(Locale.US, "%.2f", (origBaro + baroDiff)));
				tvBaroDiffValue.setText(String.format(Locale.US, "%.2f", baroDiff));
			}
		});

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					baroDiff = Float.parseFloat(etBaroRefValue.getText().toString()) - origBaro;
					saveDiff(act.getApplicationContext());
					Toast.makeText(act.getApplicationContext(), R.string.click_to_refresh, Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					Toast.makeText(act.getApplicationContext(), R.string.error_reading_reference_value, Toast.LENGTH_SHORT).show();
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


		return rootView;
	}

	private android.view.View.OnClickListener updateUnits = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			MainActivity.dhis.fBarometer.updateUnits();
		}
	};
	
	protected final static String PREF_ID = "BARO_SETTINGS";
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
		SharedPreferences settings = ctx.getSharedPreferences(PREF_ID, 0);
		baroDiff = settings.getFloat(BARO_DIFF, BARO_NO_DIFF);
		return baroDiff == BARO_NO_DIFF;
	}

	public static void saveDiff(Context ctx) {
		SharedPreferences settings = ctx.getSharedPreferences(PREF_ID, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat(BARO_DIFF, baroDiff);
		editor.commit();
	}
}