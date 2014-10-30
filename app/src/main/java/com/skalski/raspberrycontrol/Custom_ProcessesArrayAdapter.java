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

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Custom_ProcessesArrayAdapter extends ArrayAdapter<Custom_ProcessAdapter> {

    private LayoutInflater inflater;
    private List<Custom_ProcessAdapter> data;

    public Custom_ProcessesArrayAdapter(Context context, List<Custom_ProcessAdapter> objects) {
        super(context, R.layout.custom_processeslist, objects);
        inflater= LayoutInflater.from(context);
        this.data=objects;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView == null) {

            convertView = inflater.inflate(R.layout.custom_processeslist, null);
            holder = new ViewHolder();

            holder.process_image = (ImageView) convertView.findViewById(R.id.process_image);
            holder.process_name = (TextView) convertView.findViewById(R.id.process_name);
            holder.process_pid = (TextView) convertView.findViewById(R.id.process_pid_value);
            holder.process_user = (TextView) convertView.findViewById(R.id.process_user_value);

            convertView.setTag(holder);

        } else {
            holder =(ViewHolder) convertView.getTag();
        }

        switch (data.get(position).get_process_status().charAt(0)) {

            case 'R':  holder.process_image.setImageResource(R.drawable.processes_icon_02);
                break;

            case 'S':  holder.process_image.setImageResource(R.drawable.processes_icon_03);
                break;

            case 'U':  holder.process_image.setImageResource(R.drawable.processes_icon_05);
                break;

            case 'Z':  holder.process_image.setImageResource(R.drawable.processes_icon_04);
                break;

            case 'T':  holder.process_image.setImageResource(R.drawable.processes_icon_06);
                break;

            default:   holder.process_image.setImageResource(R.drawable.processes_icon_01);
                break;
        }

        if (data.get(position).get_process_name() != null)
            holder.process_name.setText(data.get(position).get_process_name());

        if (data.get(position).get_process_pid() != null)
            holder.process_pid.setText (data.get(position).get_process_pid());

        if (data.get(position).get_process_user() != null)
            holder.process_user.setText(data.get(position).get_process_user());

        return convertView;
    }

    static class ViewHolder{
        ImageView process_image;
        TextView process_name;
        TextView process_pid;
        TextView process_user;
    }
}