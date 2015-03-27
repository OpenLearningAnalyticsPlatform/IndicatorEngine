/*
 *
 *  * Copyright (C) 2015  Tanmaya Mahapatra
 *  *
 *  * This program is free software; you can redistribute it and/or
 *  * modify it under the terms of the GNU General Public License
 *  * as published by the Free Software Foundation; either version 2
 *  * of the License, or (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program; if not, write to the Free Software
 *  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package com.indicator_engine.dao;

import com.indicator_engine.datamodel.UserCredentials;


import java.util.List;

/**
 * Created by Tanmaya Mahapatra on 26-02-2015.
 */
public interface UserCredentialsDao {
    public List<UserCredentials> displayall();
    public UserCredentials displayByID(int id);
    public UserCredentials add(UserCredentials uc);
    public void delete(UserCredentials uc);
    public List<UserCredentials> searchByUserName(String uname);
    public UserCredentials findUserByName(String userName);
    public void update(UserCredentials uc);
    public void addAdminROLE(UserCredentials uc);
}