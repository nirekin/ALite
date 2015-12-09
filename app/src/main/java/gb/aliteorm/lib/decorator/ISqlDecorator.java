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

package gb.aliteorm.lib.decorator;

/**
 * Common definition of all decorators.
 * <p>
 * If required each type of column can specify it's own implementation.
 * <p>
 * <p>
 * The decorator will be called by ALiteOrm to modify the generated SQL sentences.
 * by, for example, adding quotes to string columns.
 * 
 * 
 * <p>
 * Example:
 * <p>
 * 	<code>table.str1 = abr</code>
 *  <p>
 *  once decorated will be ;
 *  <p>
 *  </code>table.str1 = 'abr'</code>
 *  
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public interface ISqlDecorator {

	/**
	 * Decorates the Object given as parameter and appends the decorated content to a StringBuilder
	 * @param strb the StringBuilder.
	 * @param o the object to decorate
	 */
	public void decorate(StringBuilder strb, Object o);

	/**
	 * Decorates the Object given as parameter and returns its decorated content
	 * @param o the object to decorate
	 * @return the decorated content
	 */
	public String decorate(Object o);
}