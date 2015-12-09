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

import java.lang.reflect.Method;

/**
 * Common definition of all CallBack container
 * <p>
 * A callback container will keep a reference of methods to invoke for each entity life cycle's event.
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public interface ICallBackContainer {

	/**
	 * Adds a callback method for a given entity life cycle's event
	 * 
	 * @param c the class identifying the entity life cycle's event invoking the callback
	 * @param m the callback method to invoke
	 */
	public void addCallBack(Class<?> c, Method m);
}
