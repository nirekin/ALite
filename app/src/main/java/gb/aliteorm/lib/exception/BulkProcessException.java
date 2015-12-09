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

package gb.aliteorm.lib.exception;

import java.util.Hashtable;

/**
 * An exception that indicates that something went wrong while
 * processing a bulk action on several persistent instances.
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class BulkProcessException extends Exception{

	/**
	 *
	 */
	private static final long serialVersionUID = -3740438831856956811L;

	private Hashtable<Object, Throwable> errors;

	public BulkProcessException(Hashtable<Object, Throwable> errors){
		super();
		this.errors = errors;
	}

	/**
	 * Returns a table of all exceptions thrown during the process.
	 * <p>
	 * The key of the table will be the persistent instance at the origin of the problem and the value will be the problem
	 * 
	 * @return the table
	 */
	public Hashtable<Object, Throwable> getErrors() {
		return errors;
	}
}