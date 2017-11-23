package betrip.kitttn.architecturecomponentstest.vm;

import android.arch.lifecycle.ViewModel;

/**
 * @author kitttn
 */

public class ActivityViewModel extends ViewModel {
    public enum State {
        STARTED, SEARCH_LOADED
    }
    public State currentState = State.STARTED;

    public void openSearch() {
        currentState = State.SEARCH_LOADED;
    }

    public void closeApp() {
        currentState = State.STARTED;
    }
}
