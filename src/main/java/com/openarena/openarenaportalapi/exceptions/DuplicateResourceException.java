//src/main/java/com/openarena/openarenaportalapi/exception/DuplicateResourceException.java
package com.openarena.openarenaportalapi.exceptions;
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}