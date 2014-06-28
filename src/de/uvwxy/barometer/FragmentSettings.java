package de.uvwxy.barometer;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import de.uvwxy.helper.IntentTools;
import de.uvwxy.sensors.BarometerReader;
import de.uvwxy.sensors.SensorReader.SensorResultCallback;
import de.uvwxy.units.Unit;
import de.uvwxy.units.UnitPrefix;

public class FragmentSettings extends Fragment {

	private static final String SETTINGS = "BARO_SETTINGS";
	private static final String SETTINGS_LENGHT_TYPE = "BARO_UNIT";
	private static final String SETTINGS_PRESSURE_TYPE = "LENGTH_UNIT";
	private static final int SETTINGS_0_METRES = 1;
	private static final int SETTINGS_1_KILOMETRES = 2;
	private static final int SETTINGS_2_FOOT = 3;
	private static final int SETTINGS_3_MILES = 4;
	private static final int SETTINGS_4_YARDS = 5;
	private static final int SETTINGS_PRESSURE_0_MBAR = 1;
	private static final int SETTINGS_PRESSURE_1_BAR = 2;
	private static final int SETTINGS_PRESSURE_2_PA = 3;
	private static final int SETTINGS_PRESSURE_3_HPA = 4;
	private static final int SETTINGS_PRESSURE_4_AT = 5;
	private static final int SETTINGS_PRESSURE_5_ATM = 6;
	private static final int SETTINGS_PRESSURE_6_TORR = 7;
	private static final int SETTINGS_PRESSURE_7_PSI = 8;

	@SuppressWarnings("unused")
	private RadioGroup rbgLengths = null;
	private RadioButton rbMetres = null;
	private RadioButton rbKilometres = null;
	private RadioButton rbFoot = null;
	private RadioButton rbMiles = null;
	private RadioButton rbYards = null;

	@SuppressWarnings("unused")
	private RadioGroup rbgPressure = null;
	private RadioButton rbMBar = null;
	private RadioButton rbBar = null;
	private RadioButton rbPa = null;
	private RadioButton rbHPa = null;
	private RadioButton rbAt = null;
	private RadioButton rbAtm = null;
	private RadioButton rbTorr = null;
	private RadioButton rbPsi = null;

	private TextView tvBaroCurrentValue = null;

	private EditText etBaroRefValue = null;

	private Unit unitPressure;
	private Unit unitLength;

	private float origBaro;
	private float currentValue = -1f;
	private float offSet;
	private BarometerReader baroReader = null;
	private SensorResultCallback cb = new SensorResultCallback() {

		@Override
		public void result(float[] f) {
			if (f != null && f.length >= 1) {
				origBaro = f[0];

				String et = etBaroRefValue.getText().toString();

				currentValue = origBaro + offSet;

				tvBaroCurrentValue.setText(Unit.from(Unit.MILLI_BAR).setValue(origBaro).to(unitPressure).toString());
			}
		}
	};

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
		rbHPa = (RadioButton) r.findViewById(R.id.rbHPa);
		rbAt = (RadioButton) r.findViewById(R.id.rbAt);
		rbAtm = (RadioButton) r.findViewById(R.id.rbAtm);
		rbTorr = (RadioButton) r.findViewById(R.id.rbTorr);
		rbPsi = (RadioButton) r.findViewById(R.id.rbPsi);

		tvBaroCurrentValue = (TextView) r.findViewById(R.id.tvBaroCurrentValue);

		etBaroRefValue = (EditText) r.findViewById(R.id.etBaroRefValue);
	}

	private void loadSettings() {
		final Activity act = getActivity();
		SharedPreferences prefs = IntentTools.getSettings(act, SETTINGS);
		int p = prefs.getInt(SETTINGS_PRESSURE_TYPE, SETTINGS_PRESSURE_0_MBAR);
		int l = prefs.getInt(SETTINGS_LENGHT_TYPE, SETTINGS_0_METRES);
		Log.d("BARO", "p=" + p + ", l=" + l);
		switch (p) {
		case SETTINGS_PRESSURE_0_MBAR:
			rbMBar.setSelected(true);
			unitPressure = Unit.from(Unit.MILLI_BAR);
			break;
		case SETTINGS_PRESSURE_1_BAR:
			rbBar.setSelected(true);
			unitPressure = Unit.from(Unit.BAR);
			break;
		case SETTINGS_PRESSURE_2_PA:
			rbPa.setSelected(true);
			unitPressure = Unit.from(Unit.PASCAL);
			break;
		case SETTINGS_PRESSURE_3_HPA:
			rbHPa.setSelected(true);
			unitPressure = Unit.from(Unit.HECTO_PASCAL);
			break;
		case SETTINGS_PRESSURE_4_AT:
			rbAt.setSelected(true);
			unitPressure = Unit.from(Unit.TECHNICAL_ATMOSPHERE);
			break;
		case SETTINGS_PRESSURE_5_ATM:
			rbAtm.setSelected(true);
			unitPressure = Unit.from(Unit.STANDARD_ATMOSPHERE);
			break;
		case SETTINGS_PRESSURE_6_TORR:
			rbTorr.setSelected(true);
			unitPressure = Unit.from(Unit.TORR);
			break;
		case SETTINGS_PRESSURE_7_PSI:
			rbPsi.setSelected(true);
			unitPressure = Unit.from(Unit.POUNDS_PER_SQUARE_INCH);
			break;
		default:
			rbMBar.setSelected(true);
			unitPressure = Unit.from(Unit.MILLI_BAR);

		}

		switch (l) {
		case SETTINGS_0_METRES:
			rbMetres.setSelected(true);
			unitLength = Unit.from(Unit.METRE);
			break;
		case SETTINGS_1_KILOMETRES:
			rbKilometres.setSelected(true);
			unitLength = Unit.from(Unit.KILOMETRES_PER_HOUR);
			break;
		case SETTINGS_2_FOOT:
			rbFoot.setSelected(true);
			unitLength = Unit.from(Unit.FOOT);
			break;
		case SETTINGS_3_MILES:
			rbMiles.setSelected(true);
			unitLength = Unit.from(Unit.MILE);
			break;
		case SETTINGS_4_YARDS:
			rbYards.setSelected(true);
			unitLength = Unit.from(Unit.YARD);
			break;
		default:
			rbMetres.setSelected(true);
			unitLength = Unit.from(Unit.METRE);

		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
		initGUI(rootView);
		loadSettings();
		final Activity act = getActivity();
		String set = SETTINGS;
		String setL = SETTINGS_LENGHT_TYPE;
		String setP = SETTINGS_PRESSURE_TYPE;

		IntentTools.switchSettings(act, rbMetres, set, setL, SETTINGS_0_METRES);
		IntentTools.switchSettings(act, rbKilometres, set, setL, SETTINGS_1_KILOMETRES);
		IntentTools.switchSettings(act, rbFoot, set, setL, SETTINGS_2_FOOT);
		IntentTools.switchSettings(act, rbMiles, set, setL, SETTINGS_3_MILES);
		IntentTools.switchSettings(act, rbYards, set, setL, SETTINGS_4_YARDS);

		IntentTools.switchSettings(act, rbMBar, set, setP, SETTINGS_PRESSURE_0_MBAR);
		IntentTools.switchSettings(act, rbBar, set, setP, SETTINGS_PRESSURE_1_BAR);
		IntentTools.switchSettings(act, rbPa, set, setP, SETTINGS_PRESSURE_2_PA);
		IntentTools.switchSettings(act, rbHPa, set, setP, SETTINGS_PRESSURE_3_HPA);
		IntentTools.switchSettings(act, rbAt, set, setP, SETTINGS_PRESSURE_4_AT);
		IntentTools.switchSettings(act, rbAtm, set, setP, SETTINGS_PRESSURE_5_ATM);
		IntentTools.switchSettings(act, rbTorr, set, setP, SETTINGS_PRESSURE_6_TORR);
		IntentTools.switchSettings(act, rbPsi, set, setP, SETTINGS_PRESSURE_7_PSI);

		rbMetres.setOnClickListener(updateUnits);
		rbKilometres.setOnClickListener(updateUnits);
		rbFoot.setOnClickListener(updateUnits);
		rbMiles.setOnClickListener(updateUnits);
		rbYards.setOnClickListener(updateUnits);

		rbMBar.setOnClickListener(updateUnits);
		rbBar.setOnClickListener(updateUnits);
		rbPa.setOnClickListener(updateUnits);
		rbHPa.setOnClickListener(updateUnits);
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
					Toast.makeText(act.getApplicationContext(), R.string.error_reading_reference_value,
							Toast.LENGTH_SHORT).show();
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

		etBaroRefValue.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					offSet = Integer.parseInt(s.toString());
				} catch (Exception e) {
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		baroReader = new BarometerReader(act.getApplicationContext(), 0, cb);
		baroReader.startReading();
		return rootView;
	}

	@Override
	public void onDestroyView() {
		baroReader.stopReading();
		super.onDestroyView();
	}

	private android.view.View.OnClickListener updateUnits = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			//			MainActivity.dhis.fBarometer.updateUnits();
		}
	};

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
		SharedPreferences settings = ctx.getSharedPreferences(SETTINGS, 0);
		baroDiff = settings.getFloat(BARO_DIFF, BARO_NO_DIFF);
		return baroDiff == BARO_NO_DIFF;
	}

	public static void saveDiff(Context ctx) {
		SharedPreferences settings = ctx.getSharedPreferences(SETTINGS, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putFloat(BARO_DIFF, baroDiff);
		editor.commit();
	}

	public static int loadPressureUnit(Context ctx) {
		SharedPreferences settings = IntentTools.getSettings(ctx, SETTINGS);
		return settings.getInt(SETTINGS_PRESSURE_TYPE, 1);
	}

	public static int loadLengthUnit(Context ctx) {
		SharedPreferences settings = IntentTools.getSettings(ctx, SETTINGS);
		return settings.getInt(SETTINGS_LENGHT_TYPE, 1);
	}

	public static Unit getPressureUnit(int i) {
		switch (i) {
		case SETTINGS_PRESSURE_0_MBAR: // SETTINGS_PRESSURE_0_MBAR
			return Unit.from(Unit.MILLI_BAR);
		case SETTINGS_PRESSURE_1_BAR: // SETTINGS_PRESSURE_1_BAR
			return Unit.from(Unit.BAR);
		case SETTINGS_PRESSURE_2_PA: // SETTINGS_PRESSURE_2_PA
			return Unit.from(Unit.PASCAL);
		case SETTINGS_PRESSURE_3_HPA: // SETTINGS_PRESSURE_3_HPA
			return Unit.from(Unit.HECTO_PASCAL);
		case SETTINGS_PRESSURE_4_AT: // SETTINGS_PRESSURE_4_AT
			return Unit.from(Unit.TECHNICAL_ATMOSPHERE);
		case SETTINGS_PRESSURE_5_ATM: // SETTINGS_PRESSURE_5_ATM
			return Unit.from(Unit.STANDARD_ATMOSPHERE);
		case SETTINGS_PRESSURE_6_TORR: // SETTINGS_PRESSURE_6_TORR
			return Unit.from(Unit.TORR);
		case SETTINGS_PRESSURE_7_PSI: // SETTINGS_PRESSURE_7_PSI
			return Unit.from(Unit.POUNDS_PER_SQUARE_INCH);
		default: // SETTINGS_PRESSURE_0_MBAR
			return Unit.from(Unit.MILLI_BAR);
		}
	}

	public static Unit getLengthUnit(int i) {
		switch (i) {
		case SETTINGS_0_METRES:
			return Unit.from(Unit.METRE);
		case SETTINGS_1_KILOMETRES:
			return Unit.from(Unit.METRE).setPrefix(UnitPrefix.KILO);
		case SETTINGS_2_FOOT:
			return Unit.from(Unit.FOOT);
		case SETTINGS_3_MILES:
			return Unit.from(Unit.MILE);
		case SETTINGS_4_YARDS:
			return Unit.from(Unit.YARD);
		default:
			return Unit.from(Unit.METRE);
		}
	}
}