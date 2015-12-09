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
import java.util.Date;

import gb.aliteorm.lib.core.Attribute;
import gb.aliteorm.lib.decorator.DateSqlDecorator;
import gb.aliteorm.lib.decorator.ISqlDecorator;
import gb.aliteorm.lib.exception.UnsupportedGetterException;

/**
 * Database column to map <code>java.util.Date</code> values.
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class DBColumnDate extends DBColumnImpl{

	/**
	 * New column to map >java.util.Date</code> values
	 * 
 	 * @param tableName the table where to create the column
	 * @param a the attribute corresponding to the column
	 * @param getter the getter method to access the attribute
	 * @throws UnsupportedGetterException will be thrown if the getter returns an unsupported type
	 */
	protected DBColumnDate(String tableName,Attribute a, Method getter) throws UnsupportedGetterException{
		super(tableName, a, getter);
	}

	@Override
	protected void defineSQL(StringBuilder strb) {
		strb.append("NUMERIC");
	}

	@Override
	public void readAttributed(StringBuilder strb , Object target){
		try {
			Date d = (Date)a.getGetter().invoke(target);
			strb.append(d.getTime());
		}catch(Exception e) {
			Log.e("bccore", "Error getter Date :" + a.getModelName() );
			strb.append(0);
		}
	}

	@Override
	protected void setAttribute(Object target, Object value, boolean isMock) throws Exception{
		Date d = (Date)value;
		if(d.getTime() > 0){
			if(!isMock)
				a.getSetter().invoke(target, value);
			else
				getMethodOnMock(a.getSetter(), target).invoke(target, value);
		}
	}

	@Override
	public ISqlDecorator getSqlDecorator(){
		return new DateSqlDecorator();
	}
}