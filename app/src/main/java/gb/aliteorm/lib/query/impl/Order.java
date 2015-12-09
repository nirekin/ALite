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

/**
 * Sorting type used to order query results of Criteria query
 *
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class Order{

	enum Sorting{
		ASC,
		DESC
	}

	private String pN;
	private Sorting s;

	/**
	 * Creates a sorting constraint
	 *
	 * @param o the desired order
	 * @param p the attribute to sort
	 */
	private Order(Sorting o, String p){
		pN = p;
		s = o;
	}

	/**
	 * Creates an ascending sorting constraint
	 *
	 * @param p the attribute to sort
	 * @return an ascending sorting constraint
	 */
	public static Order asc(String p){
		return new Order(Sorting.ASC, p);
	}

	/**
	 * Creates an descending sorting constraint
	 *
	 * @param p the attribute to sort
	 * @return an descending sorting constraint
	 */
	public static Order desc(String p){
		return new Order(Sorting.DESC, p);
	}

	/**
	 * Translates the constraint into executable an SQL sequence ( wont contains ORDER BY )
	 * @return an executable an SQL sequence
	 */
	public String getSql() {
		return pN + " " + (s == Sorting.ASC ? "asc" : "desc");
	}
}