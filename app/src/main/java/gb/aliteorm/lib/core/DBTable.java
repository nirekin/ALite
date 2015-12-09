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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import gb.aliteorm.lib.annotation.ALiteDBVersion;
import gb.aliteorm.lib.annotation.ALiteEntity;
import gb.aliteorm.lib.exception.RDuplicateColumnNameException;
import gb.aliteorm.lib.impl.columns.DBColumnFactory;
import gb.aliteorm.lib.impl.columns.IDBColumn;
import gb.aliteorm.lib.tools.ALiteOrmBuilder;
import gb.aliteorm.lib.tools.ILogPrefix;
import gb.aliteorm.lib.tools.StringTools;

/**
 * Implementation of a table holding all the attributes mapped for an entity
 *
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class DBTable {

	private ArrayList<String> names;
	private ArrayList<Attribute> attributes, updatableAttributes, insertableAttributes;

	private ALiteDBVersion requiredVersion = null;
	private String tableName;

	private boolean allowingPKConstraint = false;
	private Hashtable<String, ArrayList<DBUpdate>> updates;
	private EntityId id;
	private ElementCollection ec = null;
	private String allAttributes = null;

	/**
	 * Creates a new table
	 */
	private DBTable(){
		attributes = new ArrayList<Attribute>();
		updates = new Hashtable<String, ArrayList<DBUpdate>>();
		names = new ArrayList<String>();
	}

	/**
	 * Creates a new table to hold all the attributes of the given element collection
	 * @param ec the element collection
	 */
	protected DBTable(ElementCollection ec){
		this();
		this.ec = ec;
		tableName = ec.getTableName();

		ALiteDBVersion v = ec.getVersion();
		if(v != null){
			add(new DBUpdateFullElementCollection(this, ec));
		}
		updateRequiredVersion(ec);
	}

	/**
	 * Creates a new table to hold all the attributes of the given entity
	 * @param e the entity
	 */
	protected DBTable(Entity e){
		this();
		allowingPKConstraint = true;
		ALiteEntity ann = e.getEntityAnnotation();
		if(ann.name() != null && ann.name().trim().length() > 0){
			tableName = ann.name();
		}else{
			tableName = e.getImplementationClass().getSimpleName();
		}

		ALiteDBVersion v = e.getVersion();
		if(v != null){
			add(new DBUpdateFullEntity(this, e));
		}
		updateRequiredVersion(e);
	}

	/**
	 * Returns the name of database table where the attributes will be mapped
	 * @return the name of database table
	 */
	public String getTableName(){
		return tableName;
	}

	/**
	 * Adds an attribute into this table
	 * @param a the attribute to add
	 */
	protected void add(Attribute a){
		if(names.contains(a.getModelName()))
			throw new RDuplicateColumnNameException("" + a.getModelName());

		names.add(a.getModelName());
		attributes.add(a);

		ALiteDBVersion v = a.getVersion();
		if(v != null){
			add(new DBUpdateAttribute(this, a, allowingPKConstraint));
		}
		updateRequiredVersion(a);
	}

	/**
	 * Adds a schema update to this table
	 * @param u the  update to add
	 */
	private void add(DBUpdate u){
		if(!updates.containsKey(u.getVersion().versionNumber() + "")){
			updates.put("" + u.getVersion().versionNumber(), new ArrayList<DBUpdate>());
		}
		ArrayList<DBUpdate> up = updates.get("" + u.getVersion().versionNumber());
		Iterator<DBUpdate> it = up.iterator();
		boolean containsFullUpdate = false;
		while (it.hasNext()) {
			DBUpdate dbUpdate = (DBUpdate) it.next();
			if(dbUpdate instanceof DBUpdateFullElementCollection || dbUpdate instanceof DBUpdateFullEntity){
				containsFullUpdate = true;
				break;
			}
		}
		if(!containsFullUpdate){
			updates.get(u.getVersion().versionNumber() + "").add(u);
			if(ALiteOrmBuilder.getInstance().isShowLog())
				Log.d(ILogPrefix.ACTIVITY_LOG, "update order added : " + u);
		}
	}

	/**
	 * Creates the table into the initial database
	 * @param db the database
	 */
	protected void buildInitialDataBase(SQLiteDatabase db){
		getCreatedOrder(-1).execute(db);
	}

	/**
	 * Creates the table into the initial database
	 * @param db the database
	 * @param sqls the additional SQL sentences to execute while create the attributes into the database
	 */
	protected void buildInitialDataBase(SQLiteDatabase db, String... sqls){
		CreateTableOrder co = getCreatedOrder(-1);
		for (int i = 0; i < sqls.length; i++) {
			co.addColumnDefinition(sqls[i]);
		}
		co.execute(db);
	}

	/**
	 * Creates the table into the database for the given schema version
	 * @param db the database
	 * @param version the schema version ( -1 corresponds to the initial database version )
	 */
	protected void buildDataBase(SQLiteDatabase db, ALiteDBVersion version){
		getCreatedOrder(version.versionNumber()).execute(db);
	}

	/**
	 * Updates the table into the database for the given schema version
	 * @param db the database
	 * @param version the desired schema version ( -1 corresponds to the initial database version )
	 */
	protected void updateDataBase(SQLiteDatabase  db, int version){
		if(updates.containsKey("" + version)){
			if(ALiteOrmBuilder.getInstance().isShowLog())
				Log.d(ILogPrefix.ACTIVITY_LOG, "update required for  : " + tableName + " for version :" + version);
			ArrayList<DBUpdate> u = updates.get("" + version);
			if(u.isEmpty()){
				if(ALiteOrmBuilder.getInstance().isShowLog())
					Log.d(ILogPrefix.ACTIVITY_LOG, "no update required for  : " + tableName + " for version :" + version);
			}else{
				Iterator<DBUpdate> it = u.iterator();
				while (it.hasNext()) {
					DBUpdate dbUpdate = (DBUpdate) it.next();
					if(ALiteOrmBuilder.getInstance().isShowLog())
						Log.d(ILogPrefix.ACTIVITY_LOG, "will run : " + dbUpdate);
					dbUpdate.processUpdate(db);
				}
			}
		}else{
			if(ALiteOrmBuilder.getInstance().isShowLog())
				Log.d(ILogPrefix.ACTIVITY_LOG, "no update required for  : " + tableName + " for version :" + version);
		}
	}

	/**
	 * Returns the order to create the table into the database for the given schema version
	 * @param version the desired schema version ( -1 corresponds to the initial database version )
	 * @return the creation order
	 */
	private CreateTableOrder getCreatedOrder(int version){
		CreateTableOrder co = new CreateTableOrder(this);
		Iterator<Attribute> itF = attributes.iterator();
		while (itF.hasNext()) {
			Attribute f = itF.next();
			if(version > -1){
				if(f.getVersion() != null && f.getVersion().versionNumber() != version)
					continue;
			}
			co.addColumnDefinition(f.getDBColumn().getSqlForCreation(allowingPKConstraint));
		}
		if(ec == null && id.isComplex()){
			// TODO 1 MAKE IT NICER
			StringBuilder str = new StringBuilder()
			.append(" PRIMARY KEY (");
			IDBColumn[] cs = id.getColumns();
			for (int i = 0; i < cs.length; i++) {
				str.append(cs[i].getAttribute().getDBName())
				.append(",");
			}
			String sql = StringTools.removeLastChar(str.toString());
			co.addColumnDefinition(sql + ")");
		}
		return co;
	}

	/**
	 * Returns all database columns corresponding to the attributes that are id(s) of the table
	 * @return the id(s) columns
	 */
	protected ArrayList<IDBColumn> getAllIds(){
		ArrayList<IDBColumn> ids = new ArrayList<IDBColumn>();

		Iterator<Attribute> itF = attributes.iterator();
		while (itF.hasNext()) {
			Attribute f = (Attribute) itF.next();
			if(f.isId()){
				final IDBColumn c = f.getDBColumn();
				ids.add(c);
			}
		}
		return ids;
	}

	/**
	 * Returns the upper database version found for all the attributes
	 * @return the upper database version
	 */
	public ALiteDBVersion getRequiredVersion(){
		return requiredVersion;
	}

	/**
	 * Updates, if necessary, the upper database version based of the version of the given element
	 * @param mb the element containing the eventually new upper version
	 */
	protected void updateRequiredVersion(VersionableElement mb){
		ALiteDBVersion mbV = mb.getVersion();
		if(mbV != null){
			if(requiredVersion == null){
				requiredVersion = mbV;
			}else{
				if(mbV.versionNumber() > requiredVersion.versionNumber()){
					requiredVersion = mbV;
				}
			}
		}
	}

	/**
	 * Updates, if necessary, the upper database version based of the version of another table of attributes
	 * @param t the table containing the eventually new upper version
	 */
	protected void updateRequiredVersion(DBTable t){
		ALiteDBVersion mbV = t.getRequiredVersion();
		if(mbV != null){
			if(requiredVersion == null){
				requiredVersion = mbV;
			}else{
				if(mbV.versionNumber() > requiredVersion.versionNumber()){
					requiredVersion = mbV;
				}
			}
		}
	}

	/**
	 * Returns the database column corresponding to the given property name
	 * @param propertyName the property name
	 * @return the database column
	 */
	public IDBColumn getColumn(String propertyName){
		Iterator<Attribute> itF = attributes.iterator();
		while (itF.hasNext()) {
			Attribute f = (Attribute) itF.next();
			if(f.getDBColumn().getAttribute().getModelName().equalsIgnoreCase(propertyName)){
				return f.getDBColumn();
			}
		}
		return null;
	}

	/**
	 * Returns an iterator on all the held attributes
	 * @return the iterator
	 */
	protected Iterator<Attribute> getAttributes(){
		return attributes.iterator();
	}

	/**
	 * Returns a string containing all the attributes of this table 
	 * following this pattern :"table.field1,table.field2,table.field3"
	 *  
	 * @return the string
	 */
	public String getAllAttributes(){
		if(allAttributes == null){
			StringBuilder strb = new StringBuilder();
			for(int i = 0; i < attributes.size(); i++){
				strb.append(getTableName())
				.append(".")
				.append(attributes.get(i).getDBName());
				if( i + 1 < attributes.size())
					strb.append(",");
			}
			allAttributes = strb.toString();
		}
		return allAttributes;
	}
	
	/**
	 * Returns the simple or complex id of this table
	 * @return the id of this table
	 */
	public EntityId getId() {
		return id;
	}

	/**
	 * Sets the simple or complex id of this table
	 * @param id the id of this table
	 */
	protected void setId(EntityId id) {
		this.id = id;
		preloadAttributes();
	}

	/**
	 * Preloads all the mapped attributes to prepare the content of the update
	 * and insert orders used to insert or update entities into the mapped table
	 */
	protected void preloadAttributes(){
		updatableAttributes = new ArrayList<Attribute>() ;
		insertableAttributes =  new ArrayList<Attribute> ();

		Iterator<Attribute> it = getAttributes();
		while (it.hasNext()) {
			Attribute f = (Attribute) it.next();
			if( f.isAutoIncrement()){
				continue;
			}
			if(f.getDBColumn().isUpdatable() && id.isAnId(f) == false)
				updatableAttributes.add(f);
			if(f.getDBColumn().isInsertable())
				insertableAttributes.add(f);
		}
	}

	/**
	 * Fills an entity instance with all the attributes held into the table for the given cursor
	 * @param c the cursor to read
	 * @param result the entity instance to fill
	 */
	protected void load(Cursor c, TravelingEntity result){
		load(c, result.getContent());
	}

	/**
	 * Fills an element collection instance with all the attributes held into the table for the given cursor
	 * @param c the cursor to read
	 * @param result the element collection instance to fill
	 */
	protected void load(Cursor c, TravelingElementCollection result){
		load(c, result.getContent());
	}

	/**
	 * Fills an object with all the attributes held into the table for the given cursor
	 * @param c the cursor to read
	 * @param result the object to fill
	 */
	private void load(Cursor c, Object result){
		Iterator<Attribute> itF = getAttributes();
		IDBColumn column;
		while (itF.hasNext()) {
			Attribute f = (Attribute) itF.next();
			column = f.getDBColumn();
			DBColumnFactory.setValue(column, c, f.getTarget(result));
		}
	}

	/**
	 * Returns an order to insert an entity into the mapped table
	 * @return the insertion order
	 */
	protected IInsertOrder getInsertOrder(){
		return new IO();
	}

	/**
	 * Returns an order to update an entity into the mapped table
	 * @return the update order
	 */
	protected IUpdateOrder getUpdateOrder(){
		return new UO();
	}

	/**
	 * Returns an order to delete an entity into the mapped table
	 * @return the delete order
	 */
	protected IDeleteOrder getDeleteOrder(){
		return new DO();
	}

	private abstract class BaseOrder{

		protected abstract String getSql();

		/**
		 * Executes the order on the given database
		 * @param db the database
		 */
		public void execute(SQLiteDatabase db){
			if(ALiteOrmBuilder.getInstance().isShowSQL())
				Log.d(ILogPrefix.SQL_LOG, getSql());
			db.execSQL(getSql());
		}
	}

	private class DO extends BaseOrder implements IDeleteOrder{
		private String t;

		private String w;

		public DO(){
			this.t = tableName;
		}

		@Override
		public IDeleteOrder fill(TravelingEntity o) {
			StringBuilder s = new StringBuilder()
			.append(" WHERE ")
			.append(id.getWhereFor(o));
			this.w = s.toString();
			return this;
		}

		@Override
		protected String getSql() {
			return new StringBuilder("DELETE FROM ")
			.append(t)
			.append(w)
			.toString();
		}
	}

	private class UO extends BaseOrder implements IUpdateOrder{
		private String t;
		private ArrayList<String> v;
		private String w;

		public UO(){
			this.t = tableName;
			v = new ArrayList<String>();
		}

		@Override
		public void addColumnUpdateSql(String val) {
			v.add(val);
		}

		@Override
		public void addColumnUpdate(String c, String v) {
			addColumnUpdateSql(c + "=" + v);
		}

		@Override
		protected String getSql() {
			StringBuilder strb = new StringBuilder("UPDATE " + t + " SET ");
			Iterator<String> cName = v.iterator();
			while (cName.hasNext()) {
				strb.append(cName.next())
				.append(",");
			}

			return StringTools.removeLastChar(strb.toString()) + w;
		}

		@Override
		public IUpdateOrder fill(TravelingEntity o) {
			StringBuilder s = new StringBuilder()
			.append(" WHERE ")
			.append(id.getWhereFor(o));
			this.w = s.toString();

			Iterator<Attribute> itF = updatableAttributes.iterator();
			while (itF.hasNext()) {
				Attribute f = (Attribute) itF.next();
				f.getDBColumn().insertColumn(this, f.getTarget(o.getContent()));
			}
			return this;
		}
	}

	private class IO extends BaseOrder implements IInsertOrder{
		private String t;
		private ArrayList<String> c;
		private ArrayList<String> v;

		public IO(){
			this.t = tableName;
			c = new ArrayList<String>();
			v = new ArrayList<String>();
		}

		@Override
		public IInsertOrder fill(TravelingElementCollection o){
			fill(o.getContent());
			return this;
		}

		@Override
		public IInsertOrder fill(TravelingEntity o) {
			fill(o.getContent());
			return this;
		}

		private void fill(Object o) {
			Iterator<Attribute> itF = insertableAttributes.iterator();
			while (itF.hasNext()) {
				Attribute f = (Attribute) itF.next();
				f.getDBColumn().insertColumn(this, f.getTarget(o));
			}
		}

		@Override
		protected String getSql(){
			StringBuilder strb = new StringBuilder("INSERT INTO " + t + " (");
			Iterator<String> cName = c.iterator();
			while (cName.hasNext()) {
				strb.append(cName.next())
				.append(",");
			}

			strb = new StringBuilder(StringTools.removeLastChar(strb.toString()) + " )")
			.append(" VALUES (");

			Iterator<String> cValue = v.iterator();
			while (cValue.hasNext()) {
				strb.append(cValue.next())
				.append(",");
			}
			return StringTools.removeLastChar(strb.toString()) + " )";
		}

		public void addColumn(String columnName, String value){
			c.add(columnName);
			v.add(value);
		}
	}

	// TODO DELETE JUST FOR TESTING PURPOSE
	public Attribute getAttribute(String property){
		Iterator<Attribute> itF = attributes.iterator();
		while (itF.hasNext()) {
			Attribute f = itF.next();
			if(f.getDBColumn().getAttribute().getModelName().equalsIgnoreCase(property))
				return f;
		}
		return null;
	}
}