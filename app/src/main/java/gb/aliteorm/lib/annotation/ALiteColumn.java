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

package gb.aliteorm.lib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the column detail of the column corresponding to the annotated getter.
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ALiteColumn {

	/**
	 * Returns the name of the column in the database
	 * @return the name
	 */
    String name() default "";

    /**
     * Indicates if the column must be unique or not.
     * @return <code>true</code> is the column is unique, otherwise <code>false</code>
     */
    boolean unique() default false;

    /**
     * Indicates if the column can be null or not.
     * @return <code>true</code> is the column can be null, otherwise <code>false</code>
     */
    boolean nullable() default true;

    /**
     * Indicates if the column is part of the insert SQL to persist new instances.
	 * @return <code>true</code> if this column is part of the insert SQL, otherwise <code>false</code>
     */
    boolean insertable() default true;

    /**
	 * Indicates if the column is part of the update SQL to modify existing instances.
	 * @return <code>true</code> if this column is part of the update SQL, otherwise <code>false</code>
	 */
    boolean updatable() default true;

    /**
     * Returns the exact an complete SQL sentence to create this column into the database.
     * <p>
     * The use of this parameter will bypass all the automatic construction of the column.
     *  
     * @return the SQL to create the column
     */
    String columnDefinition() default "";

    /**
     * Returns the default value of this column content.
     * @return the default
     */
    String defaultValue() default "";
}