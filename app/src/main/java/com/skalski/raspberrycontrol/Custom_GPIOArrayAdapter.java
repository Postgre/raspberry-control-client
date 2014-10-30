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

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;

public class Custom_GPIOArrayAdapter extends ArrayAdapter<Custom_GPIOAdapter> {

    private static final String LOGTAG = "RaspberryControl";
    private static final String LOGPREFIX = "GPIO: ";

    private static final String TAG_GPIO_HIGH = "HIGH";
    private static final String TAG_GPIO_LOW = "LOW";
    private static final String TAG_GPIO_IN = "IN";
    private static final String TAG_GPIO_OUT = "OUT";

    private Custom_WebSocketClient client;
    private LayoutInflater inflater;
    private List<Custom_GPIOAdapter> data;

    public Custom_GPIOArrayAdapter(Context context, List<Custom_GPIOAdapter> objects) {
        super(context, R.layout.custom_gpiolist, objects);
        inflater= LayoutInflater.from(context);
        this.data=objects;
    }

    @Override
    public View getView (final int position, View convertView, final ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.custom_gpiolist, null);
            holder = new ViewHolder();

            holder.gpio_name = (TextView) convertView.findViewById(R.id.gpio_name_label);
            holder.gpio_value = (Button) convertView.findViewById(R.id.gpio_button_value);
            holder.gpio_direction = (Button) convertView.findViewById(R.id.gpio_button_direction);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        /* name */
        holder.gpio_name.setText("GPIO " + Integer.toString(data.get(position).get_gpio_num()));

        /* value */
        if (data.get(position).get_gpio_value() == 1) {
            holder.gpio_value.setText(TAG_GPIO_HIGH);
            holder.gpio_value.setBackgroundResource(R.drawable.button_red);
        } else if (data.get(position).get_gpio_value() == 0) {
            holder.gpio_value.setText(TAG_GPIO_LOW);
            holder.gpio_value.setBackgroundResource(R.drawable.button_green);
        }

        /* direction */
        if ((data.get(position).get_gpio_direction()).equals("in")) {
            holder.gpio_direction.setText(TAG_GPIO_IN);
            holder.gpio_direction.setBackgroundResource(R.drawable.button_purple);
            holder.gpio_value.setEnabled(false);
        } else if ((data.get(position).get_gpio_direction()).equals("out")) {
            holder.gpio_direction.setText(TAG_GPIO_OUT);
            holder.gpio_direction.setBackgroundResource(R.drawable.button_yellow);
            holder.gpio_value.setEnabled(true);
        }

        /* set value */
        holder.gpio_value.setTag(position);
        holder.gpio_value.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String gpio_num;
                String gpio_value;

                gpio_num = Integer.toString(data.get(position).get_gpio_num());
                gpio_value = "0";

                if (data.get(position).get_gpio_value() == 1)
                    gpio_value = "0";
                else if (data.get(position).get_gpio_value() == 0)
                    gpio_value = "1";

                client = ((Custom_WebSocketClient) getContext().getApplicationContext());
                if (!client.send("{\"RunCommand\":{\"cmd\":\"SetGPIO\",\"args\":\"" + gpio_num + " " + gpio_value + "\"}}")) {
                    Log.e (LOGTAG, LOGPREFIX + "can't set GPIO value");
                }
            }
        });

        /* set direction */
        holder.gpio_direction.setTag(position);
        holder.gpio_direction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String gpio_num;
                String gpio_direction;

                gpio_num = Integer.toString(data.get(position).get_gpio_num());
                gpio_direction = "out";

                if ((data.get(position).get_gpio_direction()).equals("in"))
                    gpio_direction = "out";
                else if ((data.get(position).get_gpio_direction()).equals("out"))
                    gpio_direction = "in";

                client = ((Custom_WebSocketClient) getContext().getApplicationContext());
                if (!client.send("{\"RunCommand\":{\"cmd\":\"SetGPIO\",\"args\":\"" + gpio_num + " " + gpio_direction + "\"}}")) {
                    Log.e (LOGTAG, LOGPREFIX + "can't set GPIO direction");
                }
            }
        });

        return convertView;
    }

    static class ViewHolder{
        TextView gpio_name;
        Button gpio_value;
        Button gpio_direction;
    }
}