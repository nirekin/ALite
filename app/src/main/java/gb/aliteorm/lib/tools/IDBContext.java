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

package gb.aliteorm.lib.tools;

import android.content.Context;

/**
 * Database context.
 *
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public interface IDBContext {

	/**
	 * Returns the list of the persistent entities mapped into the database
	 * @return the list of the persistent entities
	 */
	public IEntityList getEntitiesList();

	/**
	 * Returns the context of the android application using the ALiteOrm
	 * @return the context of the android
	 */
	public Context getAndroidContext();

	/**
	 * Returns the full name ( path and .db name ) used to created the database
	 * <p>
	 * <pre>
	 * Example :
	 * 	  "/sdcard/testALiteOrm.db"
	 * </pre> 
	 * @return the full name used to created the database
	 */
	public String getDBPath();
	
	// TODO EVENTUALLY ADD THE LOCALE HERE
}