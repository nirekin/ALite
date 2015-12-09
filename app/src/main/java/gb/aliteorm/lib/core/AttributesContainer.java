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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import gb.aliteorm.lib.annotation.ALiteElementCollection;
import gb.aliteorm.lib.annotation.ALiteEmbedded;
import gb.aliteorm.lib.annotation.ALiteEmbeddedId;
import gb.aliteorm.lib.annotation.ALiteTransient;
import gb.aliteorm.lib.exception.RWrongElementCollectionLocationException;
import gb.aliteorm.lib.exception.UnsupportedGetterException;
import gb.aliteorm.lib.exception.UnsupportedSupperClassException;

/**
 * Objects containing attributes mapped into the database schema.
 *
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public abstract class AttributesContainer extends VersionableElement{

	private Class<?> implementationClass;
	private AttributesContainer parent;

	protected Hashtable<String, String> override;
	protected ArrayList<Embeddable> embeddeds;
	protected SuperClass sClass;
	protected Method[] ms;

	/**
	 * Indicates if the container allows the use of on @ALiteElementCollection
	 * @return <code>true</code> if the container allows element collections, otherwise <code>false</code>
	 */
	public abstract boolean allowsElementCollection();

	/**
	 * Returns the attribute instance of the applicative object ( entity, superclass, embedded ) corresponding to this container.
	 * <p>
	 *
	 * @param o the applicative object where to look
	 * @return the attribute instance
	 */
	protected abstract Object getTarget(Object o);


	public AttributesContainer(){
		super();
		embeddeds = new ArrayList<Embeddable>();
		override = new Hashtable<String, String>();
	}

	/**
	 * Creates a container for the specified implementation class
	 * @param implementationClass the class implementing the container to create
	 * @param parent the attribute container containing the one to create
	 */
	public AttributesContainer(Class<?> implementationClass, AttributesContainer parent){
		this();
		this.implementationClass = implementationClass;
		this.parent = parent;
		ms = implementationClass.getDeclaredMethods();
		loadVersion(implementationClass);
	}

	/**
	 * Loads the attributes to map into the database for this container
	 * @param destination the table holding the loaded attributes
	 */
	protected void loadAttributes(DBTable destination){
		Method m;
		Annotation ann;
		for (int i = 0; i < ms.length; i++) {
			m = ms[i];

			int modifiers = m.getModifiers();
			if(Modifier.isAbstract(modifiers) || Modifier.isStatic(modifiers))
				continue;

			if(!Modifier.isProtected(modifiers) && !Modifier.isPublic(modifiers))
				continue;

			if(ignoreAttribute(m))
				continue;

			ann = m.getAnnotation(ALiteEmbedded.class);
			if(ann != null)
				continue;

			ann = m.getAnnotation(ALiteEmbeddedId.class);
			if(ann != null)
				continue;

			ann = m.getAnnotation(ALiteElementCollection.class);
			if(hasOneEmbeddedParent()){
				if(ann != null)
					throw new RWrongElementCollectionLocationException(" for the method:" + m.getName() + " of : " + implementationClass.getSimpleName());
			}else{
				if(ann != null){
					if(!allowsElementCollection())
						throw new RWrongElementCollectionLocationException(" for the method:" + m.getName() + " of : " + implementationClass.getSimpleName());
					continue;
				}
			}
			try{
				// The attribute will be created and added to the destination
				new Attribute(destination, this, m);
			}catch(UnsupportedGetterException uge){
				// DO Nothing
			}
		}
	}

	/**
	 * Loads the embedded attributes to map into the database for this container
	 * @param destination the table holding the loaded embedded's attributes
	 */
	protected void loadEmbeddeds(DBTable destination){
		Method m;
		Annotation ann;
		for (int i = 0; i < ms.length; i++) {
			m = ms[i];

			int modifiers = m.getModifiers();
			if(Modifier.isAbstract(modifiers) || Modifier.isStatic(modifiers))
				continue;

			if(!Modifier.isProtected(modifiers) && !Modifier.isPublic(modifiers))
				continue;

			if(ignoreAttribute(m))
				continue;

			ann = m.getAnnotation(ALiteEmbedded.class);
			if(ann != null){
				Embeddable e = new Embeddable(m, this, destination, false);
				embeddeds.add(e);
				destination.updateRequiredVersion(e);
			}

			ann = m.getAnnotation(ALiteEmbeddedId.class);
			if(ann != null){
				Embeddable e = new Embeddable(m, this, destination, true);
				embeddeds.add(e);
				destination.updateRequiredVersion(e);
			}
		}
	}

	/**
	 * Loads the superclass to map into the database for this container
	 * @param destination the table holding the loaded superclass's attributes
	 */
	protected void loadMappedSupper(DBTable destination){
		Class<?> c = implementationClass.getSuperclass();
		if(c != null){
			try{
				sClass = new SuperClass(c, this, destination);
				if(sClass != null){
					destination.updateRequiredVersion(sClass);
				}
			}catch(UnsupportedSupperClassException uuse){
				// Do Nothing
			}
		}
	}

	/**
	 * Indicates if the attribute must be ignored or not while looking for the content to map
	 * @param m the getter to access the attribute
	 * @return <code>true</code> if it must be ignored, otherwise <code>false</code>
	 */
	protected boolean ignoreAttribute(Method m){
		Annotation ann;
		if(		"getClass".equalsIgnoreCase(m.getName())
				||
				"equals".equalsIgnoreCase(m.getName())
				||
				m.getName().startsWith("set")
				||
				(
						m.getName().startsWith("get") == false
						&&
						m.getName().startsWith("is") == false
						)
				)
			return true;

		ann = m.getAnnotation(ALiteTransient.class);
		if(ann != null)
			return true;
		return false;
	}

	/**
	 * Returns the class implementing this container
	 * @return the implementation class
	 */
	public Class<?> getImplementationClass(){
		return implementationClass;
	}

	/**
	 * Defines the class implementing this container
	 * @param c the implementation class
	 */
	public void setImplementationClass(Class<?> c){
		implementationClass = c;
		ms = implementationClass.getDeclaredMethods();
		loadVersion(implementationClass);
	}

	/**
	 * Indicates if this container has at least one embedded parent
	 * @return <code>true</code> if this container has at least one embedded parent, otherwise <code>false</code>
	 */
	private boolean hasOneEmbeddedParent(){
		return this instanceof Embeddable || (parent != null && parent.hasOneEmbeddedParent());
	}

	/**
	 * Returns the parent of this container
	 * @return the parent
	 */
	protected AttributesContainer getParent(){
		return parent;
	}

	/**
	 * Adds all the attributes mapped for this container into the array passed as parameter
	 * @param result the array where to add the attributes
	 * @param table the table holding all the attributes of the whole mapped entity
	 */
	protected void addAllYourAttributes(ArrayList<Attribute> result,  DBTable table){
		Iterator<Attribute> itF = table.getAttributes();
		while (itF.hasNext()) {
			Attribute f = (Attribute) itF.next();
			AttributesContainer b = f.getContainer();
			if(b == this){
				result.add(f);
			}
		}

		Iterator<Embeddable> emIt = embeddeds.iterator();
		while (emIt.hasNext()) {
			emIt.next().addAllYourAttributes(result, table);
		}

		if(sClass != null){
			sClass.addAllYourAttributes(result, table);
		}
	}

	/**
	 * Returns the prefix use to name the attributes of this container
	 * @return the prefix
	 */
	public String getContainerPrefix(){
		return parent != null ? parent.getContainerPrefix() : "";
	}

	/**
	 * Returns all complex id of this container
	 * @return all complex id
	 */
	protected ArrayList<Embeddable> getEmbeddedIds(){
		ArrayList<Embeddable> result = new ArrayList<Embeddable>();
		Iterator<Embeddable> it = embeddeds.iterator();
		while (it.hasNext()) {
			Embeddable e = (Embeddable) it.next();
			if(e.isId())
				result.add(e);
			result.addAll(e.getEmbeddedIds());
		}

		if(sClass != null){
			result.addAll(sClass.getEmbeddedIds());
		}
		return result;
	}

	/**
	 * Returns all containers embedded within this container
	 * @return all embedded containers
	 */
	public ArrayList<Embeddable> getEmbeddeds(){
		return embeddeds;
	}

	/**
	 * Returns the superclass of this container
	 * @return the superclass
	 */
	protected SuperClass getSuperCLass(){
		return sClass;
	}

	/**
	 * Returns the list of columns to override for this container
	 * @return the list of columns
	 */
	public Hashtable<String, String> getOverride(){
		return override;
	}

	/**
	 * Returns the root parent for this container
	 * @return the root parent
	 */
	public  AttributesContainer getRootParent(){
		if(parent != null)
			return parent.getRootParent();
		return this;
	}
}