package com.nilhcem.hoodie;

import android.graphics.Canvas;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.Gravity;
import android.view.SurfaceHolder;

import com.nilhcem.hoodie.core.WatchMode;

public class MyWatchFace extends BaseWatchFaceService {

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends BaseWatchFaceService.Engine {

        private MyWatchFaceRenderer watch;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            watch = new MyWatchFaceRenderer(context);

            setWatchFaceStyle(new WatchFaceStyle.Builder(MyWatchFace.this)
                    .setAcceptsTapEvents(false)
                    .setShowUnreadCountIndicator(true)
                    .setStatusBarGravity(Gravity.TOP)
                    .setViewProtectionMode(WatchFaceStyle.PROTECT_STATUS_BAR | WatchFaceStyle.PROTECT_HOTWORD_INDICATOR)
                    .build());
        }

        @Override
        protected void onWatchModeChanged(WatchMode mode) {
            watch.setMode(mode);
        }

        @Override
        protected void onScreenSizeChanged(int newWidth, int newHeight, int chinSize) {
            watch.setSize(newWidth, newHeight, chinSize);
            invalidate();
        }

        @Override
        protected void onDrawTime(Canvas canvas, float angleHours, float angleMinutes) {
            watch.drawTime(canvas, angleHours, angleMinutes);
        }
    }
}
