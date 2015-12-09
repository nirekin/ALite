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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import gb.aliteorm.lib.annotation.ALiteEmbeddable;
import gb.aliteorm.lib.tools.ReflectionTools;

/**
 * Embeddable class whose mapping will be applied to the entity that inherit from it
 * <p>
 * An embeddable class must be annotated with <code>@ALiteEmbeddable</code>, if this
 * annotation is missing an exception will be thrown starting ALiteOrm.
 * <p>
 * The getter used to access the class must be annotated with <code>@ALiteEmbedded</code>, an exception
 * will also be thrown if this annotation is missing.
 * <p>
 * The embeddable class doesn't have its own separate database table.<br>
 * All columns from the embeddable class will be mapped to the database table of the entity that references it.
 * <p>
 *	<ul>
 *		<li>An embeddable class must have a public no argument constructor</li>
 *		<li>An embeddable class can inherit from a <code>@ALiteMappedSuperclass</code></li>
 *		<li>An embeddable class can contains <code>@ALiteId</code></li>
 *		<li>An embeddable class can contains <code>@ALiteEmbeddedId</code></li>
 *		<li>An embeddable class can contains <code>@ALiteEmbedded</code> classes</li>
 *	</ul>
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class Embeddable extends AttributesContainer{

	private Method m;
	private java.lang.reflect.Field embeddedFiel;

	private boolean isId = false;

	/**
	 * Creates an embeddable class
	 * @param m the getter to access the the class
	 * @param parentContainer the container that references the embeddable class
	 * @param destination the table holding the emebedded attributes
	 * @param isId
	 */
	public Embeddable(Method m, AttributesContainer parentContainer, DBTable destination, boolean isId){
		super(m.getReturnType(), parentContainer);
		this.isId = isId;
		this.m = m;
		this.m.setAccessible(true);

		checkAnnotation();
		try{
			embeddedFiel = ReflectionTools.getField(m);
		}catch(Exception ex){
			ex.printStackTrace();
			// Do Nothing
		}
		loadVersion(m);
		loadMappedSupper(destination);
		loadAttributes(destination);
		loadEmbeddeds(destination);
	}

	/**
	 * Checks the annotation coherence
	 */
	private void checkAnnotation(){
		Annotation ann= getImplementationClass().getAnnotation(ALiteEmbeddable.class);
		if(ann == null)
			throw new RuntimeException("Missing ALiteEmbeddable annotation for : " + getImplementationClass().getName());
	}

	/**
	 * Completes the initialization an entity instance and also all instances of its embedded attributes
	 * @param o the entity instance to complete
	 */
	protected void loadEmptyEntity(Object o){
		try{
			Object result = getImplementationClass().newInstance();

			Iterator<Embeddable> itE = embeddeds.iterator();
			while (itE.hasNext()) {
				itE.next().loadEmptyEntity(result);
			}
			if(sClass != null){
				sClass.loadEmptyEntity(result);
			}
			Method setterForEmbeddedContent = ReflectionTools.getSetter(m);
			setterForEmbeddedContent.setAccessible(true);
			setterForEmbeddedContent.invoke(o, result);
		}catch(InstantiationException e){
			throw new RuntimeException("Cannot instanciate " + getImplementationClass(), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Cannot set the embedded content " + getImplementationClass(), e);
		} catch(NoSuchMethodException e) {
			throw new RuntimeException("Cannot set the embedded content " + getImplementationClass(), e);
		} catch(InvocationTargetException e){
			throw new RuntimeException("Cannot set the embedded content " + getImplementationClass(), e);
		}
	}

	@Override
	public Object getTarget(Object o){
		if(o != null){
			try {
				Object t = getParent().getTarget(o instanceof TravelingEntity ? ((TravelingEntity)o).getContent() : o);
				if( t!= null)
					return m.invoke(t);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException("Cannot invoke the method "+ m.getName() + " on " + getImplementationClass(), e);
			}
		}
		return null;
	}

	@Override
	public String getContainerPrefix() {
		String str = getParent() != null ? getParent().getContainerPrefix() + embeddedFiel.getName() + "." : embeddedFiel.getName() + ".";
		return str;
	}

	@Override
	public boolean allowsElementCollection() {
		return false;
	}

	/**
	 * Indicates if the embeddable class is the complex id of the entity who delcares it
	 * @return <code>true</code> if the embeddable class is the complex id, otherwise <code>false</code>
	 */
	public boolean isId(){
		return isId;
	}
}