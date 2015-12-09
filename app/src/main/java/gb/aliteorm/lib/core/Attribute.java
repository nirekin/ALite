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
import java.lang.reflect.Type;

import gb.aliteorm.lib.annotation.ALiteColumn;
import gb.aliteorm.lib.exception.UnsupportedGetterException;
import gb.aliteorm.lib.impl.columns.DBColumnFactory;
import gb.aliteorm.lib.impl.columns.IDBColumn;
import gb.aliteorm.lib.tools.ReflectionTools;

/**
 * Implementation of a attribute mapped into the database
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class Attribute extends VersionableElement{

	private IDBColumn c;
	private AttributesContainer container;
	private Method getter, setter = null;
	private DBTable table;
	private String javaAttributeName = null;
	private ALiteColumn ac = null;

	/**
	 * Creates a new attribute
	 *
	 * @param table the table holding the attribute
	 * @param container the container declaring the attribute
	 * @param getter the getter used to access the attribute
	 * @throws UnsupportedGetterException will be thrown if the getter has a return type which is not supported by ALiteOrm
	 */
	public Attribute(DBTable table, AttributesContainer container, Method getter) throws UnsupportedGetterException{
		super();
		this.table = table;
		this.getter = getter;
		this.getter.setAccessible(true);
		this.container = container;
		javaAttributeName = container.getContainerPrefix() + ReflectionTools.getJavaAttributeName(getter);
		loadVersion(getter);
		c = DBColumnFactory.getColumn(table.getTableName(), this, getter);
		ac = (ALiteColumn)getter.getAnnotation(ALiteColumn.class);

		if(getVersion() == null && container.getVersion() != null){
			setVersion(container);
		}
		table.add(this);
	}

	/**
	 * Returns the return type of the getter used to access this attribute
	 * @return the return type of the getter
	 */
	public Type getColumnType(){
		return  (Type)getter.getReturnType();
	}

	/**
	 * Returns the getter to access this attribute
	 * @return the getter 
	 */
	public Method getGetter(){
		return getter;
	}

	/**
	 * Returns the setter matching the getter used to access this attribute
	 * @return the setter 
	 * @throws Exception will be thrown is there is an issue looking for the setter
	 */
	public Method getSetter() throws Exception{
		if(setter == null){
			setter = ReflectionTools.getSetter(getter);
			setter.setAccessible(true);
		}
		return setter;
	}

	/**
	 * Indicates if this attribute is the simple id or part of the complex id of the entity who owns it
	 * @return <code>true</code> if it's id or part of the id, otherwise <code>false</code>
	 */
	protected boolean isId(){
		return c != null && c.isId();
	}

	/**
	 * Indicates if this attribute is auto incremental
	 * @return <code>true</code> if it's auto incremental, otherwise <code>false</code>
	 */
	protected boolean isAutoIncrement(){
		return c != null && c.isId() && c.isAutoIncrement();
	}

	/**
	 * Returns the database column mapped to this attribute
	 * @return the mapped database column
	 */
	public IDBColumn getDBColumn(){
		return c;
	}

	/**
	 * Returns the instance of "applicative" attribute ( entity, superclass, embedded ) declaring this "mapping" attribute.
	 * <p>
	 *
	 * @param o the applicative object where to look for the "applicative" attribute
	 * @return the "applicative" attribute
	 */
	public Object getTarget(Object o){
		return container.getTarget(o instanceof TravelingEntity ? ((TravelingEntity)o).getContent(): o);
	}

	/**
	 * Returns the container declaring this attribute
	 * @return the container
	 */
	public AttributesContainer getContainer(){
		return container;
	}

	/**
	 * Returns the table holding this attribute
	 * @return the table
	 */
	public DBTable getTable() {
		return table;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Attribute == false)
			return false;
		Attribute oC = (Attribute)o;

		return table.getTableName().equalsIgnoreCase(oC.table.getTableName())
				&& getDBName().equalsIgnoreCase(oC.getDBName());
	}

	/**
	 * Returns the name of the model attribute
	 * @return the name 
	 */
	public String getModelName(){
		return javaAttributeName;
	}

	/**
	 * Returns the name of the column into the database which map this attribute
	 * @return the name of the column
	 */
	public String getDBName(){
		if(ac != null && ac.name().trim().length() > 0)
			return ac.name();
		if(container.getRootParent().getOverride().containsKey(javaAttributeName))
			return container.getRootParent().getOverride().get(javaAttributeName);
		if(javaAttributeName.indexOf(".") > 0)
			return javaAttributeName.substring(javaAttributeName.lastIndexOf(".") + 1, javaAttributeName.length());
		return javaAttributeName;
	}
}