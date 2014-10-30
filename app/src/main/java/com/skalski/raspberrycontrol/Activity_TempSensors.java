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

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.annotation.NonNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;

public class Activity_TempSensors extends ListActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String LOGTAG = "RaspberryControl";
    private static final String LOGPREFIX = "TEMPSENSORS: ";

    private static final String TAG_ERROR = "Error";
    private static final String TAG_TEMPSENSORS = "TempSensors";
    private static final String TAG_TYPE = "type";
    private static final String TAG_ID = "id";
    private static final String TAG_CRC = "crc";
    private static final String TAG_TEMP = "temp";

    private Custom_WebSocketClient client;
    private Handler clientHandler;
    private List<Custom_TempSensorsAdapter> tempsensorsArray;
    private SwipeRefreshLayout tempsensorsLayout;

    /*
     * Activity_TempSensors - onCreate()
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tempsensors);
        SuperActivityToast.onRestoreState(savedInstanceState, Activity_TempSensors.this);

        tempsensorsLayout = (SwipeRefreshLayout) findViewById(R.id.tempsensors_layout_swype);
        tempsensorsLayout.setOnRefreshListener(this);
        tempsensorsLayout.setColorSchemeResources(R.color.swipe_color_1, R.color.swipe_color_2,
                                                  R.color.swipe_color_3, R.color.swipe_color_4);
    }

    /*
     * Activity_TempSensors - onRefresh()
     */
    @Override
    public void onRefresh() {
        sendCommand();
    }

    /*
     * Activity_TempSensors - getClientHandler()
     */
    Handler getClientHandler() {

        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                JSONObject root;
                JSONArray tempsensors;
                tempsensorsArray = new ArrayList<Custom_TempSensorsAdapter>();
                tempsensorsLayout.setRefreshing(false);

                Log.i(LOGTAG, LOGPREFIX + "new message received from server");

                try {

                    root = new JSONObject(msg.obj.toString());

                    if (root.has(TAG_ERROR)) {

                        String err = getResources().getString(R.string.com_msg_3) + root.getString(TAG_ERROR);
                        toast_connection_error (err);

                    } else {

                        tempsensors = root.getJSONArray(TAG_TEMPSENSORS);

                        for (int i = 0; i < tempsensors.length(); i++) {

                            JSONObject tempsensor = tempsensors.getJSONObject(i);

                            String type = tempsensor.getString(TAG_TYPE);
                            String id = tempsensor.getString(TAG_ID);
                            String crc = tempsensor.getString(TAG_CRC);

                            float temp = (float) tempsensor.getDouble(TAG_TEMP);
                            String tempstr = String.format("%.3f", temp);

                            if (tempstr != null)
                                tempstr = tempstr + " \u2103";

                            tempsensorsArray.add(new Custom_TempSensorsAdapter(type, id, tempstr, crc));
                        }

                        if (tempsensors.length() == 0) {
                            Log.w(LOGTAG, LOGPREFIX + "can't find 1-wire temperature sensors");
                            toast_connection_error(getResources().getString(R.string.error_msg_7));
                        }
                    }

                } catch (Exception ex) {
                    Log.e (LOGTAG, LOGPREFIX + "received invalid JSON object");
                    toast_connection_error (getResources().getString(R.string.error_msg_2));
                }

                setListAdapter(new Custom_TempSensorsArrayAdapter(getApplicationContext(), tempsensorsArray));
            }
        };
    }

    /*
     * Activity_TempSensors - sendCommand()
     */
    public void sendCommand () {

        if (!client.send ("{\"RunCommand\":{\"cmd\":\"GetTempSensors\",\"args\":\"\"}}")) {
            tempsensorsLayout.setRefreshing (false);
            Log.w(LOGTAG, LOGPREFIX + "no connection to the server");
            toast_connection_error (getResources().getString(R.string.error_msg_3));
        }
        Log.i(LOGTAG, LOGPREFIX + "'GetTempSensors' request was sended");
        tempsensorsLayout.setRefreshing(true);
    }

    /*
     * Activity_TempSensors - onResume()
     */
    @Override
    public void onResume() {

        if (client == null)
            client = ((Custom_WebSocketClient) getApplicationContext());

        if (clientHandler == null)
            clientHandler = getClientHandler();

        if (client.isConnected()) {
            sendCommand();
            client.setFilter(TAG_TEMPSENSORS);
            client.setHandler(clientHandler);
        } else {
            Log.w(LOGTAG, LOGPREFIX + "no connection to the server");
            toast_connection_error (getResources().getString(R.string.error_msg_3));
        }
        super.onResume();
    }

    /*
     * Activity_TempSensors - onPause()
     */
    @Override
    public void onPause() {

        if (client != null) {
            client.setHandler(null);
            client.setFilter(null);
        }
        super.onPause();
    }

    /*
     * Activity_TempSensors - toast_connection_error()
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
     * Activity_TempSensors - onCreateOptionsMenu()
     */
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_reconnect, menu);
        return true;
    }

    /*
     * Activity_TempSensors - onOptionsItemSelected()
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
                        client.setFilter(TAG_TEMPSENSORS);
                        client.setHandler(clientHandler);
                        Log.i(LOGTAG, LOGPREFIX + "reconnecting to server");
                    }
                }
                break;
        }
        return true;
    }

    /*
     * Activity_TempSensors - onSaveInstanceState()
     */
    @Override
    public void onSaveInstanceState (@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        SuperActivityToast.onSaveState(outState);
    }

    /*
     * Activity_TempSensors - onListItemClick()
     */
    @Override
    protected void onListItemClick (ListView l, View v, int position, long id) {
        /* TODO - show temp graph */
    }
}