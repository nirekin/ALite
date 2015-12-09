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


/**
 * Criterion factory used to get Criterion that are used as a restriction in a Criteria query.
 * 
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class Restrictions {

	/**
	 * Apply an "equal" restriction to the id property
	 *
	 * @param value the id of the wanted entity
	 * @return Criterion
	 */
	public static Criterion idEq(Object value){
		return new IdEqExp(value);
	}

	/**
	 * Apply an "equal" restriction on the specified attribute
	 *
	 * @param attr the attribute to test
	 * @param value the value to test
	 * @return Criterion
	 */
	public static Criterion eq(String attr, Object value){
		return new EqExp(attr, value);
	}

	/**
	 * Apply a "not equal" restriction on the specified attribute
	 *
	 * @param attr the attribute to test
	 * @param value the value to test
	 * @return Criterion
	 */
	public static Criterion ne(String attr, Object value){
		return new NeExp(attr, value);
	}

	/**
	 * Apply a "not equal" restriction on two attributes
	 *
	 * @param attr the first attribute to test
	 * @param attr2 the second attribute to test
	 * @return Criterion
	 */
	public static Criterion neProperty(String attr, String attr2){
		return new NePropExp(attr, attr2);
	}

	/**
	 * Apply an "equal" restriction on two attributes
	 *
	 * @param attr the first attribute to test
	 * @param attr2 the second attribute to test
	 * @return Criterion
	 */
	public static Criterion eqProperty(String attr, String attr2){
		return new EqPropExp(attr, attr2);
	}

	/**
	 * Apply a "less than" restriction on two attributes
	 *
	 * @param attr the first attribute to test
	 * @param attr2 the second attribute to test
	 * @return Criterion
	 */
	public static Criterion ltProperty(String attr, String attr2){
		return new LowerThanPropExp(attr, attr2);
	}

	/**
	 * Apply a "less than or equal" restriction on two attributes
	 *
	 * @param attr the first attribute to test
	 * @param attr2 the second attribute to test
	 * @return Criterion
	 */
	public static Criterion leProperty(String attr, String attr2){
		return new LowerEqualsPropExp(attr, attr2);
	}

	/**
	 * Apply a "greater than" restriction on two attributes
	 *
	 * @param attr the first attribute to test
	 * @param attr2 the second attribute to test
	 * @return Criterion
	 */
	public static Criterion gtProperty(String attr, String attr2){
		return new GreaterThanPropExp(attr, attr2);
	}

	/**
	 * Apply a "greater than or equal" restriction on two attributes
	 *
	 * @param attr the first attribute to test
	 * @param attr2 the second attribute to test
	 * @return Criterion
	 */
	public static Criterion geProperty(String attr, String attr2){
		return new GreaterEqualsPropExp(attr, attr2);
	}

	/**
	 * Return the conjuction of two expressions
	 *
	 * @param c1 the left member 
	 * @param c2 the right member
	 * @return Criterion
	 */
	public static Criterion and(Criterion c1, Criterion... c2){
		return new LogicalGroup(LogicalGroup.AND, c1, c2);
	}

	/**
	 * Return the disjuction of two expressions
	 *
	 * @param c1 the left member
	 * @param c2 the right member
	 * @return Criterion
	 */
	public static Criterion or(Criterion c1, Criterion... c2){
		return new LogicalGroup(LogicalGroup.OR, c1, c2);
	}

	/**
	 * Apply an "in" restriction on the specified attribute
	 * <p>
	 * This method can't receive complex ids as parameters
	 *
	 * @param attr the attribute to test
	 * @param values the values to test 
	 * @return Criterion
	 */
	public static Criterion in(String attr, Collection<?> values){
		return new inExp(attr, values, true);
	}

	/**
	 * Apply an "in" restriction on the specified attribute
	 * <p>
	 * This method can't receive complex ids as parameters
	 * 
	 * @param attr the attribute to test
	 * @param values the values to test
	 * @return Criterion
	 */
	public static Criterion in(String attr, Object[] values){
		return new inExp(attr, values, true);
	}

	/**
	 * Apply an "not in" restriction on the specified attribute
	 * <p>
	 * This method can't receive complex ids as parameters
	 * 
	 * @param attr the attribute to test
	 * @param values the values to test 
	 * @return Criterion
	 */
	public static Criterion notIn(String attr, Collection<?> values){
		return new inExp(attr, values, false);
	}

	/**
	 * Apply an "not in" restriction on the specified attribute
	 * <p>
	 * This method can't receive complex ids as parameters
	 * 
	 * @param attr the attribute to test
	 * @param values the values to test
	 * @return Criterion
	 */
	public static Criterion notIn(String attr, Object[] values){
		return new inExp(attr, values, false);
	}

	
	/**
	 * Apply an "is null" restriction on the specified attribute
	 *
	 * @param attr the attribute to test
	 * @return Criterion
	 */
	public static Criterion isNull(String attr){
		return new NullExp(attr);
	}

	/**
	 * Apply an "is not null" restriction on the specified attribute
	 *
	 * @param attr the attribute to test
	 * @return Criterion
	 */
	public static Criterion isNotNull(String attr){
		return new NotNullExp(attr);
	}

	/**
	 * Apply a "greater than" restriction on the specified attribute
	 *
	 * @param attr the attribute to test
	 * @param value the value to test
	 * @return Criterion
	 */
	public static Criterion gt(String attr, Object value){
		return new GreaterThanExp(attr, value);
	}

	/**
	 * Apply a "less than" restriction on the specified attribute
	 *
	 * @param attr the attribute to test
	 * @param value the value to test
	 * @return Criterion
	 */
	public static Criterion lt(String attr, Object value){
		return new LowerThanExp(attr, value);
	}

	/**
	 * Apply a "greater than or equal" restriction on the specified attribute
	 *
	 * @param attr the attribute to test
	 * @param value the value to test
	 * @return Criterion
	 */
	public static Criterion ge(String attr, Object value){
		return new GreaterEqualsExp(attr, value);
	}

	/**
	 * Apply a "less than or equal" restriction on the specified attribute
	 *
	 * @param attr the attribute to test
	 * @param value the value to test
	 * @return Criterion
	 */
	public static Criterion le(String attr, Object value){
		return new LowerEqualsExp(attr, value);
	}

	/**
	 * Apply a "like" restriction on the specified attribute
	 *
	 *  <pre>
	 * 		Calling : like("columnA", "ABCD")
	 *
	 * 		Will generate :  .columnA like '%ABCD%'
	 * </pre>
	 *
	 * @param attr the attribute to test
	 * @param value the value to test
	 * @return Criterion
	 */
	public static Criterion like(String attr, Object value){
		return new LikeExp(OnSide.BOTH, attr, value);
	}

	/**
	 * Apply a "like" restriction on the specified attribute
	 *
	 * <pre>
	 * 		Calling : rightLike("columnA", "ABCD")
	 *
	 * 		Will generate :  .columnA like '%ABCD'
	 * </pre>
	 *
	 * @param attr the attribute to test
	 * @param value the value to test
	 * @return Criterion
	 */
	public static Criterion rightLike(String attr, Object value){
		return new LikeExp(OnSide.RIGHT, attr, value);
	}

	/**
	 * Apply a "like" restriction on the specified attribute
	 *
	 * <pre>
	 * 		Calling : leftLike("columnA", "ABCD")
	 *
	 * 		Will generate :  .columnA like 'ABCD%'
	 * </pre>
	 *
	 * @param attr the attribute to test
	 * @param value the value to test
	 * @return Criterion
	 */
	public static Criterion leftLike(String attr, Object value){
		return new LikeExp(OnSide.LEFT, attr, value);
	}


//  static Criterion			between(String attr, Object lo, Object hi) 				//    Apply a "between" constraint on the specified attribute
//  static SimpleExpression		like(String attr, Object value) 						//    Apply a "like" constraint on the specified attribute
//  static Criterion			ilike(String attr, Object value)						//    A case-insensitive "like", similar to Postgres ilike operator
//  static Criterion			ilike(String attr, String value, MatchMode matchMode)	//    A case-insensitive "like", similar to Postgres ilike operator
//	static Criterion			allEq(Map attrValues) 									//    Apply an "equals" constraint to each property in the key set of a Map
//  static Conjunction		conjunction() 														//    Group expressions together in a single conjunction (A and B and C...)
//  static Disjunction		disjunction() 														//    Group expressions together in a single disjunction (A or B or C...)
//  static SimpleExpression	like(String attr, String value, MatchMode matchMode) 		//    Apply a "like" constraint on the specified attribute
//  static NaturalIdentifier	naturalId()
//  static Criterion			not(Criterion expression) 										//    Return the negation of an expression
//  static Criterion			sizeEq(String attr, int size) 							//    Constrain a collection valued property by size
//  static Criterion			sizeGe(String attr, int size) 							//    Constrain a collection valued property by size
//  static Criterion			sizeGt(String attr, int size) 							//    Constrain a collection valued property by size
//  static Criterion			sizeLe(String attr, int size) 							//    Constrain a collection valued property by size
//  static Criterion			sizeLt(String attr, int size) 							//    Constrain a collection valued property by size
//  static Criterion			sizeNe(String attr, int size) 							//    Constrain a collection valued property by size
//  static Criterion			sqlRestriction(String sql) 										//    Apply a constraint expressed in SQL.
//  static Criterion			sqlRestriction(String sql, Object[] values, Type[] types) 		//    Apply a constraint expressed in SQL, with the given JDBC parameters.
//  static Criterion			sqlRestriction(String sql, Object value, Type type) 			//    Apply a constraint expressed in SQL, with the given JDBC parameter.
}
