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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import gb.aliteorm.lib.annotation.ALitePostLoad;
import gb.aliteorm.lib.annotation.ALitePostPersist;
import gb.aliteorm.lib.annotation.ALitePostRemove;
import gb.aliteorm.lib.annotation.ALitePostUpdate;
import gb.aliteorm.lib.annotation.ALitePrePersist;
import gb.aliteorm.lib.annotation.ALitePreRemove;
import gb.aliteorm.lib.annotation.ALitePreUpdate;
import gb.aliteorm.lib.core.ICallBackContainer;

/**
 * Tools to manipulate callback methods
 *
 * @author Guillaume Barré
 * @since 1.0
 *
 */
public class CallBackTools {

	/**
	 * Loads all the callback associated to the method list passed as parameter
	 * @param ms the list of methods eventually annotated as callback
	 * @param receiver the callBack container where to load the callback
	 */
	public static void loadCallbacks(Method[] ms, ICallBackContainer receiver){
		// TODO 1 avoid reading a second time the array of methods here
		Method m;
		Annotation ann;
		for (int i = 0; i < ms.length; i++) {
			m = ms[i];

			ann = m.getAnnotation(ALitePrePersist.class);
			if(ann != null){
				receiver.addCallBack(ALitePrePersist.class, m);
			}

			ann = m.getAnnotation(ALitePreRemove.class);
			if(ann != null){
				receiver.addCallBack(ALitePreRemove.class, m);
			}

			ann = m.getAnnotation(ALitePreUpdate.class);
			if(ann != null){
				receiver.addCallBack(ALitePreUpdate.class, m);
			}

			ann = m.getAnnotation(ALitePostLoad.class);
			if(ann != null){
				receiver.addCallBack(ALitePostLoad.class, m);
			}

			ann = m.getAnnotation(ALitePostPersist.class);
			if(ann != null){
				receiver.addCallBack(ALitePostPersist.class, m);
			}

			ann = m.getAnnotation(ALitePostRemove.class);
			if(ann != null){
				receiver.addCallBack(ALitePostRemove.class, m);
			}

			ann = m.getAnnotation(ALitePostUpdate.class);
			if(ann != null){
				receiver.addCallBack(ALitePostUpdate.class, m);
			}
		}
	}
}
