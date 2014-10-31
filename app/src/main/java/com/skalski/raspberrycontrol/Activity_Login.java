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
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import com.rampo.updatechecker.UpdateChecker;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.SuperActivityToast;

public class Activity_Login extends Activity implements OnClickListener {

    private static final String LOGTAG = "RaspberryControl";
    private static final String LOGPREFIX = "LOGIN: ";
    private Custom_WebSocketClient client;

    Button login_button;
    EditText hostname;
    EditText portnumber;
    EditText timeout;

    /*
     * Activity_Login - onCreate()
     */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String exit_status = extras.getString("exit");

            if (exit_status.equals("yes")){
                client = ((Custom_WebSocketClient) getApplicationContext());
                if (client.isConnected()) {
                    client.disconnect();
                    Log.i (LOGTAG, LOGPREFIX + "client disconnected");
                }
                finish();
            }
        }

        setContentView(R.layout.activity_login);

        /* check for updates */
        UpdateChecker checker = new UpdateChecker(this);
        checker.start();

        /* TODO - temporary solution for Webcam purpose - should be removed soon */
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        login_button = (Button) findViewById (R.id.login_btn);
        hostname = (EditText) findViewById (R.id.login_ip);
        portnumber = (EditText) findViewById (R.id.login_port);
        timeout = (EditText) findViewById (R.id.login_timeout);
        login_button.setOnClickListener(this);
    }

    /*
     * Activity_Login - onClick()
     */
    @Override
    public void onClick (View v) {

        client = ((Custom_WebSocketClient) getApplicationContext());
        Activity_Settings.pref_set_hostname(getBaseContext(), hostname.getText().toString());
        Activity_Settings.pref_set_port_number(getBaseContext(), portnumber.getText().toString());
        Activity_Settings.pref_set_timeout(getBaseContext(), timeout.getText().toString());

        switch (v.getId()){
            case R.id.login_btn:
                if (client.connect()) {
                    Log.i (LOGTAG, LOGPREFIX + "connection established");
                    Intent intent = new Intent(getApplicationContext(), Activity_MainMenu.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Log.e (LOGTAG, LOGPREFIX + "no connection to the server");
                    SuperActivityToast superActivityToast = new SuperActivityToast(this);
                    superActivityToast.setDuration(SuperToast.Duration.LONG);
                    superActivityToast.setAnimations(SuperToast.Animations.FLYIN);
                    superActivityToast.setBackground(SuperToast.Background.RED);
                    superActivityToast.setText(getResources().getString(R.string.error_msg_6));
                    superActivityToast.setIcon(SuperToast.Icon.Dark.INFO, SuperToast.IconPosition.LEFT);
                    superActivityToast.show();
                }
            break;
        }
    }

    /*
     * Activity_Login - onResume()
     */
    @Override
    public void onResume() {

        hostname.setText(Activity_Settings.pref_get_hostname(getBaseContext()));
        portnumber.setText(Activity_Settings.pref_get_port_number(getBaseContext()));
        timeout.setText(Activity_Settings.pref_get_timeout(getBaseContext()));
        super.onResume();
    }
}
