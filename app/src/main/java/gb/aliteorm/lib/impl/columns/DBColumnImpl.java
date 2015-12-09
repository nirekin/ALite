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
package gb.aliteorm.lib.impl.columns;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Method;

import gb.aliteorm.lib.annotation.ALiteColumn;
import gb.aliteorm.lib.annotation.ALiteId;
import gb.aliteorm.lib.core.Attribute;
import gb.aliteorm.lib.core.IInsertOrder;
import gb.aliteorm.lib.core.IUpdateOrder;
import gb.aliteorm.lib.decorator.DummySqlDecorator;
import gb.aliteorm.lib.decorator.ISqlDecorator;
import gb.aliteorm.lib.exception.UnsupportedGetterException;
import gb.aliteorm.lib.tools.ALiteOrmBuilder;
import gb.aliteorm.lib.tools.ILogPrefix;

/**
 * Base implementation of all column types
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public abstract class DBColumnImpl implements IDBColumn{

	protected Class<?> clazz;
	protected Attribute a;
	private ALiteColumn c = null;
	private ALiteId i = null;
	protected String tableName;

	protected abstract void defineSQL(StringBuilder strb);
	protected abstract void readAttributed(StringBuilder strb, Object target);
	protected abstract void setAttribute(Object target, Object value, boolean isMock) throws Exception;

	/**
	 * Creates a new column
	 * @param tableName the name of the database table where to map the column
	 * @param a the attribute corresponding to this column
	 * @param getter the getter used to access this column content
	 * @throws UnsupportedGetterException will be thrown is the getter returns a Type which is no supported by ALiteOrm
	 */
	protected DBColumnImpl(String tableName, Attribute a, Method getter) throws UnsupportedGetterException{
		this.a = a;
		this.tableName = tableName;
		this.clazz = a.getContainer().getImplementationClass();
		c = (ALiteColumn)getter.getAnnotation(ALiteColumn.class);
		i = (ALiteId)getter.getAnnotation(ALiteId.class);
	}

	/**
	 * Reads from the given object the value to store into this column
	 * @param target the object to read
	 * @return the value corresponding to this column
	 */
	private String read(Object target){
		StringBuilder strb = new StringBuilder();
		readAttributed(strb, target);
		return strb.toString();
	}

	/**
	 * Indicates if this column is unique or not
	 * @return <code>true</code> if this column is unique, otherwise <code>false</code>
	 */
	public boolean isUnique(){
		if(c != null)
			return c.unique();
		return false;
	}

	/**
	 * Indicates if this column is nullable or not
	 * @return <code>true</code> if this column can be null, otherwise <code>false</code>
	 */
	public boolean isNullable(){
		if(c != null)
			return c.nullable();
		return true;
	}

	/**
	 * Generate the unique/not null part of the SQL sentence to create the column and add it to the given StringBuilder
	 * @param strb the StringBuilder where to add the SQL
	 */
	private void generateUniqueNotNull(StringBuilder strb) {
		if(!isId()){
			if(isNullable() == false && isUnique()){
				strb.append(" UNIQUE NOT NULL ");
			}else if(isNullable()  && isUnique()){
				strb.append(" UNIQUE NULL ");
			}else if(isNullable() == false  && isUnique() == false){
				strb.append(" NOT NULL ");
			}else{
				strb.append(" NULL ");
			}
		}
	}

	/**
	 * Generate the primary key part of the SQL sentence to create the column and add it to the given StringBuilder
	 * @param strb the StringBuilder where to add the SQL
	 */
	private void generatePK(StringBuilder strb) {
		if(!a.getTable().getId().isComplex() && isId()){
			strb.append(" PRIMARY KEY ");
		}
	}

	/**
	 * Generate the auto increment part of the SQL sentence to create the column and add it to the given StringBuilder
	 * @param strb the StringBuilder where to add the SQL
	 */
	private void generateAutoIncrement(StringBuilder strb) {
		if(!a.getTable().getId().isComplex() && isAutoIncrement()){
			strb.append(" AUTOINCREMENT ");
		}
	}

	/**
	 * Returns the default value for thos column
	 * @return the default value for thos column
	 */
	public String getDefaultValue(){
		if(c != null && c.defaultValue().trim().length() > 0)
			return c.defaultValue();
		return "";
	}

	/**
	 * Returns the name of the database table containing this column
	 * @return the name of the database table
	 */
	public String getTableName() {
		return tableName;
	}

	@Override
	public ISqlDecorator getSqlDecorator(){
		return new DummySqlDecorator(); // TODO 2 use singleton here
	}

	@Override
	public Attribute getAttribute(){
		return a;
	}

	@Override
	public String getJoinColumDefinition(){
		StringBuilder strb= new StringBuilder()
		.append(getJoinColumName())
		.append(" ")
		.append(getSqlForCreation(false));
		return strb.toString() + " NOT NULL ";
	}

	@Override
	public String getJoinColumName(){
		StringBuilder strb= new StringBuilder()
		.append(getTableName())
		.append("_")
		.append(a.getDBName());
		return strb.toString();
	}

	@Override
	public void insertColumn(IUpdateOrder order, Object target){
		if(target != null){
			StringBuilder strb = new StringBuilder()
			.append(a.getDBName())
			.append("=")
			.append(getValue(target));
			order.addColumnUpdateSql(strb.toString());
		}
	}

	@Override
	public boolean isId(){
		return i != null;
	}

	@Override
	public boolean isAutoIncrement(){
		return isId() && i.auto();
	}

	@Override
	public String getSqlForCreation(boolean allowingPKConstraint){
		StringBuilder strb = new StringBuilder();
		strb.append(a.getDBName())
		.append(" ");
		if(c != null && c.columnDefinition() != null && c.columnDefinition().trim().length() > 0){
			strb.append(c.columnDefinition())
			.append(" ");
			return strb.toString();
		}else{
			defineSQL(strb);
			generateUniqueNotNull(strb);
			if(allowingPKConstraint){
				generatePK(strb);
				generateAutoIncrement(strb);
			}
		}
		return strb.toString();
	}

	@Override
	public String getValue(Object target){
		return read(target);
	}

	@Override
	public void setValue(Object target, Object value, boolean isMock){
		try{
			setAttribute(target, value, isMock);
		}catch(Exception e){
			throw new RuntimeException(" Error setting the value : " + value 
					+ " for the attribute : " + getAttribute().getModelName() 
					+ " on the container : " + getAttribute().getContainer().getImplementationClass(), e);
		}
	}

	@Override
	public boolean isInsertable(){
		if(c != null)
			return c.insertable();
		return true;
	}

	@Override
	public boolean isUpdatable(){
		if(c != null)
			return c.updatable();
		return true;
	}

	@Override
	public void alterTableColumn(SQLiteDatabase db, boolean allowingPKConstraint){
		StringBuilder strb = new StringBuilder()
		.append("ALTER TABLE  ")
		.append("[" + getTableName() + "]")
		.append(" ADD COLUMN ")
		.append(getSqlForCreation(allowingPKConstraint));

		String sql = strb.toString();
		if(ALiteOrmBuilder.getInstance().isShowSQL())
			Log.d(ILogPrefix.SQL_LOG, sql);
		db.execSQL(sql);
	}


	@Override
	public void insertColumn(IInsertOrder i, Object o){
		if(o != null)
			i.addColumn(a.getDBName(), getValue(o));
	}

	/**
	 * Starts the generated equals sentence for this column
	 * @param isJoin indicates if this equals will be use with a join column or not
	 * @return the start of the equals sentence
	 */
	private StringBuilder initEqualsExp(boolean isJoin){
		return new StringBuilder()
		.append(" ")
		.append(getColumnName(isJoin))
		.append("=");
	}

	/**
	 * Returns the name of this column
	 * @param isJoin indicates if this name will be use as join column or not
	 * @return the name of the column
	 */
	private String getColumnName(boolean isJoin){
		return isJoin ? getJoinColumName() : a.getDBName();
	}

	@Override
	public String builEqualsOnDecoratedBaseElement(Object o, boolean isJoin){
		return initEqualsExp(isJoin)
		.append(getSqlDecorator().decorate(o))
		.toString();
	}

	@Override
	public String builEqualsOnValue(Object o, boolean isJoin){
		return initEqualsExp(isJoin)
		.append(getValue(o))
		.toString();
	}

	/**
	 * Return the method of the given object corresponding ( name and parameters ) to specified one
	 * @param m the wanted method
	 * @param o the object where to look for the method
	 * @return the method
	 */
	protected Method getMethodOnMock(Method m , Object o){
		try {
			// TODO eventually cache this
			return o.getClass().getMethod(m.getName(), m.getParameterTypes());
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("The method : " + m.getName() + " doesn't exist for the class : " + o.getClass().getCanonicalName());
		}
	}
}