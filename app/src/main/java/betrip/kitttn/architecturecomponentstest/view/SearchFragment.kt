package betrip.kitttn.architecturecomponentstest.view

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import betrip.kitttn.architecturecomponentstest.R
import betrip.kitttn.architecturecomponentstest.di.LifecycleAware
import betrip.kitttn.architecturecomponentstest.di.modules.SearchResultsViewModelFactory
import betrip.kitttn.architecturecomponentstest.vm.EnteredTextViewModel
import betrip.kitttn.architecturecomponentstest.vm.SearchResultsViewModel
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject

/**
 * @author kitttn
 */

@LifecycleAware
class SearchFragment : ComponentFragment() {
    companion object {
        private val TAG = "SearchFragment"
    }

    @Inject lateinit var factory: SearchResultsViewModelFactory

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleComponent.inject(this)

        val enteredTextVM = ViewModelProviders.of(this).get(EnteredTextViewModel::class.java)
        val searchResultsVM = ViewModelProviders.of(this, factory).get(SearchResultsViewModel::class.java)

        searchText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.i(TAG, "onTextChanged: Setting $p0 as text...")
                if (p0 != null)
                    enteredTextVM.textChanged(p0.toString())
            }
        })
        //enteredTextVM.enteredText.observe(activity, Observer { searchText.setText(it ?: "") })
    }
}