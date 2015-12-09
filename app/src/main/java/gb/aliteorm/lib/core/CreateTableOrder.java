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

import java.util.ArrayList;
import java.util.Iterator;

import gb.aliteorm.lib.exception.EmptyCreateOrderException;
import gb.aliteorm.lib.tools.ALiteOrmBuilder;
import gb.aliteorm.lib.tools.ILogPrefix;
import gb.aliteorm.lib.tools.StringTools;

/**
 * An order to create a table into the database
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class CreateTableOrder {

	private String t;
	private ArrayList<String> c;

	/**
	 * Creates a new order
	 * @param t the table holding the fields to map
	 */
	public CreateTableOrder(DBTable t){
		this(t.getTableName());
	}

	/**
	 * Creates a new order
	 * @param t the name of the table to create
	 */
	public CreateTableOrder(String t){
		this.t = t;
		c = new ArrayList<String>();
	}

	/**
	 * Add a column definition to the create order
	 *
	 * @param s the SQL to create the column into the database
	 */
	public void addColumnDefinition(String s){
		c.add(s);
	}

	/**
	 * Returns the SQL sentence to create the database table
	 *
	 * @return the SQL sentence
	 * @throws EmptyCreateOrderException will be thrown if there is no column to create
	 */
	private String getSql() throws EmptyCreateOrderException{
		StringBuilder strb = new StringBuilder("CREATE TABLE IF NOT EXISTS [" + t + "]  (");
		if(c.isEmpty())
			throw new EmptyCreateOrderException();

		Iterator<String> cName = c.iterator();
		while (cName.hasNext()) {
			strb.append(cName.next())
			.append(",");
		}
		return StringTools.removeLastChar(strb.toString()) + " )";
	}

	/**
	 * Executes the order on the given database
	 * @param db the database
	 */
	public void execute(SQLiteDatabase db){
		try{
			if(ALiteOrmBuilder.getInstance().isShowSQL())
				Log.d(ILogPrefix.SQL_LOG, getSql());
			db.execSQL(getSql());
		}catch(EmptyCreateOrderException ecoe){
			ecoe.printStackTrace();
			// Do Nothing
		}
	}
}