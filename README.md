# ALiteOrm

ALiteOrm is a minimalist, lightweight and easy to use ORM framework especially for Android that maps objects to SQLite databases. It will help you develop your applications faster, by taking care of all database related concerns.

ALiteOrm has been built in contrast to existing frameworks or Android ORM's to avoid:

* Any kind of inheritance between the ORM classes and the Android application classes. This to allow you to have a complete control of you OO model without any external constraints
* Modifications into the Android manifest
* Extra configuration files to define the database schema and identify the mapped classes

All the schema generation and class mapping process has been directly inspired by the JPA 2 annotations. But ALiteOrm is not and will never be a complete implementation of JPA 2. 

In order to avoid any confusion ALiteOrm doesn't allow the use of JPA annotation, instead it provides its own annotation set, with a very strong JPA flavor.

The request definition and the execution, the session and transaction management have been designed following Hibernate's concepts.

##Features

* Standardized Android Library 
* Clean and simple integration to your projects with minimal configuration.
* Automatic database schema creation, with support of versions allowing to deploy schema upgrades with subsequent version releases.
* No need to deal with Android's built-in database API.
* Written in pure java, base on annotation processing and reflection.
* Allows the persistence of POJO. Refer to Map getters to learn how to map your methods to the database.
* Support for transactions.
* Many unit tests on almost every feature.
* TODO ADD stuff here request, projection.

##License

AliteOrm is available under the [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) Copyright (c)2015 Guillaume Barré.

```
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
```

##Supported version of Android

ALiteOrm supports Android 4.0 (API level 14) and above.

#Getting started

##Database and schema creation

The entry point to create the database schema is the ***ALiteOrmBuilder*** class. 

To build your database schema you need to provide:

* The Android context of your application.
* The complete path ( location and database file's name ) where to create the database.
* The list of all persistent entities.

This will be done passing an instance of a ***IDBContext*** to the build method of the ALiteOrmBuilder singleton.

```
The following code will create a database on the sdcard called MY_DATABASE.db. 
The schema will contains 3 tables mapping 3 entities : "Employee", "Client" and "Product"

ALiteOrmBuilder.getInstance()
	.build(new IDBContext() {
		@Override
		public IEntityList getEntitiesList() {
			return new IEntityList() {
				@Override
				public List> getEntities() {
					ArrayList> result = new ArrayList>();
					result.add(Employee.class);
					result.add(Client.class);
					result.add(Product.class);
					...
					return result;
				}
			};
		}

		@Override
		public String getDBPath() {
			return "/sdcard/MY_DATABASE.db";
		}

		@Override
		public Context getAndroidContext() {
			return MyActivity.this;
		}
	});
```

The initial database will be create will be created with the version number 1.

___
*NOTE*:

* *The build method of the ALiteOrmBuilder must be called when you start your application.*
* *Why? Because it is not just in charge of building your database; its also responsible of loading everything you need to manipulate the mapped entities.*

* *The best place to make this call can be in your main aplication's activity before starting using ALiteOrm and access the database.*

* *Refer to the chapter <a href="#mappclasses">Map an entity</a> to learn how map your classes to the database.*

___

##Configuration

All the configuration options are available through ALiteOrmBuilder methods.

```
The following code will turn on the log of used SQL instructions and also the log of the ALiteOrm activity
	ALiteOrmBuilder.getInstance()
	.setShowSQL(true)
	.setShowLog(true);
```

---
*NOTE:*

* *Log lines of used SQL instructions will be prefixed with **"dbSql"**.*
* *Log lines of ALireOrm activities will be prefixed with **"dbLog"**.*

___


##Global callback listeners

The ***ALiteOrmBuilder*** also allows you to define global callback listeners to apply to all entities manipulated by ALiteOrm.

```
The following code will add a global callback listener class

	ALiteOrmBuilder.getInstance().addGlobalEntityListener(ListenerGlobal.class);

	public class ListenerGlobal {

		@ALitePrePersist
		void onPrePersist(Object o) {
			...
		}

		@ALitePostPersist
		void onPostPersist(Object o) {
			...
		}

		@ALitePostLoad
		void onPostLoad(Object o) {
			...
		}

		@ALitePreUpdate
		void onPreUpdate(Object o) {
			...
		}

		@ALitePostUpdate
		void onPostUpdate(Object o) {
			...
		}

		@ALitePreRemove
		void onPreRemove(Object o) {
			...
		}

		@ALitePostRemove
		void onPostRemove(Object o) {
			...
		}
	}
```

Refer to the chapter <a href="#usecallback">Use callback methods
		and entity listener classes</a> to learn how to use global callback listener classes.
		
#Supported Types

ALiteOrm supports a limited list of based types and wrappers.

**Supported base type:**

* int
* long
* double
* float
* char
* short
* boolean
* String

**Supported wrappers:**

* java.util.Date
* java.lang.Integer
* java.lang.Long
* java.lang.Double
* java.lang.Float
* java.lang.Character
* java.lang.Short
* java.lang.Boolean
* java.lang.BigInteger
* java.lang.BigDecimal

#ALiteOrm annotations

As said previously ALiteOrm is strongly inspired by the JPA 2 annotations.

One of the main differences between ALiteOrm and JPA is that some annotations can be used only at the class level or at the method level or both.

<a name="ALiteAttributeOverride"></a>
###@ALiteAttributeOverride

Applicable to : **Class** only

**Parameters** of *@ALiteAttributeOverride*:

```
name:    The name of the attribute to override.
column:  The new column name.
```

___
_RESTRICTION:_

* _**@ALiteAttributeOverride** can be used only at the class level for classes annotated with @ALiteEntity, using it in another place won't throw an exception but won't have any effect_.

---

This annotations allows to change the name of the column mapped with an attribute. This permits for example to reference within an entity an embedded class having an attribute with the same name of one of the entity itself.


In the following example the _Embbedded_ class cannot be directly referece into _Table2_ because they both have an attribute "str" and this will throw a **RDuplicateColumnNameException** exception.

To map the reference of _Embbeded_ into _Table2_ we can change the name of the column mapped for the *str* attribute into the _Embedded_ class.

Using the following instruction the *str* attribute of the _Embedded_ class will be mapped to a column name *embedded_str*.


@ALiteAttributeOverride(name="emb.str", column="embedded_str")


```
Example 

	@ALiteEntity
	@ALiteAttributeOverrides(value={
		@ALiteAttributeOverride(name="emb.str", column="embedded_str")
	}
	public class Table2{
		private Embbedded emb;
		private String str;
	
		...
		
		@ALiteEmbedded
		public Embbedded getEmb() {
			return emb;
		}
	}
	
	@ALiteEmbeddable
	public class Embbedded{
		private String str;
		
		...
	}

	
```


<a name="ALiteAttributeOverrides"></a>
###@ALiteAttributeOverrides

Applicable to : **Class** only**Parameters** of *@ALiteAttributeOverrides*:

```
value:  The list of columns to override.
```

___
_RESTRICTION:_

* _**@ALiteAttributeOverrides** can be used only at the class level for classes annotated with @ALiteEntity, using it in another place won't throw an exception but won't have any effect_.

---

This annotations allows to specify the list of attributes to override for the annotated entity.
 
```
Example 

	@ALiteEntity
	@ALiteAttributeOverrides(value={
		@ALiteAttributeOverride(name="emb.str1", column="embedded_string1")
		@ALiteAttributeOverride(name="emb.str2", column="embedded_string2")
		@ALiteAttributeOverride(name="emb.str3", column="embedded_string3")
	}
	public class Table2{
		private Embbedded emb;
		private String str1, str2, str3;
	
		...
		
		@ALiteEmbedded
		public Embbedded getEmb() {
			return emb;
		}
	}
	
	@ALiteEmbeddable
	public class Embbedded{
		private String str1, str2, str3;
		
		...
	}
```


<a name="ALiteColumn"></a>
###@ALiteColumn

Applicable to : **Method** only

**Parameters** of *@ ALiteColumn*:

```
name:             Returns the name of the column in the database, optional, default "".
unique:           Indicates if the column must be unique or not, optional, default false.
nullable:         Indicates if the column can be null or not, optional, default true.
insertable:       Indicates if the column is part of the insert SQL to persist new instances, optional, default true.
updatable:        Indicates if the column is part of the update SQL to modify existing instances, optional, default true.
columnDefinition: The exact an complete SQL sentence to create this column into the database. The use of this parameter will bypass all the automatic construction of the column, optional, default "".
defaultValue:     The default value of this column content, optional, default "".
```
Specifies more details about the database column where is mapped the property corresponding to the annotated getter.

If no ***@ALiteColumn*** annotation is specified, or if it's specified without defining all its parameters, the default values will be applied.

```
Example 

	@ALiteEntity
	public class Table2{
		private String str1, str2, str3, str4, str5, str7;
		private boolean boolean6;
		...
		// str1 will be mapped into a column called "my_str"
		@ALiteColumn(name="my_str")
		public String getStr1() {
			return str1;
		}
		
		// the database column str2 will have a "unique" constraint
		@ALiteColumn(unique=true)
		public String getStr2() {
			return str2;
		}

		// the database column str3 will have a "non null" constraint
		@ALiteColumn(nullable=true)
		public String getStr3() {
			return str3;
		}		
		
		// the database column str4 will not be filled saving the entity, if defined the 
		// "defaultValue" will be inserted.
		@ALiteColumn(insertable =false)
		public String getStr4() {
			return str4;
		}
		
		// the database column str5 won't be modified by update
		@ALiteColumn(updatable =false)
		public String getStr5() {
			return str5;
		}
		
		// the database column will be build using the "columnDefinition" content, 
		// all other attributes, like "name", will be ignored
 		@ALiteColumn(name="blablabla" columnDefinition="my_boolean INTEGER DEFAULT '1' NULL")
		public boolean getBoolean6() {
			return boolean6;
		}

		// if str7 has not been initialized then the database column will be inserted and
		// update using the defined "defaultValue"
		@ALiteColumn(defaultValue ="foo")
		public String getStr7() {
			return str7;
		}
	}
```


<a name="ALiteDBVersion"></a>
###@ALiteDBVersion

Applicable to : **Class**, **Method**

**Parameters** of *@ALiteDBVersion*:

```
versionNumber:  Returns the version in which the annotated object has been added, default 0.
```

Identifies the schema version in which the annotated content has been added.

At the class level this annotation can be used to annotated:

* an <a href="#ALiteEntity">@ALiteEntity</a>
* an <a href="#ALiteEmbeddable">@ALiteEmbeddable</a>
* an <a href="#ALiteMappedSuperclass">@ALiteMappedSuperclass</a>

At the method level this annotation can be used to annotated:

* getter for persisted properties, within entity classes, mapped super classes or embeddable classes
* getter for embedded properties, within entity classes, mapped super classes or embeddable classes
* getter for element collection only withing entity classes

Refer to the chapter <a href="#useversion">Use schema versions</a> to learn how to create schema versions.

```
Add a new entity in the second version of your database schema

	@ALiteEntity
	@ALiteDBVersion(versionNumber=2)
	public class Table2 {

		public Table2(){
		}
		...
	}
```

```
Add in the third version of your schema a new supper class to Table2 entity previously added into the second version

	@ALiteEntity
	@ALiteDBVersion(versionNumber=2)
	public class Table2 extends SuperVersion3{

		public Table2(){
		}
		...
	}
		
	@ALiteMappedSuperclass
	@ALiteDBVersion(versionNumber=3)
	public class SuperVersion3{
		...
	}
```

```
Add in the fourth version of your schema an embedded class to Table2 entity previously added into the second version

	@ALiteEntity
	@ALiteDBVersion(versionNumber=2)
	public class Table2 extends SuperVersion3{
	
		private EmbeddableVersion4 emb;

		public Table2(){
		}
			
		@ALiteEmbedded
		public EmbeddableVersion4 getEmb() {
			return emb;
		}

		public void setEmb(EmbeddableVersion4 emb) {
			this.emb = emb;
		}
		...
	}
		
	@ALiteEmbeddable
	@ALiteDBVersion(versionNumber=4)
	public class EmbeddableVersion4{
		...
	}
```

TODO example for method

<a name="ALiteElementCollection"></a>
###@ALiteElementCollection

Applicable to : **Method** only

**Parameters** of *@ALiteElementCollection*:

```
tableName:        Returns the name of the table where the collection content will be mapped, optional, default "".
collectionClass:  Returns the implementation class of the collection itself, optional, default "".
contentClass:     Returns the implementation class of the collection content, optional, default "".
```
This annotation gives you a simpler way to map a collection of basic types, wrappers or objects marked as **@ALiteEmbeddable**.

---
_EXCEPTION:_

* _Using an **@ALiteElementCollection** defining an interface as generic type without specifying the contentClass will throw a **RWrongCollectionContentTypeException**._

___

Refer to the chapter <a href="#mapcollection">Map collections</a> to learn how use element collections.

TODO example

<a name="ALiteEmbeddable"></a>
###@ALiteEmbeddable

Applicable to : **Class** only

Identifies an embeddable class whose mapping will be applied to the entities that reference it defining a getter annotated with ***@ALiteEmbedded***.

* An embeddable class must have a public no argument constructor
* An embeddable class can inherit from a <a href="#ALiteMappedSuperclass">@ALiteMappedSuperclass </a>
* An embeddable class can contains <a href="#ALiteId">@ ALiteId </a>
* An embeddable class can contains <a href="#ALiteEmbeddedId">@ALiteEmbeddedId </a>
* An embeddable class can contains <a href="#ALiteEmbedded">@ALiteEmbedded </a>
* An embeddable class can be annotated with <a href="#ALiteDBVersion">@ALiteDBVersion </a>

The getter used to access the attribute must be annotated with @ALiteEmbedded, an exception will also be thrown if this annotation is missing.

---
_RESTRICTION:_

* _An embeddable class doesn't support **@ALiteElementCollection**_.

---
_EXCEPTION:_

* _Specifying a **@ALiteElementCollection** within an embeddable class will throw a **RWrongElementCollectionLocationException**._

___

Refer to the chapter  <a href="#mappclasses">Map an entity</a> to learn how to map embeddable classes to the database.

```
	@ALiteEmbeddable
	public class Embeddable {
		
		public Embeddable(){
		}
		...
	}
```

<a name="ALiteEmbedded"></a>
###@ALiteEmbedded

Applicable to : **Method** only

Apply to a getter this annotation is used to specify a persistent field or property of an entity whose value is an instance of an embeddable class. 

The embeddable class must be annotated at the class level with the <a href="#ALiteEmbeddable">@ ALiteEmbeddable </a> annotation. 

---
_EXCEPTION:_

* _A **RuntimeException** will be thrown if a getter annotated with **@ALiteEmbedded** reference a class not annotated with **@ALiteEmbeddable**._

___

```
Mapping an entity containing an embeddable attribute

	@ALiteEntity
	public class Table2 {

		private Embeddable emb;

		public Table2(){
		}

		@ALiteEmbedded
		public Embeddable getEmb() {
			return emb;
		}

		public void setEmb(Embeddable emb) {
			this.emb = emb;
		}
		...
	}

	@ALiteEmbeddable
	public class Embeddable {
		...
	}
```

<a name="ALiteEmbeddedId"></a>
###@ALiteEmbeddedId

Applicable to : **Method** only

This annotation allows you to define which embeddable class is the composite identifier of your entity.


---
*NOTE:*

* _The composite identifier of an entity can be located anywhere, within the entity's main class itself or within one of the entity's super classes or even within one embedded class of the entity or super classes... The only restriction about the @ALiteEmbeddedId is that you cannot specify more than one per the whole mix of entity/superclass(es)/embeddable(s)..._

---
_EXCEPTION:_

* _Specifying more than one **@ALiteEmbeddedId** will throw a **RMoreThanOneIdException**_.
* _A **RNoIdException** exception will be thrown if the required **@ALiteEmbeddedId** is not specified_.
* _A **RuntimeException** will be thrown if a @ALiteEmbeddedId reference a class not annotated with **@ALiteEmbeddable**._

___

Refer to the chapter <a href="#mappclasses">Map an entity</a> to learn how define an entity id.
	
```
Composite identifier
	
	@ALiteEntity
	public class Table2{
		private MyId id;
		
		public Table2(){
		}
		
		@ALiteEmbeddedId
		public MyId getId() {
			return id;
		}
		
		public void setId(MyId id) {
			this.id = id;
		}
		...
	}
	
    @ALiteEmbeddable
    public class MyId {
        private int val1, val2;
		
		 public MyId(){
		 }
		
		 public int getVal1() {
		     return val1;
		 }
        
        public int getVal1() {
            return val2;
        }
		...
	}

```

<a name="ALiteEntity"></a>
###@ALiteEntity

Applicable to : **Class** only

**Parameters** of *@ALiteEntity*:

```
name:  Returns the optional name of the mapped database table, optional, default "".
```

For each of the Java classes that you want to persist into your database, you will need to add this annotation.

Each class annotated with this annotation will be mapped into its own database table.

* A mapped class must have a public no argument constructor
* By default we will use the class name as name of the related database table
* A mapped class must contains one <a href="#ALiteId">@ALiteId</a> or one <a href="#ALiteEmbeddedId">@ALiteEmbeddedId</a>
* A mapped class can inherit from a <a href="#ALiteMappedSuperclass">@ALiteMappedSuperclass</a>
* A mapped class can reference <a href="#ALiteEmbeddable">@ALiteEmbeddable</a>
* A mapped class can reference <a href="#ALiteElementCollection">@ALiteElementCollection</a>
* A mapped class can be annotated with <a href="#ALiteDBVersion">@ALiteDBVersion</a>
* A mapped class can be annotated with <a href="#ALiteAttributeOverrides">@ALiteAttributeOverrides</a> or <a href="#ALiteAttributeOverride">@ALiteAttributeOverride</a>
* A mapped class can be annotated with <a href="#ALiteEntityListeners">@ALiteEntityListeners</a>
* A mapped class can be annotated with <a href="#ALiteExcludeGlobalListeners">@ALiteExcludeGlobalListeners</a>
* A mapped class can be annotated with <a href="#ALiteExcludeSessionListeners">@ALiteExcludeSessionListeners</a>

Refer to the chapter <a href="#mappclasses">Map an entity</a> to learn how map your classes to the database.

---
_EXCEPTION:_

* _All intents of using the ALiteOrm with classes not annotated with **@ALiteEntity** will throw a **RNoEntityException**._

___

```
The entity will be mapped to a database table named "Table2"

	@ALiteEntity
	public class Table2 {

		public Table2(){
		}
		...
	}
```

```
The entity will be mapped to a database table named "table_2"

	@ALiteEntity(name="table_2")
	public class Table2 {

		public Table2(){
		}
		...
	}
```

<a name="ALiteEntityListeners"></a>
###@ALiteEntityListeners

Applicable to : **Class** only

**Parameters** of *@ALiteEntityListeners*:

```
value:  Returns the list of entity listeners associated to the entity.
```
Specifies callback listener classes to be used for the annotated entity class.

Refer to the chapter <a href="#usecallback">Use callback methods
		and entity listener classes</a> to learn how to use callback listeners on entities.

---
_RESTRICTION:_

* _This annotation can be applied only to entity classes._

___
		
```
Adding a single callback listener class to an entity

	@ALiteEntity
	@ALiteEntityListeners(Listener1.class)
	public class Table2 {

		public Table2(){
		}
		...
	}

	// The callback listener class implementation
	public class Listener1 {

		@ALitePrePersist
		void onPrePersist(Object o) {
			...
		}

		@ALitePostPersist
		void onPostPersist(Object o) {
		   	...
		}

		@ALitePostLoad
		void onPostLoad(Object o) {
	    	...
	    }

		@ALitePreUpdate
		void onPreUpdate(Object o) {
		    ...
		}

		@ALitePostUpdate
		void onPostUpdate(Object o) {
		    ...
		}

		@ALitePreRemove
		void onPreRemove(Object o) {
		    ...
		}

		@ALitePostRemove
		void onPostRemove(Object o) {
		    ...
		}
	}
```

```
Adding multiple callback listener classes to an entity

	@ALiteEntity			
	@ALiteEntityListeners({Listener1.class,Listener2.class})
	public class Table2 {

		public Table2(){
		}
		...
	}
```

<a name="ALiteExcludeGlobalListeners"></a>
###@ALiteExcludeGlobalListeners

Applicable to : **Class** only

Specifies that the annotated entity will ignore the callback listener classes defined at the global level.

Refer to the chapter <a href="#usecallback">Use callback methods
		and entity listener classes</a> to learn how to exclude global listeners.

---
_RESTRICTION:_

* _This annotation can be applied only to entity classes._

___

```
Ignore global callback listener classes for a specified entity

	@ALiteEntity
	@ALiteExcludeGlobalListeners
	public class Table2 {

		public Table2(){
		}
		...
	}
```

<a name="ALiteExcludeSessionListeners"></a>
###@ALiteExcludeSessionListeners

Applicable to : **Class** only

Specifies that the annotated entity will ignore the callback listener classes defined at session level.

Refer to the chapter <a href="#usecallback">Use callback methods
		and entity listener classes</a> to learn how to exclude session listeners.

---
_RESTRICTION:_

* _This annotation can be applied only to entity classes._

___

```
Ignore session callback methods for a specified entity

	@ALiteEntity
	@ALiteExcludeSessionListeners
	public class Table2 {

		public Table2(){
		}
		...
	}
```

<a name="ALiteId"></a>
###@ALiteId

Applicable to : **Method** only

**Parameters** of *@ALiteId*:

```
auto:  Indicates if the simple Id is auto incremental or not, optional, default true.
```

This annotation allows you to define which single property is the unique identifier of your entity.

* By default the auto increment attribute of the @ALiteId is set to "true".
* Using the auto increment set to "true" can be done only with properties of types: **int** or **java.lang.Integer**

---
*NOTE:*

* _The unique id of an entity can be located anywhere, within the entity's main class itself or within one of the entity's super classes or even within one embedded class of the entity or super classes... The only restriction about the @ALiteId is that you cannot specify more than one per the whole mix of entity/superclass(es)/embeddable(s)..._

---
_EXCEPTION:_

* _Specifying more than one **@ALiteId** will throw a **RMoreThanOneIdException**._
* _A **RNoIdException** exception will be thrown if the required **@ALiteID** is not specified._
* _Using the auto increment set to "true" with properties of types other than **int** or **java.lang.Integer** will throw a **RWrongAutoIncrementTypeException**._

___

Refer to the chapter <a href="#mappclasses">Map an entity</a> to learn how define an entity id	

```
Auto increment on int
	
	@ALiteEntity
	public class Table2{
		private int id;
		
		public Table2(){
		}
		
		@ALiteId()
		public int getId() {
			return id;
		}
		
		public void setId(int id) {
			this.id = id;
		}
		...
	}

```

```
Non auto increment on string
	
	@ALiteEntity
	public class Table2 {
		private String id;
		
		public Table2(){
		}
		
		@ALiteId(auto=false)
		public String getId() {
			return id;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		...
	}
```


<a name="ALiteMappedSuperclass"></a>
###@ALiteMappedSuperclass


Applicable to : **Class** only

Identifies a class whose mapping will be applied to the entities that inherit from it.

The mapped superclass doesn't have its own separate database table. 
All columns from the mapped superclass will be mapped to the database table of the entity that inherit from it.

* A mapped super class can contains an @ALiteId
* A mapped super class can contains an @ALiteEmbeddedId
* A mapped super class can inherit from a @ALiteMappedSuperclassclass
* A mapped super class can contains an @ALiteEmbedded
* An embeddable class can be annotated with @ALiteDBVersion

---
*NOTE:*

* *If a mapped class inherits from a superclass that is not annotated with @ALiteMappedSuperclass then this superclass will be ignored by the persistence.*

___
_RESTRICTION:_

* _A mapped superclass doesn't support **@ALiteElementCollection**_.

---
_EXCEPTION:_

* _Specifying a **@ALiteElementCollection** within a mapped superclass will throw a **RWrongElementCollectionLocationException**._

___
Refer to the chapter <a href="#mappclasses">Map an entity</a> to learn how map your super classes to the database.

```
Simple inheritance

	@ALiteEntity
	public class Table2 extends SuperTable2{

		public Table2(){
		}
		...
	}

	@ALiteMappedSuperclass
	public class SuperTable2 {
		...
	}
```

```
Multiple levels of inheritance are possibles, there is no limitation regarding the number of levels

	@ALiteEntity
	public class Table2 extends SuperTable2{

		public Table2(){
		}
		...
	}

	@ALiteMappedSuperclass
	public class SuperTable2 extends SuperSuperTable2{
		...
	}

	@ALiteMappedSuperclass
	public class SuperSuperTable2{
		...
	}
```

<a name="ALitePostLoad"></a>
###@ALitePostLoad


Applicable to : **Method** only

Is used to specify a callback method called after an entity has been retrieved from the database. 

This annotation may be applied to methods of an entity class or a callback listener class.

Refer to the chapter <a href="#usecallback">Use callback methods
		and entity listener classes</a> to learn how to use methods of callback listeners.
		
<a name="ALitePostPersist"></a>
###@ALitePostPersist


Applicable to : **Method** only

Is used to specify a callback method called after storing a new entity into the database. 

This annotation may be applied to methods of an entity class or a callback listener class.

Refer to the chapter <a href="#usecallback">Use callback methods
		and entity listener classes</a> to learn how to use methods of callback listeners.

<a name="ALitePostRemove"></a>
###@ALitePostRemove


Applicable to : **Method** only

Is used to specify a callback method after an entity has been deleted into the database. 

This annotation may be applied to methods of an entity class or a callback listener class.

Refer to the chapter <a href="#usecallback">Use callback methods
		and entity listener classes</a> to learn how to use methods of callback listeners.
		
<a name="ALitePostUpdate"></a>
###@ALitePostUpdate


Applicable to : **Method** only

Is used to specify a callback method called after an entity has been updated into the database. 

This annotation may be applied to methods of an entity class or a callback listener class.

Refer to the chapter <a href="#usecallback">Use callback methods
		and entity listener classes</a> to learn how to use methods of callback listeners.
		
<a name="ALitePrePersist"></a>
###@ALitePrePersist


Applicable to : **Method** only

Is used to specify a callback method called before a new entity is persisted into the database. 

This annotation may be applied to methods of an entity class or a callback listener class.

Refer to the chapter <a href="#usecallback">Use callback methods
		and entity listener classes</a> to learn how to use methods of global callback listeners.
		
<a name="ALitePreRemove"></a>
###@ALitePreRemove


Applicable to : **Method** only

Is used to specify a callback method before deleting an entity into the database.

This annotation may be applied to methods of an entity class or a callback listener class.

Refer to the chapter <a href="#usecallback">Use callback methods
		and entity listener classes</a> to learn how to use methods of global callback listeners.
		
<a name="ALitePreUpdate"></a>
###@ALitePreUpdate


Applicable to : **Method** only

Is used to specify a callback method called before updating an entity in the database. 

This annotation may be applied to methods of an entity class or a callback listener class.

Refer to the chapter <a href="#usecallback">Use callback methods
		and entity listener classes</a> to learn how to use methods of global callback listeners.
		
<a name="ALiteStringLength"></a>
###@ALiteStringLength


Applicable to : **Method** only

This annotation allows to specify the desired length of the database column mapped with the String corresponding to the annotated getter.

If no @ALiteStringLength annotation is specified, a String will be mapped to a column of type VARCHAR(255);

___
*NOTES:*

*Using* **@ALiteStringLength** *to annotated a getter with a return type other than "String"" will generate no error but will have no effect.*
___

**Parameters** of *@ALiteStringLength*:

```
length:  The desired length of the database column mapped with the String.
```


```
Mapping a specific string to VARCHAR(40) and VARCHAR(255)

	@ALiteEntity
	public class Table2{

		private String fString40, fString;
		private int fInt;

		public Table2(){
		}

		// Will create a database : VARCHAR(40)
		@ALiteStringLength(length=40)
		public String getfString40() {
			return fString40;
		}
	
		// Will create a database : VARCHAR(255)	
		public String getfString() {
			return fString;
		}
			
		// Will have no effect
		@ALiteStringLength(length=40)
		public int getfInt() {
			return fInt;
		}
		...
	}
```
This annotation can also be used to specify the length of a column used to map element collection of type "Collection of String".

In the case of an element collection mapping if no @ALiteStringLength annotation is specified, the "Collection of String" will be mapped into a column of type TEXT.

```
Mapping a specific collection of string to VARCHAR(122) column

	@ALiteEntity
	public class Table2{

		private List names;

		public Table2(){
		}

		@ALiteElementCollection
		@ALiteStringLength(length=122)
		public List getNames() {
			return names;
		}
		...
	}
```

<a name="ALiteTransient"></a>
###@ALiteTransient


Applicable to : **Method** only

Specifies that the property corresponding to the annotated getter is not persistent.

Can be used to annotate methods of entity classes, mapped super classes, or embeddable classes.

#How to

<a name="mappclasses"></a>
##Map an entity

<a name="mapcollection"></a>
##Map collections

<a name="useversion"></a>
##Use schema versions

<a name="usetransaction"></a>
##Use transactions

<a name="makerequests"></a>
##Make requests

<a name="usprojections"></a>
##Use projections

<a name="usecallback"></a>
##Use callback methods and entity listener classes


