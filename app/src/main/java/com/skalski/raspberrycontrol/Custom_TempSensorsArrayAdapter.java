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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class Custom_TempSensorsArrayAdapter extends ArrayAdapter<Custom_TempSensorsAdapter> {

    private LayoutInflater inflater;
    private List<Custom_TempSensorsAdapter> data;

    public Custom_TempSensorsArrayAdapter(Context context, List<Custom_TempSensorsAdapter> objects) {
        super(context, R.layout.custom_tempsensorslist, objects);
        inflater= LayoutInflater.from(context);
        this.data=objects;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView == null) {

            convertView = inflater.inflate(R.layout.custom_tempsensorslist, null);
            holder = new ViewHolder();

            holder.tempsensor_type = (TextView) convertView.findViewById(R.id.tempsensor_type);
            holder.tempsensor_id = (TextView) convertView.findViewById(R.id.tempsensor_id_value);
            holder.tempsensor_temp = (TextView) convertView.findViewById(R.id.tempsensor_temp_value);
            holder.tempsensor_crc = (TextView) convertView.findViewById(R.id.tempsensor_crc_value);

            convertView.setTag(holder);

        } else {
            holder =(ViewHolder) convertView.getTag();
        }

        if (data.get(position).get_tempsensor_type() != null)
            holder.tempsensor_type.setText(data.get(position).get_tempsensor_type());

        if (data.get(position).get_tempsensor_id() != null)
            holder.tempsensor_id.setText(data.get(position).get_tempsensor_id());

        if (data.get(position).get_tempsensor_temp() != null)
            holder.tempsensor_temp.setText (data.get(position).get_tempsensor_temp());

        if (data.get(position).get_tempsensor_crc() != null)
            holder.tempsensor_crc.setText(data.get(position).get_tempsensor_crc());

        return convertView;
    }

    static class ViewHolder{
        TextView tempsensor_type;
        TextView tempsensor_id;
        TextView tempsensor_temp;
        TextView tempsensor_crc;
    }
}