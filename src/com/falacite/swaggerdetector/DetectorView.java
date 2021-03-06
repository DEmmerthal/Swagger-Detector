package com.falacite.swaggerdetector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created with IntelliJ IDEA.
 * User: davidemmerthal
 * Date: 7/18/12
 * Time: 9:01 PM
 * To change this template use File | Settings | File Templates.
 */

public class DetectorView extends SurfaceView implements SurfaceHolder.Callback {
    class DetectorThread extends Thread {
        private SurfaceHolder _surfaceHolder;
        private boolean _run = false;
        private float _detectorPercentage = 0;
        private Paint _paint = new Paint();

        public DetectorThread(SurfaceHolder surfaceHolder, Context context) {
            _surfaceHolder = surfaceHolder;
            _paint.setColor(Color.RED);
            _paint.setStrokeWidth(300);
        }
        //Calculate detector percentage based off of a range between 7 and 9.8(Maximum gravity sensor output)
        public void setDetectorPercentage(float gravityY) {
            Log.d("gravityY", String.valueOf(gravityY));
            if (gravityY < 5){
                gravityY = 5;
            }
            else if (gravityY > 9.8){
                gravityY = (float) 9.8;
            }
            Log.d("gravityY", String.valueOf(gravityY));
            _detectorPercentage = (float) ((gravityY - 5)/(9.8 - 5));
        }

        public void setRunning(boolean run) {
            _run = run;
        }

        private void doDraw(Canvas c) {
            c.drawColor(Color.BLACK);
            Log.d("_detectorPercentage", String.valueOf(_detectorPercentage));
            Log.d("calculatedHeight", String.valueOf(c.getHeight() - (c.getHeight() * _detectorPercentage)));

            c.drawLine(350, c.getHeight(), 350, c.getHeight() - (c.getHeight() * _detectorPercentage), _paint);
        }

        @Override
        public void run() {
            Canvas c;
            while (_run) {
                c = null;
                try {
                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder) {
                        doDraw(c);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        _surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }

        }
    }

    private DetectorThread _thread;

    public DetectorView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        getHolder().addCallback(this);
        _thread = new DetectorThread(getHolder(), context);
    }

    public DetectorThread getThread() {
        return _thread;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        _thread.setRunning(true);
        _thread.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // simply copied from sample application LunarLander:
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        _thread.setRunning(false);
        while (retry) {
            try {
                _thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // we will try it again and again...
            }
        }

    }
}

