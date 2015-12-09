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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import gb.aliteorm.lib.query.impl.Criteria;
import gb.aliteorm.lib.query.impl.ISqlString;
import gb.aliteorm.lib.tools.ALiteOrmBuilder;
import gb.aliteorm.lib.tools.ILogPrefix;

/**
 * A "select " order to query a database table
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class SelectOrder {

	private String t;
	private String select = "";
	private String w = "";

	/**
	 * Select from a table base on the specified criteria condition
	 *
	 * @param e the entity mapped to the database table
	 * @param c the criteria defining the where condition
	 */
	public SelectOrder(Entity e, Criteria c){
		t = e.getTable().getTableName();
		w = c.getSql(true, true, true);
		select = c.getSelect();
	}

	/**
	 * Select from a table base on a specified "where" condition
	 *
	 * @param t the name of the database table
	 * @param w the string defining the where condition
	 */
	public SelectOrder(String t, String w){
		this.t = t;
		this.w = w;
		select = ISqlString.SELECT_ALL_SQL;
	}

	/**
	 * Select from a table base on specified "where" condition
	 *
	 * @param e the entity mapped to the database table
	 * @param e the string defining the where condition
	 */
	public SelectOrder(Entity e, String w){
		this(e.getTable().getTableName(), w);
	}

	/**
	 * Returns the executable SQL sentence
	 * @return the SQL
	 */
	private String getSql(){
		return select + t + " " + w;
	}

	/**
	 * Executes the order on the given database
	 * @param db the database
	 * @return the cursor containing the matched query results
	 */
	public Cursor execute(SQLiteDatabase db){
		if(ALiteOrmBuilder.getInstance().isShowSQL())
			Log.d(ILogPrefix.SQL_LOG, getSql());
		return db.rawQuery(getSql(), new String[] {});
	}
}