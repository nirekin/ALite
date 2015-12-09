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
package gb.aliteorm.lib.query.impl;

import gb.aliteorm.lib.core.Entity;
import gb.aliteorm.lib.exception.RWrongPropertyNameException;
import gb.aliteorm.lib.impl.columns.DBColumnBoolean;
import gb.aliteorm.lib.impl.columns.DBColumnDate;
import gb.aliteorm.lib.impl.columns.IDBColumn;

/**
 * Implementation of a "like" restriction on the specified attribute
 *
 *  <pre>
 * 		Calling : new LikeExp(OnSide.BOTH,"columnA", "ABCD")
 * 		Will generate :  .columnA like '%ABCD%'
 *
 * 		Calling : new LikeExp(OnSide.LEFT,"columnA", "ABCD")
 * 		Will generate :  .columnA like 'ABCD%'
 *
 * 		Calling : new LikeExp(OnSide.RIGHT,"columnA", "ABCD")
 * 		Will generate :  .columnA like '%ABCD'
 * </pre>
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class LikeExp implements Criterion{

	private String pN;
	private Object v;
	private OnSide side;

	/**
	 * Create the new restriction
	 * @param onSide the side of the comparison
	 * @param attr the attribute to test
	 * @param value the tested value
	 */
	protected LikeExp(OnSide onSide, String attr, Object value){
		pN = attr;
		v = value;
		side = onSide;
	}

	@Override
	public String getSql(Entity e) {
		IDBColumn col = e.getTable().getColumn(pN);
		if(col == null)
			throw new RWrongPropertyNameException("attribute :" + pN + " not found for :" + e.getTable().getTableName());
		StringBuilder st = new StringBuilder()
		.append(" ")
		.append(e.getTable().getTableName())
		.append(".")
		.append(col.getAttribute().getDBName())
		.append(" like ");

		String s = "" + v;
		if(col instanceof DBColumnBoolean == false){
			if(side == OnSide.BOTH || side == OnSide.LEFT)
				s = s + "%";
			if(side == OnSide.BOTH || side == OnSide.RIGHT)
				s = "%" + s;
		}

		String str = "";
		if(col instanceof DBColumnDate){
			str = s;
		}else{
			str = col.getSqlDecorator().decorate(s);
		}

		if(!str.startsWith("'"))
			str = "'" + str;
		if(!str.endsWith("'"))
			str = str + "'";
		st.append(str);
		return st.toString();
	}
}

enum OnSide{
	BOTH,
	RIGHT,
	LEFT
}
