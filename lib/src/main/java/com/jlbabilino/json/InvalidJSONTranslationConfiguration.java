package com.jlbabilino.json;

/**
 * This exception is thrown when a {@link JSONSerializable} or a
 * {@link JSONDeserializable} type has an invalid configuration of
 * annotations. For example, a type would be invalid if there were no
 * determiner and no constructor marked. Negative array indices would
 * be invalid, etc.
 * 
 * @author Justin Babilino
 */
public class InvalidJSONTranslationConfiguration extends Exception {
    
    /**
     * Creates an {@code InvalidJSONTranslationConfiguration} with a string error message.
     * 
     * @param message the error message
     */
    public InvalidJSONTranslationConfiguration(String message) {
        super(message);
    }
}
