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

import gb.aliteorm.lib.core.Entity;

/**
 * Representation of a query project that can be added as a kind of selection to a Criteria.
 * <p>
 * Built-in projection types are provided by the Projectionss factory class.
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public interface Projection {

	/**
	 * Translates the projection into a part of an executable an SQL sequence
	 * @return a part of an executable an SQL sequence
	 */
	public String getSql(Entity entity);
}