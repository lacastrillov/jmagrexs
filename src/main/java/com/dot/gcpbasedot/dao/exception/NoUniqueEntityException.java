package com.dot.gcpbasedot.dao.exception;

/**
 * Exception thrown when a search for a unique entity returns more than one.
 * 
 * @author lacastrillov@gmail.com
 * 
 */
public class NoUniqueEntityException extends RuntimeException {

	/**
	 * Serial nr.
	 */
	private static final long serialVersionUID = 1L;

	public NoUniqueEntityException(Exception e) {
		super(e);
	}
}
