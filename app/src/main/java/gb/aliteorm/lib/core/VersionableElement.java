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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import gb.aliteorm.lib.annotation.ALiteDBVersion;

/**
 * Base implementation of all elements used to define the database mapping.
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class VersionableElement {

	protected ALiteDBVersion version;

	/**
	 * Creates a new element
	 */
	public VersionableElement(){
	}

	/**
	 * Loads the database version where this element has been added.
	 * @param clazz the class annotated ( with @ALiteDBVersion ) which implementing this element
	 */
	protected void loadVersion(Class<?> clazz){
		Annotation ann = clazz.getAnnotation(ALiteDBVersion.class);
		if(ann != null)
			version = (ALiteDBVersion)ann;
	}

	/**
	 * Loads the database version where this element has been added
	 * @param m the getter annotated ( with @ALiteDBVersion ) which return this element
	 */
	protected void loadVersion(Method m){
		Annotation ann = m.getAnnotation(ALiteDBVersion.class);
		if(ann != null)
			version = (ALiteDBVersion)ann;
	}

	/**
	 * Returns the database version where this element has been added
	 * @return the database version where this element has been added
	 */
	protected ALiteDBVersion getVersion(){
		return version;
	}

	/**
	 * Sets the version for this element from the one of the container who contains it.
	 * @param model the container who contains the element
	 */
	protected void setVersion(AttributesContainer model){
		this.version = model.getVersion();
	}
}