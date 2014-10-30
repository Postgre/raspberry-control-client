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

/*
 * Copyright 2013 Flavien Laurent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skalski.raspberrycontrol;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.annotation.NonNull;
import de.psdev.licensesdialog.LicensesDialog;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.util.OnClickWrapper;
import com.github.johnpersano.supertoasts.util.Wrappers;
import com.skalski.raspberrycontrol.ActionBar.*;

public class Activity_MainMenu extends Activity {

    private static final String LOGTAG = "RaspberryControl";
    private static final String LOGPREFIX = "MAINMENU: ";

	/*
	 * NoBoringActionBar variables
	 */
    private int ActionBar_Height;
    private int ActionBar_MinHeaderTranslation;
    
    private View ActionBar_Header;
    private View ActionBar_PlaceHolderView;
    private ImageView ActionBar_HeaderLogo;
    
    private RectF ActionBar_Rect1 = new RectF();
    private RectF ActionBar_Rect2 = new RectF();
    private TypedValue ActionBar_TypedValue = new TypedValue();
    
    private AccelerateDecelerateInterpolator ActionBar_SmoothInterpolator;
    private ActionBar_AlphaForegroundColorSpan ActionBar_AlphaForegroundColorSpan;
    private SpannableString ActionBar_SpannableString;
    
    /*
     * MainMenu variables
     */
    private Custom_WebSocketClient client;
    private ListView MainMenu_ListView;
    
	private Integer[] menu_icon = {
			R.drawable.mainmenu_icon_01,
			R.drawable.mainmenu_icon_02,
			R.drawable.mainmenu_icon_03,
			R.drawable.mainmenu_icon_04,
            R.drawable.mainmenu_icon_05,
			R.drawable.mainmenu_icon_06,
			R.drawable.mainmenu_icon_07,
			R.drawable.mainmenu_icon_08,
			R.drawable.mainmenu_icon_09,
            R.drawable.mainmenu_icon_10,
			R.drawable.mainmenu_icon_11};

	/*
	 * Activity_MainMenu - onCreate()
	 */
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar_KenBurnsView ActionBar_HeaderPicture;
        int ActionBar_TitleColor;
        int ActionBar_HeaderHeight;

        ActionBar_HeaderHeight = getResources().getDimensionPixelSize(R.dimen.mainmenu_header_height);
        ActionBar_MinHeaderTranslation = -ActionBar_HeaderHeight + get_action_bar_height();
        ActionBar_SmoothInterpolator = new AccelerateDecelerateInterpolator();

        setContentView(R.layout.activity_mainmenu);

        Wrappers wrappers = new Wrappers();
        wrappers.add(onClickWrapper);
        SuperActivityToast.onRestoreState(savedInstanceState, Activity_MainMenu.this, wrappers);

        MainMenu_ListView = (ListView) findViewById(R.id.mainmenu_listview);
        ActionBar_Header = findViewById(R.id.mainmenu_header);
        
        ActionBar_HeaderPicture = (ActionBar_KenBurnsView) findViewById(R.id.mainmenu_header_picture);
        ActionBar_HeaderPicture.setResourceIds(R.drawable.mainmenu_picture_00, R.drawable.mainmenu_picture_01);
        ActionBar_HeaderLogo = (ImageView) findViewById(R.id.mainmenu_header_logo);
        ActionBar_TitleColor = getResources().getColor(R.color.actionbar_title_color);

        ActionBar_SpannableString = new SpannableString(getString(R.string.app_name));
        ActionBar_AlphaForegroundColorSpan = new ActionBar_AlphaForegroundColorSpan(ActionBar_TitleColor);

        client = ((Custom_WebSocketClient) getApplicationContext());

        setup_action_bar();
        setup_list_view();

        MainMenu_ListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	            	
                Intent intent;
                                
                switch (position){

		            	/* GPIO Control */
		    			case 1:
                            if (client.isConnected()) {
                                Log.i(LOGTAG, LOGPREFIX + "starting 'Activity_GPIO'");
                                intent = new Intent(getApplicationContext(), Activity_GPIO.class);
                                startActivity(intent);
                            } else {
                                toast_connection_error();
                            }
                        break;

		    			/* Temperature Sensors */
		    			case 2:
                            if (client.isConnected()) {
                                Log.i(LOGTAG, LOGPREFIX + "starting 'Activity_TempSensors'");
                                intent = new Intent(getApplicationContext(), Activity_TempSensors.class);
                                startActivity(intent);
                            } else {
                                toast_connection_error();
                            }
		    			break;

		    			/* Processes Management */
		    			case 3:
                            if (client.isConnected()) {
                                Log.i(LOGTAG, LOGPREFIX + "starting 'Activity_Processes'");
                                intent = new Intent(getApplicationContext(), Activity_Processes.class);
                                startActivity(intent);
                            } else {
                                toast_connection_error();
                            }
		    			break;

		    			/* Monitor System Resources */
		    			case 4:
                            if (client.isConnected()) {
                                Log.i(LOGTAG, LOGPREFIX + "starting 'Activity_ResourcesMonitor'");
                                intent = new Intent(getApplicationContext(), Activity_ResourcesMonitor.class);
                                startActivity(intent);
                            } else {
                                toast_connection_error();
                            }
		    			break;

		    			/* Terminal Emulator */
                        case 5:
                        if (client.isConnected()) {
                            Log.i(LOGTAG, LOGPREFIX + "starting 'Activity_Terminal'");
                            intent = new Intent(getApplicationContext(), Activity_Terminal.class);
                            startActivity(intent);
                        } else {
                            toast_connection_error();
                        }
                        break;
		    			
		    			/* IR Remote Control */
		    			case 6:
                            if (client.isConnected()) {
                                Log.i(LOGTAG, LOGPREFIX + "starting 'Activity_RemoteControl'");
                                intent = new Intent(getApplicationContext(), Activity_RemoteControl.class);
                                startActivity(intent);
                            } else {
                                toast_connection_error();
                            }
		    			break;
		    			
		    			/* Webcam Surveillance */
		    			case 7:
                            if (client.isConnected()) {
                                Log.i(LOGTAG, LOGPREFIX + "starting 'Activity_Webcam'");
                                intent = new Intent(getApplicationContext(), Activity_Webcam.class);
                                startActivity(intent);
                            } else {
                                toast_connection_error();
                            }
		    			break;

		    			/* Settings */
		    			case 8:
                            Log.i(LOGTAG, LOGPREFIX + "starting 'Activity_Settings'");
		    				intent = new Intent(getApplicationContext(), Activity_Settings.class);
		    				startActivity(intent);
		    			break;
		    			
		    			/* About 'Raspberry Control' */
		    			case 9:
                            Log.i(LOGTAG, LOGPREFIX + "starting 'Activity_ReadMe'");
		    				intent = new Intent(getApplicationContext(), Activity_ReadMe.class);
		    				startActivity(intent);
		    			break;
		    			
		    			/* Open Source Licenses */
		    			case 10:
                            Log.i(LOGTAG, LOGPREFIX + "starting 'LicensesDialog'");
		    				new LicensesDialog(Activity_MainMenu.this, R.raw.notices, false, true).show();
		    			break;
		
		    			/* Exit */
		    			case 11:
		    				intent = new Intent(getApplicationContext(), Activity_Login.class);
		    				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    				intent.putExtra("exit","yes");
		    				startActivity(intent);
		    			break;
                }                
            }
        });
    }

    /*
     * Activity_MainMenu - onClickWrapper()
     */
    OnClickWrapper onClickWrapper = new OnClickWrapper("id_mainmenu", new SuperToast.OnClickListener() {

        @Override
        public void onClick(View view, Parcelable token) {
        if (!client.connect())
          toast_connection_error ();
        }
    });

    /*
     * Activity_MainMenu - onResume()
     */
    @Override
    public void onResume() {

        client = ((Custom_WebSocketClient) getApplicationContext());
        super.onResume();
    }

    /*
     * Activity_MainMenu - toast_connection_error()
     */
    public void toast_connection_error() {
        Log.w(LOGTAG, LOGPREFIX + "unsuccessful attempt to start new Activity");
        SuperActivityToast superActivityToast = new SuperActivityToast(Activity_MainMenu.this, SuperToast.Type.BUTTON);
        superActivityToast.setDuration(SuperToast.Duration.EXTRA_LONG);
        superActivityToast.setAnimations(SuperToast.Animations.FLYIN);
        superActivityToast.setBackground(SuperToast.Background.RED);
        superActivityToast.setButtonIcon(SuperToast.Icon.Dark.REFRESH, getResources().getString(R.string.com_msg_1));
        superActivityToast.setText(getResources().getString(R.string.error_msg_1));
        superActivityToast.setOnClickWrapper(onClickWrapper);
        superActivityToast.show();
    }

    /*
     * Activity_MainMenu - setup_list_view()
     */
    private void setup_list_view () {

    	ActionBar_PlaceHolderView = getLayoutInflater().inflate(R.layout.noboringactionbar_view_placeholder, MainMenu_ListView, false);
        Custom_MainMenuList adapter = new Custom_MainMenuList(Activity_MainMenu.this, menu_icon,
                                      getResources().getStringArray(R.array.mainmenu_options),
                                      getResources().getStringArray(R.array.mainmenu_descriptions));
        
        MainMenu_ListView.addHeaderView(ActionBar_PlaceHolderView);
        MainMenu_ListView.setAdapter(adapter);
        MainMenu_ListView.setOnScrollListener(new AbsListView.OnScrollListener() {
        	
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            	int scroll_y;
            	float ratio;
                scroll_y = get_scroll_y();
                ActionBar_Header.setTranslationY(Math.max(-scroll_y, ActionBar_MinHeaderTranslation));
                ratio = clamp(ActionBar_Header.getTranslationY() / ActionBar_MinHeaderTranslation, 0.0f, 1.0f);
                interpolate (ActionBar_HeaderLogo, get_action_bar_iconview(), ActionBar_SmoothInterpolator.getInterpolation(ratio));
                set_title_alpha (clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
            }
        });
    }
    
    /*
     * Activity_MainMenu - onKeyDown()
     *   Disable BACK button
     */
    @Override
    public boolean onKeyDown (int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
            && event.getRepeatCount() == 0) {
            onBackPressed();
        }
        return true;
    }

    /*
     * Activity_MainMenu - onBackPressed()
     *   Disable BACK button
     */
    @Override
    public void onBackPressed () {
        SuperActivityToast superActivityToast = new SuperActivityToast(this);
        superActivityToast.setDuration(SuperToast.Duration.LONG);
        superActivityToast.setAnimations(SuperToast.Animations.FLYIN);
        superActivityToast.setBackground(SuperToast.Background.RED);
        superActivityToast.setText(getResources().getString(R.string.com_msg_2));
        superActivityToast.setIcon(SuperToast.Icon.Dark.INFO, SuperToast.IconPosition.LEFT);
        superActivityToast.show();
    }

    /*
     * Activity_MainMenu - onSaveInstanceState()
     */
    @Override
    public void onSaveInstanceState (@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        SuperActivityToast.onSaveState(outState);
    }

    /*
     * NoBoringActionBar - set_title_alpha()
     */
    private void set_title_alpha (float alpha) {
    	ActionBar_AlphaForegroundColorSpan.setAlpha(alpha);
    	ActionBar_SpannableString.setSpan(ActionBar_AlphaForegroundColorSpan, 0,
                                          ActionBar_SpannableString.length(),
                                          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (getActionBar() != null)
            getActionBar().setTitle(ActionBar_SpannableString);
    }

    /*
     * NoBoringActionBar - clamp()
     */
    public static float clamp (float value, float max, float min) {
        return Math.max(Math.min(value, min), max);
    }

    /*
     * NoBoringActionBar - interpolate()
     */
    private void interpolate (View view1, View view2, float interpolation) {
    	get_on_screen_rect (ActionBar_Rect1, view1);
    	get_on_screen_rect (ActionBar_Rect2, view2);

        float scaleX = 1.0F + interpolation * (ActionBar_Rect2.width() / ActionBar_Rect1.width() - 1.0F);
        float scaleY = 1.0F + interpolation * (ActionBar_Rect2.height() / ActionBar_Rect1.height() - 1.0F);
        float translationX = 0.5F * (interpolation * (ActionBar_Rect2.left + ActionBar_Rect2.right - ActionBar_Rect1.left - ActionBar_Rect1.right));
        float translationY = 0.5F * (interpolation * (ActionBar_Rect2.top + ActionBar_Rect2.bottom - ActionBar_Rect1.top - ActionBar_Rect1.bottom));

        view1.setTranslationX(translationX);
        view1.setTranslationY(translationY - ActionBar_Header.getTranslationY());
        view1.setScaleX(scaleX);
        view1.setScaleY(scaleY);
    }

    /*
     * NoBoringActionBar - get_on_screen_rect()
     */
    private RectF get_on_screen_rect (RectF rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect;
    }

    /*
     * NoBoringActionBar - get_scroll_y()
     */
    public int get_scroll_y () {
        View c = MainMenu_ListView.getChildAt(0);
        if (c == null)
            return 0;

        int firstVisiblePosition = MainMenu_ListView.getFirstVisiblePosition();
        int top = c.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1)
            headerHeight = ActionBar_PlaceHolderView.getHeight();

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

    /*
     * NoBoringActionBar - setup_action_bar()
     */
    private void setup_action_bar () {
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setIcon(R.drawable.ic_transparent);
    }

    /*
     * NoBoringActionBar - get_action_bar_iconview()
     */
    private ImageView get_action_bar_iconview () {
        return (ImageView) findViewById(android.R.id.home);
    }

    /*
     * NoBoringActionBar - get_action_bar_height() 
     */
    public int get_action_bar_height () {
        if (ActionBar_Height != 0)
            return ActionBar_Height;
        getTheme().resolveAttribute(android.R.attr.actionBarSize, ActionBar_TypedValue, true);
        ActionBar_Height = TypedValue.complexToDimensionPixelSize(ActionBar_TypedValue.data,
                                                                  getResources().getDisplayMetrics());
        return ActionBar_Height;
    }
}