/*
 * Copyright (C) 2009 Stefano Sanna
 * 
 * gerdavax@gmail.com - http://www.gerdavax.it
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
package it.gerdavax.android.bluetooth.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ReflectionUtils {
	
	private ReflectionUtils() {
		
	}
	
	public static void printMethods(Class myClass) {
    	Constructor[] constructors = myClass.getConstructors();
    	
    	for (int i = 0; i < constructors.length; i++) {
    		System.out.println(constructors[i].toString());
    	}
    	
    	Method[] methods = myClass.getMethods();
    	
    	for (int i = 0; i < methods.length; i++) {
    		System.out.println(methods[i].toString());
    	}
    }
	
	public static void explorePackage(Class myClass) {
		
	}
}
