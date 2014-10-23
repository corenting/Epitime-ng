package fr.corenting.epitime_ng.utils;

public class UnsuportedTypeException extends Exception {
    public UnsuportedTypeException()                                { super(); }
    public UnsuportedTypeException(String message)                  { super(message); }
    public UnsuportedTypeException(String message, Throwable cause) { super(message, cause); }
    public UnsuportedTypeException(Throwable cause)                 { super(cause); }
}
