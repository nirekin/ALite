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
package gb.aliteorm.lib.core;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import gb.aliteorm.lib.annotation.ALiteElementCollection;
import gb.aliteorm.lib.annotation.ALiteEmbeddable;
import gb.aliteorm.lib.annotation.ALiteStringLength;
import gb.aliteorm.lib.exception.RMappingException;
import gb.aliteorm.lib.exception.RWrongCollectionContentTypeException;
import gb.aliteorm.lib.exception.RWrongElementCollectionTypeException;
import gb.aliteorm.lib.impl.columns.DBColumnFactory;
import gb.aliteorm.lib.impl.columns.IDBColumn;
import gb.aliteorm.lib.query.impl.SqlTools;
import gb.aliteorm.lib.tools.ALiteOrmBuilder;
import gb.aliteorm.lib.tools.ILogPrefix;
import gb.aliteorm.lib.tools.ReflectionTools;
// TODO JAVADOC
/**
 *
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class ElementCollection  extends AttributesContainer{

	private EntityId id;
	private DBTable table;
	private Entity entity;
	private ALiteElementCollection mainAnn;
	private Method collectionGetter, collectionSetter;
	private Class<?> containerClass;
	private String tName;
	private boolean isBaseTypedCollection;
	private Field collectionField;

	/**
	 * Creates a new collection mapping
	 * @param entity the entity referencing the collection to map
	 * @param m the getter to access the collection
	 * @param s the setter to set the collection
	 */
	public ElementCollection(Entity entity, Method m, Method s){
		super();
		this.entity = entity;
		id = entity.getId();
		this.collectionGetter = m;
		this.collectionSetter = s;

		this.collectionGetter.setAccessible(true);
		this.collectionSetter.setAccessible(true);

		if(entity.getVersion() != null){
			setVersion(entity);
		}else{
			loadVersion(m);
		}

		collectionField = loadCollectionField();
		setImplementationClass(getCollectionContentClass());
		checkAnnotation();
		tName = loadTableName();
		table = new DBTable(this);
		table.setId(id);
		containerClass = loadContainerClass();
		checkContentType();
		checkCollectionType();
		if(!isBaseTypedCollection){
			Annotation ann = getImplementationClass().getAnnotation(ALiteEmbeddable.class);
			if(ann == null){
				throw new RWrongElementCollectionTypeException("for " + getImplementationClass().getName() + " is not @ALiteEmbeddable");
			}
			loadMappedSupper(table);
			loadAttributes(table);
			loadEmbeddeds(table);
			table.preloadAttributes();
		}
	}

	/**
	 * Checks how the content of the collection is implemented
	 */
	private void checkContentType(){
		isBaseTypedCollection = DBColumnFactory.isBaseType(getImplementationClass());
		if(!isBaseTypedCollection){
			if(getImplementationClass().isInterface()){
				if(mainAnn != null && (mainAnn.contentClass() == null || mainAnn.contentClass().trim().length() == 0)){
					throw new RWrongCollectionContentTypeException("the type of the collection \"" + collectionField.getName() + "\" of \"" + entity.getTable().getTableName() +"\" is an Interface and the implementation is not definde using @ALiteElementCollection(contentClass=\"xxx\"");
				}else if(mainAnn != null){
					String contentClassStr = mainAnn.contentClass();
					try{
						setImplementationClass(Class.forName(contentClassStr));
					}catch(Exception e){
						throw new RWrongCollectionContentTypeException("the type of the collection content \"" + contentClassStr + "\" cannot be found");
					}
				}
			}
		}
	}

	private void checkCollectionType(){
		// TODO 1 IMPLEMENT THIS
	}

	/**
	 * Loads the class used to implement the collection
	 * @return the class used to implement the collection
	 */
	private Class<?> loadContainerClass() {
		if(collectionField.getType().isInterface()){
			if(mainAnn != null && mainAnn.collectionClass() != null && mainAnn.collectionClass().trim().length() > 0){
				// The user specified the desired collection class
				try{
					return Class.forName(mainAnn.collectionClass());
				}catch(ClassNotFoundException cnfe){
					throw new RWrongCollectionContentTypeException("the type of the collection \"" + mainAnn.collectionClass() + "\" cannot be found");
				}
			}else{
				if("java.util.List".equalsIgnoreCase(collectionField.getType().getName())){
					return ArrayList.class;
				}else if("java.util.Set".equalsIgnoreCase(collectionField.getType().getName())){
					return HashSet.class;
				}
			}
		}else{
			return collectionField.getType();
		}
		return null;
	}

	/**
	 * Loads the database table name where to load the collection content
	 * @return the mapped database table name
	 */
	private String loadTableName(){
		if(mainAnn != null){
			if(mainAnn.tableName() != null && mainAnn.tableName().trim().length() > 0)
				return mainAnn.tableName();
		}
		return entity.getTable().getTableName() + "_" + collectionField.getName();
	}

	/**
	 * Builds the database table into the initial database
	 * @param db the database
	 */
	public void buildInitialDataBase(SQLiteDatabase db){
		if(ALiteOrmBuilder.getInstance().isShowLog())
			Log.d(ILogPrefix.ACTIVITY_LOG, "creating the table for the entity : " + getImplementationClass().getName());
		if(isBaseTypedCollection){
			CreateTableOrder or = new CreateTableOrder(tName);
			Annotation ann = collectionGetter.getAnnotation(ALiteStringLength.class);
			or.addColumnDefinition(collectionField.getName() + " " + DBColumnFactory.getBaseTypeSql(getImplementationClass(), (ALiteStringLength)ann));
			IDBColumn[] idCOlumns = id.getColumns();
			for (int i = 0; i < idCOlumns.length; i++) {
				or.addColumnDefinition(idCOlumns[i].getJoinColumDefinition());
			}
			or.execute(db);
		}else{
			String[] columnDefinitions = new String[id.getColumns().length];
			IDBColumn[] idCOlumns = id.getColumns();
			for (int i = 0; i < idCOlumns.length; i++) {
				columnDefinitions[i] = idCOlumns[i].getJoinColumDefinition();
			}
			table.buildInitialDataBase(db, columnDefinitions);
		}
	}

	/**
	 * Updates the database table for a specific schema version
     * @param db the database
     * @param version The database version.
	 */
	public void updateDataBase(SQLiteDatabase db, int version){
		if(ALiteOrmBuilder.getInstance().isShowLog())
			Log.d(ILogPrefix.ACTIVITY_LOG, "updating the table for the entity : " + getImplementationClass().getName());
		table.updateDataBase(db, version);
	}

	/**
	 * Returns the name of the database table where the collection fields will be mapped
	 * @return the name of the database table where the collection fields will be mapped
	 */
	public String getTableName(){
		return tName;
	}

	/**
	 * Check the annotation on the getter user to access the collection
	 */
	private void checkAnnotation(){
		Annotation ann = collectionGetter.getAnnotation(ALiteElementCollection.class);
		if(ann != null){
			mainAnn = (ALiteElementCollection)ann;
		}
	}

	/**
	 * Loads the attribute referencing the collection
	 * @return the collection field
	 */
	private Field loadCollectionField() {
		return ReflectionTools.getField(collectionGetter);
	}

	/**
	 * Returns the implementation class of the collection content
	 * @return the implementation class of the collection content
	 */
	private Class<?> getCollectionContentClass(){
		ParameterizedType cType = (ParameterizedType)collectionField.getGenericType();
		return (Class<?>) cType.getActualTypeArguments()[0];
	}

	/**
	 * Returns the table holding all the fields of the collection
	 * @return the table holding all the fields of the collection
	 */
	public DBTable getTable(){
		return table;
	}

	/**
	 * Load the collection content into the given entity
	 * @param db  the database
	 * @param o the entity where to load the content
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void load(SQLiteDatabase db, TravelingEntity o){
		Cursor cursor = buildSelectOrder(o).execute(db);
		try{
			Collection container = (Collection<?>)containerClass.newInstance();
			if(container != null){
				while(cursor.moveToNext()){
					if(isBaseTypedCollection){
						container.add(DBColumnFactory.readCursor(collectionField.getName(), cursor, getImplementationClass()));
					}else{
						Object content = getImplementationClass().newInstance();
						Iterator<Embeddable> itE = embeddeds.iterator();
						while (itE.hasNext()) {
							itE.next().loadEmptyEntity(content);
						}

						if(sClass != null){
							sClass.loadEmptyEntity(content);
						}

						table.load(cursor, new TravelingElementCollection(content));
						container.add(content);
					}
				}
				collectionSetter.invoke(o.getContent(), container);
			}
		} catch (InstantiationException ie){
			throw new RMappingException("error loading the elementCollection :" + collectionField.getName() + " for the entity " + entity.getTable().getTableName(), ie);
		} catch(IllegalAccessException iae){
			throw new RMappingException("error loading the elementCollection :" + collectionField.getName() + " for the entity " + entity.getTable().getTableName(), iae);
		} catch (InvocationTargetException ite) {
			throw new RMappingException("error loading the elementCollection :" + collectionField.getName() + " for the entity " + entity.getTable().getTableName(), ite);
		}finally{
			cursor.close();
		}
	}

	/**
	 * Saves the collection content of the passed entity
	 * @param db the database
	 * @param o the entity containing the collection content to save
	 */
	public void save(SQLiteDatabase db, TravelingEntity o){
		delete(db, o);
		Object relationContent = getObject(o.getContent());
		if(relationContent instanceof Collection){
			Object[] content = ((Collection<?>)relationContent).toArray();
			for(int i = 0; i < content.length; i++){
				Object oC = content[i];
				// TODO CLEAN THIS
				IInsertOrder or = table.getInsertOrder();
				if(isBaseTypedCollection){
					or.addColumn(collectionField.getName(), DBColumnFactory.getDecorator(getImplementationClass()).decorate(oC));
					IDBColumn[] idCOlumns = id.getColumns();
					for (int j = 0; j < idCOlumns.length; j++) {
						or.addColumn(idCOlumns[j].getJoinColumName(), idCOlumns[j].getValue(idCOlumns[j].getAttribute().getTarget(o)));
					}
				}else{
					IDBColumn[] idCOlumns = id.getColumns();
					for (int j = 0; j < idCOlumns.length; j++) {
						or.addColumn(idCOlumns[j].getJoinColumName(), idCOlumns[j].getValue(idCOlumns[j].getAttribute().getTarget(o)));
					}
					or.fill(new TravelingElementCollection(oC));
				}
				or.execute(db);
			}
		}
	}

	/**
	 * Returns the collection content of the passed entity
	 * @param o the entity containing the content to return
	 * @return the collection content
	 */
	private Object getObject(Object o){
		try {
			return collectionGetter.invoke(o instanceof TravelingEntity ? ((TravelingEntity)o).getContent() : o);
		} catch (Exception e1) {
			throw new RMappingException("error invoking :" + collectionGetter.getName() + " for the field " +  collectionField.getName(), e1);
		}
	}

	/**
	 * Deletes the collection content for the given entity
	 * @param db the database
	 * @param o the entity referencing the collection content to delete
	 */
	private void delete(SQLiteDatabase db, TravelingEntity o){
		SqlTools.delete(db, tName,id.getJoinWhereFor(o));
		removeOrphans(db);
	}

	/**
	 * Removes all orphans which are not anymore referenced by an entity
	 * @param db the database
	 */
	protected void removeOrphans(SQLiteDatabase db){
		if(!id.isComplex()){
			IDBColumn idColumn = id.getColumns()[0];
			SqlTools.removeOrphans(db, tName, idColumn.getJoinColumName() , idColumn , entity);
		}else{
			// delete from Entity5_ages where not exists (select 1 from Entity5 where  Entity5_idBigDecimal = idBigDecimal AND  Entity5_idBigInteger = idBigInteger ... )
			StringBuffer strb = new StringBuffer(); // TODO MOVE THIS SQL
			strb.append("delete from ")
			.append(tName)
			.append(" where not exists (")
			.append("select 1 from ")
			.append(entity.getTable().getTableName())
			.append(" where ");

			IDBColumn[] idCOlumns = id.getColumns();
			for (int j = 0; j < idCOlumns.length; j++) {
				strb.append(" ")
				.append(idCOlumns[j].getJoinColumName())
				.append(" = ")
				.append(idCOlumns[j].getAttribute().getDBName());
				if(j + 1 < idCOlumns.length)
					strb.append(" AND ");
			}
			strb.append(" )");
			db.execSQL(strb.toString());
		}
	}

	/**
	 * Returns a select order to query the collection content for the given entity
	 * @param o the entity referencing the desired content
	 * @return the select order
	 */
	private SelectOrder buildSelectOrder(TravelingEntity o){
		SelectOrder order = new SelectOrder(tName, " WHERE " + id.getJoinWhereFor(o));

		return order;
	}

	@Override
	public Object getTarget(Object o){
		return o;
	}

	@Override
	public boolean allowsElementCollection() {
		return false;
	}
}
