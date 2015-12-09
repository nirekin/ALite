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

import java.lang.reflect.Method;

import gb.aliteorm.lib.core.Attribute;
import gb.aliteorm.lib.decorator.ISqlDecorator;
import gb.aliteorm.lib.decorator.StringSqlDecorator;
import gb.aliteorm.lib.exception.UnsupportedGetterException;

/**
 * Database column to map <code>char</code> and <code>Character</code> values.
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class DBColumnChar extends DBColumnImpl{

	/**
	 * New column to map <code>char</code> and <code>Character</code> values
	 * 
 	 * @param tableName the table where to create the column
	 * @param a the attribute corresponding to the column
	 * @param getter the getter method to access the attribute
	 * @throws UnsupportedGetterException will be thrown if the getter returns an unsupported type
	 */
	protected DBColumnChar(String tableName, Attribute a, Method getter) throws UnsupportedGetterException{
		super(tableName, a, getter);
	}

	@Override
	protected void defineSQL(StringBuilder strb) {
		strb.append("VARCHAR(1)");
		if(getDefaultValue() != null && getDefaultValue().trim().length() > 0){
			if(getDefaultValue().length() > 1)
				throw new RuntimeException("Wrong the default value for char is longer than 1: " + a.getModelName() );
			strb.append(" DEFAULT '" + getDefaultValue() + "'");
		}
	}

	@Override
	public void readAttributed(StringBuilder strb, Object target){
		try {
			char ch = (char)a.getGetter().invoke(target);
			if(("" + ch).trim().length() == 0){
				strb.append("''");
			}else{
				getSqlDecorator().decorate(strb, ch);
			}
		}catch(Exception e) {
			Log.e("bccore", "Error getter char :" + a.getModelName() );
			strb.append("''");
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
}
