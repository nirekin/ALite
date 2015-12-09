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

package gb.aliteorm.lib.core;

/**
 * Base implementation of all applicative objects traveling through the ALiteOrm layers.
 * <p>
 * The hierarchy of traveling objects as no business or technical purpose,
 * it has been implemented as wrapper only to clarify the methods' signatures within ALiteOrm.
 *
 * <p>
 * An applicative object can be :
 * <ul>
 *		<li>An instance of entities to persist, delete, save, load...</li>
 *		<li>A simple id  </li>
 *		<li>A complex id</li>
 *		<li>An Element collection</li>
 *	</ul>
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public abstract class TravelingValue {

	private Object content;

	/**
	 * Create a new traveling value
	 * @param content the applicative object who travels
	 */
	public TravelingValue(Object content){
		this.content = content;
	}

	/**
	 * Returns the the applicative object who travels
	 * @return the the applicative object who travels
	 */
	public Object getContent() {
		return content;
	}
}
