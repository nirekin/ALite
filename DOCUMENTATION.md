#Table of content

* <a href="#1000">Annotations</a>
* <a href="#2000">Database creation</a>
* <a href="#3000">Configuration</a>
* <a href="#4000">Mapping</a>
* <a href="#5000">Supported types</a>
* <a href="#6000">Entities</a>
* <a href="#6500">Ids</a>
* <a href="#7000">Super classes</a>
* <a href="#8000">Embedded classes</a>
* <a href="#9000">Collections</a>
* <a href="#9500">Schema versions</a>
* <a href="#10000">Save data</a>
* <a href="#11000">Update data</a>
* <a href="#12000">Delete data</a>
* <a href="#13000">Transactions</a>
* <a href="#14000">Requests</a>
* <a href="#15000">Projections</a>
* <a href="#16000">Callbacks</a>


<a name ="1000"></a>
#Annotations

As said into the introduction ALiteOrm is strongly inspired by the JPA 2 annotations.

One of the main differences between ALiteOrm and JPA is that some annotations can be used only at the class level or at the method level and very few of them can be used at both levels.

<a name="ALiteAttributeOverride"></a>
##@ALiteAttributeOverride

Applicable to : **Class** only

#####Parameters:
```
name:    The name of the attribute to override.
column:  The new column name.
```

#####Restriction:
* ***@ALiteAttributeOverride*** can be used only at the class level for classes annotated with ***@ALiteEntity***, using it in another place won't generate an exception but will have no effect.

This annotations allows to change the name of the column mapped to an attribute. This permits for example to reference within an entity an embedded class having an attribute with the same name of one of the entity itself.

In the following example the _Embbedded_ class cannot be directly referenced into _Table2_ because they both have an attribute "str" and this will throw a ***RDuplicateColumnNameException*** exception.

To map the reference of _Embbeded_ into _Table2_ we can change the name of the column mapped for the *str* attribute into the _Embedded_ class.

Using the following instruction the *str* attribute of the _Embedded_ class will be mapped to a column name *embedded_str*.

```
@ALiteAttributeOverride(name="emb.str", column="embedded_str")
```

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
##@ALiteAttributeOverrides

Applicable to : **Class** only

#####Parameters:
```
value:  The list of columns to override.
```

#####Restriction:
* ***@ALiteAttributeOverrides***  can be used only at the class level for classes annotated with ***@ALiteEntity***, using it in another place won't generate an exception but will have no effect.

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
##@ALiteColumn

Applicable to : **Method** only

#####Parameters:

```
name:             Returns the name of the column in the database, optional, default "".
unique:           Indicates if the column must be unique or not, optional, default false.
nullable:         Indicates if the column can be null or not, optional, default true.
insertable:       Indicates if the column is part of the insert SQL to persist new instances, optional, default true.
updatable:        Indicates if the column is part of the update SQL to modify existing instances, optional, default true.
columnDefinition: The exact an complete SQL sentence to create this column into the database. The use of this parameter will bypass all the automatic construction of the column, optional, default "".
defaultValue:     The default value of this column content, optional, default "".
```

Specifies more details about the database column where is mapped the attribute corresponding to the annotated getter.

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
##@ALiteDBVersion

Applicable to : **Class**, **Method**

#####Parameters:
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

Refer to this <a href="#9500">chapter</a> to learn how to create schema versions.

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


<a name="ALiteElementCollection"></a>
##@ALiteElementCollection

Applicable to : **Method** only

#####Parameters:
```
tableName:        Returns the name of the table where the collection content will be mapped, optional, default "".
collectionClass:  Returns the implementation class of the collection itself, optional, default "".
contentClass:     Returns the implementation class of the collection content, optional, default "".
```
This annotation gives you a simpler way to map a collection of basic types, wrappers or objects marked as **@ALiteEmbeddable**.

#####Exception:

* Using an ***@ALiteElementCollection*** defining an interface as generic type without specifying the contentClass will throw a ***RWrongCollectionContentTypeException***.


Refer to this <a href="#9000">chapter</a> to learn how use element collections.

TODO example



<a name="ALiteEmbeddable"></a>
##@ALiteEmbeddable

Applicable to : **Class** only

Identifies an embeddable class whose mapping will be applied to the entity that reference it defining a getter annotated with ***@ALiteEmbedded***.

An embeddable class will not have its own database table. The mapped columns of the embeddable class will be added to the entity's table.

* An embeddable class must have a public no argument constructor
* An embeddable class can inherit from a <a href="#ALiteMappedSuperclass">@ALiteMappedSuperclass </a>
* An embeddable class can contains <a href="#ALiteId">@ ALiteId </a>
* An embeddable class can contains <a href="#ALiteEmbeddedId">@ALiteEmbeddedId </a>
* An embeddable class can contains <a href="#ALiteEmbedded">@ALiteEmbedded </a>
* An embeddable class can be annotated with <a href="#ALiteDBVersion">@ALiteDBVersion </a>

The getter used to access the attribute must be annotated with @ALiteEmbedded, an exception will also be thrown if this annotation is missing.

#####Restriction:

* An embeddable class doesn't support ***@ALiteElementCollection***.

#####Exception:

* Specifying a ***@ALiteElementCollection*** within an embeddable class will throw a ***RWrongElementCollectionLocationException***.


Refer to this <a href="#8000">chapter</a> to learn how to map embeddable classes to the database.

```
	@ALiteEmbeddable
	public class Embeddable {
		
		public Embeddable(){
		}
		...
	}
```


<a name="ALiteEmbedded"></a>
##@ALiteEmbedded

Applicable to : **Method** only

Apply to a getter this annotation is used to specify a persistent field or property of an entity whose value is an instance of an embeddable class. 

The embeddable class must be annotated at the class level with the ***<a href="#ALiteEmbeddable">@ALiteEmbeddable </a>*** annotation. 

#####Exception:

* A ***RuntimeException*** will be thrown if a getter annotated with ***@ALiteEmbedded*** reference a class not annotated with ***@ALiteEmbeddable***.


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
##@ALiteEmbeddedId

Applicable to : **Method** only

This annotation allows you to define which embeddable class is the composite identifier of your entity.

An embeddable id will not have its own database table. The mapped columns of the embeddable id will be added to the entity's table.

#####Note:

* The composite identifier of an entity can be located anywhere, within the entity's main class itself or within one of the its super classes or even within one embedded class of the entity or super classes... The only restriction about the ***@ALiteEmbeddedId*** is that you cannot specify more than one per the whole mix of entity/superclass(es)/embeddable(s)...

#####Exception:

* Specifying more than one ***@ALiteEmbeddedId*** will throw a ***RMoreThanOneIdException***.
* A ***RNoIdException*** exception will be thrown if the required ***@ALiteEmbeddedId*** is not specified.
* A ***RuntimeException*** will be thrown if a @ALiteEmbeddedId reference a class not annotated with ***@ALiteEmbeddable***.



Refer to this <a href="#6500">chapter</a> to learn how define an entity ids.
	
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
##@ALiteEntity

Applicable to : **Class** only

#####Parameters:
```
name:  Returns the optional name of the mapped database table, optional, default "".
```

For each of the Java classes that you want to persist into your database, you will need to use this annotation.

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

Refer to this <a href="#6000">chapter</a> to learn how map your classes to the database.

#####Exception:

* All intents of using the ALiteOrm with classes not annotated with ***@ALiteEntity*** will throw a ***RNoEntityException***.


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
##@ALiteEntityListeners

Applicable to : **Class** only

#####Parameters:
```
value:  Returns the list of entity listeners associated to the entity.
```
Specifies callback listener classes to be used for the annotated entity class.

Refer to this <a href="#1600">chapters</a> to learn how to use callback listeners on entities.

#####Restriction:

* This annotation can be applied only to entity classes.

		
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
##@ALiteExcludeGlobalListeners

Applicable to : **Class** only

Specifies that the annotated entity will ignore the callback listener classes defined at the global level.

Refer to this <a href="#16000">chapter</a> to learn how to exclude global listeners.

#####Restriction:

* This annotation can be applied only to entity classes.


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
##@ALiteExcludeSessionListeners

Applicable to : **Class** only

Specifies that the annotated entity will ignore the callback listener classes defined at session level.

Refer to this <a href="#16000">chapter</a> to learn how to exclude session listeners.

#####Restriction:

* This annotation can be applied only to entity classes.


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
##@ALiteId

Applicable to : **Method** only

#####Parameters:
```
auto:  Indicates if the simple Id is auto incremental or not, optional, default true.
```

This annotation allows you to define which single property is the unique identifier of your entity.

* By default the auto increment attribute of the ***@ALiteId*** is set to "true".
* Using the auto increment set to "true" can be done only with properties of types: **int** or **java.lang.Integer**

#####Note:

* The unique id of an entity can be located anywhere, within the entity's main class itself or within one of the entity's super classes or even within one embedded class of the entity or super classes... The only restriction about the ***@ALiteId*** is that you cannot specify more than one per the whole mix of entity/superclass(es)/embeddable(s)...

#####Restriction:

* Specifying more than one ***@ALiteId*** will throw a ***RMoreThanOneIdException***.
* A ***RNoIdException*** exception will be thrown if the required ***@ALiteID*** is not specified.
* Using the auto increment set to "true" with properties of types other than **int** or **java.lang.Integer** will throw a ***RWrongAutoIncrementTypeException***.


Refer to this <a href="#6500">chapter</a> to learn how define an entity id	

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
##@ALiteMappedSuperclass

Applicable to : **Class** only

Identifies a class whose mapping will be applied to the entities that inherit from it.

The mapped superclass doesn't have its own separate database table. 
All columns from the mapped superclass will be mapped to the database table of the entity that inherit from it.

* A mapped super class can contains an <a href="#ALiteId">@ALiteId</a>
* A mapped super class can contains an <a href="#ALiteEmbeddedId">@ALiteEmbeddedId</a>
* A mapped super class can inherit from a <a href="#ALiteMappedSuperclassclass">@ALiteMappedSuperclassclass</a>
* A mapped super class can contains an <a href="#ALiteEmbedded">@ALiteEmbedded</a>
* An embeddable class can be annotated with <a href="#ALiteDBVersion">@ALiteDBVersion</a>

#####Note:

* If a mapped class inherits from a superclass that is not annotated with ***@ALiteMappedSuperclass*** then this superclass will be ignored by the persistence.

#####Restriction:

* A mapped superclass doesn't support ***@ALiteElementCollection***.

#####Exception:

* Specifying a ***@ALiteElementCollection*** within a mapped superclass will throw a ***RWrongElementCollectionLocationException***.

Refer to this <a href="#7000">chapter</a> to learn how map your super classes to the database.

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
##@ALitePostLoad

Applicable to : **Method** only

Is used to specify a callback method called after an entity has been retrieved from the database. 

This annotation may be applied to methods of an entity class or a callback listener class.

Refer to this <a href="#16000">chapter</a> to learn how to use methods of callback listeners.
		
<a name="ALitePostPersist"></a>
##@ALitePostPersist

Applicable to : **Method** only

Is used to specify a callback method called after storing a new entity into the database. 

This annotation may be applied to methods of an entity class or a callback listener class.

Refer to this <a href="#16000">chapter</a>  to learn how to use methods of callback listeners.

<a name="ALitePostRemove"></a>
##@ALitePostRemove

Applicable to : **Method** only

Is used to specify a callback method called after an entity has been deleted into the database. 

This annotation may be applied to methods of an entity class or a callback listener class.

Refer to this <a href="#16000">chapter</a> to learn how to use methods of callback listeners.
		
<a name="ALitePostUpdate"></a>
##@ALitePostUpdate

Applicable to : **Method** only

Is used to specify a callback method called after an entity has been updated into the database. 

This annotation may be applied to methods of an entity class or a callback listener class.

Refer to this <a href="#16000">chapter</a>  to learn how to use methods of callback listeners.
		
<a name="ALitePrePersist"></a>
##@ALitePrePersist

Applicable to : **Method** only

Is used to specify a callback method called before a new entity is persisted into the database. 

This annotation may be applied to methods of an entity class or a callback listener class.

Refer to this <a href="#16000">chapter</a> to learn how to use methods of callback listeners.
		
<a name="ALitePreRemove"></a>
##@ALitePreRemove

Applicable to : **Method** only

Is used to specify a callback method called before deleting an entity into the database.

This annotation may be applied to methods of an entity class or a callback listener class.

Refer to this <a href="#16000">chapter</a>  to learn how to use methods of callback listeners.
		
<a name="ALitePreUpdate"></a>
##@ALitePreUpdate

Applicable to : **Method** only

Is used to specify a callback method called before updating an entity in the database. 

This annotation may be applied to methods of an entity class or a callback listener class.

RRefer to this <a href="#16000">chapter</a> to learn how to use methods of callback listeners.
		
<a name="ALiteStringLength"></a>
##@ALiteStringLength

Applicable to : **Method** only

This annotation allows to specify the desired length of the database column mapped with the String corresponding to the annotated getter.

#####Parameters:
```
length:  The desired length of the database column mapped with the String.
```

#####Note:

* Using* ***@ALiteStringLength*** to annotated a getter with a return type other than "String"" will generate no error but will have no effect.

#####Restriction:

* If no ***@ALiteStringLength*** annotation is specified, a String will be mapped to a column of type **VARCHAR(255)**;

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

In the case of an element collection mapping if no ***@ALiteStringLength*** annotation is specified, the "Collection of String" will be mapped into a column of type **TEXT**.

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
##@ALiteTransient

Applicable to : **Method** only

Specifies that the property corresponding to the annotated getter is not persistent.

Can be used to annotate methods of entity classes, mapped super classes, or embeddable classes.


<a name ="2000"></a>
#Database creation

The entry point to create the database schema is the ***ALiteOrmBuilder*** class. 

To build your database schema you need to provide:

* The Android context of your application.
* The complete path ( location and database file's name ) where to create the database.
* The list of all persistent entities.

This will be done passing an instance of a ***IDBContext*** to the build method of the ***ALiteOrmBuilder*** singleton.

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

#####Note:
* The build method of the ALiteOrmBuilder must be called when you start your application.
* Why? Because it is not just in charge of building your database; its also responsible of loading everything you need to manipulate the mapped entities.
* The best place to make this call can be in your main aplication's activity before starting using ALiteOrm and access the database.
* Refer to this <a href="#4000"> chapter </a> to learn how map your classes to the database.

<a name ="3000"></a>
#Configuration


All the configuration options are available through ***ALiteOrmBuilder*** methods.

```
The following code will turn on the log of used SQL instructions 
and also the log of the ALiteOrm activity

	ALiteOrmBuilder.getInstance()
	.setShowSQL(true)
	.setShowLog(true);
```

#####Note:
* Log lines of used SQL instructions will be prefixed with **"dbSql"**.
* Log lines of ALireOrm activities will be prefixed with **"dbLog"**.

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

Refer to this <a href="#16000">chapter</a> to learn how to use global callback listener classes.
		


<a name ="4000"></a>
#Mapping
TODO
<a name ="5000"></a>
#Supported types

ALiteOrm supports a limited list of based types and wrappers.


#####Supported base type:
* int
* long
* double
* float
* char
* short
* boolean
* String

#####Supported wrappers:
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

<a name ="6000"></a>
#Entities
TODO

<a name ="6500"></a>
#Ids
TODO

<a name ="7000"></a>
#Super classes
TODO

<a name ="8000"></a>
#Embedded classes
TODO

<a name ="9000"></a>
#Collections
TODO

<a name ="9500"></a>
#Schema versions
TODO

<a name ="10000"></a>
#Save data
Each class annotated with ***<a href="#ALiteEntity">@ALiteEntity</a>*** can be saved using a instance of ***Session***.
#####Exception:

* Any intent to save an instance of a class not anotated with ***@ALiteEntity***  will throw a ***RNoEntityException***.

```
Considering the following entity

	@ALiteEntity
	public class User{
		...
	}
```

```
Saving a single instance of an entity

	Session s = new Session();
	User u = new User();
	u.setAge(88);
	...
	
	s.save(u);
	s.close();
```

```
Saving several instances of an entity

	Session s = new Session();
	User u1 = new User();
	u1.setAge(88);
	...
	User u2 = new User();
	u2.setAge(89);
	...
	User u3 = new User();
	u3.setAge(90);
	...
	
	try{
		s.save(u1, u2, u3);
	}catch(BulkProcessException bpe){
		throw new RuntimeException(bpe);
	}
	s.close();
```

```
Saving a list of instances of an entity

	Session s = new Session();
	List<User> users = new List<>();
	users.add(...);
	...
		
	try{
		s.save(users);
	}catch(BulkProcessException bpe){
		throw new RuntimeException(bpe);
	}	
	s.close();
```

<a name ="11000"></a>
#Update data
Each class annotated with ***<a href="#ALiteEntity">@ALiteEntity</a>*** can be updated using a instance of ***Session***.

#####Exception:

* Any intent to update an instance of a class not anotated with ***@ALiteEntity***  will throw a ***RNoEntityException***.

As the "update" of an entity use the same session methods than the "save" please refer to the examples <a href="#10000">here</a>

<a name ="12000"></a>
#Delete data

Each class annotated with ***<a href="#ALiteEntity">@ALiteEntity</a>*** can be deleted using a instance of ***Session***.

#####Exception:

* Any intent to delete an instance of a class not anotated with ***@ALiteEntity***  will throw a ***RNoEntityException***.

#####Note:

* If you try to delete an entity with un uninitialized id then the where condition will be build using the default value(s) of the database column(s) corresponding this the entity primary key.

```
Considering the following entity

	@ALiteEntity
	public class User{
		...
	}
```

```
Deleting a single instance of an entity

	Session s = new Session();
	User u = new User();
	u.setId(88);
	...
	
	s.delete(u);
	s.close();
```

```
Deleting several instances of an entity

	Session s = new Session();
	User u1 = new User();
	u1.setId(88);
	...
	User u2 = new User();
	u2.setId(89);
	...
	User u3 = new User();
	u3.setId(90);
	...
	
	try{
		s.delete(u1, u2, u3);
	}catch(BulkProcessException bpe){
		throw new RuntimeException(bpe);
	}
	s.close();
```

```
Deleting a list of instances of an entity

	Session s = new Session();
	List<User> users = new List<>();
	users.add(...);
	...
		
	try{
		s.delete(users);
	}catch(BulkProcessException bpe){
		throw new RuntimeException(bpe);
	}	
	
	s.close();
```

<a name ="13000"></a>
#Transactions
TODO

<a name ="14000"></a>
#Requests
TODO

<a name ="15000"></a>
#Projections

Even if the code related to Projection has been committed this part is still under development. So please don't used it!

The projections will be documented once completed and tested.


<a name ="16000"></a>
##Callbacks

A callback methods must be annotated using at least one of the following annotations:

* ***<a href="#ALitePrePersist">@ALitePrePersist </a>***
* ***<a href="#ALitePostPersist">@ALitePostPersist </a>***
* ***<a href="#ALitePostLoad">@ALitePostLoad </a>***
* ***<a href="#ALitePreUpdate">@ALitePreUpdate </a>***
* ***<a href="#ALitePostUpdate">@ALitePostUpdate </a>***
* ***<a href="#ALitePreRemove">@ALitePreRemove </a>***
* ***<a href="#ALitePostRemove">@ALitePostRemove </a>***

The same method can be used for several callback events by annotating it with more than one annotation.

###Internal Callback Methods

Internal callback methods are callback methods defined directly within an entity class.

Internal callback methods must fulfill the following specifications:

* Can have any name
* Can have any access level: private, package, protected or public
* Should always take no arguments and return void
* Sould not be static

```
Adding internal callback methods to an entity

		@ALiteEntity
		public class Table2 {

			public Table2(){
			}

			@ALitePrePersist
			private void onPrePersist() {
				...
			}

			@ALitePostPersist
			private void onPostPersist() {
				...
			}

			@ALitePostLoad
			private void onPostLoad() {
				...
			}

			@ALitePreUpdate
			private void onPreUpdate() {
				...
			}

			@ALitePostUpdate
			private void onPostUpdate() {
				...
			}

			@ALitePreRemove
			private void onPreRemove() {
				...
			}

			@ALitePostRemove
			private void onPostRemove() {
				...
			}
		}
```

```
Using the same callback method for more than one event

		@ALiteEntity
		public class Table2 {

			public Table2(){
			}

			@ALitePrePersist
			@ALitePreUpdate
			@ALitePreRemove
			// TODO test this
			private void preProcess() {
				...
			}

			@ALitePostPersist
			private void onPostPersist() {
				...
			}

			@ALitePostLoad
			private void onPostLoad() {
				...
			}
			
			private void onPreUpdate() {
				...
			}

			@ALitePostUpdate
			private void onPostUpdate() {
				...
			}
			
			private void onPreRemove() {
				...
			}

			@ALitePostRemove
			private void onPostRemove() {
				...
			}
		}
```

###External Callback Methods

External callback methods are callback methods defined in a separate entity listener class.

External callback methods must fulfill the following specifications:

* Should have a public no-arg constructor or no constructor at all
* Can have any name
* Can have any access level: private, package, protected or public
* Should return void
* Should take one argument of a type taht matches the entity target of the lifecycle event
* Sould be stateless
* Sould not be static

The entity listener class can be attached :

* As an external listener : To a scpecific entity using the @ALiteEntityListeners annotation within the entity class
* As a session listener : To all entities manipulated within a Session using the method addSessionEntityListener(Class<?> c)
* As aglobal listener : To all entities manipulated by the framework using the method addSessionEntityListener(Class<?> c) of the ALiteOrm singleton

#####Note:

* Adding as entity listener a class that provides no methods annotated with ***@AlitePrePersist***, ***@ALitePostPersist***, ***@ALitePostLoad***, ***@AlitePreUpdate***, ***@AlitePostUpdate***, ***@AlitePreRemove*** or ***@AlitePostRemove*** will generate no error but will have no effect.

* An entity listener class doesn't need to define all annotations ***@AlitePrePersist***, ***@ALitePostPersist***, ***@ALitePostLoad***, ***@AlitePreUpdate***, ***@AlitePostUpdate***, ***@AlitePreRemove*** or ***@AlitePostRemove*** you can just defined the subset required by your application.

```
Adding a single entity listener to an entity

		@ALiteEntity
		@ALiteEntityListeners(Listener1.class)
		public class Table2 {

			public Table2(){
			}
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
Adding multiple entity listeners to an entity

		@ALiteEntity
		@ALiteEntityListeners({Listener1.class,Listener2.class})
		public class Table2 {

			public Table2(){
			}
		}
		

		// First callback listener class implementation
		public class Listener1 {

			@ALitePrePersist
			void onPrePersist(Object o) {
				...
			}
			
			@ALitePostPersist
			void onPostPersist(Object o) {
				...
			}
			...
		}
		
		// Second callback listener class implementation
		public class Listener2 {

			@ALitePrePersist
			void onPrePersist(Object o) {
				...
			}
			
			@ALitePostPersist
			void onPostPersist(Object o) {
				...
			}
			...
		}
```

```
Adding a single entity listener to the session

		Session s = new Session();
		s.addSessionEntityListener(Listener1.class);
				

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
			...
		}
```

```
Adding multiple entity listeners to the session

		Session s = new Session();
		s.addSessionEntityListener(Listener1.class)
		.addSessionEntityListener(Listener2.class);
				

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
			...

		}
		
		// The callback listener class implementation
		public class Listener2 {

			@ALitePrePersist
			void onPrePersist(Object o) {
				...
			}
			
			@ALitePostPersist
			void onPostPersist(Object o) {
				...
			}
			...
		}
```

```
Removing an entity listener from the session

		Session s = new Session();
		s.removeSessionEntityListener(Listener1.class);
				

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
			...
		}
```

```
Adding a global entity listener to the whole framwork
		ALiteOrmBuilder.getInstance().addGlobalEntityListener(Listener1.class);

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
			...
		}
```

```
Adding multiple global entity listeners to the whole framework

		ALiteOrmBuilder.getInstance()
		.addGlobalEntityListener(Listener1.class)
		.addGlobalEntityListener(Listener2.class);
				

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
			...
		}
		
		// The callback listener class implementation
		public class Listener2 {

			@ALitePrePersist
			void onPrePersist(Object o) {
				...
			}
			
			@ALitePostPersist
			void onPostPersist(Object o) {
				...
			}
			...
		}
```

```
Removing a global entity listener

		ALiteOrmBuilder.getInstance()
		.removeGlobalEntityListener(Listener1.class);
				

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
			...
		}
```

###Execution sequence

When en event occurs on an entity ALiteOrm will invoke the callback methods in the following order.

* Internal Callback Methods
* External listeners' methods (1)
* Session listeners' methods (1)
* Global listeners' methods (1)

(1) In case of multiples listeners the will be invoke base on the order they were added as parameter to the ***@ALiteEntityListeners*** annotation oro to the Session or ***ALiteOrmBuilder*** using the methods **addSessionEntityListener** or **addGlobalEntityListener**

###Exclusion of entity listeners

You can desactivate the invocation of all Session listeners and Global listeners for a specific entity using the annotations ***@ALiteExcludeSessionListeners*** and ***@ALiteExcludeGlobalListeners***.

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












