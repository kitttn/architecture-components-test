package betrip.kitttn.architecturecomponentstest.vm

import android.arch.lifecycle.ViewModel
import io.reactivex.processors.BehaviorProcessor

/**
 * @author kitttn
 */

sealed class NavigationState

class AppStartedState : NavigationState()

class SearchLoadedState : NavigationState()

data class CountryOpenedState(val countryName: String) : NavigationState()

class ActivityViewModel : ViewModel() {
    var currentState: NavigationState = AppStartedState()

    fun openSearch() {
        currentState = SearchLoadedState()
    }

    fun openCountryDetails(countryName: String) {
        currentState = CountryOpenedState(countryName)
    }

    fun closeApp() {
        currentState = AppStartedState()
    }
}