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

import com.indicator_engine.datamodel.GLACategory;

import java.util.List;

/**
 * Created by Tanmaya Mahapatra on 23-03-2015.
 */
public interface GLACategoryDao {
    public long add(GLACategory glaCategory);
    public List<GLACategory> loadCategoryRange(long maxId);
    public int getTotalCategories();
    public List<String> selectAllMinors();
    public GLACategory  loadcategoryByname(String categoryname);

}