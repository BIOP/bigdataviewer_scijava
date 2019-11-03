/*-
 * #%L
 * SciJava cache implementation using Google Guava.
 * %%
 * Copyright (C) 2015 - 2017 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package org.scijava.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * {@link CacheService} implementation wrapping a guava {@link Cache}.
 *
 * Small modification by Nicolas Chiaruttini : using weak keys
 * @author Mark Hiner
 */
@Plugin(type = Service.class)
public class GuavaWeakCacheService extends AbstractService implements CacheService {

    private Cache<Object, Object> cache;

    @Override
    public void initialize() {
        cache = CacheBuilder.newBuilder().weakKeys().weakValues().build();
    }

    @Override
    public void put(final Object key, final Object value) {
        cache.put(key, value);
    }

    @Override
    public Object get(final Object key) {
        return cache.getIfPresent(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> V get(final Object key, final Callable<V> valueLoader)
            throws ExecutionException
    {
        return (V) cache.get(key, valueLoader);
    }
}
