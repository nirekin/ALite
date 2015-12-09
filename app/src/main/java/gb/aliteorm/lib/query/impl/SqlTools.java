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

package gb.aliteorm.lib.query.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import gb.aliteorm.lib.core.Entity;
import gb.aliteorm.lib.impl.columns.IDBColumn;
import gb.aliteorm.lib.tools.ALiteOrmBuilder;
import gb.aliteorm.lib.tools.ILogPrefix;

/**
 * Tools box to group the database calls and manipulations
 *
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class SqlTools {

	/**
	 * Counts all the row within the given table.
	 *
	 * @param db the database
	 * @param tableName the table where to count
	 * @return the number of rows
	 */
	public static int countAll(SQLiteDatabase db, String tableName){
		String sql = String.format(ISqlString.COUNT_SQL, tableName);
		if(ALiteOrmBuilder.getInstance().isShowSQL())
			Log.d(ILogPrefix.SQL_LOG, sql);

		Cursor c = db.rawQuery(sql, new String[] {});
		if (c != null && c.moveToFirst()) {
			return c.getInt(0);
		}
		return 0;
	}

	/**
	 * Returns the maximum int value for the given column.
	 *
	 * @param db the database
	 * @param tableName the desired table name
	 * @param columnName the desired column name
	 * @return the maximum int value for the given column
	 */
	public static int getMaxInt(SQLiteDatabase db, String tableName, String columnName){
		String sql = String.format(ISqlString.MAX_SQL, columnName, tableName);
		if(ALiteOrmBuilder.getInstance().isShowSQL())
			Log.d(ILogPrefix.SQL_LOG, sql);

		Cursor c = db.rawQuery(sql, new String[] {});
		if (c != null && c.moveToFirst()) {
			return c.getInt(0);
		}
		return -1;
	}

	/**
	 * Delete from the DB where "key" = "value".
	 * @param db the database
	 * @param tableName the table containing the rows to delete
	 * @param where the where condition to identity the rows to delete
	 */
	public static void delete(SQLiteDatabase db, String tableName ,String where){
		String sql = String.format(ISqlString.DELETE_SQL, tableName, where);
		if(ALiteOrmBuilder.getInstance().isShowSQL())
			Log.d(ILogPrefix.SQL_LOG, sql);
		db.execSQL(sql);
	}

	/**
	 * Delete DELETE FROM tableName WHERE joinName NOT IN ( SELECT DISTINCT(foreignId.getName()) FROM foreignEntity.getTableName()).
	 *
	 * @param db the database
	 * @param tableName the table where to delete the rows
	 * @param joinName the joinName linking the table where to delete and the foreignEntity
	 * @param foreignId the id column of the foreignEntity
	 * @param foreignEntity the foreign Entity, the class that use to reference the orphans to delete
	 */
	public static void removeOrphans(SQLiteDatabase db, String tableName, String joinName, IDBColumn foreignId ,Entity foreignEntity){
		String sql = String.format(ISqlString.DELETE_ORPHANS, tableName, joinName, foreignId.getAttribute().getDBName(), foreignEntity.getTable().getTableName());
		if(ALiteOrmBuilder.getInstance().isShowSQL())
			Log.d(ILogPrefix.SQL_LOG, sql);
		db.execSQL(sql);
	}
}