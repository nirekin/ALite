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

/**
 * Database update to add a new ElementCollection
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class DBUpdateFullElementCollection extends DBUpdate{


	private DBTable t;

	/**
	 * Creates a new database update to add a given ElementCollection
	 * @param table the table holding all the attributes of the element collection
	 * @param ec the ElementCollection to add to the database
	 */
	public DBUpdateFullElementCollection(DBTable table, ElementCollection ec){
		super(ec.getVersion());
		this.t = table;
	}

	@Override
	public void processUpdate(SQLiteDatabase db){
		t.buildDataBase(db, getVersion());
	}

	@Override
	public String toString(){
		return " v: " + getVersion().versionNumber() + " FullModelCollection: " + t.getTableName();
	}
}