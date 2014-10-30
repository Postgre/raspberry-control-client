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

public class Custom_TempSensorsAdapter {

    private String tempsensor_type;
    private String tempsensor_id;
    private String tempsensor_temp;
    private String tempsensor_crc;

    public Custom_TempSensorsAdapter(String tempsensor_type, String tempsensor_id,
                                     String tempsensor_temp, String tempsensor_crc) {
        this.tempsensor_type = tempsensor_type;
        this.tempsensor_id = tempsensor_id;
        this.tempsensor_temp = tempsensor_temp;
        this.tempsensor_crc = tempsensor_crc;
    }

    public String get_tempsensor_type() {
        return tempsensor_type;
    }

    public String get_tempsensor_id() {
        return tempsensor_id;
    }

    public String get_tempsensor_temp() {
        return tempsensor_temp;
    }

    public String get_tempsensor_crc() {
        return tempsensor_crc;
    }
}