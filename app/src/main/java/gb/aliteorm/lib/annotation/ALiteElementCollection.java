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
 * Defines a collection of instances of a basic type, wrapper or embeddable class.
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ALiteElementCollection {

	/**
	 * Returns the name of the table where the collection content will be mapped
	 * @return the name of the table where the collection content will be mapped
	 */
	String tableName() default "";

	/**
	 * Returns the implementation class of the collection itself
	 * @return the implementation class of the collection itself
	 */
	String collectionClass() default "";

	/**
	 * Returns the implementation class of the collection content
	 * @return the implementation class of the collection content
	 */
	String contentClass() default "";
}