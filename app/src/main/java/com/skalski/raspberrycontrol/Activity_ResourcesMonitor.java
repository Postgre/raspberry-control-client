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
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.graphics.Color;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONObject;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;

public class Activity_ResourcesMonitor extends Activity {

    private static final String LOGTAG = "RaspberryControl";
    private static final String LOGPREFIX = "STATISTICS: ";

    private static final String TAG_ERROR = "Error";
    private static final String TAG_STATISTICS = "Statistics";

    private static final String TAG_STAT_KERNEL = "kernel";
    private static final String TAG_STAT_UPTMIE = "uptime";
    private static final String TAG_STAT_SERIAL = "serial";
    private static final String TAG_STAT_MAC = "mac_addr";

    private static final String TAG_STAT_USED_SPACE = "used_space";
    private static final String TAG_STAT_FREE_SPACE = "free_space";

    private static final String TAG_STAT_RAM_USAGE = "ram_usage";
    private static final String TAG_STAT_SWAP_USAGE = "swap_usage";

    private static final String TAG_STAT_CPU_LOAD = "cpu_load";
    private static final String TAG_STAT_CPU_TEMP = "cpu_temp";
    private static final String TAG_STAT_CPU_USAGE = "cpu_usage";

    private Custom_WebSocketClient client;
    private Handler clientHandler;
    private Timer timer;
    private PieGraph filesystem_usage_graph;

    private TextView kernel_value, uptime_value, serial_value, mac_value;
    private TextView used_space_value, free_space_value;
    private TextView cpu_load_value, cpu_temp_value, cpu_usage_value;
    private Custom_ProgressBar ram_usage_value;
    private Custom_ProgressBar swap_usage_value;

    /*
     * Activity_ResourcesMonitor - onCreate()
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_resourcesmonitor);
        SuperActivityToast.onRestoreState(savedInstanceState, Activity_ResourcesMonitor.this);
        filesystem_usage_graph = (PieGraph) findViewById(R.id.res_filesystem_usage_graph);

        kernel_value = (TextView) findViewById (R.id.res_kernel_value);
        uptime_value = (TextView) findViewById (R.id.res_uptime_value);
        serial_value = (TextView) findViewById (R.id.res_serial_value);
        mac_value = (TextView) findViewById (R.id.res_mac_value);

        used_space_value = (TextView) findViewById (R.id.res_filesystem_used_space_value);
        free_space_value = (TextView) findViewById (R.id.res_filesystem_free_space_value);

        ram_usage_value = (Custom_ProgressBar) findViewById (R.id.res_memory_ram_value);
        swap_usage_value = (Custom_ProgressBar) findViewById (R.id.res_memory_swap_value);

        cpu_load_value = (TextView) findViewById (R.id.res_cpu_avg_load_value);
        cpu_temp_value = (TextView) findViewById (R.id.res_cpu_temp_value);
        cpu_usage_value = (TextView) findViewById (R.id.res_cpu_usage_value);
    }

    /*
     * Activity_ResourcesMonitor - getClientHandler()
     */
    Handler getClientHandler() {

        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                JSONObject root;

                Log.i(LOGTAG, LOGPREFIX + "new message received from server");

                try {

                    root = new JSONObject(msg.obj.toString());

                    if (root.has(TAG_ERROR)) {

                        String err = getResources().getString(R.string.com_msg_3) + root.getString(TAG_ERROR);
                        Log.e (LOGTAG, LOGPREFIX + root.getString(TAG_ERROR));
                        toast_connection_error (err);

                    } else {

                        JSONObject statistics;

                        statistics = root.getJSONObject(TAG_STATISTICS);

                        kernel_value.setText(statistics.getString(TAG_STAT_KERNEL));
                        uptime_value.setText(statistics.getString(TAG_STAT_UPTMIE));
                        serial_value.setText(statistics.getString(TAG_STAT_SERIAL));
                        mac_value.setText(statistics.getString(TAG_STAT_MAC));

                        float f_used_space = (float) statistics.getDouble(TAG_STAT_USED_SPACE);
                        float f_free_space = (float) statistics.getDouble(TAG_STAT_FREE_SPACE);

                        filesystem_usage_graph.removeSlices();

                        PieSlice slice = new PieSlice();
                        slice.setColor(Color.parseColor("#99CC00"));
                        slice.setValue(f_used_space);
                        filesystem_usage_graph.addSlice(slice);

                        slice = new PieSlice();
                        slice.setColor(Color.parseColor("#AA66CC"));
                        slice.setValue(f_free_space);
                        filesystem_usage_graph.addSlice(slice);

                        used_space_value.setText (String.format("%.3f", f_used_space) + " MB");
                        free_space_value.setText (String.format("%.3f", f_free_space) + " MB");

                        ram_usage_value.setProgress(statistics.getInt(TAG_STAT_RAM_USAGE));
                        ram_usage_value.setText(Integer.toString(statistics.getInt(TAG_STAT_RAM_USAGE)) + "%");

                        swap_usage_value.setProgress(statistics.getInt(TAG_STAT_SWAP_USAGE));
                        swap_usage_value.setText(Integer.toString(statistics.getInt(TAG_STAT_SWAP_USAGE)) + "%");

                        cpu_load_value.setText(statistics.getString(TAG_STAT_CPU_LOAD));
                        cpu_temp_value.setText(Integer.toString(statistics.getInt(TAG_STAT_CPU_TEMP)) + " \u2103");
                        cpu_usage_value.setText(Integer.toString(statistics.getInt(TAG_STAT_CPU_USAGE)) + "%");
                    }

                } catch (Exception ex) {
                    Log.e (LOGTAG, LOGPREFIX + "received invalid JSON object");
                    toast_connection_error (getResources().getString(R.string.error_msg_2));
                }
            }
        };
    }

    /*
     * Activity_ResourcesMonitor - sendCommand()
     */
    public void sendCommand () {

        if (!client.send ("{\"RunCommand\":{\"cmd\":\"GetStatistics\",\"args\":\"\"}}")) {
            Log.w(LOGTAG, LOGPREFIX + "no connection to the server");
            toast_connection_error(getResources().getString(R.string.error_msg_3));
        }
        Log.i(LOGTAG, LOGPREFIX + "'GetStatistics' request was sended");
    }

    /*
     * Activity_ResourcesMonitor - onResume()
     */
    @Override
    public void onResume() {

        if (client == null)
            client = ((Custom_WebSocketClient) getApplicationContext());

        if (clientHandler == null)
            clientHandler = getClientHandler();

        if (client.isConnected()) {
            sendCommand();
            client.setFilter(TAG_STATISTICS);
            client.setHandler(clientHandler);
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    sendCommand();
                }
            },2000,4000);
        } else {
            Log.w(LOGTAG, LOGPREFIX + "no connection to the server");
            toast_connection_error (getResources().getString(R.string.error_msg_3));
        }
        super.onResume();
    }

    /*
     * Activity_ResourcesMonitor - onPause()
     */
    @Override
    public void onPause() {

        if (timer != null)
            timer.cancel();
        if (client != null) {
            client.setHandler(null);
            client.setFilter(null);
        }
        super.onPause();
    }

    /*
     * Activity_ResourcesMonitor - toast_connection_error()
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
     * Activity_ResourcesMonitor - onCreateOptionsMenu()
     */
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_reconnect, menu);
        return true;
    }

    /*
     * Activity_ResourcesMonitor - onOptionsItemSelected()
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
                        client.setFilter(TAG_STATISTICS);
                        client.setHandler(clientHandler);
                        Log.i(LOGTAG, LOGPREFIX + "reconnecting to server");
                    }
                }
            break;
        }
        return true;
    }

    /*
     * Activity_ResourcesMonitor - onSaveInstanceState()
     */
    @Override
    public void onSaveInstanceState (@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        SuperActivityToast.onSaveState(outState);
    }
}