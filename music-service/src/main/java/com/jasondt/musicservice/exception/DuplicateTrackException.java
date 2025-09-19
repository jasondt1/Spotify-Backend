package com.jasondt.musicservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateTrackException extends RuntimeException {
    public DuplicateTrackException(String message) {
        super(message);
    }
}
