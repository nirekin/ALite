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
// TODO JAVADOC
package gb.aliteorm.lib.tools;

import android.database.sqlite.SQLiteDatabase;

import gb.aliteorm.lib.exception.RRollbackException;

/**
 *
 * Exposes methods to manage a SQLite transactions.
 *
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class Transaction {

	private SQLiteDatabase  db;

	/**
	 * Creates a new transaction on the given database
	 * @param db the database
	 */
	protected Transaction(SQLiteDatabase  db){
		this.db = db;
	}

	/**
	 * Begins a transaction in EXCLUSIVE mode.
	 */
	public void beginTransaction(){
		db.beginTransaction();
	}

	/**
	 * Marks the current transaction as successful and ends the transaction.
	 */
	public void commit(){
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * End the transaction.
	 */
	public void rollback(){
		try{
			db.endTransaction();
		}catch(Exception e){
			throw new RRollbackException(e);
		}
	}

	/**
	 * Returns true if the current thread has a transaction pending.
	 * @return true if the current thread is in a transaction.
	 */
	public boolean inTransaction(){
		return db.inTransaction();
	}
}