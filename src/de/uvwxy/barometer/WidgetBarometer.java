package de.uvwxy.barometer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import de.uvwxy.sensors.BarometerReader;
import de.uvwxy.sensors.SensorReader.SensorResultCallback;

public class WidgetBarometer extends AppWidgetProvider {
    protected final static String PREF_ID = "BARO_SETTINGS";
    private final static String PREF_BARO_OLD = "BARO_SETTINGS";

    private static final String UPDATE_BARO = "UPDATE_BARO";

    private BarometerReader baroReader = null;
    private Barometer baro = null;
    private float oldValue;
    private float currentValue;
    private Context ctx;

    private Bitmap getUnitsBitmap() {
        return baro.drawUnitsWithMemory(currentValue, oldValue, FragmentSettings.loadPressureUnit(ctx),
                FragmentSettings.loadLengthUnit(ctx));

    }

    private void init(Context ctx) {
        this.ctx = ctx;

        if (baroReader == null) {
            baroReader = new BarometerReader(ctx, 50, cb);
        }

        if (baro == null) {
            baro = new Barometer();
            baro.loadBitmaps(ctx);
        }
    }

    private SensorResultCallback cb = new SensorResultCallback() {
        @Override
        public void result(float[] f) {
            if (f != null && f.length >= 1) {
                currentValue = f[0];
                currentValue = FragmentSettings.getDiffedBaro(ctx, currentValue);

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ctx);

                RemoteViews remoteViews;
                ComponentName watchWidget;

                remoteViews = new RemoteViews(ctx.getPackageName(), R.layout.widget_main);
                watchWidget = new ComponentName(ctx, WidgetBarometer.class);
                Bitmap bitmap = getUnitsBitmap();
                remoteViews.setImageViewBitmap(R.id.ivWBaro, bitmap);
                appWidgetManager.updateAppWidget(watchWidget, remoteViews);
            }
        }
    };

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        init(context);
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Intent to register clicks
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_main);
            views.setOnClickPendingIntent(R.id.ivWBaro, getPendingSelfIntent(context, UPDATE_BARO));

            appWidgetManager.updateAppWidget(appWidgetId, views);

            readValues();
            baroReader.startReading();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        init(context);

        super.onReceive(context, intent);

        if (UPDATE_BARO.equals(intent.getAction())) {
            readValues();
            baroReader.startReading();
        }

    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private void readValues() {
        SharedPreferences settings = ctx.getSharedPreferences(PREF_ID, 0);
        oldValue = settings.getFloat(PREF_BARO_OLD, -1);
    }

}
