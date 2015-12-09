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
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Helper class to manage database creation and get open database.
 *
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class MySQLiteHelper extends SQLiteOpenHelper {


	/**
	 * Create the persistence layer database.
	 *
	 * @param dbcontext the context used to specify the database context and path
	 * @param requiredDbVersion the required database version ( coming from @ALiteDBVersion )
	 */
	public MySQLiteHelper(IDBContext dbcontext, int requiredDbVersion) {
		super(dbcontext.getAndroidContext(), dbcontext.getDBPath(), null, requiredDbVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if(ALiteOrmBuilder.getInstance().isShowLog())
			Log.d(ILogPrefix.ACTIVITY_LOG, "MySQLiteHelper: Initial database creation : " + db.getPath() );
		ALiteOrmBuilder.getInstance().buildInitialDataBase(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(ALiteOrmBuilder.getInstance().isShowLog())
			Log.d(ILogPrefix.ACTIVITY_LOG, "MySQLiteHelper: Database update : " + db.getPath() + " from : " + oldVersion + " to : " + newVersion);
		ALiteOrmBuilder.getInstance().updateDataBase(db, oldVersion, newVersion);
	}
}