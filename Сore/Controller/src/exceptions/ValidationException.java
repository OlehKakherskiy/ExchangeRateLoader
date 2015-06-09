package exceptions;


public class ValidationException extends Exception {

    public ValidationException(String text, String paramName) {
        super(text);
        this.paramName = paramName;
    }

    public ValidationException(String text){
        super(text);
    }

    private String paramName;

    public String getParamName() {
        return paramName;
    }
}
