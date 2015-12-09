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
import gb.aliteorm.lib.core.EntityId;
import gb.aliteorm.lib.core.TravelingComplexId;
import gb.aliteorm.lib.exception.RNoIdException;
import gb.aliteorm.lib.impl.columns.IDBColumn;

/**
 * Implementation of an "equal" restriction on the id property
 *
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class IdEqExp implements Criterion{

	private final Object v;

	/**
	 * Create the new restriction
	 * @param value the tested value
	 */
	protected IdEqExp(Object value){
		this.v = value;
	}

	@Override
	public String getSql(Entity e) {
		EntityId id = e.getId();
		if(id == null)
			throw new RNoIdException(" id not found for : " + e.getTable().getTableName());
		if(!id.isComplex()){
			IDBColumn idCOlumn = id.getColumns()[0];
			StringBuilder s = new StringBuilder()
			.append(" ")
			.append(e.getTable().getTableName())
			.append(".")
			.append(idCOlumn.getAttribute().getDBName())
			.append("=");
			idCOlumn.getSqlDecorator().decorate(s, v);
			return s.toString();
		}else{
			StringBuilder s = new StringBuilder()
			.append(" ")
			.append(id.getWhereOn(new TravelingComplexId(v)))
			.append(" ");
			return s.toString();
		}
	}
}