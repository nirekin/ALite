#ALiteOrm

ALiteOrm is a minimalist, lightweight and easy to use ORM framework specially built for Android. It will help you develop your applications faster, by taking care of all database related concerns.

ALiteOrm has been built in contrast to existing frameworks or Android ORM's to avoid:

* Any kind of inheritance between the ORM classes and the Android application classes. This to allow you to have a complete control of you OO model without any external constraints
* Modifications into the Android manifest
* Extra configuration files to define the database schema and identify the mapped classes

All the schema generation and class mapping process has been directly inspired by the JPA 2 annotations. But ALiteOrm is not and will never be a complete implementation of JPA 2. In order to avoid any confusion ALiteOrm doesn't allow the use of JPA annotation, instead it provides its own annotation set, with a very strong JPA flavor.

The request definition and the execution, the session and transaction management have been designed following Hibernate's concepts.

###Features

* Standardized Android Library 
* Clean and simple integration to your projects with minimal configuration.
* Automatic database schema creation, with support of versions allowing to deploy schema upgrades with subsequent version releases.
* No need to deal with Android's built-in database API.
* Written in pure java, base on annotation processing and reflection.
* Allows the persistence of POJO.
* Support for transactions.


###License

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

###Supported version of Android

ALiteOrm supports Android 4.0 (API level 14) and above.

#Getting started

###Annotated your classes

```
@ALiteEntity
public class User {
    
    private int age, id;
    private String firstName, lastName;
    
    public User(){
    }

    @ALiteId
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
```

###Register your entities and build your database

```
ALiteOrmBuilder.getInstance()
                .build(new IDBContext() {
                    @Override
                    public IEntityList getEntitiesList() {
                        return new IEntityList() {
                            @Override
                            public List<Class<?>> getEntities() {
                                ArrayList<Class<?>> result = new ArrayList<Class<?>>();
                                result.add(User.class);
                                return result;
                            }
                        };
                    }

                    @Override
                    public String getDBPath() {
                        return "/sdcard/myDataBase.db";
                    }

                    @Override
                    public Context getAndroidContext() {
                        return getInstrumentation().getContext();
                    }
                });
```

###Save your data and make requests

####Insert data
```
Session session = new Session();
User u = new User();
u.setAge(88);
u.setFirstName("Roger");
u.setLastName("Moore");
session.save(u);
session.close();
```
####Update data
```
u.setAge(89);
u.setFirstName("Older Roger");
session.save(u);
```

####Delete data
```
session.delete(u);
```

####Look for data

#####Look for all rows into a table 
```
 List l = session.createCriteria(User.class)
 			.addOrder(Order.desc("age"))
			.list();
```
#####Look for some rows into a table 
```
List l = session.createCriteria(User.class)
			.add(Restrictions.like("firstName", "Older"))
			.addOrder(Order.desc("age"))
			.list();
```
#####Look for a specific rows into a table 
```
List l = session.createCriteria(User.class)
			.add(Restrictions.eq("id", 100))
			.list();
```
####Use transactions
```
Session session = new Session();
User u = new User();
...
s.startTransaction().beginTransaction();
try{
	session.save(u);
	s.getTransaction().commit();
}catch(Exception e){
	s.getTransaction().rollback();
}fianlly{
	session.close();
}
```

#You want more

Check the detailed This files must be valid against this [documentation](https://github.com/nirekin/ALiteOrm/blob/master/DOCUMENTATION.md).




