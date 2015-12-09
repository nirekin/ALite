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



/**
 * Factory used to get Projection that are used as a kind of selection in a Criteria query.
 * 
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class Projections {

	/**
	 * Adds attribute value projection
	 *
	 * @param attr the name of the attribute whose values should add to the result set
	 * @return Projection
	 */
	public static Projection attribute(String attr){
		return new ProjAttribute(attr);
	}
	
	/**
	 * Creates a distinct projection
	 *
	 * @return Projection
	 */
	public static Projection distinct(){
		return new ProjDistinct();
	}
	
	/**
	 * Specifies the class used to implement the projection results
	 * <p>
	 * This implementation class will be ignored if the criteria contains aggregated projections 
	 * 
	 * @param clazz the class
	 */
	public static Projection implementationClass(Class<?> clazz){
		return new ProjImplementationClass(clazz);
	}
}

/**

Hibernate: select count(this_.name) as y0_, count(*) as y1_, count(distinct this_.name) as y2_, avg(this_.type) as y3_, max(this_.type) as y4_, min(this_.name) as y5_, max(this_.name) as y6_, min(this_.type) as y7_, sum(this_.type) as y8_, sum(this_.name) as y9_ from application_user this_ where this_.name like ?

 

.setProjection( Projections.projectionList()
                .add( Projections.count("name"))
                .add( Projections.rowCount())
                .add( Projections.countDistinct("name"))
                .add( Projections.avg("type"))
                .add( Projections.max("type"))
                .add( Projections.min("name"))
                .add( Projections.max("name"))
                .add( Projections.min("type"))
                .add( Projections.sum("type"))
                .add( Projections.sum("name")) // summ on a string
                )

                o = crit.uniqueResult();

                or

                o = crit.list(); then will return

                o             ArrayList<E>  (id=95)    
                [0]          Object[10]  (id=109)      

)             

 

               

o             Object[10]  (id=106)      
                [0]          Long  (id=117)  
                               value     4            
                [1]          Long  (id=117)  
                               value     4            
                [2]          Long  (id=120)  
                               value     3            
                [3]          Double  (id=121)             
                               value     1.25      
                [4]          "2" (id=123)      
                [5]          "ad" (id=124)    
                [6]          "xd" (id=125)    
                [7]          "1" (id=126)      
                [8]          "5" (id=127)      
                [9]          "0" (id=128)      

 

 

 

 

- - - - - - -  -

 

 

Hibernate: select count(this_.name) as y0_, count(*) as y1_, count(distinct this_.name) as y2_, avg(this_.type) as y3_, max(this_.type) as y4_, min(this_.name) as y5_, max(this_.name) as y6_, min(this_.type) as y7_, sum(this_.type) as y8_, sum(this_.name) as y9_, this_.name as y10_ from application_user this_ where this_.name like ? group by this_.name

 

.setProjection( Projections.projectionList()

                .add( Projections.count("name"))
                .add( Projections.rowCount())
                .add( Projections.countDistinct("name"))
                .add( Projections.avg("type"))
                .add( Projections.max("type"))
                .add( Projections.min("name"))
                .add( Projections.max("name"))
                .add( Projections.min("type"))
                .add( Projections.sum("type"))
                .add( Projections.sum("name"))
                .add( Projections.groupProperty("name")) //GROUP BY

                )

               

o             ArrayList<E>  (id=109)  
                [0]          Object[11]  (id=126)      
                               [0]          Long  (id=129)  
                               [1]          Long  (id=129)  
                               [2]          Long  (id=129)  
                               [3]          Double  (id=131)             
                               [4]          "1" (id=133)      
                               [5]          "ad" (id=134)    
                               [6]          "ad" (id=135)    
                               [7]          "1" (id=137)      
                               [8]          "1" (id=138)      
                               [9]          "0" (id=139)      
                               [10]        "ad" (id=140)    
                [1]          Object[11]  (id=127)      
                               [0]          Long  (id=145)  
                               [1]          Long  (id=145)  
                               [2]          Long  (id=129)  
                               [3]          Double  (id=146)             
                               [4]          "2" (id=147)      
                               [5]          "dd" (id=148)   
                               [6]          "dd" (id=149)   
                               [7]          "1" (id=150)      
                               [8]          "3" (id=151)      
                               [9]          "0" (id=152)      
                               [10]        "dd" (id=153)   

                [2]          Object[11]  (id=128)      
                               [0]          Long  (id=129)  
                               [1]          Long  (id=129)  
                               [2]          Long  (id=129)  
                               [3]          Double  (id=154)             
                               [4]          "1" (id=155)      
                               [5]          "xd" (id=156)    
                               [6]          "xd" (id=157)    
                               [7]          "1" (id=158)      
                               [8]          "1" (id=159)      
                               [9]          "0" (id=160)      
                               [10]        "xd" (id=161)    
              

- - - - - - - - - -


.setProjection( Projections.projectionList()
                .add( Projections.count("name"))
                .add( Projections.rowCount())
                .add( Projections.countDistinct("name"))
                .add( Projections.avg("type"))
                .add( Projections.max("type"))
                .add( Projections.min("name"))
                .add( Projections.max("name"))
                .add( Projections.min("type"))
                .add( Projections.sum("type"))
                .add( Projections.sum("name"))
                .add( Projections.groupProperty("name")) //GROUP BY
                )

or

 

.setProjection( Projections.projectionList()
                .add( Projections.count("name"))
                .add( Projections.rowCount())
                .add( Projections.countDistinct("name"))
                .add( Projections.projectionList()
                               .add(Projections.avg("type"))
                               .add( Projections.max("type"))
                               .add( Projections.min("name"))
                               .add( Projections.max("name"))
                               .add( Projections.min("type"))
                               .add( Projections.groupProperty("name"))
                )
                .add( Projections.sum("type"))
                .add( Projections.sum("name"))
                )

or

 

.setProjection( Projections.projectionList()
                .add( Projections.count("name"))
                .add( Projections.rowCount())
                .add( Projections.countDistinct("name"))
                .add( Projections.projectionList()
                               .add(Projections.avg("type"))
                               .add( Projections.max("type"))
                               .add( Projections.min("name"))
                               .add( Projections.max("name"))
                               .add( Projections.min("type"))
                )

                .add( Projections.sum("type"))
                .add( Projections.sum("name"))
                .add( Projections.groupProperty("name"))
                )

               

                will generate the same query and produce the same result

               

               

- - - - - - - - - - - - - - -

 

To avoid doing a select all

 

.setProjection( Projections.projectionList()
                .add( Projections.property("name"))
                .add( Projections.property("password"))
                )
                ;

                o = crit.list();

                o             ArrayList<E>  (id=111)  
                               [0]          Object[2]  (id=127)        
                                               [0]          "ad" (id=131)    
                                               [1]          "1" (id=132)      
                               [1]          Object[2]  (id=128)        
                                               [0]          "dd" (id=133)   
                                               [1]          "2" (id=134)      
                               [2]          Object[2]  (id=129)        
                                               [0]          "xd" (id=135)    
                                               [1]          "3" (id=136)      
                               [3]          Object[2]  (id=130)        
                                               [0]          "dd" (id=137)   
                                               [1]          "4" (id=138)      
 

- - - - - - - - - - - - - -

 

Hibernate: select this_.name as y0_, this_.password as y1_, min(this_.type) as y2_ from application_user this_ where this_.name like ?

 

.setProjection( Projections.projectionList()

                .add( Projections.property("name"))

                .add( Projections.property("password"))

                .add( Projections.min("type")) // Adding this will produce just one row

                )

               

                o             ArrayList<E>  (id=106)  

                               [0]          Object[3]  (id=126)        

                                               [0]          "ad" (id=127)    

                                               [1]          "1" (id=128)      

                                               [2]          "1" (id=129)      

 

- - - - - - - - - - - - - -

 

Hibernate: select distinct count(distinct this_.type) as y0_ from application_user this_ where this_.name like ?

 

.setProjection( Projections.distinct(Projections.countDistinct("type"))

                )

                ;

                o = crit.list();

               

                o             ArrayList<E>  (id=110)  

                               [0]          Long  (id=127)  

 

 

Hibernate: select count(distinct this_.type) as y0_ from application_user this_ where this_.name like ?

 

.setProjection( Projections.countDistinct("type")

                )

                ;

                o = crit.list();

               

                o             ArrayList<E>  (id=111)  
                               [0]          Long  (id=127)  
                                               value     2            

� 2015 Microsoft T�rminos Privacidad y cookies Desarrolladores Espa�ol

    


*/