package betrip.kitttn.architecturecomponentstest.vm

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

/**
 * @author kitttn
 */

class EnteredTextViewModel : ViewModel() {
    private val enteredText = BehaviorSubject.create<String>()

    fun getEnteredText(): Observable<String> = enteredText

    fun textChanged(enteredText: String) = this.enteredText.onNext(enteredText)
}