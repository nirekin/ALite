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
 * Several SQL strings used into the library.
 *
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public interface ISqlString {

	public static final String SELECT_SQL = "SELECT ";
	public static final String SELECT_ALL_SQL = SELECT_SQL + "* FROM ";
	public static final String SELECT_ALL_SQL_PARAM_T_W = SELECT_ALL_SQL + " %1$s WHERE %2$s";

	public static final String MAX_SQL = SELECT_SQL + "MAX(%1$s) FROM %2$s";

	public static final String DELETE_SQL = "DELETE FROM %1$s WHERE %2$s";

	public static final String DELETE_ALL_SQL = "DELETE FROM %1$s";
	public static final String COUNT_SQL = SELECT_SQL + "COUNT(*) FROM %1$s";

	public static final String DELETE_ORPHANS = "DELETE FROM %1$s WHERE %2$s NOT IN ( SELECT DISTINCT(%3$s) FROM %4$s)";

	public static final String LIMIT_SQL = " LIMIT %1$s ";
	public static final String OFFSET_SQL = " OFFSET %1$s ";

	public static final String ORDER_BY_SQL = " ORDER BY ";
	public static final String WHERE_SQL = " WHERE ";
	public static final String AND_SQL = " AND ";
}