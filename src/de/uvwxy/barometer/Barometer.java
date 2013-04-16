package de.uvwxy.barometer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.Log;

public class Barometer {
	private static final int TEXT_SIZE = 16;
	private final float degreesPerStep = +30f;
	private final float millsPerStep = 10f;
	private final float firstStepDegrees = -134f;
	private final float degreesPerMilliBar = degreesPerStep / millsPerStep;
	float[] valuesMillibar = new float[] { 960, 970, 980, 990, 1000, 1010, 1020, 1030, 1040, 1050 };

	private final int textY = 64 + 48;

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
		Log.i("BARO", String.format("%f %f %f %f", firstStepDegrees, millibars, valuesMillibar[0], degreesPerMilliBar));
		Log.i("BARO", "Ret = " + (firstStepDegrees + (millibars - valuesMillibar[0]) * degreesPerMilliBar));
		return firstStepDegrees + (millibars - valuesMillibar[0]) * degreesPerMilliBar;
	}

	protected Bitmap drawMillisWithMemory(float millisCurrent, float millisMemory) {
		Bitmap face = drawUnitsMillibar();

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

		canvas.drawText(String.format("%.2f", millisCurrent), canvas.getWidth() / 2, canvas.getHeight() - textY - TEXT_SIZE, paint);

		return face;
	}

	protected Bitmap drawUnitsMillibar() {

		float degreesPerStep = +30f;
		float firstStepDegrees = -134f;

		Paint paint = new Paint();
		paint.setTextSize(TEXT_SIZE);
		paint.setAntiAlias(true);
		paint.setTextAlign(Align.CENTER);
		Matrix matrix = new Matrix();

		Bitmap bm = Bitmap.createBitmap(face.getWidth(), face.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bm);

		canvas.drawBitmap(face, matrix, paint);

		canvas.save();

		canvas.rotate(firstStepDegrees, canvas.getWidth() / 2, canvas.getHeight() / 2);
		for (int i = 0; i < valuesMillibar.length; i++) {
			canvas.drawText("" + (int) valuesMillibar[i], canvas.getWidth() / 2, textY, paint);
			canvas.rotate(degreesPerStep, canvas.getWidth() / 2, canvas.getHeight() / 2);
		}

		canvas.restore();

		canvas.drawText("mb", canvas.getWidth() / 2, canvas.getHeight() - textY, paint);

		return bm;
	}

	private void drawUnitsHg() {
		// TODO: ...
	}
}
