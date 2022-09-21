/*******************************************************************************
 * Copyright (c) 2010-present Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Stuart McCulloch (Sonatype, Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.sisu;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The PostConstruct annotation is used on a method that needs to be executed 
 * after dependency injection is done to perform any initialization. This 
 * method MUST be invoked before the class is put into service. This 
 * annotation MUST be supported on all classes that support dependency 
 * injection. The method annotated with PostConstruct MUST be invoked even 
 * if the class does not request any resources to be injected. Only one 
 * method can be annotated with this annotation. The method on which the 
 * PostConstruct annotation is applied MUST fulfill all of the following 
 * criteria:
 * <p>
 * <ul>
 * <li>The method MUST NOT have any parameters except in the case of 
 * interceptors in which case it takes an InvocationContext object as 
 * defined by the Interceptors specification.</li>
 * <li>The method defined on an interceptor class MUST HAVE one of the 
 * following signatures:
 * <p>
 * void &#060;METHOD&#062;(InvocationContext)
 * <p>
 * Object &#060;METHOD&#062;(InvocationContext) throws Exception
 * <p>
 * <i>Note: A PostConstruct interceptor method must not throw application 
 * exceptions, but it may be declared to throw checked exceptions including 
 * the java.lang.Exception if the same interceptor method interposes on 
 * business or timeout methods in addition to lifecycle events. If a 
 * PostConstruct interceptor method returns a value, it is ignored by 
 * the container.</i>
 * </li>
 * <li>The method defined on a non-interceptor class MUST HAVE the 
 * following signature:
 * <p>
 * void &#060;METHOD&#062;()
 * </li>
 * <li>The method on which PostConstruct is applied MAY be public, protected, 
 * package private or private.</li>
 * <li>The method MUST NOT be static except for the application client.</li>
 * <li>The method MAY be final.</li>
 * <li>If the method throws an unchecked exception the class MUST NOT be put into   
 * service except in the case of EJBs where the EJB can handle exceptions and 
 * even recover from them.</li></ul>
 *
 * @deprecated Avoid this annotation, use pure constructor injection whenever possible.
 */
@Target( value = { ElementType.METHOD } )
@Retention( RetentionPolicy.RUNTIME )
@Documented
@Deprecated
public @interface PostConstruct {
}
