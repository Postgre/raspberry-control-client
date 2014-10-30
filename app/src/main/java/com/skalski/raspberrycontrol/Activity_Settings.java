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
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Context;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

public class Activity_Settings extends Activity {

    private static final String LOGTAG = "RaspberryControl";
    private static final String LOGPREFIX = "SETTINGS: ";

    private static final String TAG_HOSTNAME = "hostname";
    private static final String TAG_PORT_NUMBER = "portnumber";
    private static final String TAG_TIMEOUT = "timeout";
    private static final String TAG_DISABLE_SPLASH_SCREEN = "disable_splash_screen";
    private static final String TAG_DISABLE_NOTIFICATIONS = "disable_notifications";
    private static final String TAG_DISABLE_MULTIPLE_NOTIFICATIONS = "disable_multiple_notifications";
    private static final String TAG_TERMINAL_PORT_NUMBER = "terminal_portnumber";
    private static final String TAG_TERMINAL_FONT = "terminal_font";
    private static final String TAG_REMOTE_DEVICE_NAME = "remote_device_name";
    private static final String TAG_WEBCAM_STREAM = "webcam_stream";
    private static final String TAG_WEBCAM_FPS = "webcam_fps";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        PrefsFragment mPrefsFragment = new PrefsFragment();
        mFragmentTransaction.replace(android.R.id.content, mPrefsFragment);
        mFragmentTransaction.commit();
    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }
    }

    public static String pref_get_hostname (Context context){
        String value;
        value =  PreferenceManager.getDefaultSharedPreferences(context).getString(TAG_HOSTNAME, "192.168.0.2");
        Log.i(LOGTAG, LOGPREFIX + "pref_get_hostname() value: " + value);
        return value;
    }

    public static void pref_set_hostname (Context context, String value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_HOSTNAME, value);
        editor.commit();
        Log.i(LOGTAG, LOGPREFIX + "pref_set_hostname() value: " + value);
    }

    public static String pref_get_port_number (Context context){
        String value;
        value =  PreferenceManager.getDefaultSharedPreferences(context).getString(TAG_PORT_NUMBER, "8080");
        Log.i(LOGTAG, LOGPREFIX + "pref_get_port_number() value: " + value);
        return value;
    }

    public static void pref_set_port_number (Context context, String value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_PORT_NUMBER, value);
        editor.commit();
        Log.i(LOGTAG, LOGPREFIX + "pref_set_port_number() value: " + value);
    }

    public static String pref_get_timeout (Context context){
        String value;
        value =  PreferenceManager.getDefaultSharedPreferences(context).getString(TAG_TIMEOUT, "5000");
        Log.i(LOGTAG, LOGPREFIX + "pref_get_timeout() value: " + value);
        return value;
    }

    public static void pref_set_timeout (Context context, String value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TIMEOUT, value);
        editor.commit();
        Log.i(LOGTAG, LOGPREFIX + "pref_set_timeout() value: " + value);
    }

    public static Boolean pref_splash_screen_disabled (Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(TAG_DISABLE_SPLASH_SCREEN, false);
    }

    public static Boolean pref_notifications_disabled (Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(TAG_DISABLE_NOTIFICATIONS, false);
    }

    public static Boolean pref_multiple_notifications_disabled (Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(TAG_DISABLE_MULTIPLE_NOTIFICATIONS, false);
    }

    public static String pref_get_terminal_port_number (Context context){
        String value;
        value =  PreferenceManager.getDefaultSharedPreferences(context).getString(TAG_TERMINAL_PORT_NUMBER, "8081");
        Log.i(LOGTAG, LOGPREFIX + "pref_get_terminal_port_number() value: " + value);
        return value;
    }

    public static String pref_get_terminal_font (Context context){
        String value;
        value =  PreferenceManager.getDefaultSharedPreferences(context).getString(TAG_TERMINAL_FONT, "50");
        Log.i(LOGTAG, LOGPREFIX + "pref_get_terminal_font() value: " + value);
        return value;
    }

    public static String pref_get_remote_device_name (Context context){
        String value;
        value =  PreferenceManager.getDefaultSharedPreferences(context).getString(TAG_REMOTE_DEVICE_NAME, null);
        Log.i(LOGTAG, LOGPREFIX + "pref_get_remote_device_name() value: " + value);
        return value;
    }

    public static String pref_get_stream_url (Context context){
        String value;
        value =  PreferenceManager.getDefaultSharedPreferences(context).getString(TAG_WEBCAM_STREAM, "");
        Log.i(LOGTAG, LOGPREFIX + "pref_get_stream_url() value: " + value);
        return value;
    }

    public static Boolean pref_get_fps_indicator (Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(TAG_WEBCAM_FPS, false);
    }
}