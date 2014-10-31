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

public class Custom_ProcessAdapter {

    private String process_name;
    private String process_pid;
    private String process_user;
    private String process_status;

    public Custom_ProcessAdapter (String process_name, String process_pid,
                                  String process_user, String process_status) {
        this.process_name = process_name;
        this.process_pid = process_pid;
        this.process_user = process_user;
        this.process_status = process_status;
    }

    public String get_process_name() {
        return process_name;
    }

    public String get_process_pid() {
        return process_pid;
    }

    public String get_process_user() {
        return process_user;
    }

    public String get_process_status() {
        return process_status;
    }
}
