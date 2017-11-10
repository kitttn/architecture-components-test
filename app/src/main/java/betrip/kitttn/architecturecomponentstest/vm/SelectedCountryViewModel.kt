package betrip.kitttn.architecturecomponentstest.vm

import android.arch.lifecycle.ViewModel
import betrip.kitttn.architecturecomponentstest.services.CountryDetailsLoader

/**
 * @author kitttn
 */

class SelectedCountryViewModel(private val detailsLoader: CountryDetailsLoader) : ViewModel() {

}