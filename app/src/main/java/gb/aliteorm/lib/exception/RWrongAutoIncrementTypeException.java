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

/**
 * An exception that indicates that a getter annotated with <code>@ALiteId(auto=true)</code> has
 * a return type different than <code>int</code> or <code>Integer</code>.
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class RWrongAutoIncrementTypeException extends RuntimeException{

	/**
	 *
	 */
	private static final long serialVersionUID = -6557233662619011193L;

	public RWrongAutoIncrementTypeException(String msg){
		super(msg);
	}
}
