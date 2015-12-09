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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gb.aliteorm.lib.core.Entity;
import gb.aliteorm.lib.exception.RNoEntityException;
import gb.aliteorm.lib.tools.ALiteOrmBuilder;
import gb.aliteorm.lib.tools.Session;
import gb.aliteorm.lib.tools.StringTools;

/**
 * API for retrieving entities using Criterion objects.
 * <p>
 * It allows you to define a variable number of conditions to be placed upon the result set.
 * <p>
 * Session is the factory for Criteria.<br>
 * Restrictions is the factory for Criterion.
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 *
 */
public class Criteria {

	
	
	private Session session;
	private ArrayList<Order> orders;
	private ArrayList<Criterion> criterions;
	private ArrayList<ProjAttribute> projAttributes;
	
	private ProjDistinct distinct;
	private ProjImplementationClass implementationClass;
	
	private SQLiteDatabase  db;
	private Entity entity;
	private int offset, limit;

	/**
	 * Builds a query criteria targeting the given database to select instances of the given class
	 *
	 * @param session the session which uses this criteria
	 * @param database the database to query
	 * @param clazz the Mapped class returned by the select
	 */
	public Criteria(Session session, SQLiteDatabase database, Class<?> clazz){
		this.session = session;
		this.db = database;
		entity = ALiteOrmBuilder.getInstance().getEntity(clazz);
		if(entity == null)
			throw new RNoEntityException("For : " + clazz);
		orders = new ArrayList<Order>();
		criterions = new ArrayList<Criterion>();
		projAttributes = new ArrayList<ProjAttribute>();
	}

	/**
	 * List all instances corresponding to the constraints defined by the criteria
	 *
	 * <p>
	 * This operation cascades to associated instances
	 * 
	 * @return all listed instances
	 */
	@SuppressWarnings("rawtypes")
	public List list(){
		return entity.list(session, this, db, true);
	}

	/**
	 * List all instances corresponding to the constraints defined by the criteria
	 *
	 * <p>
	 * This operation won't cascade to associated instances
	 * 
	 * @return all listed instances
	 */
	@SuppressWarnings("rawtypes")
	public List listShallow(){
		return entity.list(session, this, db, false);
	}

	/**
	 * Return a cursor providing  access to the result set returned by this criteria
	 *
	 * <p>
	 * This operation won't trigger callbacks
	 * <p>
	 * Using this method you are suppose to close the cursor...
	 *  
	 * 
	 * @return the cursor
	 */
	public Cursor getCursor(){
		return entity.getCursor(this, db);
	}
	
	/**
	 * Delete all instances corresponding to the constraints defined by the criteria
	 * <p>
	 * This operation cascades to associated instances
	 * 
	 */
	public void delete(){
		entity.delete(session, this, db);
	}

	/**
	 * Returns the executable SQL sentence corresponding to the constraints defined by the criteria
	 *
	 * @param includeOrder include the orders added to the criteria
	 * @param includeLimit include the limit added to the criteria
	 * @param includeOffset include the offset added to the criteria
	 * 
	 * @return the executable an SQL sequence
	 */
	public String getSql(boolean includeOrder, boolean includeLimit, boolean includeOffset){
		StringBuilder strb =  new StringBuilder()
		.append(getCriterionSql());
		if(includeOrder)
			strb.append(getOrderSql());
		if(includeLimit)
			strb.append(getLimitSql());
		if(includeOffset)
			strb.append(getOffsetSql());
		return strb.toString();
	}

	/**
	 * Builds the "where" part of the constraints
	 *
	 * @return the "where" part of the constraints
	 */
	private String getCriterionSql(){
		if(!criterions.isEmpty()){
			StringBuilder s = new StringBuilder(ISqlString.WHERE_SQL);
			for(int i = 0; i < criterions.size(); i++){
				s.append(criterions.get(i).getSql(entity));
				if(i + 1 < criterions.size())
					s.append(ISqlString.AND_SQL);
			}
			return s.toString();
		}
		return "";
	}

	/**
	 * Builds the "order by" part of the constraints
	 *
	 * @return the "order by" part of the constraints
	 */
	private String getOrderSql(){
		if(!orders.isEmpty()){
			StringBuilder s = new StringBuilder(ISqlString.ORDER_BY_SQL);
			Iterator<Order> it = orders.iterator();
			while (it.hasNext()) {
				s.append(it.next().getSql())
				.append(",");
			}
			return StringTools.removeLastChar(s.toString());
		}
		return "";
	}

	/**
	 * Adds an order constraints to the criteria
	 *
	 * @param order the constraints to add
	 * @return the criteria
	 */
	public Criteria addOrder(Order order){
		orders.add(order);
		return this;
	}

	/**
	 * Adds a criterion to the criteria
	 *
	 * @param criterion the criterion to add
	 * @return the criteria
	 */
	public Criteria add(Criterion criterion){
		criterions.add(criterion);
		return this;
	}
	
	/**
	 * Adds a projection to the criteria
	 *
	 * @param projection the projection to add
	 * @return the criteria
	 */
	public Criteria add(Projection projection){
		if(projection instanceof ProjAttribute)
			projAttributes.add((ProjAttribute)projection);
		if(projection instanceof ProjDistinct )
			distinct =  (ProjDistinct)projection;
		if(projection instanceof ProjImplementationClass)
			implementationClass = (ProjImplementationClass)projection;
		return this;
	}

	/**
	 * Specifies the offset to apply to the query result
	 *
	 * @param offset the result offset
	 * @return the criteria
	 */
	public Criteria setOffset(int offset) {
		if(offset > 0)
			this.offset = offset;
		return this;
	}

	/**
	 * Specifies the limit to apply to the query result
	 *
	 * @param limit the result limit
	 * @return the criteria
	 */
	public Criteria setLimit(int limit) {
		if(limit > 0)
			this.limit = limit;
		return this;
	}

	/**
	 * Returns the SQL limit sentence
	 *
	 * @return the SQL limit sentence
	 */
	private String getLimitSql(){
		if(limit > 0){
			return String.format(ISqlString.LIMIT_SQL, limit);
		}
		return "";
	}

	/**
	 * Returns the SQL offset sentence
	 *
	 * @return the SQL offset sentence
	 */
	private String getOffsetSql(){
		if(offset > 0){
			return String.format(ISqlString.OFFSET_SQL, offset);
		}
		return "";
	}
	
	/**
	 * Returns the SQL select sentence
	 * <p>
	 * If the criteria doesn't have any projections to reduce the columns added
	 * to the result then the select sentence will include all the columns 
	 * of the database table
	 *
	 * @return the SQL select sentence
	 */
	public String getSelect(){
		if(projAttributes.isEmpty()){
			if(distinct == null)
				return ISqlString.SELECT_ALL_SQL;
			else{
				return ISqlString.SELECT_SQL + distinct.getSql(entity) + " (" + entity.getTable().getAllAttributes() + ") FROM ";
			}
		}else{
			StringBuilder strb = new StringBuilder(ISqlString.SELECT_SQL);
			if(distinct != null)
				strb.append(distinct.getSql(entity));
			int l = projAttributes.size();
			for(int i= 0; i < l; i++){
				strb.append(projAttributes.get(i).getSql(entity));
				if(i +1 < l)
					strb.append(",");	
			}
			strb.append(" FROM ");
			return strb.toString();
		}
	}
	
	/**
	 * Indicates whether the criteria has projections or not.
	 * @return <CODE>true</CODE> if it contains projections, <CODE>false</CODE> otherwise.
	 */
	public boolean hasPojections(){
		return !projAttributes.isEmpty();
	}
	
	/**
	 * Indicates whether the criteria has aggregated projections or not.
	 * @return <CODE>true</CODE> if it contains aggregated projections, <CODE>false</CODE> otherwise.
	 */
	public boolean hasAggregatedPojections(){
		// TODO FINISH THIS
		return false;
	}
	
	/**
	 * Returns the number of non aggregated projected attributes  
	 * @return the number
	 */
	public int getAttributesLength(){
		return projAttributes.size();
	}
	
	/**
	 * Returns an iterator on the non aggregated projected attributes
	 * @return the iterator 
	 */
	public Iterator<ProjAttribute> getProjectedAttributes(){
		return projAttributes.iterator();
	}
	
	/**
	 * Returns the projections implementation class
	 * 
	 * @return the implementation class
	 */
	public ProjImplementationClass getImplementationClass(){
		return implementationClass;
	}
}