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

package gb.aliteorm.lib.query.impl;

import java.util.Collection;

import gb.aliteorm.lib.core.Entity;
import gb.aliteorm.lib.exception.RWrongPropertyNameException;
import gb.aliteorm.lib.impl.columns.IDBColumn;

/**
 * Implementation of an "not" restriction on the specified attribute
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class inExp implements Criterion{

	private final String pN;
	private final Object[] values;
	private boolean in = true;

	/**
	 * Create the new restriction
	 * @param attr the attribute to test
	 * @param values the tested values
	 * @param in <CODE>true</CODE> if if it's a "select in", <CODE>false</CODE> otherwise. 
	 */
	protected inExp(String attr, Object[] values, boolean in ){
		pN = attr;
		this.values = values;
		this.in = in;
	}

	/**
	 * Create the new restriction
	 * @param attr the attribute to test
	 * @param values the tested values
	 * @param in <CODE>true</CODE> if if it's a "select in", <CODE>false</CODE> otherwise. 
	 */
	protected inExp(String attr, Collection<?> values, boolean in ){
		pN = attr;
		this.values = values.toArray (new Object[values.size ()]);
		this.in = in;
	}

	
	@Override
	public String getSql(Entity e) {
		IDBColumn col = e.getTable().getColumn(pN);
		if(col == null)
			throw new RWrongPropertyNameException("attribute :" + pN + " not found for :" + e.getTable().getTableName());
		StringBuilder s = new StringBuilder()
		.append(" ")
		.append(e.getTable().getTableName())
		.append(".")
		.append(col.getAttribute().getDBName())
		.append(in ? " in (" : " not in (");
		for(int i = 0; i < values.length; i++){
			col.getSqlDecorator().decorate(s, values[i]);
			if(i + 1 < values.length)
				s.append(",");
		}
		s.append(")");
		return s.toString();
	}
}