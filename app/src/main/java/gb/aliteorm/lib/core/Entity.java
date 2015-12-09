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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import gb.aliteorm.lib.annotation.ALiteAttributeOverride;
import gb.aliteorm.lib.annotation.ALiteAttributeOverrides;
import gb.aliteorm.lib.annotation.ALiteElementCollection;
import gb.aliteorm.lib.annotation.ALiteEmbedded;
import gb.aliteorm.lib.annotation.ALiteEmbeddedId;
import gb.aliteorm.lib.annotation.ALiteEntity;
import gb.aliteorm.lib.annotation.ALiteEntityListeners;
import gb.aliteorm.lib.annotation.ALiteExcludeGlobalListeners;
import gb.aliteorm.lib.annotation.ALiteExcludeSessionListeners;
import gb.aliteorm.lib.annotation.ALitePostLoad;
import gb.aliteorm.lib.annotation.ALitePostPersist;
import gb.aliteorm.lib.annotation.ALitePostRemove;
import gb.aliteorm.lib.annotation.ALitePostUpdate;
import gb.aliteorm.lib.annotation.ALitePrePersist;
import gb.aliteorm.lib.annotation.ALitePreRemove;
import gb.aliteorm.lib.annotation.ALitePreUpdate;
import gb.aliteorm.lib.exception.RMoreThanOneIdException;
import gb.aliteorm.lib.exception.RNoEntityException;
import gb.aliteorm.lib.exception.RNoIdException;
import gb.aliteorm.lib.exception.RNoResultException;
import gb.aliteorm.lib.exception.RNonUniqueResultException;
import gb.aliteorm.lib.exception.RWrongAutoIncrementTypeException;
import gb.aliteorm.lib.impl.columns.DBColumnFactory;
import gb.aliteorm.lib.impl.columns.DBColumnInt;
import gb.aliteorm.lib.impl.columns.IDBColumn;
import gb.aliteorm.lib.query.impl.Criteria;
import gb.aliteorm.lib.query.impl.ISqlString;
import gb.aliteorm.lib.query.impl.ProjAttribute;
import gb.aliteorm.lib.query.impl.SqlTools;
import gb.aliteorm.lib.tools.ALiteOrmBuilder;
import gb.aliteorm.lib.tools.CallBackTools;
import gb.aliteorm.lib.tools.ILogPrefix;
import gb.aliteorm.lib.tools.ReflectionTools;
import gb.aliteorm.lib.tools.Session;

/**
 * Entity mapped into the data base.
 * <p>
 * Each entity must be annotated with <code>@ALiteEntity</code>.<br>
 * All intents of using ALiteOrm with classes not annotated with <code>@ALiteEntity</code> will throw an exception
 * <p>
 * Each class annotated with @ALiteEntity will be mapped into its own database table.
 * <p>
 *
 * <ul>
 *		<li>A mapped class must have a public no argument constructor</li>
 *		<li>By default we will use the class name as name of the related database table</li>
 *		<li>A mapped class must contains one <code>@ALiteEnId</code> or one <code>@ALiteEmbeddedId</code></li>
 *		<li>A mapped class can inherit from a <code>@ALiteMappedSuperclass</code></li>
 *		<li>A mapped class can reference <code>@ALiteEmbeddable</code></li>
 *		<li>A mapped class can reference <code>@ALiteElementCollection</code></li>
 *		<li>A mapped class can be annotated with <code>@ALiteDBVersion</code></li>
 *		<li>A mapped class can be annotated with <code>@ALiteAttributeOverrides</code> or <code>@ALiteAttributeOverride</code></li>
 *		<li>A mapped class can be annotated with <code>@ALiteEntityListeners</code></li>
 *		<li>A mapped class can be annotated with <code>@ALiteExcludeGlobalListeners</code></li>
 *		<li>A mapped class can be annotated with <code>@ALiteExcludeSessionListeners</code></li>
 *	</ul>
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class Entity  extends AttributesContainer{

	private static long timeLoad;
	private EntityId id;
	private DBTable table;
	private ALiteEntity mainAnn;
	private ArrayList<ElementCollection> elementCollections;
	private Hashtable<Class<?>, Method> internalCallbacks;
	private ArrayList<EntityListener> externalsCallbacks;
	private boolean excludeSessionListener = false, excludeGlobalListener = false;
	private Hashtable<String, Cursor> cursors = new Hashtable<String, Cursor>();

	/**
	 * Creates a new entity
	 * @param implementationClass the class implementing the entity
	 */
	public Entity(Class<?> implementationClass){
		super(implementationClass, null);
		elementCollections = new ArrayList<ElementCollection>();
		internalCallbacks = new Hashtable<Class<?>, Method>();
		externalsCallbacks = new ArrayList<EntityListener>();

		checkAnnotation();
		loadOverride();
		table = new DBTable(this);
		loadMappedSupper(table);
		loadAttributes(table);
		loadEmbeddeds(table);
		loadElementCollection();
		CallBackTools.loadCallbacks(ms, new ICallBackContainer() {
			@Override
			public void addCallBack(Class<?> c, Method m) {
				Entity.this.addCallBack(c, m);
			}
		});
		getId();
	}

	/**
	 * Loads the overridden columns for this entity
	 */
	private void loadOverride(){
		Annotation ann = getImplementationClass().getAnnotation(ALiteAttributeOverride.class);
		if(ann != null){
			ALiteAttributeOverride a = (ALiteAttributeOverride)ann;
			override.put(a.name(), a.column());

		}
		ann = getImplementationClass().getAnnotation(ALiteAttributeOverrides.class);
		if(ann != null){
			ALiteAttributeOverrides a = (ALiteAttributeOverrides)ann;
			for (int i = 0; i < a.value().length; i++) {
				override.put(a.value()[i].name(), a.value()[i].column());
			}
		}
	}

	/**
	 * Loads the element collection for this entity
	 */
	private void loadElementCollection(){
		// TODO 1 avoid reading a second time the array of methods here
		Method m;
		Annotation ann;
		for (int i = 0; i < ms.length; i++) {
			m = ms[i];
			if(ignoreAttribute(m))
				continue;

			ann = m.getAnnotation(ALiteEmbedded.class);
			if(ann != null)
				continue;

			ann = m.getAnnotation(ALiteEmbeddedId.class);
			if(ann != null)
				continue;

			ann = m.getAnnotation(ALiteElementCollection.class);
			if(ann != null){
				try{
					Method s = ReflectionTools.getSetter(m);
					ElementCollection ec = new ElementCollection(this, m, s);
					elementCollections.add(ec);
					table.updateRequiredVersion(ec.getTable());
				}catch(NoSuchMethodException nsme){
					throw new RuntimeException("Error getting the setter for : " + m.getName() + " for : " + getImplementationClass().getName());
				}
			}
		}
	}

	/**
	 * Checks the annotation
	 * @throws RNoEntityException will be thrown if the implementation class is not annotated with <code>@ALiteENtity</code>
	 */
	private void checkAnnotation() throws RNoEntityException{
		Annotation ann = getImplementationClass().getAnnotation(ALiteEntity.class);
		if(ann == null)
			throw new RNoEntityException("For : " + getImplementationClass().getName());
		mainAnn = (ALiteEntity)ann;

		ann = getImplementationClass().getAnnotation(ALiteEntityListeners.class);
		if(ann != null){
			ALiteEntityListeners e = (ALiteEntityListeners)ann;
			loadExternalCallbacks(e);
		}

		ann = getImplementationClass().getAnnotation(ALiteExcludeGlobalListeners.class);
		if(ann != null){
			excludeGlobalListener = true;
		}

		ann = getImplementationClass().getAnnotation(ALiteExcludeSessionListeners.class);
		if(ann != null){
			excludeSessionListener = true;
		}
	}

	/**
	 * Builds the table to map the entity into the initial database
	 * @param db the database
	 */
	public void buildInitialDataBase(SQLiteDatabase db){
		if(ALiteOrmBuilder.getInstance().isShowLog())
			Log.d(ILogPrefix.ACTIVITY_LOG, "creating the table for the entity : " + getImplementationClass().getName());
		table.buildInitialDataBase(db);

		Iterator<ElementCollection> it = elementCollections.iterator();
		while (it.hasNext()) {
			ElementCollection ec = (ElementCollection ) it.next();
			ec.buildInitialDataBase(db);
		}
	}

	/**
	 * Updates the database table for a specific schema version
	 * @param db the database
	 * @param oldVersion The old database version.
	 * @param newVersion The new database version.
	 */
	public void updateDataBase(SQLiteDatabase  db, int oldVersion, int newVersion){
		if(ALiteOrmBuilder.getInstance().isShowLog())
			Log.d(ILogPrefix.ACTIVITY_LOG, "updating the table for the entity : " + getImplementationClass().getName());
		for(int i = oldVersion + 1; i <= newVersion; i++){
			table.updateDataBase(db, i);
		}

		Iterator<ElementCollection> it = elementCollections.iterator();
		while (it.hasNext()) {
			ElementCollection ec = (ElementCollection ) it.next();
			for(int i = oldVersion + 1; i <= newVersion; i++){
				ec.updateDataBase(db, i);
			}
		}
	}

	/**
	 * Returns the annotation associated with this entity
	 * @return the annotation
	 */
	public ALiteEntity getEntityAnnotation(){
		return mainAnn;
	}


	/**
	 * Returns the entity's id
	 *
	 * @return the entity's id
	 *
	 * @throws RMoreThanOneIdException will be thrown if the whole definition of the entity contains more than one <code>@ALiteId</code> or <code>@AliteEmbeddedId</code>
	 * @throws RNoIdException will be thrown if the whole definition of the entity contains no <code>@ALiteId</code> or <code>@AliteEmbeddedId</code>
	 * @throws RWrongAutoIncrementTypeException will be thrown if the getter annotated with <code>@ALiteId(auto=true)</code> has a wrong return type
	 */
	public EntityId getId() throws RMoreThanOneIdException, RNoIdException, RWrongAutoIncrementTypeException{
		if(id == null){
			ArrayList<IDBColumn> ids = table.getAllIds();
			ArrayList<Embeddable> embeddesIds = getEmbeddedIds();
			if(ids.isEmpty() && embeddesIds.isEmpty()){
				throw new RNoIdException("No id defined for : " + getImplementationClass().getName());
			}
			if((ids.size() >0 && embeddesIds.size() > 0)
					||
					(ids.size() == 0 && embeddesIds.size() > 1)
					||
					(ids.size() > 1 && embeddesIds.size() == 0)
					){
				throw new RMoreThanOneIdException("More than one ID ( ALiteId or ALiteEmbeddedId ) for  : " + getImplementationClass().getName());
			}

			if(ids.size() == 1){
				IDBColumn idColumn = ids.get(0);
				if(idColumn.isAutoIncrement() && idColumn instanceof DBColumnInt == false)
					throw new RWrongAutoIncrementTypeException("Autoincrement is only available for int or Integer attributes : " + getImplementationClass().getName());
				id = new EntityId(ids.get(0));
			}else{
				id = new EntityId(embeddesIds.get(0), table);
			}
		}
		table.setId(id);
		return id;
	}

	/**
	 * Returns the table holding all the attributes mapped for this entity
	 * @return the table
	 */
	public DBTable getTable(){
		return table;
	}

	/**
	 * Counts instances into the table mapped with this entity
	 * @param db the database
	 * @return the number of instances
	 */
	public int countAll(SQLiteDatabase db){
		String sql = String.format(ISqlString.COUNT_SQL, table.getTableName());
		if(ALiteOrmBuilder.getInstance().isShowSQL())
			Log.d(ILogPrefix.SQL_LOG, sql);

		Cursor cur = db.rawQuery(sql, new String[] {});
		if (cur != null && cur.moveToFirst()) {
			return cur.getInt(0);
		}
		return 0;
	}

	/**
	 * Deletes an instance into the table mapped with this entity
	 * <p>
	 * This operation cascades to associated instances
	 * 
	 * @param session the session calling the delete
	 * @param db the database
	 * @param e the instance to delete
	 */
	public void delete(Session session, SQLiteDatabase db, TravelingEntity e){
		runCallBack(session, ALitePreRemove.class, e);
		table.getDeleteOrder().fill(e).execute(db);
		removeOrphans(db);
		runCallBack(session, ALitePostRemove.class, e);
	}

	/**
	 * Deletes all instances into the table mapped with this entity
	 * <p>
	 * This operation cascades to associated instances
	 * 
	 * @param db the database
	 */
	public void deleteAll(SQLiteDatabase db){
		String sql = String.format(ISqlString.DELETE_ALL_SQL, table.getTableName());
		if(ALiteOrmBuilder.getInstance().isShowSQL())
			Log.d(ILogPrefix.SQL_LOG, sql);
		db.execSQL(sql);
		removeOrphans(db);
	}

	/**
	 * Lists instances from the table mapped with this entity for a given criteria
	 * <p>
	 * This operation cascades to associated instances
	 * 
	 * @param session the session calling the list
	 * @param criteria the criteria for the selection
	 * @param db the database
	 * @param includeRelated indicates id the related content must be loaded or not
	 * @return the instances corresponding to the criteria for the selection
	 */
	public List<Object> list(Session session, Criteria criteria, SQLiteDatabase db, boolean includeRelated){
		Cursor cursor = getCursor(criteria, db);
		if(criteria.hasPojections()){
			return loadProjection(criteria, session, db, cursor, includeRelated);
		}else{
			ArrayList<Object> resultList = new ArrayList<Object>();
			if (cursor.moveToFirst()) {
				do {
					resultList.add(loadEntity(session, db, cursor, includeRelated).getContent());
				} while (cursor.moveToNext());
			}
			cursor.close();
			return resultList;
		}
	}

	/**
	 * Return a cursor providing  access to the result set returned by a criteria
	 *
	 * @param criteria the criteria for the selection
	 * @param db the database
	 * @return the cursor
	 */
	public Cursor getCursor(Criteria criteria, SQLiteDatabase db){
		return new SelectOrder(this, criteria).execute(db);
	}

	
	/**
	 * Deletes instances from the table mapped with this entity for a given criteria
	 * <p>
	 * This operation cascades to associated instances
	 * 
	 * @param session the session calling the delete
	 * @param criteria the criteria for the delete
	 * @param db the database
	 */
	public void delete(Session session, Criteria criteria, SQLiteDatabase db){
		new DeleteOrder(this, criteria).execute(db);
		removeOrphans(db);
	}

	/**
	 * Loads an instance of this entity corresponding to the given id
	 * <p>
	 * This operation cascades to associated instances 
	 * 
	 * @param session the session calling the load
	 * @param db the database
	 * @param id the id of the instance to load
	 * @param includeRelated indicates id the related content must be loaded or not
	 * @return the loaded instance
	 * @throws RNonUniqueResultException will be thrown if there is more than one instance/row for the given id
	 * @throws RNoResultException will be thrown if there is no instance/row for the given id
	 */
	public Object load(Session session, SQLiteDatabase db, TravelingId id, boolean includeRelated) throws RNonUniqueResultException, RNoResultException{
		timeLoad = System.currentTimeMillis();
		Cursor cursor = buildSelectOrder(id).execute(db);
		if(cursor.getCount() == 0)
			throw new RNoResultException("For : " + getImplementationClass().getName() + ", Id : " + id);
		if(cursor.getCount() > 1)
			throw new RNonUniqueResultException("For : " + getImplementationClass().getName());

		cursor.moveToFirst();

		try {
			TravelingEntity o = loadEntity(session, db, cursor, includeRelated);
			Log.d("dbTime", " load "+ o + " : " + (System.currentTimeMillis()  - timeLoad));
			return o.getContent();
		}finally{

			cursor.close();
		}
	}

	/**
	 * Loads an instance of this entity with the content of the given cursor
	 * <p>
	 * This operation cascades to associated instances
	 *  
	 * @param s the session calling the load
	 * @param db the database
	 * @param c the cursor to read
	 * @param includeRelated indicates id the related content must be loaded or not
	 * @return the loaded and filled instance
	 */
	private TravelingEntity loadEntity(Session s, SQLiteDatabase db, Cursor c, boolean includeRelated){
		TravelingEntity result = loadEmptyEntity();
		table.load(c, result);
		if(includeRelated){
			Iterator<ElementCollection> itEc = elementCollections.iterator();
			while (itEc.hasNext()) {
				itEc.next().load(db, result);

			}
		}
		runCallBack(s, ALitePostLoad.class, result);
		return result;
	}

	/**
	 * Loads the results of a select with projections
	 * <p>
	 * This operation won't cascade to associated instances
	 *  
	 * @param criteria the criteria containing the projection 
	 * @param s the session calling the load
	 * @param db the database
	 * @param c the cursor to read
	 * @param includeRelated indicates id the related content must be loaded or not
	 * @return the loaded and filled instance
	 */
	private List<Object> loadProjection(Criteria criteria, Session s, SQLiteDatabase db, Cursor c, boolean includeRelated){
		if(criteria.hasAggregatedPojections()){
			// TODO FINISH THIS
			return null;
		}else{
			ArrayList<Object> resultList = new ArrayList<Object>();
			if (c.moveToFirst()) {
				do {
					if(criteria.getImplementationClass() == null){
						Iterator<ProjAttribute> it = criteria.getProjectedAttributes();
						ArrayList<Object> result = new ArrayList<Object>();
						while (it.hasNext()) {
							ProjAttribute projAttribute = (ProjAttribute) it.next();
							Attribute f = table.getAttribute(projAttribute.getAttributeName());
							result.add(DBColumnFactory.readCursor(f.getDBName(), c, f.getColumnType()));
						}
						resultList.add(result);
					}else{
						Iterator<ProjAttribute> it = criteria.getProjectedAttributes();
						Object result;
						try {
							result = criteria.getImplementationClass().getImplementationClass().newInstance();
							while (it.hasNext()) {
								ProjAttribute projAttribute = (ProjAttribute) it.next();
								Attribute f = table.getAttribute(projAttribute.getAttributeName());
								DBColumnFactory.setValue(f.getDBColumn(), c, result, true);
							}
							resultList.add(result);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} while (c.moveToNext());
			}
			c.close();
			return resultList;
		}
	}

	/**
	 * Loads the related content of given instance
	 * <p>
	 * This operation will only affect the relative content ( ElementCollection ) of the received instance.
	 * <p> 
	 * All the related content of the received instance will be overwritten by the one loaded from the database.  
	 * 
	 * <p>
	 * This method won't trigger callback listeners
	 *  
	 * @param s the session calling the load
	 * @param result the instance to load
	 * @param db the database
	 * @return the instance filled with its related content
	 */
	public TravelingEntity loadDeep(Session s, TravelingEntity result, SQLiteDatabase db){
		Iterator<ElementCollection> itEc = elementCollections.iterator();
		while (itEc.hasNext()) {
			itEc.next().load(db, result);
		}
		return result;
	}

	/**
	 * Initializes the entity instance and also all instances of its embedded attributes
	 * <p>
	 * This operation cascades to associated instances
	 * 
	 * @return the initialized entity instance
	 */
	private TravelingEntity loadEmptyEntity(){
		try {
			Object result = getImplementationClass().newInstance();
			Iterator<Embeddable> itE = embeddeds.iterator();
			while (itE.hasNext()) {
				itE.next().loadEmptyEntity(result);
			}

			if(sClass != null){
				sClass.loadEmptyEntity(result);
			}
			return new TravelingEntity(result);
		} catch (InstantiationException e){
			throw new RuntimeException("Cannot instanciate " + getImplementationClass().getName(), e);
		} catch(IllegalAccessException e) {
			throw new RuntimeException("Cannot instanciate " + getImplementationClass().getName(), e);
		}
	}

	/**
	 * Saves an instance of this entity into the database
	 * <p>
	 * This operation cascades to associated instances
	 * 
	 * @param session the session calling the save
	 * @param db the database
	 * @param o the instance to save
	 */
	public void save(Session session, SQLiteDatabase db, TravelingEntity o) {
		if(!id.isComplex()){
			IDBColumn idColumn = id.getColumns()[0];
			if(idColumn.isAutoIncrement()){
				String s = idColumn.getValue(idColumn.getAttribute().getTarget(o));
				if(s == null || s.trim().length() == 0)
					s = "0";
				int idV = Integer.parseInt(s);
				if(idV== 0){
					runCallBack(session, ALitePrePersist.class, o);
					insertAutoIncrement(db, o);
					saveRelated(db, o);
					runCallBack(session, ALitePostPersist.class, o);
				}else{
					if(exists(db, o)){
						runCallBack(session, ALitePreUpdate.class, o);
						updateAutoIncrement(db, o);
						saveRelated(db, o);
						runCallBack(session, ALitePostUpdate.class, o);
					}else{
						// TODO LAUNCH an exception here the persisted content has been deleted
					}
				}
			}else{
				saveNoAutoINcrement(session, db, o);
			}
		}else{
			saveNoAutoINcrement(session, db, o);
		}
	}

	/**
	 * Saves a non auto incremental instance of this entity
	 * 
	 * @param session the session calling the save
	 * @param db the database
	 * @param o the instance to save
	 */
	private void saveNoAutoINcrement(Session session, SQLiteDatabase db, TravelingEntity o){
		if(!exists(db, o)){
			runCallBack(session, ALitePrePersist.class, o);
			insertNonAutoIncrement(db, o);
			saveRelated(db, o);
			runCallBack(session, ALitePostPersist.class, o);
		}else{
			runCallBack(session, ALitePreUpdate.class, o);
			updateNonAutoIncrement(db, o);
			saveRelated(db, o);
			runCallBack(session, ALitePostUpdate.class, o);
		}
	}

	/**
	 * Saves all the content relative to the given instance
	 * 
	 * @param db the database
	 * @param o the instance to save
	 */
	private void saveRelated(SQLiteDatabase db, TravelingEntity o) {
		Iterator<ElementCollection> itColl = elementCollections.iterator();
		while (itColl.hasNext()) {
			ElementCollection coll = (ElementCollection) itColl.next();
			coll.save(db, o);
		}
	}

	/**
	 * Saves an auto incremental instance of this entity
	 * 
	 * @param db the database
	 * @param e the instance to save
	 */
	private void insertAutoIncrement(SQLiteDatabase db, TravelingEntity e){
		table.getInsertOrder().fill(e).execute(db);
		int lastId = SqlTools.getMaxInt(db, table.getTableName(), id.getColumns()[0].getAttribute().getDBName());
		if(lastId > -1){
			id.getColumns()[0].setValue(id.getColumns()[0].getAttribute().getTarget(e.getContent()), lastId, false);
		}
	}

	/**
	 * Updates a non auto incremental instance of this entity
	 * 
	 * @param db the database
	 * @param o the instance to update
	 */
	private void updateNonAutoIncrement(SQLiteDatabase db, TravelingEntity o){
		table.getUpdateOrder().fill(o).execute(db);
	}

	/**
	 * Inserts an on auto incremental instance of this entity
	 * 
	 * @param db the database
	 * @param e the instance to insert
	 */
	private void insertNonAutoIncrement(SQLiteDatabase db, TravelingEntity e){
		table.getInsertOrder().fill(e).execute(db);
	}

	/**
	 * Updates an auto incremental instance of this entity
	 * 
	 * @param db the database
	 * @param o the instance ton update
	 */
	private void updateAutoIncrement(SQLiteDatabase db, TravelingEntity o){
		table.getUpdateOrder().fill(o).execute(db);
	}

	/**
	 * Returns an order to select the entity corresponding to the given id
	 * 
	 * @param idValue the id of the desired instance
	 * @return the select order
	 */
	private SelectOrder buildSelectOrder(TravelingId idValue){
		StringBuilder s = new StringBuilder()
		.append(" WHERE ")
		.append(id.getWhereOn(idValue));

		SelectOrder order = new SelectOrder(this, s.toString());
		return order;
	}

	/**
	 * Indicates if an instance exists into the database
	 * 
	 * @param db the database
	 * @param o the instance to look for
	 * @return <code>true</code> if the instance/row exists, otherwise <code>false</code>
	 */
	private boolean exists(SQLiteDatabase db, TravelingEntity o){
		String sql = String.format(ISqlString.SELECT_ALL_SQL_PARAM_T_W, table.getTableName(), id.getWhereFor(o));
		if(ALiteOrmBuilder.getInstance().isShowSQL())
			Log.d(ILogPrefix.SQL_LOG, sql);

		Cursor cur = db.rawQuery(sql, new String[] {});
		return (cur != null && cur.moveToFirst());
	}

	/**
	 * Removes orphans for all content related to this entity
	 * 
	 * @param db the database
	 */
	public void removeOrphans(SQLiteDatabase db){
		Iterator<ElementCollection> itC2 = elementCollections.iterator();
		while (itC2.hasNext()) {
			itC2.next().removeOrphans(db);
		}
	}

	/**
	 * Adds a callback method to this entity
	 * 
	 * @param c the class identifying the life cycle event related to the added method
	 * @param m the callback method to add
	 */
	private void addCallBack(Class<?> c, Method m){
		internalCallbacks.put(c, m);
	}

	/**
	 * Runs the callback methods added to this entity
	 * 
	 * @param session the session calling the execution of the callback methods
	 * @param c the class identifying the life cycle event triggering the call
	 * @param o the entity target of the callback
	 */
	private void runCallBack(Session session, Class<?> c, TravelingEntity o){
		// We first invoke the internal callbacks
		if(internalCallbacks.containsKey(c)){
			try {
				Method m = internalCallbacks.get(c);
				m.setAccessible(true);
				m.invoke(o.getContent());
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException("Error calling the callback " + c + " on the content " + o, e);
			}
		}

		// Then we invoke the externals
		Iterator<EntityListener> it = externalsCallbacks.iterator();
		while (it.hasNext()) {
			EntityListener externalListener = (EntityListener) it.next();
			externalListener.runCallBack(c, o);
		}

		// Then we invoke the externals from the session
		if(!excludeSessionListener)
			session.runCallBack(c, o);

		// And finally we will invoke the global
		if(!excludeGlobalListener)
			ALiteOrmBuilder.getInstance().runCallBack(c, o);
	}

	/**
	 * Loads the external callback methods associated to this entity
	 * 
	 * @param ann the annotation defining the external callback listener to add
	 */
	private void loadExternalCallbacks(ALiteEntityListeners ann){
		Class<?>[] cls = ann.value();
		for (int i = 0; i < cls.length; i++) {
			final Class<?> c = cls[i];
			EntityListener l = new EntityListener(c);
			CallBackTools.loadCallbacks(c.getDeclaredMethods(), l);
			externalsCallbacks.add(l);
		}
	}

	/**
	 * Checks if the given column exists into the mapped table for the entity.
	 * <p>
	 * This method call is not optimized, it'd supposed to be used just for testing purpose...
	 *
	 * @param db the database
	 * @param columnName the column to look for
	 * @return <code>true</code> if the column exists, otherwise <code>false</code>
	 */
	public boolean columnExists(SQLiteDatabase db, String columnName) {
		return columnExists(db, table.getTableName(), columnName);
	}

	/**
	 * Checks if the given column exists into the mapped table for the entity.
	 * <p>
	 * This method call is not optimized, it'd supposed to be used just for testing purpose...
	 *
	 * @param db the database
	 * @param tableName the table  where to look for the column
	 * @param columnName columnName the column to look for
	 * @return <code>true</code> if the column exists, otherwise <code>false</code>
	 */
	public boolean columnExists(SQLiteDatabase db, String tableName, String columnName) {
		try{
			if(!cursors.containsKey(tableName)){
				String sql = "SELECT * FROM " + tableName + " LIMIT 0";
				if(ALiteOrmBuilder.getInstance().isShowSQL())
					Log.d(ILogPrefix.SQL_LOG, sql);
				cursors.put(tableName, db.rawQuery( sql, null ));
			}
			if(cursors.get(tableName).getColumnIndex(columnName) != -1)
				return true;
			else
				return false;
		}catch (Exception Exp){
			return false;
		}
	}

	@Override
	public boolean allowsElementCollection() {
		return true;
	}

	@Override
	public Object getTarget(Object o){
		return o;
	}

	/**
	 * Returns the id of the instance passed as parameter
	 * 
	 * @param o the entity
	 * @return the entity's id
	 */
	public TravelingId getTravelingId(Object o){
		return id.isComplex() ? new TravelingComplexId(o) : new TravelingSimpleId(o);
	}
}