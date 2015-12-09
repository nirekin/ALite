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

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import gb.aliteorm.lib.core.Entity;
import gb.aliteorm.lib.core.EntityListener;
import gb.aliteorm.lib.core.TravelingEntity;

/**
 * This class is the entry point of the persistence layer.
 * <p>
 * It allows you to build the database schema and specify the desired configuration.
 *
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class ALiteOrmBuilder {

	private static ALiteOrmBuilder instance;

	private Hashtable<Class<?>, Entity> mEnts;
	private MySQLiteHelper dbHelper;
	private boolean showSQL = false;
	private boolean showLog = false;
	private int requiredVersionNumber = 1;
	private ArrayList<EntityListener> externalsCallbacks;

	private ALiteOrmBuilder(){
		mEnts = new Hashtable<Class<?>, Entity>();
		externalsCallbacks = new ArrayList<EntityListener>();
		instance = this;
	}

	/**
	 * Returns the unique instance of the builder
	 * @return the unique instance
	 */
	public static ALiteOrmBuilder getInstance(){
		return instance != null ? instance : new ALiteOrmBuilder();
	}

	/**
	 * Returns the Entity corresponding to the given class.
	 *
	 * A RuntimeException will be thrown if the given class has not been previously registered and
	 * built using the "build(Context context, IEntityList list)" method.
	 *
	 * @param clazz the class annotated with ALiteEntity corresponding to the desired Entity
	 * @return the built Entity
	 */
	public Entity getEntity(Class<?> clazz){
		Entity eM = mEnts.get(clazz);
		if(eM == null){
			throw new RuntimeException("The class " + clazz.getName() + " has not been registered as an Entity");
		}
		return eM;
	}

	/**
	 * Requests the logging of SQL instructions.
	 *
	 * SQL instructions will be logged at the DEBUG level with the prefix "dbSql."
	 *
	 * @param showSQL <code>true</code> if you want to log the SQL instructions otherwise <code>false</code>
	 * @return the unique instance of the builder
	 */
	public ALiteOrmBuilder setShowSQL(boolean showSQL) {
		this.showSQL = showSQL;
		return this;
	}

	/**
	 * Indicates if the logging of SQL instructions is on or not.
	 * @return <CODE>true</CODE> if the logging is turned on, <CODE>false</CODE> otherwise.
	 */
	public boolean isShowSQL() {
		return showSQL;
	}

	/**
	 * Requests the logging of the layer's debug instructions.
	 *
	 * Debug instructions will be logged at the DEBUG level with the prefix "bdLog"
	 *
	 * @param showLog <code>true</code> if you want to log the SQL instructions otherwise <code>false</code>
	 * @return the unique instance of the builder
	 */
	public ALiteOrmBuilder setShowLog(boolean showLog) {
		this.showLog = showLog;
		return this;
	}

	/**
	 * Indicates if the logging of debug instructions is on or not.
	 * @return <CODE>true</CODE> if the logging is turned on, <CODE>false</CODE> otherwise.
	 */
	public boolean isShowLog() {
		return showLog;
	}

	/**
	 * Creates or updates the database schema and make it ready to use
	 *
	 * @param dbcontext the context specifying where and how to build the database
	 * @return the unique instance of the builder
	 */
	public ALiteOrmBuilder build(IDBContext dbcontext){
		List<Class<?>> l = dbcontext.getEntitiesList().getEntities();
		Iterator<Class<?>> it = l.iterator();
		while (it.hasNext()) {
			Class<?> c = (Class<?>) it.next();
			Entity e = new Entity(c);
			if(e.getTable().getRequiredVersion() != null && requiredVersionNumber < e.getTable().getRequiredVersion().versionNumber())
				requiredVersionNumber = e.getTable().getRequiredVersion().versionNumber();
			mEnts.put(c, e);
		}
		dbHelper = new MySQLiteHelper(dbcontext, requiredVersionNumber);
		return this;
	}

	/**
	 * Called when the database needs to be created
	 * @param db the database
	 */
	protected void buildInitialDataBase(SQLiteDatabase  db){
		if(!mEnts.isEmpty()){
			Enumeration<Entity> en = mEnts.elements();
			while (en.hasMoreElements()) {
				en.nextElement().buildInitialDataBase(db);
			}
		}
	}

	/**
	 * Called when the database needs to be upgraded
	 * @param db the database
	 * @param oldVersion the old database version
	 * @param newVersion the new database version
	 */
	protected void updateDataBase(SQLiteDatabase  db, int oldVersion, int newVersion){
		if(!mEnts.isEmpty()){
			Enumeration<Entity> en = mEnts.elements();
			while (en.hasMoreElements()) {
				en.nextElement().updateDataBase(db, oldVersion, newVersion);
			}
		}
	}

	/**
	 * Create and/or open a read/write database
	 * @return a read/write database object valid until close() is called
	 */
	public SQLiteDatabase openWritableDatabase(){
		return dbHelper.getWritableDatabase();
	}

	/**
	 * Adds a new global entity listener
	 * @param c the entity listener to add
	 * @return the unique instance of the builder
	 */
	public ALiteOrmBuilder addGlobalEntityListener(Class<?> c) {
		EntityListener l = new EntityListener(c);
		CallBackTools.loadCallbacks(c.getDeclaredMethods(), l);
		externalsCallbacks.add(l);
		return this;
	}

	/**
	 * Removes a global entity listener
	 * @param c the entity listener to remove
	 * @return the unique instance of the builder
	 */
	public ALiteOrmBuilder removeGlobalEntityListener(Class<?> c) {
		Iterator<EntityListener> it = externalsCallbacks.iterator();
		while (it.hasNext()) {
			EntityListener e = (EntityListener) it.next();
			if(e.getListener() == c)
				it.remove();
		}
		return this;
	}

	/**
	 * Invokes callback methods execution
	 * @param c the annotation class identifying the entity life cycle's event executed
	 * @param o the instance target of the callback
	 */
	public void runCallBack(Class<?> c, TravelingEntity o){
		Iterator<EntityListener> it = externalsCallbacks.iterator();
		while (it.hasNext()) {
			EntityListener externalListener = (EntityListener) it.next();
			externalListener.runCallBack(c, o);
		}
	}
}