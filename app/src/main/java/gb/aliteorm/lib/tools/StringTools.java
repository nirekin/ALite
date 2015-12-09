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
// TODO JAVADOC
package gb.aliteorm.lib.tools;

import java.lang.reflect.Method;

/**
 * Tools box to manipulate String.
 *
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class StringTools {

	/**
	 * Removes the last character of the given String.
	 * If it's equal to null or if its length < 1 then the received string will be returned
	 *
	 * @param str the string where to remove the last char
	 * @return the received String without the last character
	 */
	public static String removeLastChar(String str){
		if(str != null && str.length() > 1)
			return str.substring(0, str.length() -1);
		return str;
	}

	/**
	 * Returns the name of the setter matching the received getter.
	 *
	 * Will work only for POJO style setter and getter
	 *
	 * <pre>
	 *  - getXyz(); --> setXyz();
	 *  - isXyz(); --> setXyz();
	 * </pre>
	 *
	 * @param getter the getter
	 * @return the setter's name
	 */
	protected static String getSetterName(Method getter){
		String n = getter.getName();
		if(n.startsWith("get")){
			return "set" + n.substring(3, n.length());
		}else if(n.startsWith("is")){
			return "set" + n.substring(2, n.length());
		}
		return null;
	}
}