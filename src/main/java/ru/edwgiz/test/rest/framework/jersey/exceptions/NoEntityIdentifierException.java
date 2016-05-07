package ru.edwgiz.test.rest.framework.jersey.exceptions;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

public class NoEntityIdentifierException extends WebApplicationException {

    public NoEntityIdentifierException() {
        super("No entity identifier", BAD_REQUEST);
    }

}
