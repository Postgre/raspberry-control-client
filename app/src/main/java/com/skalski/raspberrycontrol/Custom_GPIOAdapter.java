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

public class Custom_GPIOAdapter {

    private int gpio_num;
    private int gpio_value;
    private String gpio_direction;

    public Custom_GPIOAdapter(int gpio_num, int gpio_value, String gpio_direction) {
        this.gpio_num = gpio_num;
        this.gpio_value = gpio_value;
        this.gpio_direction = gpio_direction;
    }

    public int get_gpio_num() {
        return gpio_num;
    }

    public int get_gpio_value() {
        return gpio_value;
    }

    public String get_gpio_direction() {
        return gpio_direction;
    }
}