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

import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import gb.aliteorm.lib.annotation.ALiteStringLength;
import gb.aliteorm.lib.core.Attribute;
import gb.aliteorm.lib.core.IInsertOrder;
import gb.aliteorm.lib.decorator.ISqlDecorator;
import gb.aliteorm.lib.decorator.StringSqlDecorator;
import gb.aliteorm.lib.exception.UnsupportedGetterException;

/**
 * Database column to map <code>String</code> values.
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class DBColumnString extends DBColumnImpl{

	private ALiteStringLength length = null;

	/**
	 * New column to map <code>String</code> values
	 * 
 	 * @param tableName the table where to create the column
	 * @param a the attribute corresponding to the column
	 * @param getter the getter method to access the attribute
	 * @throws UnsupportedGetterException will be thrown if the getter returns an unsupported type
	 */
	protected DBColumnString(String tableName, Attribute a, Method getter) throws UnsupportedGetterException{
		super(tableName, a, getter);
		Annotation ann = getter.getAnnotation(ALiteStringLength.class);
		if(ann != null){
			length = (ALiteStringLength)ann;
		}
	}

	private int getLength(){
		return length != null ? length.length() : 255;
	}

	@Override
	protected void defineSQL(StringBuilder strb) {
		strb.append("VARCHAR(" + getLength() + ")");
		if(getDefaultValue() != null && getDefaultValue().trim().length() > 0){
			if(getDefaultValue().length() > getLength())
				throw new RuntimeException("Wrong the default value for String is longer than the column length: " + a.getModelName() );
			strb.append(" DEFAULT '" + getDefaultValue() + "'");
		}
	}

	@Override
	public void readAttributed(StringBuilder strb, Object target){
		try {
			getSqlDecorator().decorate(strb, a.getGetter().invoke(target));
		}catch(Exception e) {
			Log.e("bccore", "Error getter String :" + a.getModelName() );
		}
	}

	@Override
	protected void setAttribute(Object target, Object value, boolean isMock) throws Exception{
		if(!isMock)
			a.getSetter().invoke(target, value);
		else
			getMethodOnMock(a.getSetter(), target).invoke(target, value);
	}

	@Override
	public ISqlDecorator getSqlDecorator(){
		return new StringSqlDecorator();
	}

	@Override
	public void insertColumn(IInsertOrder i, Object o){
		if(o != null){
			String value = getValue(o);
			if(value != null && "null".equalsIgnoreCase(value) == false && "'null'".equalsIgnoreCase(value) == false){
				i.addColumn(a.getDBName(), value);
			}
		}
	}
}