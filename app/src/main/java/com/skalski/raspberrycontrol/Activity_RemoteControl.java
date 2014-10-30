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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import org.json.JSONObject;

public class Activity_RemoteControl extends Activity {

    private static final String LOGTAG = "RaspberryControl";
    private static final String LOGPREFIX = "REMOTEIR: ";
    private static final String TAG_ERROR = "Error";

    private Custom_WebSocketClient client;
    private Handler clientHandler;
    Button[] button = new Button[44];
    String device;

    /*
     * Activity_RemoteControl - onCreate()
     */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remotecontrol);

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                String button;
                button = (String) v.getTag();

                if (device == null)
                    toast_connection_error (getResources().getString(R.string.error_msg_10));
                else
                    sendCommand (device, button);
            }
        };

        OnLongClickListener listener1 = new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog diaBox = makeAndShowDialogBox(v);
                diaBox.show();
                return true;
            }
        };

        for(int i=0; i<44; ++i) {
            String buttonID = String.format("button%02d", i);
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            button[i] = ((Button) findViewById(resID));
            button[i].setOnClickListener(listener);
            button[i].setOnLongClickListener(listener1);
        }
    }

    /*
     * Activity_RemoteControl - getClientHandler()
     */
    Handler getClientHandler() {

        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                JSONObject root;
                Log.i (LOGTAG, LOGPREFIX + "new message received from server");

                try {

                    root = new JSONObject(msg.obj.toString());

                    if (root.has(TAG_ERROR)) {
                        String err = getResources().getString(R.string.com_msg_3) + root.getString(TAG_ERROR);
                        Log.e (LOGTAG, LOGPREFIX + root.getString(TAG_ERROR));
                        toast_connection_error (err);
                    }

                } catch (Exception ex) {
                    Log.e (LOGTAG, LOGPREFIX + "received invalid JSON object");
                    toast_connection_error (getResources().getString(R.string.error_msg_2));
                }
            }
        };
    }

    /*
     * Activity_RemoteControl - setTags()
     */
    public void setTags() {

        for(int i=0; i<44; ++i) {

            String tag;
            String tag_button;

            tag_button = String.format("tag_" + button[i].getId());
            tag = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString(tag_button, null);
            if (tag != null)
                button[i].setTag(tag);
        }
        super.onResume();
    }

    /*
     * Activity_RemoteControl - onResume()
     */
    @Override
    public void onResume() {

        if (client == null)
            client = ((Custom_WebSocketClient) getApplicationContext());

        if (clientHandler == null)
            clientHandler = getClientHandler();

        if (client.isConnected()) {
            setTags();
            client.setHandler(clientHandler);
        } else {
            Log.w(LOGTAG, LOGPREFIX + "no connection to the server");
            toast_connection_error (getResources().getString(R.string.error_msg_3));
        }

        device = Activity_Settings.pref_get_remote_device_name (getBaseContext());

        super.onResume();
    }

    /*
     * Activity_RemoteControl - toast_connection_error()
     */
    void toast_connection_error (String error_msg) {
        SuperActivityToast superActivityToast = new SuperActivityToast(this);
        superActivityToast.setDuration(SuperToast.Duration.LONG);
        superActivityToast.setAnimations(SuperToast.Animations.FLYIN);
        superActivityToast.setBackground(SuperToast.Background.RED);
        superActivityToast.setText(error_msg);
        superActivityToast.setIcon(SuperToast.Icon.Dark.INFO, SuperToast.IconPosition.LEFT);
        superActivityToast.show();
    }

    /*
     * Activity_RemoteControl - sendCommand()
     */
    public void sendCommand (String device, String button) {

        client = ((Custom_WebSocketClient) getApplicationContext());
        if (!client.send ("{\"RunCommand\":{\"cmd\":\"SendIR\",\"args\":\"" + device + " " + button + "\"}}")) {
            Log.w(LOGTAG, LOGPREFIX + "no connection to the server");
            toast_connection_error (getResources().getString(R.string.error_msg_3));
        }
        Log.i(LOGTAG, LOGPREFIX + "'SendIR' request was sended - DEVICE: " + device + " BUTTON: " + button);
    }

    /*
     * Activity_RemoteControl - onSaveInstanceState()
     */
    @Override
    public void onSaveInstanceState (@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        SuperActivityToast.onSaveState(outState);
    }

    /*
     * Activity_RemoteControl - onCreateOptionsMenu()
     */
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_reconnect, menu);
        return true;
    }

    /*
     * Activity_RemoteControl - onOptionsItemSelected()
     */
    public boolean onOptionsItemSelected (MenuItem element) {
        switch (element.getItemId()){
            case R.id.reconnect:
                if (client.isConnected()) {
                    Log.w (LOGTAG, LOGPREFIX + "you are already connected to the server");
                    toast_connection_error (getResources().getString(R.string.error_msg_5));
                } else {
                    if (!client.connect()) {
                        Log.w(LOGTAG, LOGPREFIX + "can't connect to server - please try once again");
                        toast_connection_error(getResources().getString(R.string.error_msg_4));
                    } else {
                        client.setHandler(clientHandler);
                        Log.i(LOGTAG, LOGPREFIX + "reconnecting to server");
                    }
                }
                break;
        }
        return true;
    }

    /*
     * Activity_RemoteControl - makeAndShowDialogBox()
     */
    private AlertDialog makeAndShowDialogBox (final View Button) {

        final EditText setTag = new EditText(this);
        setTag.setText((String) Button.getTag());

        return new AlertDialog.Builder(this)

                .setTitle(getResources().getString(R.string.ir_msg_2))
                .setMessage(getResources().getString(R.string.ir_msg_1))
                .setView(setTag)

                .setPositiveButton(getResources().getString(R.string.com_msg_5), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String tag;
                        tag = setTag.getText().toString();

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        SharedPreferences.Editor Editor = sharedPreferences.edit();
                        Editor.putString("tag_" + Button.getId(), tag);
                        Editor.commit();
                        Button.setTag(tag);
                    }
                })

                .setNegativeButton(getResources().getString(R.string.com_msg_6), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })

                .create();
    }
}