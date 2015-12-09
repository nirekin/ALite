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
import android.util.Log;
import gb.aliteorm.lib.query.impl.Criteria;
import gb.aliteorm.lib.tools.ALiteOrmBuilder;
import gb.aliteorm.lib.tools.ILogPrefix;

/**
 * An order to delete instances into a database table
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class DeleteOrder {

	private String t;
	private String w = "";

	/**
	 * Deletes instances base on a criteria condition
	 *
	 * @param e the entity mapping the instances to delete
	 * @param c the criteria defining the where condition
	 */
	public DeleteOrder(Entity e, Criteria c){
		t = e.getTable().getTableName();
		w = c.getSql(false, false, false);
	}

	/**
	 * Deletes from a table base on a  "where" condition
	 *
	 * @param t the name of the table where to delete
	 * @param w the string defining the where condition
	 */
	public DeleteOrder(String t, String w){
		this.t = t;
		this.w = w;
	}

	/**
	 * Deletes from a table base on a  "where" condition
	 *
	 * @param e the entity mapping the instances to delete
	 * @param w the string defining the where condition
	 */
	public DeleteOrder(Entity e, String w){
		this(e.getTable().getTableName(), w);
	}

	/**
	 * Returns the executable SQL sentence
	 * @return the executable an SQL sequence
	 */
	private String getSql(){
		return "DELETE FROM " + t + " " + w;
	}

	/**
	 * Executes the order on the given database
	 * @param db the database
	 */
	public void execute(SQLiteDatabase db){
		if(ALiteOrmBuilder.getInstance().isShowSQL())
			Log.d(ILogPrefix.SQL_LOG, getSql());
		db.execSQL(getSql());
	}
}