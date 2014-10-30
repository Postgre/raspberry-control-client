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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.github.johnpersano.supertoasts.SuperActivityToast;

public class Activity_Terminal extends Activity {

    private static final String LOGTAG = "RaspberryControl";
    private static final String LOGPREFIX = "TERMINAL: ";

    private WebView webview;
    private String URL;

    /*
     * Activity_Terminal - onStart()
     */
	public void onStart() {
		super.onStart();
	}

    /*
     * Activity_Terminal - onResume()
     */
	public void onResume() {
		super.onResume();
	}

    /*
     * Activity_Terminal - onCreate()
     */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

        URL = "http://" + Activity_Settings.pref_get_hostname(getBaseContext()) + ":";
        URL = URL + Activity_Settings.pref_get_terminal_port_number(getBaseContext());
        Log.i(LOGTAG, LOGPREFIX + "connecting to: " + URL);

        webview =(WebView)findViewById(R.id.shell_webView);
        webview.setWebViewClient(new WebViewClient());

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setSupportZoom(false);
        webview.getSettings().setAllowContentAccess(false);
        webview.getSettings().setTextZoom(Integer.parseInt(Activity_Settings.pref_get_terminal_font(getBaseContext())));
        webview.loadUrl(URL);
    }

    /*
     * Activity_Terminal - onSaveInstanceState()
     */
    @Override
    public void onSaveInstanceState (@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        SuperActivityToast.onSaveState(outState);
    }
}