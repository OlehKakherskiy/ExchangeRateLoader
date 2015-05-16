package exceptions;

/**
 * Created by User on 13.05.2015.
 */
public class RequestException extends Exception{

    public RequestException(){
        super();
    }

    public RequestException(String message){
        super(message);
    }
}
