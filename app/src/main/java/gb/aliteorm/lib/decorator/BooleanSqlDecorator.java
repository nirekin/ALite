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
 * Decorator used to decorate "boolean" and "Boolean".
 * <p>
 * This decorator can decorate values like <code>true</code> , <code>false</code>, 0 and 1.
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class BooleanSqlDecorator implements ISqlDecorator{

	@Override
	public void decorate(StringBuilder strb, Object o){
		if(Boolean.parseBoolean("" + o) || "1".equals("" + o))
			strb.append(1);
		else
			strb.append(0);
	}

	@Override
	public String decorate(Object o) {
		if(Boolean.parseBoolean("" + o) || "1".equals("" + o))
			return "1";
		else
			return "0";
	}
}