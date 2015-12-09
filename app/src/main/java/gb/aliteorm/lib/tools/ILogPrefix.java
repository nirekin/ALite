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

package gb.aliteorm.lib.tools;

/**
 * Android log files' prefixes used by ALiteOrm
 *
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public interface ILogPrefix {

	/**
	 * Prefix used to log all SQL Queries
	 */
	public static final String SQL_LOG = "dbSql"; // TODO CHECK WHERE THIS IS USED AND IF IT'S OK WITH THE LOGGED CONTENT

	/**
	 * Prefix used to log the activity of the persistence layer
	 */
	public static final String ACTIVITY_LOG = "dbLog"; // TODO CHECK WHERE THIS IS USED AND IF IT'S OK WITH THE LOGGED CONTENT

	/**
	 * Prefix used to log the automated test content errors
	 */
	public static final String TEST_LOG = "dbTest";
}
