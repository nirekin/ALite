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
 * Specifies that the attribute corresponding to the annotated getter is the unique simple Id of the entity.
 * 
 * @author Guillaume Barré
 * @since 1.0
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ALiteId {
	/**
	 * Indicates if the simple Id is auto incremental or not
	 * <br>
	 * This can be used only on getters returning "int" or "Integer"
	 * @return <code>true</code> if the simple Id is auto incremental, otherwise <code>false</code>
	 */
	boolean auto() default true;
}