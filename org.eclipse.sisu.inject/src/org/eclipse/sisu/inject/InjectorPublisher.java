/*******************************************************************************
 * Copyright (c) 2010, 2015 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stuart McCulloch (Sonatype, Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.sisu.inject;

import java.util.List;
import java.util.Map;

import org.eclipse.sisu.Internal;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Publisher of {@link Binding}s from a single {@link Injector}; ranked according to a given {@link RankingFunction}.
 */
public final class InjectorPublisher
    implements BindingPublisher
{
    // ----------------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------------

    private static final TypeLiteral<Object> OBJECT_TYPE_LITERAL = TypeLiteral.get( Object.class );

    // ----------------------------------------------------------------------
    // Implementation fields
    // ----------------------------------------------------------------------

    private final Injector injector;

    private final RankingFunction function;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    public InjectorPublisher( final Injector injector, final RankingFunction function )
    {
        this.injector = injector;
        this.function = function;
    }

    public InjectorPublisher( final Injector injector )
    {
        this( injector, injector.getInstance( RankingFunction.class ) );
    }

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    public Injector getInjector()
    {
        return injector;
    }

    public <T> void subscribe( final BindingSubscriber<T> subscriber )
    {
        final TypeLiteral<T> type = subscriber.type();

        publishExactMatches( type, subscriber );

        final Class<?> clazz = type.getRawType();
        if ( clazz != type.getType() )
        {
            publishGenericMatches( type, subscriber, clazz );
        }
        if ( clazz != Object.class )
        {
            publishWildcardMatches( type, subscriber );
        }
    }

    public <T> void unsubscribe( final BindingSubscriber<T> subscriber )
    {
        final Map<Key<?>, ?> ourBindings = injector.getBindings();
        for ( final Binding<T> binding : subscriber.bindings() )
        {
            if ( binding == ourBindings.get( binding.getKey() ) )
            {
                subscriber.remove( binding );
            }
        }
    }

    public int maxBindingRank()
    {
        return function.maxRank();
    }

    @Override
    public int hashCode()
    {
        return injector.hashCode();
    }

    @Override
    public boolean equals( final Object rhs )
    {
        if ( this == rhs )
        {
            return true;
        }
        if ( rhs instanceof InjectorPublisher )
        {
            return injector.equals( ( (InjectorPublisher) rhs ).injector );
        }
        return false;
    }

    @Override
    public String toString()
    {
        return Logs.toString( injector );
    }

    // ----------------------------------------------------------------------
    // Implementation methods
    // ----------------------------------------------------------------------

    private static <T, S> boolean isAssignableFrom( final TypeLiteral<T> type, final Binding<S> binding )
    {
        // don't match the exact implementation as it's already covered by an explicit binding
        final Class<?> implementation = Implementations.find( binding );
        if ( null != implementation && type.getRawType() != implementation )
        {
            return TypeArguments.isAssignableFrom( type, TypeLiteral.get( implementation ) );
        }
        return false;
    }

    private <T> void publishExactMatches( final TypeLiteral<T> type, final BindingSubscriber<T> subscriber )
    {
        final List<Binding<T>> bindings = injector.findBindingsByType( type );
        for ( int i = 0, size = bindings.size(); i < size; i++ )
        {
            final Binding<T> binding = bindings.get( i );
            if ( null == Sources.getAnnotation( binding, Internal.class ) )
            {
                subscriber.add( binding, function.rank( binding ) );
            }
        }
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    private <T, S> void publishGenericMatches( final TypeLiteral<T> type, final BindingSubscriber<T> subscriber,
                                               final Class<S> rawType )
    {
        final List<Binding<S>> bindings = injector.findBindingsByType( TypeLiteral.get( rawType ) );
        for ( int i = 0, size = bindings.size(); i < size; i++ )
        {
            final Binding binding = bindings.get( i );
            if ( null == Sources.getAnnotation( binding, Internal.class ) && isAssignableFrom( type, binding ) )
            {
                subscriber.add( binding, function.rank( binding ) );
            }
        }
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    private <T> void publishWildcardMatches( final TypeLiteral<T> type, final BindingSubscriber<T> subscriber )
    {
        final List<Binding<Object>> bindings = injector.findBindingsByType( OBJECT_TYPE_LITERAL );
        for ( int i = 0, size = bindings.size(); i < size; i++ )
        {
            final Binding binding = bindings.get( i );
            if ( null == Sources.getAnnotation( binding, Internal.class ) && isAssignableFrom( type, binding ) )
            {
                subscriber.add( binding, function.rank( binding ) );
            }
        }
    }
}
