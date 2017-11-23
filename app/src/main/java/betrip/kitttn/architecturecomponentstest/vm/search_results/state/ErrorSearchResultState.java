package betrip.kitttn.architecturecomponentstest.vm.search_results.state;

/**
 * @author kitttn
 */

public class ErrorSearchResultState implements SearchResultState {
    public enum ErrorCode {
        NO_NETWORK, OTHER
    }

    public String message = "";
    public ErrorCode error = ErrorCode.OTHER;

    public ErrorSearchResultState(String message, ErrorCode error) {
        this.message = message;
        this.error = error;
    }
}
