package betrip.kitttn.architecturecomponentstest.vm.selected_country.state;

/**
 * @author kitttn
 */

public class ErrorSelectedCountryState implements SelectedCountryState {
    public enum ErrorCode {
        NO_NETWORK, OTHER
    }

    public String message = "";
    public ErrorCode error = ErrorCode.OTHER;

    public ErrorSelectedCountryState(String message, ErrorCode error) {
        this.message = message;
        this.error = error;
    }
}
