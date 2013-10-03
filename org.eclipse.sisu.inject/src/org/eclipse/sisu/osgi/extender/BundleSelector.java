/*******************************************************************************
 * Copyright (c) 2010, 2013 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stuart McCulloch (Sonatype, Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.sisu.osgi.extender;

import org.osgi.framework.Bundle;

/**
 * Pluggable strategy for selecting extendable bundles. <br>
 * <br>
 * Used by {@link ModuleBundleTracker} to decide whether a {@link Bundle} should be relevant for the Sisu Extender or
 * not. <br>
 * <br>
 * Custom implementations are located by {@link ModuleBundleTracker} by checking if a a system property is defined whose
 * name matches the fully qualified name of this class and whose value corresponds to a proper implementation featuring
 * a public default constructor. <br>
 * <br>
 * In order for the extender to be able to load the custom implementation, the custom class must be provided by a
 * fragment attached to the Sisu bundle.
 * 
 * @see ModuleBundleTracker
 * @see DefaultBundleSelector
 */
public interface BundleSelector
{
    /**
     * Tells whether a bundle should be extended or not.
     * 
     * @param bundle the tested bundle
     * @return true if the bundle should be extended, false otherwise
     */
    public boolean select( Bundle bundle );
}