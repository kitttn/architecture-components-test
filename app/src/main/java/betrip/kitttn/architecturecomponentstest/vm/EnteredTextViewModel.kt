package betrip.kitttn.architecturecomponentstest.vm

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

/**
 * @author kitttn
 */

class EnteredTextViewModel : ViewModel() {
    val enteredText = MutableLiveData<String>()

    fun textChanged(enteredText: String) = this.enteredText.postValue(enteredText)

    fun textErased() = this.enteredText.postValue("")
}