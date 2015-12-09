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


import android.database.Cursor;
import android.util.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import gb.aliteorm.lib.annotation.ALiteStringLength;
import gb.aliteorm.lib.core.Attribute;
import gb.aliteorm.lib.decorator.BooleanSqlDecorator;
import gb.aliteorm.lib.decorator.DateSqlDecorator;
import gb.aliteorm.lib.decorator.DummySqlDecorator;
import gb.aliteorm.lib.decorator.ISqlDecorator;
import gb.aliteorm.lib.decorator.StringSqlDecorator;
import gb.aliteorm.lib.exception.UnsupportedGetterException;
import gb.aliteorm.lib.tools.ALiteOrmBuilder;
import gb.aliteorm.lib.tools.ILogPrefix;

/**
 * Factory of column implementations.
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class DBColumnFactory {

	/**
	 * Creates a new column to mapped into the data base
	 * 
	 * @param tableName the table where to create the column
	 * @param a the attribute corresponding to the column
	 * @param m the getter method to access the attribute
	 * @return the created column
	 * @throws UnsupportedGetterException will be thrown if the getter returns an unsupported type
	 */
	public static IDBColumn getColumn(String tableName, Attribute a, Method m) throws UnsupportedGetterException{

		Class<?> clazz = a.getContainer().getImplementationClass();
		Type t = m.getReturnType();
		if (t.equals(Integer.TYPE)) {
			return new DBColumnInt(tableName, a, m);
		}else if (t.equals(Long.TYPE)){
			return new DBColumnLong(tableName, a, m);
		}else if (t.equals(Double.TYPE)){
			return new DBColumnDouble(tableName, a, m);
		}else if (t.equals(Float.TYPE)){
			return new DBColumnFloat(tableName, a, m);
		}else if (t.equals(Character.TYPE)){
			return new DBColumnChar(tableName, a, m);
		}else if (t.equals(Short.TYPE)){
			return new DBColumnShort(tableName, a, m);
		}else if (t.equals(Boolean.TYPE)){
			return new DBColumnBoolean(tableName, a, m);
		}else if(t.toString().equalsIgnoreCase("class " + String.class.getName())){
			return new DBColumnString(tableName, a, m);
		}else if(t.toString().equalsIgnoreCase("class " + Date.class.getName())){
			return new DBColumnDate(tableName, a, m);
		}else if(t.toString().equalsIgnoreCase("class " + Integer.class.getName())){
			return new DBColumnInt(tableName, a, m);
		}else if(t.toString().equalsIgnoreCase("class " + Long.class.getName())){
			return new DBColumnLong(tableName, a, m);
		}else if(t.toString().equalsIgnoreCase("class " + Double.class.getName())){
			return new DBColumnDouble(tableName, a, m);
		}else if(t.toString().equalsIgnoreCase("class " + Float.class.getName())){
			return new DBColumnFloat(tableName, a, m);
		}else if(t.toString().equalsIgnoreCase("class " + Character.class.getName())){
			return new DBColumnChar(tableName, a, m);
		}else if(t.toString().equalsIgnoreCase("class " + Short.class.getName())){
			return  new DBColumnShort(tableName, a, m);
		}else if(t.toString().equalsIgnoreCase("class " + Boolean.class.getName())){
			return new DBColumnBoolean(tableName, a, m);
		}else if(t.toString().equalsIgnoreCase("class " + BigDecimal.class.getName())){
			return new DBColumnBigDecimal(tableName, a, m);
		}else if(t.toString().equalsIgnoreCase("class " + BigInteger.class.getName())){
			return new DBColumnBigInteger(tableName, a, m);
		}
		throw new RuntimeException("Unsupported return type : " + clazz + " for the method:" + m.getName());
	}

	/**
	 * Returns the decorator associated to the column
	 * @param t the type to decorate
	 * @return the decorator
	 */
	public static ISqlDecorator getDecorator(final Type t){
		// TODO MERGE THIS AN THE RETURN OF DECORATOR INTO CLASS DBColumn...
		if (t.equals(Boolean.TYPE)){
			return new BooleanSqlDecorator();
		}else if(t.toString().equalsIgnoreCase("class " + String.class.getName())){
			return new StringSqlDecorator();
		}else if(t.toString().equalsIgnoreCase("class " + Date.class.getName())){
			return new DateSqlDecorator();
		}else if(t.toString().equalsIgnoreCase("class " + Character.class.getName())){
			return new StringSqlDecorator();
		}else if(t.toString().equalsIgnoreCase("class " + Boolean.class.getName())){
			return new BooleanSqlDecorator();
		}
		return new DummySqlDecorator();
	}

	/**
	 * Indicates if the given type is one of the base types supported by ALiteOrm
	 * @param t the type to test
	 * @return <code>true</code> if the type is a supported base type, otherwise <code>false</code>
	 */
	public static boolean isBaseType(final Type t){
		if (t.equals(Integer.TYPE)) {
			return true;
		}else if (t.equals(Long.TYPE)){
			return true;
		}else if (t.equals(Double.TYPE)){
			return true;
		}else if (t.equals(Float.TYPE)){
			return true;
		}else if (t.equals(Character.TYPE)){
			return true;
		}else if (t.equals(Short.TYPE)){
			return true;
		}else if (t.equals(Boolean.TYPE)){
			return true;
		}else if(t.toString().equalsIgnoreCase("class " + String.class.getName())){
			return true;
		}else if(t.toString().equalsIgnoreCase("class " + Date.class.getName())){
			return true;
		}else if(t.toString().equalsIgnoreCase("class " + Integer.class.getName())){
			return true;
		}else if(t.toString().equalsIgnoreCase("class " + Long.class.getName())){
			return true;
		}else if(t.toString().equalsIgnoreCase("class " + Double.class.getName())){
			return true;
		}else if(t.toString().equalsIgnoreCase("class " + Float.class.getName())){
			return true;
		}else if(t.toString().equalsIgnoreCase("class " + Character.class.getName())){
			return true;
		}else if(t.toString().equalsIgnoreCase("class " + Short.class.getName())){
			return true;
		}else if(t.toString().equalsIgnoreCase("class " + Boolean.class.getName())){
			return true;
		}else if(t.toString().equalsIgnoreCase("class " + BigDecimal.class.getName())){
			return true;
		}else if(t.toString().equalsIgnoreCase("class " + BigInteger.class.getName())){
			return true;
		}
		return false;
	}

	/**
	 * Writes into the given persistent instance the cursor's content corresponding to one column
	 * @param column the column to read
	 * @param cursor the cursor to read
	 * @param result the persistent instance
	 */
	public static void setValue(IDBColumn column, Cursor cursor,  Object result){
		setValue(column, cursor,  result, false);
	}
		
	/**
	 * Writes into the given persistent instance the cursor's content corresponding to one column
	 * @param column the column to read
	 * @param cursor the cursor to read
	 * @param result the persistent instance
	 * @param isMock indicates if the result is a mock object ( for example one defined using <code>Projections.implementationClass(Class<?> class)</code> )
	 */
	public static void setValue(IDBColumn column, Cursor cursor,  Object result, boolean isMock){
		
		Type t = column.getAttribute().getColumnType();
		try{
			int index = cursor.getColumnIndex(column.getAttribute().getDBName());
			if (t.equals(Integer.TYPE)) {
				column.setValue(result, cursor.getInt(index), isMock);
			}else if (t.equals(Long.TYPE)){
				column.setValue(result, cursor.getLong(index), isMock);
			}else if (t.equals(Double.TYPE)){
				column.setValue(result, cursor.getDouble(index), isMock);
			}else if (t.equals(Float.TYPE)){
				column.setValue(result, cursor.getFloat(index), isMock);
			}else if (t.equals(Character.TYPE)){
				column.setValue(result, cursor.getString(index).charAt(0),isMock);
			}else if (t.equals(Short.TYPE)){
				column.setValue(result, cursor.getShort(index), isMock);
			}else if (t.equals(Boolean.TYPE)){
				column.setValue(result, cursor.getInt(index) == 1 ? true : false, isMock);
			}else if(t.toString().equalsIgnoreCase("class " + String.class.getName())){
				column.setValue(result, cursor.getString(index), isMock);
			}else if(t.toString().equalsIgnoreCase("class " + Date.class.getName())){
				column.setValue(result, new Date(Long.parseLong(cursor.getString(index))), isMock);
			}else if(t.toString().equalsIgnoreCase("class " + Integer.class.getName())){
				column.setValue(result, Integer.valueOf(cursor.getInt(index)), isMock);
			}else if(t.toString().equalsIgnoreCase("class " + Long.class.getName())){
				column.setValue(result, Long.valueOf(cursor.getLong(index)), isMock);
			}else if(t.toString().equalsIgnoreCase("class " + Double.class.getName())){
				column.setValue(result, Double.valueOf(cursor.getDouble(index)), isMock);
			}else if(t.toString().equalsIgnoreCase("class " + Float.class.getName())){
				column.setValue(result, Float.valueOf(cursor.getFloat(index)), isMock);
			}else if(t.toString().equalsIgnoreCase("class " + Character.class.getName())){
				column.setValue(result, Character.valueOf(cursor.getString(index).charAt(0)), isMock);
			}else if(t.toString().equalsIgnoreCase("class " + Short.class.getName())){
				column.setValue(result, new Short(cursor.getShort(index)), isMock);
			}else if(t.toString().equalsIgnoreCase("class " + Boolean.class.getName())){
				column.setValue(result, Boolean.valueOf(cursor.getInt(index) == 1 ? true : false), isMock);
			}else if(t.toString().equalsIgnoreCase("class " + BigDecimal.class.getName())){
				column.setValue(result, new BigDecimal(cursor.getString(index)), isMock);
			}else if(t.toString().equalsIgnoreCase("class " + BigInteger.class.getName())){
				column.setValue(result, new BigInteger(cursor.getString(index)), isMock);
			}
		}catch(Exception e){
			if(ALiteOrmBuilder.getInstance().isShowLog())
				Log.d(ILogPrefix.ACTIVITY_LOG, "not founded : " + e.getMessage());
		}
	}

	/**
	 * Returns the value read from the cursor for one column and type
	 * @param columnName the column to read
	 * @param cursor the cursor to read
	 * @param t the type to read
	 * @return the value
	 */
	public static Object readCursor(String columnName, Cursor cursor, Type t){
		try{
			int index = cursor.getColumnIndex(columnName);
			if (t.equals(Integer.TYPE)) {
				return cursor.getString(index);
			}else if (t.equals(Long.TYPE)){
				return cursor.getString(index);
			}else if (t.equals(Double.TYPE)){
				return cursor.getString(index);
			}else if (t.equals(Float.TYPE)){
				return cursor.getString(index);
			}else if (t.equals(Character.TYPE)){
				return cursor.getString(index).charAt(0);
			}else if (t.equals(Short.TYPE)){
				return cursor.getString(index);
			}else if (t.equals(Boolean.TYPE)){
				return cursor.getInt(index);
			}else if(t.toString().equalsIgnoreCase("class " + String.class.getName())){
				return cursor.getString(index);
			}else if(t.toString().equalsIgnoreCase("class " + Date.class.getName())){
				return new Date(Long.parseLong(cursor.getString(index)));
			}else if(t.toString().equalsIgnoreCase("class " + Integer.class.getName())){
				return Integer.valueOf(cursor.getString(index));
			}else if(t.toString().equalsIgnoreCase("class " + Long.class.getName())){
				return Long.valueOf(cursor.getString(index));
			}else if(t.toString().equalsIgnoreCase("class " + Double.class.getName())){
				return Double.valueOf(cursor.getString(index));
			}else if(t.toString().equalsIgnoreCase("class " + Float.class.getName())){
				return Float.valueOf(cursor.getString(index));
			}else if(t.toString().equalsIgnoreCase("class " + Character.class.getName())){
				return Character.valueOf(cursor.getString(index).charAt(0));
			}else if(t.toString().equalsIgnoreCase("class " + Short.class.getName())){
				return new Short(cursor.getString(index));
			}else if(t.toString().equalsIgnoreCase("class " + Boolean.class.getName())){
				return Boolean.valueOf(cursor.getInt(index) == 1 ? true : false);
			}else if(t.toString().equalsIgnoreCase("class " + BigDecimal.class.getName())){
				return new BigDecimal(cursor.getString(index));
			}else if(t.toString().equalsIgnoreCase("class " + BigInteger.class.getName())){
				return new BigInteger(cursor.getString(index));
			}
		}catch(Exception e){
			if(ALiteOrmBuilder.getInstance().isShowLog())
				Log.d(ILogPrefix.ACTIVITY_LOG, "not founded : " + e.getMessage());
		}
		return null;
	}

	/**
	 * Returns the SQL type corresponding to the given type and eventually for a specific length
	 * @param clazz the type of the column
	 * @param length the length of the column
	 * @return the SQL type of the column
	 */
	public static String getBaseTypeSql(final Class<?> clazz,  ALiteStringLength length){
		if (clazz.equals(Integer.TYPE)) {
			return "INTEGER";
		}else if (clazz.equals(Long.TYPE)){
			return "NUMERIC";
		}else if (clazz.equals(Double.TYPE)){
			return "REAL";
		}else if (clazz.equals(Float.TYPE)){
			return "FLOAT";
		}else if (clazz.equals(Character.TYPE)){
			return "VARCHAR(1)";
		}else if (clazz.equals(Short.TYPE)){
			return "INTEGER";
		}else if (clazz.equals(Boolean.TYPE)){
			return "INTEGER DEFAULT '0'";
		}else if(clazz.toString().equalsIgnoreCase("class " + String.class.getName())){
			if(length != null){
				return "VARCHAR(" + length.length() + ")";
			}else{
				return "TEXT";
			}
		}else if(clazz.toString().equalsIgnoreCase("class " + Date.class.getName())){
			return "NUMERIC";
		}else if(clazz.toString().equalsIgnoreCase("class " + BigDecimal.class.getName())){
			return "VARCHAR(255)";
		}else if(clazz.toString().equalsIgnoreCase("class " + BigInteger.class.getName())){
			return "VARCHAR(255)";
		}
		return "";
	}
}