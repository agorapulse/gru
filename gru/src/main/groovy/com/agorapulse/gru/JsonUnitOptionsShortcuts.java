package com.agorapulse.gru;

import net.javacrumbs.jsonunit.core.Option;

/**
 * Provides JsonUnit options' shortcuts for <code>expect</code> block so they can be used without additional imports.
 */
//CHECKSTYLE:OFF
public interface JsonUnitOptionsShortcuts {

    Option TREATING_NULL_AS_ABSENT = Option.TREATING_NULL_AS_ABSENT;
    Option IGNORING_ARRAY_ORDER = Option.IGNORING_ARRAY_ORDER;
    Option IGNORING_EXTRA_FIELDS = Option.IGNORING_EXTRA_FIELDS;
    Option IGNORING_EXTRA_ARRAY_ITEMS = Option.IGNORING_EXTRA_ARRAY_ITEMS;
    @Deprecated Option COMPARING_ONLY_STRUCTURE = Option.COMPARING_ONLY_STRUCTURE;
    Option IGNORING_VALUES = Option.IGNORING_VALUES;

}
