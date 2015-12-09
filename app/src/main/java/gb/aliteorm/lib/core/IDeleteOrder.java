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
 * Definition of a delete order
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public interface IDeleteOrder {

	/**
	 * Fill this order with the content of the persistent instance to delete.
	 * <p>
	 * Basically this will complete the generation of the where clause of the order
	 *
	 * @param o the persistent instance to delete
	 * @return the order
	 */
	public IDeleteOrder fill(TravelingEntity o);

	/**
	 * Executes the order on the given database
	 * @param db the database
	 */
	public void execute(SQLiteDatabase db);
}
