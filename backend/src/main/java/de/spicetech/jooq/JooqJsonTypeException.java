package de.spicetech.jooq;

public class JooqJsonTypeException extends RuntimeException {

	private static final long serialVersionUID = 2404577892829543822L;

	public JooqJsonTypeException(String message) {
		super(message);
	}

	public JooqJsonTypeException(String message, Exception e) {
		super(message, e);
	}

}
