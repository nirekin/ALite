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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;

/**
 * Implementation of a class used as entity listener.
 * <p>
 * An entity listener is a class defining one or more methods annotated with
 * @PrePersist, @PostPersist, @PostLoad, @PreUpdate, @PostUpdate, @PreRemove, @PostRemove
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class EntityListener implements ICallBackContainer{

	private Class<?> l;
	private Hashtable<Class<?>, Method> cbs;

	/**
	 * Creates a new entity listener.
	 * @param l the listener class
	 */
	public EntityListener(Class<?> l){
		this.l = l;
		cbs = new Hashtable<Class<?>, Method>();
	}

	@Override
	public void addCallBack(Class<?> c, Method m) {
		cbs.put(c, m);
	}

	/**
	 * Invokes a callback method on a entity for a given entity life cycle's event
	 * @param c the class identifying the entity life cycle's event invoking the callback
	 * @param o the persistent instance target of the callback
	 */
	public void runCallBack(Class<?> c, TravelingEntity o){
		if(cbs.containsKey(c)){
			try {
				Method m = cbs.get(c);
				m.setAccessible(true);
				Object ol = l.newInstance(); // TODO CACHING ????
				m.invoke(ol, o.getContent());
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
				throw new RuntimeException("Error calling the callback " + c + " on the content " + o, e);
			}
		}
	}

	/**
	 * Returns the listener class 
	 * @return the class
	 */
	public Class<?> getListener() {
		return l;
	}
}