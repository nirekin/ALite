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

package gb.aliteorm.lib.impl.columns;

import android.database.sqlite.SQLiteDatabase;

import gb.aliteorm.lib.core.Attribute;
import gb.aliteorm.lib.core.IInsertOrder;
import gb.aliteorm.lib.core.IUpdateOrder;
import gb.aliteorm.lib.decorator.ISqlDecorator;

/**
 * Common definition of all column types.
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public interface IDBColumn {

	/**
	 * Indicates if the column is the simple id or part of the complex id of it's entity
	 * @return <code>true</code> if the column is an id, otherwise <code>false</code>
	 */
	public boolean isId();

	/**
	 * Indicates if the column is auto incremental 
	 * @return <code>true</code> if auto incremental, otherwise <code>false</code>
	 */
	public boolean isAutoIncrement();

	 /**
     * Indicates if the column is part of the insert SQL to persist new instances
	 * @return <code>true</code> if this column is part of the insert SQL, otherwise <code>false</code>
     */
	public boolean isInsertable();

    /**
     * Indicates if the column can be null or not
     * @return <code>true</code> is the column can be null, otherwise <code>false</code>
     */
	public boolean isUpdatable();

	/**
	 * Returns the attribute corresponding to this column
	 * @return the attribute
	 */
	public Attribute getAttribute();

	/**
	 * Returns the decorator associated to this column
	 * @return the decorator
	 */
	public ISqlDecorator getSqlDecorator();

	/**
	 * Returns the SQL sentence to create this column into the database
	 * @param allowingPKConstraint indicates if the SQL must contain the table key constraint specifications
	 * @return the SQL sentence
	 */
	public String getSqlForCreation(boolean allowingPKConstraint);

	/**
	 * Alter the database to add this column
	 * @param db the database
	 * @param allowingPKConstraint indicates if the SQL must contain the table key constraint specifications
	 */
	public void alterTableColumn(SQLiteDatabase db, boolean allowingPKConstraint);

	/**
	 * Reads from the given persistent instance the value to store into this column
	 * @param target the persistent instance
	 * @return the value read from the instance
	 */
	public String getValue(Object target);

	/**
	 * Writes into the given persistent instance the specified value
	 * @param target the persistent instance
	 * @param value the value
	 * @param isMock indicates if the target is a mock object ( for example one defined using <code>Projections.implementationClass(Class<?> class)</code> )
	 */
	public void setValue(Object target, Object value, boolean isMock);

	/**
	 * Reads from the given persistent instance the column value to add into the order to insert the persistent instance
	 * @param i the order to insert the instance
	 * @param o the persistent instance
	 */
	public void insertColumn(IInsertOrder i, Object o);

	/**
	 * Reads from the given persistent instance the value to add into the order to update the persistent instance
	 * @param u the order to update persistent instance
	 * @param o the persistent instance
	 */
	public void insertColumn(IUpdateOrder u, Object o);

	/**
	 * Returns the database column name associated to its table name
	 * <p>
	 * Will be for example : table1_columnName
	 *
	 * @return the name
	 */
	public String getJoinColumName();

	/**
	 * Returns the SQL sentence to create this column when its used as foreign key
	 * @return the SQL sentence
	 */
	public String getJoinColumDefinition();

	/**
	 * Return an equals sentence on this column
	 * @param o the object to decorated and use as value into the equal
	 * @param isJoin indicates if this equals will be use with a join column or not
	 * @return the equals sentence
	 */
	public String builEqualsOnDecoratedBaseElement(Object o, boolean isJoin);

	/**
	 * Return an equals sentence on this column
	 * @param o the model object containing the value to extract an use into the equal
	 * @param isJoin indicates if this equals will be use with a join column or not
	 * @return the equals sentence
	 */
	public String builEqualsOnValue(Object o, boolean isJoin);

}