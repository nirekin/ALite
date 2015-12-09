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

import java.util.ArrayList;
import java.util.Arrays;

import gb.aliteorm.lib.core.Entity;

/**
 * Implementation of the conjuction or disjuction of two expressions
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class LogicalGroup implements Criterion{

	public static final String AND = "AND";
	public static final String OR = "OR";

	private ArrayList<Criterion> crits;
	private String operator;

	/**
	 * Creates a new conjuction or disjuction of two expressions
	 * @param operator the Operator "AND" or "OR"
	 * @param c1 the left member
	 * @param c2 the right member
	 */
	protected LogicalGroup(String operator, Criterion c1, Criterion... c2){
		this.operator = operator;
		crits = new ArrayList<Criterion>();
		crits.add(c1);
		crits.addAll(Arrays.asList(c2));
	}

	@Override
	public String getSql(Entity entity) {
		StringBuilder s = new StringBuilder()
		.append("(");
		for(int i = 0; i < crits.size(); i++){
			s.append(crits.get(i).getSql(entity));
			if(i + 1 < crits.size()){
				s.append(" ")
				.append(operator)
				.append(" ");
			}
		}
		s.append(")");
		return s.toString();
	}
}