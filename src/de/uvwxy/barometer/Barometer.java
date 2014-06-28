package de.uvwxy.barometer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import de.uvwxy.sensors.BarometerReader;
import de.uvwxy.units.Unit;

public class Barometer {
	private static final int TEXT_SIZE = 32;
	private final float degreesPerStep = +30f;
	private final float millsPerStep = 10f;
	private final float firstStepDegrees = -135f;
	private final float degreesPerMilliBar = degreesPerStep / millsPerStep;
	float[] valuesMillibar = new float[] { 960, 970, 980, 990, 1000, 1010, 1020, 1030, 1040, 1050 };

	double[][] valuesUnits = new double[][] {
			{ 860, 880, 900, 920, 940, 960, 980, 1000, 1020, 1040 },
			{ 0.86, 0.88, 0.9, 0.92, 0.94, 0.96, 0.98, 1, 1.02, 1.04 },
			{ 86000, 88000, 90000, 92000, 94000, 96000, 98000, 100000, 102000, 104000 },
			{ 600, 620, 640, 660, 680, 700, 720, 740, 760, 780 },
			{ 0.86, 0.88, 0.90, 0.92, 0.94, 0.96, 0.98, 1.00, 1.02, 1.04 },
			{ 0.84, 0.86, 0.88, 0.90, 0.92, 0.94, 0.96, 0.98, 1.00, 1.02 },
			{ 860, 880, 900, 920, 940, 960, 980, 1000, 1020, 1040 },
			{ 12.47324547, 12.76332094, 13.05339642, 13.3434719, 13.63354737, 13.92362285, 14.21369832, 14.5037738,
					14.79384928, 15.08392475 } };
	String[] pressureNames = new String[] { "%d mb", "%.3f bar", "%d Pa", "%d hPa", "%.3f at", "%.3f atm", "%d Torr",
			"%.3f psi", };
	String[] valuesUnitsDisplay = new String[] { "%.0f", "%.3f", "%.0f", "%.0f", "%.3f", "%.3f", "%.0f", "%.3f", };
	
	private final int textY = 64 + 128;

	private Bitmap face = null;
	private Bitmap knob = null;
	private Bitmap handle = null;
	private Bitmap handle_memory = null;

	protected void loadBitmaps(Context ctx) {

		Options o = new Options();
		o.inScaled = false;

		face = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.baro_face, o);
		knob = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.baro_nob, o);
		handle = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.baro_handle, o);
		handle_memory = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.baro_memory_handle, o);
	}

	protected float getFirstMillis() {
		return valuesMillibar[0];
	}

	private float millibarToDegrees(float millibars) {
		float deg = firstStepDegrees + (millibars - valuesMillibar[0]) * degreesPerMilliBar;
		float limLeft = firstStepDegrees - degreesPerMilliBar * 5;
		float limRight = limLeft * -1;
		deg = deg < limLeft ? limLeft : deg;
		deg = deg > limRight ? limRight : deg;

		return deg;
	}

	protected Bitmap drawUnitsWithMemory(float millisCurrent, float millisMemory, int typePressure, int typeLength) {
		Bitmap face = drawUnits(typePressure);

		Canvas canvas = new Canvas(face);
		Paint paint = new Paint();
		paint.setTextSize(TEXT_SIZE);
		paint.setAntiAlias(true);
		paint.setTextAlign(Align.CENTER);
		Matrix matrix = new Matrix();

		matrix.setRotate(millibarToDegrees(millisCurrent), canvas.getWidth() / 2, canvas.getHeight() / 2);
		canvas.drawBitmap(handle, matrix, paint);

		matrix.setRotate(millibarToDegrees(millisMemory), canvas.getWidth() / 2, canvas.getHeight() / 2);
		canvas.drawBitmap(handle_memory, matrix, paint);

		matrix = new Matrix();
		canvas.drawBitmap(knob, matrix, paint);

		canvas.drawText(
				Unit.from(Unit.MILLI_BAR).setValue(millisCurrent).to(FragmentSettings.getPressureUnit(typePressure))
						.toString(), canvas.getWidth() / 2, canvas.getHeight() - textY, paint);
		canvas.drawText(
				"["
						+ Unit.from(Unit.MILLI_BAR).setValue((millisCurrent - millisMemory))
								.to(FragmentSettings.getPressureUnit(typePressure)).toString() + "]",
				canvas.getWidth() / 2, canvas.getHeight() - textY + TEXT_SIZE, paint);

		canvas.drawText(
				"["
						+ Unit.from(Unit.METRE)
								.setValue(BarometerReader.getHeightFromDiff(millisCurrent, millisMemory))
								.to(FragmentSettings.getLengthUnit(typeLength)) + "]", canvas.getWidth() / 2,
				canvas.getHeight() - textY + 2 * TEXT_SIZE, paint);
		return face;
	}

	protected Bitmap drawUnits(int type) {
		Paint paint = new Paint();
		paint.setTextSize(TEXT_SIZE);
		paint.setAntiAlias(true);
		paint.setTextAlign(Align.CENTER);
		Matrix matrix = new Matrix();

		Bitmap bm = Bitmap.createBitmap(face.getWidth(), face.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bm);

		canvas.drawBitmap(face, matrix, paint);

		if (type > valuesUnits.length) {
			return bm;
		}

		canvas.save();

		canvas.rotate(firstStepDegrees, canvas.getWidth() / 2, canvas.getHeight() / 2);
		for (int i = 0; i < valuesUnits[type - 1].length; i++) {
			canvas.drawText(String.format(valuesUnitsDisplay[type - 1], valuesUnits[type - 1][i]),
					canvas.getWidth() / 2, textY, paint);
			canvas.rotate(degreesPerStep, canvas.getWidth() / 2, canvas.getHeight() / 2);
		}

		canvas.restore();

		return bm;
	}
}
