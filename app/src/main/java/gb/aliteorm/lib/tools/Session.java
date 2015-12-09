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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import gb.aliteorm.lib.core.Entity;
import gb.aliteorm.lib.core.EntityListener;
import gb.aliteorm.lib.core.TravelingEntity;
import gb.aliteorm.lib.exception.BulkProcessException;
import gb.aliteorm.lib.exception.RNoEntityException;
import gb.aliteorm.lib.exception.RNoResultException;
import gb.aliteorm.lib.exception.RNonUniqueResultException;
import gb.aliteorm.lib.query.impl.Criteria;

/**
 *
 * The session it's basically used to expose methods to manipulate entities
 * and to support transactions.
 *
 *
 * @author Guillaume Barré
 * @since 1.0
 * 
 */
public class Session {

	private Transaction tr = null;
	private SQLiteDatabase  db;
	private ArrayList<EntityListener> externalsCallbacks;

	/**
	 * Creates a new session.
	 * <P>
	 * A freshly open database ( see getWritableDatabase() ) will be associated with each session.
	 */
	public Session(){
		db = ALiteOrmBuilder.getInstance().openWritableDatabase();
		externalsCallbacks = new ArrayList<EntityListener>();
	}

	/**
	 * Starts a new transaction on the open database and associated it to this session.
	 *
	 * @return the new transaction
	 */
	public Transaction startTransaction(){
		tr = new Transaction(db);
		return tr;
	}

	/**
	 * Closes the transaction associated to this session.
	 *<p>
	 * All uncommitted changes will be lost.
	 */
	public void closeTransaction(){
		tr.rollback();
		tr = null;
	}

	/**
	 * Returns the freshly open database associated with this session.
	 *
	 * @return the database associated with this session
	 */
	public SQLiteDatabase getDatabase(){
		return db;
	}

	/**
	 * Returns the started transaction associated with this session.
	 *
	 * @return the started transaction associated with this session
	 * @throws RuntimeException will be thrown if the transaction has not been started
	 */
	public Transaction getTransaction() throws RuntimeException{
		if(tr == null)
			throw new RuntimeException("The transaction has not been started");
		return tr;
	}

	/**
	 * Closes the session.
	 * <P>
	 * Closing the session will also close the open database associated with the session and all uncommitted changes will be lost.
	 */
	public void close(){
		if(tr != null && tr.inTransaction()){
			tr.rollback();
		}
		try{
			if(db != null)
				db.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Saves multiples entities into the database.
	 * <P>
	 * If one or more entities cannot be saved the process will continue to try to save the others.
	 * 
	 * <p>
	 * This operation cascades to associated instances
	 * 
	 * @param entities the entities to save
	 * @throws BulkProcessException exceptions encountered while saving one or more entities
	 */
	public void save(Object... entities) throws BulkProcessException{
		Hashtable<Object, Throwable> e = new Hashtable<Object, Throwable>();
		for(Object o : entities){
			try{
				save(o);
			}catch(Throwable t){
				e.put(o,  t);
			}
		}
		if(!e.isEmpty()){
			throw new BulkProcessException(e);
		}
	}

	/**
	 * Saves a list of entities into the database.
	 * <P>
	 * If one or more entities cannot be saved the process will continue to try to save the others.
	 * 
	 * <p>
	 * This operation cascades to associated instances
	 * 
	 * @param entities the entities to save
	 * @throws BulkProcessException exceptions encountered while saving one or more entities
	 */
	public void save(List<Object> entities) throws BulkProcessException{
		Hashtable<Object, Throwable> e = new Hashtable<Object, Throwable>();
		Iterator<Object> it = entities.iterator();
		while (it.hasNext()) {
			Object o = (Object) it.next();
			try{
				save(o);
			}catch(Throwable t){
				e.put(o,  t);
			}
		}
		if(!e.isEmpty()){
			throw new BulkProcessException(e);
		}
	}

	/**
	 * Saves a single entity.
	 * 
	 * <p>
	 * This operation cascades to associated instances
	 * 
	 * @param e the entity to save
	 * @throws RNoEntityException will be thrown if the received object is not an entity
	 */
	public void save(Object e) throws RNoEntityException{
		Entity en = ALiteOrmBuilder.getInstance().getEntity(e.getClass());
		if(en == null)
			throw new RNoEntityException("For : " + e.getClass().getName());
		en.save(this, db, new TravelingEntity(e));
	}

	/**
	 * Remove a persistent instances from the database
	 * <p>
	 * This operation cascades to associated instances
	 * <p>
	 * If one or more instances cannot be removed the process will continue to try to delete the others.
	 *
	 * @param objs the instances to remove
	 * @throws BulkProcessException exceptions encountered while removing one or more instances
	 */
	public void delete(Object... objs) throws BulkProcessException{
		Hashtable<Object, Throwable> e = new Hashtable<Object, Throwable>();
		for(Object o : objs){
			try{
				delete(o);
			}catch(Throwable t){
				e.put(o,  t);
			}
		}
		if(!e.isEmpty()){
			throw new BulkProcessException(e);
		}
	}

	/**
	 * Remove a persistent instances from the database
	 * <p>
	 * This operation cascades to associated instances
	 * <p>
	 * If one or more instances cannot be removed the process will continue to try to delete the others.
	 *
	 * @param objs the instances to remove
	 * @throws BulkProcessException exceptions encountered while removing one or more instances
	 */
	public void delete(List<Object> objs) throws BulkProcessException{
		Hashtable<Object, Throwable> e = new Hashtable<Object, Throwable>();
		Iterator<Object> it = objs.iterator();
		while (it.hasNext()) {
			Object o = (Object) it.next();
			try{
				delete(o);
			}catch(Throwable t){
				e.put(o,  t);
			}
		}
		if(!e.isEmpty()){
			throw new BulkProcessException(e);
		}
	}

	/**
	 * Remove a persistent instances from the database
	 * <p>
	 * This operation cascades to associated instances
	 * <p>
	 *
	 * @param o the instance to remove
	 * @throws RNoEntityException will be thrown if the received object is not a valid instance
	 */
	public void delete(Object o) throws RNoEntityException{
		Entity e = ALiteOrmBuilder.getInstance().getEntity(o.getClass());
		if(e == null)
			throw new RNoEntityException("For : " + o.getClass().getName());
		e.delete(this, db, new TravelingEntity(o));
	}

	/**
	 * Remove all instance from the database
	 * 
	 * <p>
	 * This operation cascades to associated instances
	 * 
	 *
	 * @param c the class identifying the table where to delete the rows
	 * @throws RNoEntityException will be thrown if the received class is not a valid class
	 */
	public void deleteAll( Class<?> c) throws RNoEntityException{
		Entity e = ALiteOrmBuilder.getInstance().getEntity(c);
		if(e == null)
			throw new RNoEntityException("For : " + c.getName());
		e .deleteAll(db);
	}

	/**
	 * Counts all instances into the table mapped with the received class.
	 *
	 * @param c the class identifying the table to counts the rows
	 * @return the number of rows
	 * @throws RNoEntityException will be thrown if the received object is not an entity
	 */
	public int countAll(Class<?> c) throws RNoEntityException{
		Entity e = ALiteOrmBuilder.getInstance().getEntity(c);
		if(e == null)
			throw new RNoEntityException("For : " + c.getName());
		return e.countAll(db);
	}

	/**
	 * Loads a single entity
	 * 
	 * <p>
	 * This operation cascades to associated instances
	 * 
	 * @param clazz the class identifying the entity to load
	 * @param id the id of the desired entity
	 * @return the load entity
	 * @throws RNoEntityException will be thrown if the received object is not an entity
	 * @throws RNonUniqueResultException will be thrown if more than one result exist for the given Id
	 * @throws RNoResultException will be thrown if there is no entity corresponding to the given Id
	 */
	public Object load(Class<?> clazz, Object id) throws RNoEntityException, RNonUniqueResultException, RNoResultException{
		Entity e = ALiteOrmBuilder.getInstance().getEntity(clazz);
		if(e == null)
			throw new RNoEntityException("For : " + clazz);
		return e.load(this, db, e.getTravelingId(id), true);
	}

	/**
	 * Loads the related content of a given entity
	 * 
	 * <p>
	 * This operation will only affect the relative content ( ElementCollection ) of the received instance.
	 * <p> 
	 * All the related content of the received instance will be overwritten by the one loaded from the database.  
	 * 
	 * <p>
	 * This method won't trigger callback listeners
     *
	 * @param instance the applicative instance to fill with its relative content
	 * @return the filled instance
	 * 
	 * @throws RNoEntityException will be thrown if the received object is not an entity
	 * @throws RNonUniqueResultException will be thrown if more than one result exist for the given Id
	 * @throws RNoResultException will be thrown if there is no entity corresponding to the given Id
	 */
	public Object loadDeep(Object instance) throws RNoEntityException, RNonUniqueResultException, RNoResultException{
		Entity e = ALiteOrmBuilder.getInstance().getEntity(instance.getClass());
		if(e == null)
			throw new RNoEntityException("For : " + instance.getClass());
		return e.loadDeep(this, new TravelingEntity(instance), db);
	}
	
	/**
	 * Loads a single entity
	 * 
	 * <p>
	 * This operation won't cascade to associated instances
	 * 
	 * @param clazz the class identifying the entity to load
	 * @param id the id of the desired entity
	 * @return the load entity
	 * @throws RNoEntityException will be thrown if the received object is not an entity
	 * @throws RNonUniqueResultException will be thrown if more than one result exist for the given Id
	 * @throws RNoResultException will be thrown if there is no entity corresponding to the given Id
	 */
	public Object loadShallow(Class<?> clazz, Object id) throws RNoEntityException, RNonUniqueResultException, RNoResultException{
		Entity e = ALiteOrmBuilder.getInstance().getEntity(clazz);
		if(e == null)
			throw new RNoEntityException("For : " + clazz);
		return e.load(this, db, e.getTravelingId(id), false);
	}
	
	/**
	 * Creates a criteria to make a request on the table mapped with the received class
	 *
	 * @param clazz the class the class identifying the entity to query
	 * @return the new created criteria
	 */
	public Criteria createCriteria(Class<?> clazz){
		return new Criteria(this, db, clazz);
	}

	/**
	 * Adds a new entity listener to the session
	 * @param c the entity listener to add
	 * @return the session
	 */
	public Session addSessionEntityListener(Class<?> c) {
		EntityListener l = new EntityListener(c);
		CallBackTools.loadCallbacks(c.getDeclaredMethods(), l);
		externalsCallbacks.add(l);
		return this;
	}

	/**
	 * Removes an entity listener from the session
	 * @param c the entity listener to remove
	 * @return the session
	 */
	public Session removeSessionEntityListener(Class<?> c) {
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
	 * @param o the target of the callback
	 */
	public void runCallBack(Class<?> c, TravelingEntity o){
		Iterator<EntityListener> it = externalsCallbacks.iterator();
		while (it.hasNext()) {
			EntityListener externalListener = (EntityListener) it.next();
			externalListener.runCallBack(c, o);
		}
	}

	// TODO DELETE JUST FOR TESTING PURPOSE
	public Object getTarget(Class<?> clazz, String property, Object o){
		Entity e = ALiteOrmBuilder.getInstance().getEntity(clazz);
		if(e == null)
			throw new RNoEntityException("For : " + clazz);
		return e.getTable().getAttribute(property).getTarget(o);

	}
}