package headers;

public class TransmissionErrorException extends Exception {
    public TransmissionErrorException(String message, boolean errorCheck) {
        errorCheck = true;
    }
}
