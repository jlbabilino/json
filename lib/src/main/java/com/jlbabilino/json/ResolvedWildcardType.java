/*
 * Copyright (C) 2021 Justin Babilino
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jlbabilino.json;

import static com.jlbabilino.json.ResolvedTypes.asString;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * This internal implementation of {@link WildcardType} is used with
 * {@code ResolvedTypes} when resolving wildcard types.
 * 
 * @see ResolvedTypes
 * @author Justin Babilino
 */
class ResolvedWildcardType implements WildcardType {

    /**
     * The lower bounds
     */
    private final Type[] lowerBounds;
    /**
     * The upper bounds
     */
    private final Type[] upperBounds;

    /**
     * Constructs a {@code ResolvedWildcardType} with lower and upper bounds.
     * 
     * @param lowerBounds the lower bounds
     * @param upperBounds the upper bounds
     */
    ResolvedWildcardType(Type[] lowerBounds, Type[] upperBounds) {
        this.lowerBounds = lowerBounds;
        this.upperBounds = upperBounds;
    }

    /**
     * Gets the lower bounds.
     * 
     * @return the lower bounds
     */
    @Override
    public Type[] getLowerBounds() {
        return lowerBounds;
    }

    /**
     * Gets the upper bounds.
     * 
     * @return the upper bounds
     */
    @Override
    public Type[] getUpperBounds() {
        return upperBounds;
    }

    /**
     * Generates a string representation of this {@code WildcardType}. The resulting {@code String}
     * appears the same as it would in actual Java code, but uses canonical strings exclusively.
     * 
     * @return the string representing this wildcard type
     */
    @Override
    public String toString() {
        StringBuilder boundsStringBuilder = new StringBuilder();
        if (upperBounds.length > 0) {
            boundsStringBuilder.append(" extends ");
            for (int i = 0; i < upperBounds.length - 1; i++) {
                boundsStringBuilder.append(asString(upperBounds[i])).append(" & ");
            }
            boundsStringBuilder.append(upperBounds[upperBounds.length - 1]);
        } else if (lowerBounds.length > 0) {
            boundsStringBuilder.append(" super ");
            for (int i = 0; i < lowerBounds.length - 1; i++) {
                boundsStringBuilder.append(asString(lowerBounds[i])).append(" & ");
            }
            boundsStringBuilder.append(lowerBounds[lowerBounds.length - 1]);
        }
        return "?" + boundsStringBuilder.toString();
    }
}
