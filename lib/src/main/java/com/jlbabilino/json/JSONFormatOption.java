/*
 * Copyright (C) 2021 Triple Helix Robotics - FRC Team 2363
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

/**
 * This interface contains several subclasses that model JSON formatting
 * options.
 *
 * @author Justin Babilino
 */
public interface JSONFormatOption {

    /**
     * This enum sets the amount of spaces in indents.
     */
    public static enum IndentSpaces implements JSONFormatOption {

        ZERO(0), ONE(1), TWO(2), THREE(3), FOUR(4), SIX(6);

        public final int spaces;

        private IndentSpaces(int spaces) {
            this.spaces = spaces;
        }
    }

    /**
     * This enum changes whether or not each array item begins on a new line.
     */
    public static enum ArrayNewlinePerItem implements JSONFormatOption {

        TRUE(true), FALSE(false);

        public final boolean value;

        private ArrayNewlinePerItem(boolean value) {
            this.value = value;
        }
    }

    /**
     * This enum changes whether or not the initial array declaration begins on a
     * new line.
     */
    public static enum ArrayBeginOnNewline implements JSONFormatOption {

        TRUE(true), FALSE(false);

        public final boolean value;

        private ArrayBeginOnNewline(boolean value) {
            this.value = value;
        }
    }

    /**
     * This enum changes whether or not each keyed entry begins on a new line.
     */
    public static enum ObjectNewlinePerItem implements JSONFormatOption {

        TRUE(true), FALSE(false);

        public final boolean value;

        private ObjectNewlinePerItem(boolean value) {
            this.value = value;
        }
    }

    /**
     * This enum changes whether or not the initial object declaration begins on a
     * new line.
     */
    public static enum ObjectBeginOnNewline implements JSONFormatOption {

        TRUE(true), FALSE(false);

        public final boolean value;

        private ObjectBeginOnNewline(boolean value) {
            this.value = value;
        }
    }
}