/*
 * Copyright (C) 2015 Guillaume Barré
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gb.aliteorm.lib.core;

import android.database.sqlite.SQLiteDatabase;

import gb.aliteorm.lib.annotation.ALiteDBVersion;

/**
 * Base implementation of all database updates defined with @ALiteDBVersion
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public abstract class DBUpdate {

	private ALiteDBVersion v;

	/**
	 * Runs this update
	 * @param db the database
	 */
	public abstract void processUpdate(SQLiteDatabase db);

	/**
	 * Returns the SQL sentence corresponding to this update
	 * @return the SQL sentence
	 */
	public abstract String toString();

	/**
	 * Creates a new database update
	 * @param v the version corresponding to this update  ( -1 corresponds to the initial database version )
	 */
	protected DBUpdate(ALiteDBVersion v){
		this.v = v;
	}

	/**
	 * Returns the version corresponding to this update  ( -1 corresponds to the initial database version )
	 * @return the version 
	 */
	protected ALiteDBVersion getVersion(){
		return v;
	}
}
