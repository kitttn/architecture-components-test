package betrip.kitttn.architecturecomponentstest.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import betrip.kitttn.architecturecomponentstest.R
import betrip.kitttn.architecturecomponentstest.di.LifecycleAware
import betrip.kitttn.architecturecomponentstest.di.modules.Factory
import betrip.kitttn.architecturecomponentstest.view.adapters.CountryNameFlagAdapter
import betrip.kitttn.architecturecomponentstest.vm.*
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

    @Inject lateinit var factory: Factory<SearchResultsViewModel>

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
                if (p0 != null) {
                    enteredTextVM.textChanged(p0.toString())
                    searchResultsVM.startSearch(p0.toString())
                }
            }
        })

        searchResultsRV.layoutManager = LinearLayoutManager(activity)
        val adapter = CountryNameFlagAdapter(mutableListOf())
        searchResultsRV.adapter = adapter

        searchResultsVM.searchResults.observe(activity, Observer {
            when (it) {
                null -> return@Observer
                is SearchResultLoading -> Toast.makeText(activity, "Loading...", Toast.LENGTH_SHORT).show()
                is SearchResultError -> Toast.makeText(activity, "${it.errorCode}; Reason: ${it.errorReason}", Toast.LENGTH_LONG).show()
                is SearchResultSuccess -> {
                    Toast.makeText(activity, "Complete!", Toast.LENGTH_SHORT).show()
                    with(adapter) {
                        countries.clear()
                        countries.addAll(it.data.map { it.name })
                        notifyDataSetChanged()
                    }
                }
            }
        })
    }
}