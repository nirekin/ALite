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

import gb.aliteorm.lib.core.Entity;

/**
 * Implementation of a class used to implement the projection results
 * 
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class ProjImplementationClass implements Projection{

	private final Class<?> implementationClass;
	

	/**
	 * Create the new projection
	 * @param implementationClass the implementation class
	 */
	protected ProjImplementationClass(Class<?> implementationClass){
		this.implementationClass = implementationClass;
	}

	@Override
	public String getSql(Entity e) {
		// Do Nothing
		return "";
	}
	
	/**
	 * Returns the implementation class
	 * @return the class
	 */
	public Class<?> getImplementationClass(){
		return implementationClass;
	}
}