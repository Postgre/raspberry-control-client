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

import java.net.URI;
import java.net.URISyntaxException;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.skalski.raspberrycontrol.SecureWebSocktes.SecureWebSockets_WebSocket;
import com.skalski.raspberrycontrol.SecureWebSocktes.SecureWebSockets_WebSocketConnection;
import com.skalski.raspberrycontrol.SecureWebSocktes.SecureWebSockets_WebSocketException;
import com.skalski.raspberrycontrol.SecureWebSocktes.SecureWebSockets_WebSocketOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class Custom_WebSocketClient extends Application implements SecureWebSockets_WebSocket.WebSocketConnectionObserver {

    private static final String LOGTAG = "RaspberryControl";
    private static final String LOGPREFIX = "WEBSOCKET: ";

    private static final String TAG_HAN_GPIOSTATE = "GPIOState";
    private static final String TAG_HAN_TEMPSENSORS = "TempSensors";
    private static final String TAG_HAN_PROCESSES = "Processes";
    private static final String TAG_HAN_STATISTICS = "Statistics";
    private static final String TAG_HAN_NOTIFICATION = "Notification";
    private static final String TAG_ERROR = "Error";

    private SecureWebSockets_WebSocketConnection Connection;
    private SecureWebSockets_WebSocketOptions Options;
    private Handler ClientHandler;
    private String ClientFilter;
    private URI ServerURI;
    private volatile boolean IsConnected = false;

    /*
     * Custom_WebSocketClient - connect()
     */
    public boolean connect() {

        if (!this.IsConnected) {

            String hostname = Activity_Settings.pref_get_hostname(getBaseContext());
            String portnumber = Activity_Settings.pref_get_port_number(getBaseContext());
            int timeout = Integer.parseInt(Activity_Settings.pref_get_timeout(getBaseContext()));

            this.Connection = new SecureWebSockets_WebSocketConnection();
            this.Options = new SecureWebSockets_WebSocketOptions();
            Options.setSocketConnectTimeout(timeout);

            try {
                this.ServerURI = new URI("ws://" + hostname + ":" + portnumber);
                Connection.connect(ServerURI, this, Options);
            } catch (SecureWebSockets_WebSocketException e) {
                Log.e (LOGTAG, LOGPREFIX + "can't connect to server 'SecureWebSockets_WebSocketException'");
                this.IsConnected = false;
                return false;
            } catch (URISyntaxException e1) {
                Log.e (LOGTAG, LOGPREFIX + "can't connect to server 'URISyntaxException'");
                this.IsConnected = false;
                return false;
            } catch (Exception ex) {
                Log.e (LOGTAG, LOGPREFIX + "can't connect to server 'Exception'");
                this.IsConnected = false;
                return false;
            }

            this.IsConnected = true;
            return true;
        }
        return true;
    }

    /*
     * Custom_WebSocketClient - disconnect()
     */
    public void disconnect() {
        Connection.disconnect();
    }

    /*
     * Custom_WebSocketClient - send()
     */
    public boolean send (String msg) {
        if (IsConnected) {
            Connection.sendTextMessage(msg);
            return true;
        }
        return false;
    }

    /*
     * Custom_WebSocketClient - setHandler()
     */
    public void setHandler (Handler handler) {
        Log.i (LOGTAG, LOGPREFIX + "new message handler: " + handler);
        this.ClientHandler = handler;
    }

    /*
     * Custom_WebSocketClient - setFilter()
     */
    public void setFilter (String filter) {
        Log.i (LOGTAG, LOGPREFIX + "new message filter: " + filter);
        this.ClientFilter = filter;
    }

    /*
     * Custom_WebSocketClient - isConnected()
     */
    public boolean isConnected () {
        return this.IsConnected;
    }

    /*
     * Custom_WebSocketClient - onOpen()
     */
    @Override
    public void onOpen() {

        this.IsConnected = true;
        Log.i (LOGTAG, LOGPREFIX + "onOpen() - connection opened to: " + ServerURI.toString());
    }

    /*
     * Custom_WebSocketClient - onClose()
     */
    @Override
    public void onClose (WebSocketCloseNotification code, String reason) {

        this.IsConnected = false;
        Log.i (LOGTAG, LOGPREFIX + "onClose() - " + code.name() + ", " + reason);
    }

    /*
     * Custom_WebSocketClient - onTextMessage()
     */
    @Override
    public void onTextMessage (String payload) {

        Message payload_msg = new Message();
        boolean send_message = false;

        try {

            JSONObject jsonObj = new JSONObject(payload);

            if (jsonObj.has(TAG_HAN_GPIOSTATE))
                if ((this.ClientHandler != null) && (this.ClientFilter.equals(TAG_HAN_GPIOSTATE)))
                    send_message = true;

            if (jsonObj.has(TAG_HAN_TEMPSENSORS))
                if ((this.ClientHandler != null) && (this.ClientFilter.equals(TAG_HAN_TEMPSENSORS)))
                    send_message = true;

            if (jsonObj.has(TAG_HAN_PROCESSES))
                if ((this.ClientHandler != null) && (this.ClientFilter.equals(TAG_HAN_PROCESSES)))
                    send_message = true;

            if (jsonObj.has(TAG_HAN_STATISTICS))
                if ((this.ClientHandler != null) && (this.ClientFilter.equals(TAG_HAN_STATISTICS)))
                    send_message = true;

            if (jsonObj.has(TAG_ERROR))
                if (this.ClientHandler != null)
                    send_message = true;

            if (jsonObj.has(TAG_HAN_NOTIFICATION)) {

                if (Activity_Settings.pref_notifications_disabled(getBaseContext())) {

                    Log.i(LOGTAG, LOGPREFIX + "notification: disabled");

                } else {
                    int notification_id;

                    if (Activity_Settings.pref_multiple_notifications_disabled(getBaseContext()))
                        notification_id = 0;
                    else
                        notification_id = (int) System.currentTimeMillis();

                    Notification new_notification = new Notification.Builder(this)
                            .setContentTitle(getResources().getString(R.string.app_name))
                            .setContentText(jsonObj.getString(TAG_HAN_NOTIFICATION))
                            .setSmallIcon(R.drawable.ic_launcher).build();
                    new_notification.defaults |= Notification.DEFAULT_ALL;
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(notification_id, new_notification);

                    Log.i(LOGTAG, LOGPREFIX + "notification: " + notification_id);
                }
            }

        } catch (JSONException e) {
            Log.e (LOGTAG, LOGPREFIX + "can't parse JSON object");
        }

        if (send_message) {
            payload_msg.obj = payload;
            ClientHandler.sendMessage(payload_msg);
        }
    }

    /*
     * Custom_WebSocketClient - onRawTextMessage()
     */
    @Override
    public void onRawTextMessage (byte[] payload) {
        Log.wtf (LOGTAG, LOGPREFIX + "we didn't expect 'RawText' Message");
    }

    /*
     * Custom_WebSocketClient - onBinaryMessage()
     */
    @Override
    public void onBinaryMessage (byte[] payload) {
        Log.wtf (LOGTAG, LOGPREFIX + "we didn't expect 'BinaryMessage' Message");
    }
}