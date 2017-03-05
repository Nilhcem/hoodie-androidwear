package com.nilhcem.hoodie;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.view.SurfaceHolder;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.nilhcem.hoodie.core.TimerHelper;
import com.nilhcem.hoodie.core.TimezoneHelper;
import com.nilhcem.hoodie.core.WatchMode;

import java.util.Calendar;

public abstract class BaseWatchFaceService extends CanvasWatchFaceService {

    @Override
    public CanvasWatchFaceService.Engine onCreateEngine() {
        CanvasWatchFaceService.Engine engine = super.onCreateEngine();
        if (BuildConfig.DEBUG && !(engine instanceof Engine)) {
            throw new IllegalStateException("Engine must be an instance of BaseWatchFaceService.Engine");
        }
        return engine;
    }

    public abstract class Engine extends CanvasWatchFaceService.Engine {

        protected Context context;

        protected int width;
        protected int height;

        private TimezoneHelper timezoneHelper;
        private TimerHelper timerHelper;
        private boolean ambient;
        private boolean lowBitAmbient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            context = BaseWatchFaceService.this;
            timezoneHelper = new TimezoneHelper(BaseWatchFaceService.this, this);
            timerHelper = new TimerHelper(this, getUpdateRateMs());
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);

            if (ambient != inAmbientMode) {
                ambient = inAmbientMode;
                onWatchModeChanged(getCurrentWatchMode());
                invalidate();
                timerHelper.updateTimer();
            }
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            Point screenSize = new Point();
            ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(screenSize);
            int chinSize = insets.getSystemWindowInsetBottom();
            int newWidth = screenSize.x;
            int newHeight = screenSize.y;
            if (width != newWidth || height != newHeight) {
                onScreenSizeChanged(newWidth, newHeight, chinSize);
                width = newWidth;
                height = newHeight;
            }
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);
            timezoneHelper.setTimeToNow();

            Calendar calendar = timezoneHelper.getCalendar();
            float seconds = calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND) / 1000f;
            float minutes = calendar.get(Calendar.MINUTE) + seconds / 60f;
            float hours = calendar.get(Calendar.HOUR) + minutes / 60f;
            onDrawTime(canvas, hours * 30f, minutes * 6f);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            lowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            timezoneHelper.onVisibilityChanged(visible);
            timerHelper.updateTimer();
        }

        private WatchMode getCurrentWatchMode() {
            WatchMode watchMode;
            if (ambient) {
                if (lowBitAmbient) {
                    watchMode = WatchMode.LOW_BIT;
                } else {
                    watchMode = WatchMode.AMBIENT;
                }
            } else {
                watchMode = WatchMode.INTERACTIVE;
            }
            return watchMode;
        }

        protected int getUpdateRateMs() {
            return 1000;
        }

        protected abstract void onDrawTime(Canvas canvas, float hoursRotation, float minutesRotation);

        protected abstract void onWatchModeChanged(WatchMode mode);

        protected abstract void onScreenSizeChanged(int newWidth, int newHeight, int chinSize);
    }
}
