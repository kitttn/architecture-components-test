package betrip.kitttn.architecturecomponentstest.vm

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject

/**
 * @author kitttn
 */

class EnteredTextViewModel : ViewModel() {
    private val enteredText = PublishSubject.create<String>()
    private var lastQuery = ""

    fun getEnteredText(): Observable<String> = enteredText

    fun getLastQuery(): Single<String> {
        return Single.just(lastQuery)
    }

    fun textChanged(enteredText: String) {
        lastQuery = enteredText
        this.enteredText.onNext(enteredText)
    }
}