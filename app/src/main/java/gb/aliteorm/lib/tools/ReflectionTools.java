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

package gb.aliteorm.lib.tools;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import gb.aliteorm.lib.exception.UnsupportedGetterException;

/**
 * Tools box providing helpers mainly used by the reflection to process annotations.
 *
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class ReflectionTools {

	/**
	 * Returns the setter corresponding to the specified getter.
	 *	<p>
	 * Will work only for POJO style setter and getter
	 *
	 * <pre>
	 *  - getXyz(); --> setXyz();
	 *  - isXyz(); --> setXyz();
	 * </pre>
	 *
	 * @param getter the getter
	 * @return the setter
	 * @throws NoSuchMethodException will be thrown if the setter cannot be found
	 */
	public static Method getSetter(Method getter) throws NoSuchMethodException{
		return getter.getDeclaringClass().getMethod(StringTools.getSetterName(getter),  getter.getReturnType());
	}

	/**
	 * Returns the name of the java attribute corresponding to the received getter
	 *
	 * @param getter the getter
	 * @return the name
	 * @throws UnsupportedGetterException will be thrown if the java attribute name cannot be found
	 */
	public static String getJavaAttributeName(Method getter) throws UnsupportedGetterException{
		if(getter == null)
			return "";
		String n = getter.getName();
		return getJavaAttributeName(n);
	}

	/**
	 * Returns the name of the java attribute corresponding to the received getter's name
	 *
	 * @param getter the name of the getter
	 * @return the name
	 * @throws UnsupportedGetterException will be thrown if the java attribute name cannot be found
	 */
	private static String getJavaAttributeName(String getter) throws UnsupportedGetterException{
		if(getter == null || getter.trim().length() == 0)
			return "";
		String f = "";
		if(getter.startsWith("get")){
			f = getter.substring(3, getter.length());
		}else if(getter.startsWith("is")){
			f = getter.substring(2, getter.length());
		}
		if(f.trim().length() > 0){
			return f.substring(0, 1).toLowerCase() + f.substring(1, f.length());
		}
		throw new UnsupportedGetterException("Wrong getter name : " + getter);
	}

	/**
	 * Returns the field corresponding to the received getter.
	 * @param getter
	 * @return the field corresponding to the received getter or <code>null</code> if the field cannot be found
	 */
	public static Field getField(Method getter) {
		try {
			return getter.getDeclaringClass().getDeclaredField(ReflectionTools.getJavaAttributeName(getter));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}