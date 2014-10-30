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
import android.widget.ImageView;
import android.widget.ListView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.annotation.NonNull;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Activity_GPIO extends ListActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String LOGTAG = "RaspberryControl";
    private static final String LOGPREFIX = "GPIO: ";

    private static final String TAG_ERROR = "Error";
    private static final String TAG_GPIOSTATE = "GPIOState";
    private static final String TAG_GPIO = "gpio";
    private static final String TAG_VALUE = "value";
    private static final String TAG_DIRECTION = "direction";
    private static final String TAG_REVISION = "Revision";

    private Custom_WebSocketClient client;
    private Handler clientHandler;
    private List<Custom_GPIOAdapter> gpioArray;
    private SwipeRefreshLayout gpioLayout;
    private ImageView gpioPinout;

    /*
     * Activity_GPIO - onCreate()
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpio);
        SuperActivityToast.onRestoreState(savedInstanceState, Activity_GPIO.this);

        gpioPinout = (ImageView) findViewById(R.id.gpio_pinout);
        gpioLayout = (SwipeRefreshLayout) findViewById(R.id.gpio_layout_swype);
        gpioLayout.setOnRefreshListener(this);
        gpioLayout.setColorSchemeResources(R.color.swipe_color_1, R.color.swipe_color_2,
                                           R.color.swipe_color_3, R.color.swipe_color_4);
    }

    /*
     * Activity_GPIO - onRefresh()
     */
    @Override
    public void onRefresh() {
        sendCommand();
    }

    /*
     * Activity_GPIO - getClientHandler()
     */
    Handler getClientHandler() {

        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                JSONObject root;
                JSONArray gpios;
                gpioArray = new ArrayList<Custom_GPIOAdapter>();
                gpioLayout.setRefreshing(false);

                Log.i (LOGTAG, LOGPREFIX + "new message received from server");

                try {

                    root = new JSONObject(msg.obj.toString());

                    if (root.has(TAG_ERROR)) {

                        String err = getResources().getString(R.string.com_msg_3) + root.getString(TAG_ERROR);
                        Log.e (LOGTAG, LOGPREFIX + root.getString(TAG_ERROR));
                        toast_connection_error (err);

                    } else {

                        gpios = root.getJSONArray(TAG_GPIOSTATE);

                        for (int i = 0; i < gpios.length(); i++) {

                            JSONObject gpioss = gpios.getJSONObject(i);

                            int gpio = gpioss.getInt(TAG_GPIO);
                            int value = gpioss.getInt(TAG_VALUE);
                            String direction = gpioss.getString(TAG_DIRECTION);

                            gpioArray.add(new Custom_GPIOAdapter(gpio, value, direction));
                        }

                        if (gpios.length() == 0) {
                            Log.w (LOGTAG, LOGPREFIX + "can't find exported GPIO's on server side");
                            toast_connection_error(getResources().getString(R.string.error_msg_8));
                        }

                        if (gpioPinout.getDrawable().getConstantState() == getResources().getDrawable (R.drawable.gpio_unknown).getConstantState()) {
                            if (root.has(TAG_REVISION)) {
                                String revision;
                                revision = root.getString(TAG_REVISION);

                                Log.i (LOGTAG, LOGPREFIX + "set new GPIO layout image");

                                if (revision.equals("0002") ||
                                    revision.equals("0003")) {

                                    gpioPinout.setImageResource(R.drawable.gpio_pinout_1);

                                } else if (revision.equals("0004") ||
                                           revision.equals("0005") ||
                                           revision.equals("0006") ||
                                           revision.equals("0007") ||
                                           revision.equals("0008") ||
                                           revision.equals("0009") ||
                                           revision.equals("000d") ||
                                           revision.equals("000e") ||
                                           revision.equals("000f")) {

                                    gpioPinout.setImageResource(R.drawable.gpio_pinout_2);

                                } else if (revision.equals("0010") ||
                                           revision.equals("0011")) {

                                    gpioPinout.setImageResource(R.drawable.gpio_pinout_3);

                                } else {
                                    Log.wtf (LOGTAG, LOGPREFIX + "your Raspberry Pi board is weird");
                                }
                            }
                        }
                    }

                } catch (Exception ex) {
                    Log.e (LOGTAG, LOGPREFIX + "received invalid JSON object");
                    toast_connection_error (getResources().getString(R.string.error_msg_2));
                }

                setListAdapter(new Custom_GPIOArrayAdapter(getApplicationContext(), gpioArray));
            }
        };
    }

    /*
     * Activity_GPIO - sendCommand()
     */
    public void sendCommand () {

        if (!client.send ("{\"RunCommand\":{\"cmd\":\"GetGPIO\",\"args\":\"\"}}")) {
            gpioLayout.setRefreshing (false);
            Log.w(LOGTAG, LOGPREFIX + "no connection to the server");
            toast_connection_error (getResources().getString(R.string.error_msg_3));
        }
        Log.i(LOGTAG, LOGPREFIX + "'GetGPIO' request was sended");
        gpioLayout.setRefreshing(true);
    }

    /*
     * Activity_GPIO - onResume()
     */
    @Override
    public void onResume() {

        if (client == null)
            client = ((Custom_WebSocketClient) getApplicationContext());

        if (clientHandler == null)
            clientHandler = getClientHandler();

        if (client.isConnected()) {
            sendCommand();
            client.setFilter(TAG_GPIOSTATE);
            client.setHandler(clientHandler);
        } else {
            Log.w(LOGTAG, LOGPREFIX + "no connection to the server");
            toast_connection_error (getResources().getString(R.string.error_msg_3));
        }
        super.onResume();
    }

    /*
     * Activity_GPIO - onPause()
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
     * Activity_GPIO - toast_connection_error()
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
     * Activity_GPIO - onCreateOptionsMenu()
     */
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_reconnect, menu);
        return true;
    }

    /*
     * Activity_GPIO - onOptionsItemSelected()
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
                        client.setFilter(TAG_GPIOSTATE);
                        client.setHandler(clientHandler);
                        Log.i(LOGTAG, LOGPREFIX + "reconnecting to server");
                    }
                }
                break;
        }
        return true;
    }

    /*
     * Activity_GPIO - onSaveInstanceState()
     */
    @Override
    public void onSaveInstanceState (@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        SuperActivityToast.onSaveState(outState);
    }

    /*
     * Activity_GPIO - onListItemClick()
     */
    @Override
    protected void onListItemClick (ListView l, View v, int position, long id) {
        /* TODO - change gpio label */
    }
}