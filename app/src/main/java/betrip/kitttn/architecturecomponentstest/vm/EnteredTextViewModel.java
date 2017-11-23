package betrip.kitttn.architecturecomponentstest.vm;

import android.arch.lifecycle.ViewModel;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * @author kitttn
 */

public class EnteredTextViewModel extends ViewModel {
    private PublishSubject<String> enteredText = PublishSubject.create();
    private String lastQuery = "";

    public Observable<String> getEnteredText() {
        return enteredText;
    }

    public String getLastQuery() {
        return lastQuery;
    }

    public void textChanged(String newText) {
        this.lastQuery = newText;
        this.enteredText.onNext(newText);
    }
}
