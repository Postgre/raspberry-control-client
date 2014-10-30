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

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;

public class Activity_Processes extends ListActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String LOGTAG = "RaspberryControl";
    private static final String LOGPREFIX = "PROCESSES: ";

    private static final String TAG_ERROR = "Error";
    private static final String TAG_PROCESSES = "Processes";
    private static final String TAG_NAME = "name";
    private static final String TAG_USER= "user";
    private static final String TAG_STATE = "state";
    private static final String TAG_PID = "pid";

    private Custom_WebSocketClient client;
    private Handler clientHandler;
    private List<Custom_ProcessAdapter> processesArray;
    private SwipeRefreshLayout processesLayout;

    /*
     * Activity_Processes - onCreate()
     */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processes);
        SuperActivityToast.onRestoreState(savedInstanceState, Activity_Processes.this);

        processesLayout = (SwipeRefreshLayout) findViewById(R.id.processes_layout_swype);
        processesLayout.setOnRefreshListener(this);
        processesLayout.setColorSchemeResources(R.color.swipe_color_1, R.color.swipe_color_2,
                                                R.color.swipe_color_3, R.color.swipe_color_4);
    }

    /*
     * Activity_Processes - onRefresh()
     */
    @Override
    public void onRefresh() {
        sendCommand();
    }

    /*
     * Activity_Processes - getClientHandler()
     */
    Handler getClientHandler() {

        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                JSONObject root;
                JSONArray processes;
                processesArray = new ArrayList<Custom_ProcessAdapter>();
                processesLayout.setRefreshing(false);

                Log.i (LOGTAG, LOGPREFIX + "new message received from server");

                try {

                    root = new JSONObject(msg.obj.toString());

                    if (root.has(TAG_ERROR)) {

                        String err = getResources().getString(R.string.com_msg_3) + root.getString(TAG_ERROR);
                        Log.e (LOGTAG, LOGPREFIX + root.getString(TAG_ERROR));
                        toast_connection_error (err);

                    } else {

                        processes = root.getJSONArray(TAG_PROCESSES);

                        for (int i = 0; i < processes.length(); i++) {

                            JSONObject process = processes.getJSONObject(i);

                            String name = process.getString(TAG_NAME);
                            String user = process.getString(TAG_USER);
                            String state = process.getString(TAG_STATE);

                            int pid = process.getInt(TAG_PID);
                            String pidstr = Integer.toString(pid);

                            if (state != null)
                                state = state.substring(0, 1);

                            processesArray.add(new Custom_ProcessAdapter(name, pidstr, user, state));
                        }

                        if (processes.length() == 0) {
                            Log.e(LOGTAG, LOGPREFIX + "list of processes is empty - please report a bug");
                            toast_connection_error(getResources().getString(R.string.error_msg_9));
                        }
                    }

                } catch (Exception ex) {
                    Log.e (LOGTAG, LOGPREFIX + "received invalid JSON object");
                    toast_connection_error (getResources().getString(R.string.error_msg_2));
                }

                setListAdapter(new Custom_ProcessesArrayAdapter(getApplicationContext(), processesArray));
            }
        };
    }

    /*
     * Activity_Processes - sendCommand()
     */
    public void sendCommand () {
        if (!client.send ("{\"RunCommand\":{\"cmd\":\"GetProcesses\",\"args\":\"\"}}")) {
            processesLayout.setRefreshing (false);
            Log.w(LOGTAG, LOGPREFIX + "no connection to the server");
            toast_connection_error (getResources().getString(R.string.error_msg_3));
        }
        Log.i(LOGTAG, LOGPREFIX + "'GetProcesses' request was sended");
        processesLayout.setRefreshing(true);
    }

    /*
     * Activity_Processes - onResume()
     */
    @Override
    public void onResume() {

        if (client == null)
            client = ((Custom_WebSocketClient) getApplicationContext());

        if (clientHandler == null)
            clientHandler = getClientHandler();

        if (client.isConnected()) {
            sendCommand();
            client.setFilter(TAG_PROCESSES);
            client.setHandler(clientHandler);
        } else {
            Log.w(LOGTAG, LOGPREFIX + "no connection to the server");
            toast_connection_error (getResources().getString(R.string.error_msg_3));
        }
        super.onResume();
    }

    /*
     * Activity_Processes - onPause()
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
     * Activity_Processes - toast_connection_error()
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
     * Activity_Processes - onCreateOptionsMenu()
     */
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_reconnect, menu);
        return true;
    }

    /*
     * Activity_Processes - onOptionsItemSelected()
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
                        client.setFilter(TAG_PROCESSES);
                        client.setHandler(clientHandler);
                        Log.i(LOGTAG, LOGPREFIX + "reconnecting to server");
                    }
                }
                break;
        }
        return true;
    }

    /*
     * Activity_Processes - onSaveInstanceState()
     */
    @Override
    public void onSaveInstanceState (@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        SuperActivityToast.onSaveState(outState);
    }

    /*
     * Activity_Processes - onListItemClick()
     */
    @Override
    protected void onListItemClick (ListView l, View v, int position, long id) {
        AlertDialog diaBox = makeAndShowDialogBox (this.processesArray.get(position).get_process_pid());
        diaBox.show();
    }

    /*
     * Activity_Processes - makeAndShowDialogBox()
     */
    private AlertDialog makeAndShowDialogBox (final String pid) {

        return new AlertDialog.Builder(this)

                .setTitle(getResources().getString(R.string.processes_msg_2) + pid)
                .setMessage(getResources().getString(R.string.processes_msg_1))

                .setPositiveButton(getResources().getString(R.string.com_msg_5), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (!client.send ("{\"RunCommand\":{\"cmd\":\"KillProcess\",\"args\":\"" + pid + "\"}}")) {
                            processesLayout.setRefreshing (false);
                            Log.w(LOGTAG, LOGPREFIX + "no connection to the server");
                            toast_connection_error (getResources().getString(R.string.error_msg_3));
                        }
                        processesLayout.setRefreshing(true);
                    }
                })

                .setNegativeButton(getResources().getString(R.string.com_msg_6), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })

                .create();
    }
}