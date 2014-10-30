/*
 * Copyright 2014 Lukasz Skalski <lukasz.skalski@op.pl>
 *
 * This file is part of Raspberry Control.
 *
 * Raspberry Control is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Raspberry Control is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Raspberry Control.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.skalski.raspberrycontrol;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.skalski.raspberrycontrol.Mjpeg.Mjpeg_InputStream;
import com.skalski.raspberrycontrol.Mjpeg.Mjpeg_View;
import java.io.IOException;

public class Activity_Webcam extends Activity {

    private static final String LOGTAG = "RaspberryControl";
    private static final String LOGPREFIX = "WEBCAM: ";

    private Mjpeg_View mjpeg;
    private boolean pause;

    /*
     * Activity_Webcam - onCreate()
     */
	@Override
    public void onCreate(Bundle savedInstanceState) {

        String stream_url, info;
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        mjpeg = new Mjpeg_View(this);

        stream_url = Activity_Settings.pref_get_stream_url(getBaseContext());
        if (stream_url.length()==0) {
            stream_url = "http://194.168.163.96/axis-cgi/mjpg/video.cgi?resolution=320x240";
            info = getResources().getString(R.string.webcam_msg_1);
        } else {
            /* http://<hostname>:8082/?action=stream" */
            stream_url = "http://" + stream_url;
            info = getResources().getString(R.string.webcam_msg_2) + stream_url + getResources().getString(R.string.webcam_msg_3);
        }

        setContentView(mjpeg);

        try {
            mjpeg.setSource(Mjpeg_InputStream.read(stream_url));
            mjpeg.setDisplayMode(Mjpeg_View.SIZE_BEST_FIT);
            mjpeg.showFps(Activity_Settings.pref_get_fps_indicator(getBaseContext()));
            mjpeg.setKeepScreenOn(true);
            pause = false;
            Toast toast = Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG);
            toast.show();
            Log.i(LOGTAG, LOGPREFIX + "Connected to: " + stream_url);
        } catch (IOException e) {
            Log.e (LOGTAG, LOGPREFIX + "can't connect to stream: " + stream_url);
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_msg_11), Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
    }

    /*
     * Activity_Webcam - onPause()
     */
    public void onPause() {
        pause = true;
        if (mjpeg != null)
            mjpeg.stopPlayback();
        super.onPause();
    }

    /*
     * Activity_Webcam - onResume()
     */
    public void onResume() {
        super.onResume();
        if (pause) {
            mjpeg.resumePlayback();
            pause = false;
        }
    }

    /*
     * Activity_Webcam - onSaveInstanceState()
     */
    @Override
    public void onSaveInstanceState (@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}