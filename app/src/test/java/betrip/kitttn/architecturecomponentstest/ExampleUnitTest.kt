package betrip.kitttn.architecturecomponentstest

import betrip.kitttn.architecturecomponentstest.vm.EnteredTextViewModel
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun lastQueryProducesRealLastResult() {
        val enteredTextViewModel = EnteredTextViewModel()
        enteredTextViewModel.textChanged("Hello")

        enteredTextViewModel.getEnteredText()
                .subscribe({ println(it) }, Throwable::printStackTrace)

        enteredTextViewModel.getLastQuery()
                .test()
                .assertValues("Hello")
                .assertComplete()

        enteredTextViewModel.textChanged("World!")

        enteredTextViewModel.getLastQuery()
                .test()
                .assertValues("World!")
                .assertComplete()
    }
}
