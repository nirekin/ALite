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
 * Implementation of a "less than" restriction on two attributes
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class LowerThanPropExp implements Criterion{

	private String pN;
	private String oN ;

	/**
	 * Create the new restriction
	 * @param attr1 the first attribute to test
	 * @param attr2 the second attribute to test
	 */
	protected LowerThanPropExp(String attr1, String attr2){
		pN = attr1;
		oN = attr2;
	}

	@Override
	public String getSql(Entity e) {
		IDBColumn colp = e.getTable().getColumn(pN);
		if(colp == null)
			throw new RWrongPropertyNameException("attribute :" + pN + " not found for :" + e.getTable().getTableName());
		IDBColumn colo = e.getTable().getColumn(oN);
		if(colo == null)
			throw new RWrongPropertyNameException("attribute :" + oN + " not found for :" + e.getTable().getTableName());
		return new StringBuilder()
		.append(" ")
		.append(e.getTable().getTableName())
		.append(".")
		.append(colp.getAttribute().getDBName())
		.append("<")
		.append(e.getTable().getTableName())
		.append(".")
		.append(colo.getAttribute().getDBName())
		.toString();
	}
}