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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Custom_MainMenuList extends ArrayAdapter <String> {

	private final Activity  custom_mainmenu_context;
	private final Integer[] custom_mainmenu_icon;
	private final String[]  custom_mainmenu_title;
	private final String[]  custom_mainmenu_description;
	
	/*
	 * Custom_MainMenuList()
	 */
	public Custom_MainMenuList (Activity mainmenu_context, Integer[] mainmenu_icon,
                                String[] mainmenu_title, String[] mainmenu_description) {
		super(mainmenu_context, R.layout.custom_mainmenulist, mainmenu_title);
		this.custom_mainmenu_context = mainmenu_context;
		this.custom_mainmenu_icon = mainmenu_icon;
		this.custom_mainmenu_title = mainmenu_title;
		this.custom_mainmenu_description = mainmenu_description;
	}
	
	/*
	 * getView()
	 */
	@Override
	public View getView (int position, View view, ViewGroup parent) {
		
		LayoutInflater inflater = custom_mainmenu_context.getLayoutInflater();
		View rowView= inflater.inflate(R.layout.custom_mainmenulist, null, true);
		
		ImageView mainmenu_icon = (ImageView) rowView.findViewById(R.id.mainmenu_icon);
		TextView mainmenu_title = (TextView) rowView.findViewById(R.id.mainmenu_title);
		TextView mainmenu_description = (TextView) rowView.findViewById(R.id.mainmenu_description);
		
		mainmenu_icon.setImageResource(custom_mainmenu_icon[position]);
		mainmenu_title.setText(custom_mainmenu_title[position]);
		mainmenu_description.setText(custom_mainmenu_description[position]);
		
		return rowView;
	}
}