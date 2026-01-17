package org.example.interfaces;

import org.example.exception.ValidationException;

public interface Validatable {
    void validate() throws ValidationException;
}
