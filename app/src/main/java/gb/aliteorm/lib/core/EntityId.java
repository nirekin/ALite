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
// TODO JAVADOC
package gb.aliteorm.lib.core;

import java.util.ArrayList;

import gb.aliteorm.lib.impl.columns.IDBColumn;

/**
 * Representation of the id of an entity.
 * <p>
 * An id can be simple, defined by @ALiteId or complex, defined by @ALiteEmbeddedId
 *
 * @See ALiteId
 * @See ALiteEmbeddedId
 * 
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class EntityId {

	private IDBColumn[] columns;

	/**
	 * Creates a simple id for an entity having a single primary key
	 * @param c the primary key column
	 */
	public EntityId(IDBColumn c){
		columns = new IDBColumn[]{c};
	}

	/**
	 * Creates a complex id for an entity having a complex primary key, defined using  @ALiteEmbeddedId
	 * @param e the embeddable class defining the complex primary key
	 * @param t the table containing the complex key
	 */
	public EntityId(Embeddable e, DBTable t){
		ArrayList<Attribute> cs = new ArrayList<Attribute>();
		e.addAllYourAttributes(cs, t);
		columns = new IDBColumn[cs.size()];
		for(int i = 0; i < cs.size(); i++){
			columns[i] = cs.get(i).getDBColumn();
		}
	}

	/**
	 * Indicates if this id is complex or not
	 * @return <code>true</code> in case of a complex key, otherwise <code>false</code>
	 */
	public boolean isComplex(){
		return columns.length > 1;
	}

	/**
	 * Returns the columns used to defined the id
	 * @return the columns used to defined the id
	 */
	public IDBColumn[] getColumns(){
		return columns;
	}

	/**
	 * Returns the where clause corresponding to the traveling id
	 * <p>
	 * NOTE : the generated clause dosn't start with "WHERE", it returns only the column definition of the clause
	 * @param id the id
	 * @return the generated where clause
	 */
	public String getWhereOn(TravelingId id){
		return getWhereOn(id, false);
	}

	/**
	 * Returns the where clause corresponding to the received id
	 * <p>
	 * NOTE : the generated clause dosn't start with "WHERE", it returns only the column definition of the clause
	 * @param id the id
	 * @param isJoin indicates if this clause will be use on a joined table
	 * @return the generated where clause
	 */
	public String getWhereOn(TravelingId id, boolean isJoin){
		if(id instanceof TravelingSimpleId)
			return getWhereOn((TravelingSimpleId)id, isJoin);
		else
			return getWhereOn((TravelingComplexId)id, isJoin);
	}

	/**
	 * Returns the where clause corresponding to the received id
	 * <p>
	 * NOTE : the generated clause dosn't start with "WHERE", it returns only the column definition of the clause
	 * @param id the id
	 * @param isJoin indicates if this clause will be use on a joined table
	 * @return the generated where clause
	 */
	private String getWhereOn(TravelingSimpleId id, boolean isJoin){
		return columns[0].builEqualsOnDecoratedBaseElement(id.getContent(), isJoin);
	}

	/**
	 * Returns the where clause corresponding to the received id
	 * <p>
	 * NOTE : the generated clause dosn't start with "WHERE", it returns only the column definition of the clause
	 * @param id the id
	 * @param isJoin indicates if this clause will be use on a joined table
	 * @return the generated where clause
	 */
	private String getWhereOn(TravelingComplexId id, boolean isJoin){
		StringBuilder strb = new StringBuilder(" ");
		for(int i = 0; i < columns.length; i++){
			strb.append(columns[i].builEqualsOnValue(id.getContent(), isJoin));
			if(i + 1 < columns.length)
				strb.append(" AND ");
		}
		return strb.toString();
	}

	/**
	 * Returns the where clause corresponding to the received entity
	 * <p>
	 * NOTE : the generated clause dosn't start with "WHERE", it returns only the column definition of the clause
	 * @param o the entity
	 * @return the generated where clause
	 */
	public String getWhereFor(TravelingEntity o){
		return getWhereOn(extractId(o), false);
	}

	/**
	 * Returns the where clause corresponding to the received entity
	 * <p>
	 * NOTE : the generated clause dosn't start with "WHERE", it returns only the column definition of the clause
	 * @param o the entity
	 * @return the generated where clause
	 */
	public String getJoinWhereFor(TravelingEntity o){
		return getWhereOn(extractId(o), true);
	}

	/**
	 * Extracts the Id of a entity
	 * @param e the entity holding the id to extract
	 * @return the extracted id
	 */
	public TravelingId extractId(TravelingEntity e){
		if(isComplex()){
			Object result = columns[0].getAttribute().getTarget(e.getContent());
			return new TravelingComplexId(result);
		}else{
			Object result = columns[0].getValue(columns[0].getAttribute().getTarget(e.getContent()));
			return new TravelingSimpleId(result);
		}
	}

	/**
	 * Indicates if the given attribute is the simple id or if it's part of the complex id of entity who owns it
	 * @param f the attribute to test
	 * @return <code>true</code> if this attribute is an id attribute, otherwise <code>false</code>
	 */
	public boolean isAnId(Attribute f){
		// TODO OPTIMIZE THIS
		for(int i = 0; i < columns.length; i++){
			if(columns[i].getAttribute().equals(f))
				return true;
		}
		return false;
	}
}
