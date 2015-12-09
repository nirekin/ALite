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
import java.util.Iterator;

import gb.aliteorm.lib.annotation.ALiteMappedSuperclass;
import gb.aliteorm.lib.exception.UnsupportedSupperClassException;

/**
 * Superclass whose mapping will be applied to the entity that inherit from it.
 * <p>
 * A superclass must be annotated with <code>@ALiteMappedSuperclass</code>, if this
 * annotation is missing an exception will be thrown ALiteOrm and the superclass won't
 * be include into the mapping.
 * <p>
 * The mapped superclass doesn't have its own separate database table.<br>
 * All columns from the superclass will be mapped to the database table of the entity that inherit from it.
 * <p>
 *	<ul>
 *		<li>A mapped superclass can contains an <code>@ALiteId</code></li>
 *		<li>A mapped superclass can contains an <code>@ALiteEmbeddedId</code></li>
 *		<li>A mapped superclass can inherit from a <code>@ALiteMappedSuperclass</code> </li>
 *		<li>A mapped superclass can contains an <code>@ALiteEmbedded</code></li>
 *	</ul>
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class SuperClass extends AttributesContainer{

	/**
	 * Creates a new superclass
	 *
	 * @param implementationClass the superclass
	 * @param parentContainer the container that inherits from the superclass
	 * @param destination the table holding the superclass's attributes
	 * @throws UnsupportedSupperClassException will be thrown if the superclass in not annotated with <code>@ALiteMappedSuperclass</code>
	 */
	public SuperClass(Class<?> implementationClass, AttributesContainer parentContainer, DBTable destination) throws UnsupportedSupperClassException{
		super(implementationClass, parentContainer);

		checkAnnotation();
		loadMappedSupper(destination);
		loadAttributes(destination);
		loadEmbeddeds(destination);
	}

	/**
	 * Checks the annotation coherence
	 * @throws UnsupportedSupperClassException will be thrown if the superclass in not annotated with <code>@ALiteMappedSuperclass</code>
	 */
	private void checkAnnotation() throws UnsupportedSupperClassException{
		Annotation ann = getImplementationClass().getAnnotation(ALiteMappedSuperclass.class);
		if(ann == null)
			throw new UnsupportedSupperClassException("Missing ALiteMappedSuperclass annotation for : " + getImplementationClass().getName());
	}

	/**
	 * Completes the initialization a persistent instance and also all instances of its embedded attributes
	 * @param o the persistent instance to complete
	 */
	protected void loadEmptyEntity(Object o){
		Iterator<Embeddable> itE = embeddeds.iterator();
		while (itE.hasNext()) {
			itE.next().loadEmptyEntity(o);
		}
		if(sClass != null){
			sClass.loadEmptyEntity(o);
		}
	}

	@Override
	public Object getTarget(Object o){
		return getParent().getTarget(o);
	}

	@Override
	public boolean allowsElementCollection() {
		return false;
	}
}