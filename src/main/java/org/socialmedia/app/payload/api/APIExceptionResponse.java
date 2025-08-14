package org.socialmedia.app.payload.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class APIExceptionResponse {
    private String message;
    private Integer statusCode;
}
