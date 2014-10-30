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
import android.text.Html;
import android.widget.TextView;

public class Activity_ReadMe extends Activity {
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readme);
        
        TextView text_1 = (TextView) findViewById (R.id.dialog_info_text1);
        TextView text_2 = (TextView) findViewById (R.id.dialog_info_text2);
        
        text_1.setText(Html.fromHtml("<b><font color=#aa0000>Raspberry Control ver. 2.0</font></b>" +
                                     "<br><br><center><b><font color=#aa0000>PRE-RELEASE VERSION</font></b></center><br>" +
                                     "<br><center>Control Raspberry Pi with your Android Device</center>"));
        text_2.setText(Html.fromHtml("<html> " +
                        "<br>Author: Lukasz Skalski<br>" +
                        "<br>lukasz.skalski@op.pl<br>" +
                        "<br>" +
                        "<br><center>More information coming soon!</center><br>" +
                        "<br>" +
                        "<br><b><font color=#aa0000>Image Licenses:</font></b><br>" +
                        "<br> <a href=http://en.wikipedia.org/wiki/Raspberry_Pi#mediaviewer/File:Raspberry_Pi_B%2B_top.jpg>Image 1</a>" +
                             "<a href=http://creativecommons.org/licenses/by-sa/3.0/> (CC BY-SA 3.0)</a>" +
                        "<br> <a href=http://commons.wikimedia.org/wiki/File:Raspberry_Pi_Model_B_Rev._2.jpg>Image 2</a>" +
                             "<a href=http://creativecommons.org/licenses/by-sa/3.0/> (CC BY-SA 3.0)</a>" +
                        "<br> <a href=https://www.flickr.com/photos/lwallenstein/3869245383/>Image 3</a>" +
                             "<a href=https://creativecommons.org/licenses/by-sa/2.0/> (CC BY-SA 2.0)</a>" +
                        "</html>"));
    }
}