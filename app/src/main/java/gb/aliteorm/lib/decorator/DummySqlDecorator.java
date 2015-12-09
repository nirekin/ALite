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

package gb.aliteorm.lib.decorator;

/**
 * This is the default decorator used by all columns  until they specify their own implementation.
 * <p>
 * This decorator won't change nothing to the content to decorate.
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class DummySqlDecorator implements ISqlDecorator{

	@Override
	public void decorate(StringBuilder strb, Object o){
		strb.append(o);
	}

	@Override
	public String decorate(Object o) {
		return "" + o;
	}
}