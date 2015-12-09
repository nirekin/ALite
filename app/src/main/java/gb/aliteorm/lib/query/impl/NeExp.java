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

import gb.aliteorm.lib.core.Entity;
import gb.aliteorm.lib.exception.RWrongPropertyNameException;
import gb.aliteorm.lib.impl.columns.IDBColumn;

/**
 * Implementation of a "not equal" restriction on the scpecified attribute
 * 
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class NeExp implements Criterion{

	private final String pN;
	private final Object v;

	/**
	 * Create the new restriction
	 * @param attr the attribute to test
	 * @param value the tested value
	 */
	protected NeExp(String attr, Object value){
		pN = attr;
		v = value;
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
		.append("!=");
		col.getSqlDecorator().decorate(s, v);
		return s.toString();
	}
}