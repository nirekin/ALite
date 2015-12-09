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
 * To persist a Class needs to be annotated with @ALiteEntity.
 * <p>
 * <ul>
 *		<li>A persistent class must have a public no argument constructor</li>
 * 		<li>By default we will use the class name as name of the mapped database table</li>
 * 		<li>The entity class can contains one <code>@ALiteId </code> or <code>@ALiteEmbeddedId</code></li>
 * 		<li>The entity class can contains <code>@ALiteElementCollection</code></li>
 * 		<li>The entity class can reference <code>@ALiteEmbeddable</code> classes</li>
 * 		<li>The entity class can inherit from a <code>@ALiteMappedSuperclass</code> class</li>
 * </ul>
 * 
 * @see ALiteId
 * @see ALiteEmbeddedId
 * @see ALiteElementCollection
 * @see ALiteEmbeddable
 * @see ALiteMappedSuperclass
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ALiteEntity {

	/**
	 * Returns the optional name of the mapped database table
	 * @return the optional name of the mapped database table
	 */
	 String name() default "";
}