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

import gb.aliteorm.lib.tools.ALiteOrmBuilder;
import gb.aliteorm.lib.tools.ILogPrefix;

/**
 * Database update to add an attribute.
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class DBUpdateAttribute extends DBUpdate{

	private Attribute f;
	private DBTable t;
	private boolean allowingPKConstraint;

	/**
	 * Creates a new database update to add a given attribute
	 * @param t the table containing the attribute to add
	 * @param f the attribute to add
	 * @param allowingPKConstraint indicates if the SQL used to add the attribute in the table 
	 * must or not contain the table key constraint specifications
	 * 
	 */
	public DBUpdateAttribute(DBTable t, Attribute f, boolean allowingPKConstraint){
		super(f.getVersion());
		this.f = f;
		this.t = t;
		this.allowingPKConstraint = allowingPKConstraint;
	}

	@Override
	public void processUpdate(SQLiteDatabase db){
		if(f.getVersion() != null && f.getVersion().versionNumber() > super.getVersion().versionNumber()){
			if(ALiteOrmBuilder.getInstance().isShowLog())
				Log.d(ILogPrefix.ACTIVITY_LOG, "ColumnContainer: the attribute : " + f.getDBColumn().getAttribute().getModelName() + " from : " + f.getClass() + " will be added later in:" + f.getVersion().versionNumber() + " and not int :" + super.getVersion());
			return;
		}
		f.getDBColumn().alterTableColumn(db, allowingPKConstraint);
	}

	@Override
	public String toString(){
		return " v: " + getVersion().versionNumber() + " Attribute: " + f.getDBColumn().getAttribute().getDBName() + " , Table:" + t.getTableName();
	}
}
