package betrip.kitttn.architecturecomponentstest.view

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Toast
import betrip.kitttn.architecturecomponentstest.R
import betrip.kitttn.architecturecomponentstest.activity.BaseActivity
import betrip.kitttn.architecturecomponentstest.di.LifecycleAware
import betrip.kitttn.architecturecomponentstest.di.modules.Factory
import betrip.kitttn.architecturecomponentstest.plusAssign
import betrip.kitttn.architecturecomponentstest.view.adapters.CountryNameFlagAdapter
import betrip.kitttn.architecturecomponentstest.vm.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author kitttn
 */

@LifecycleAware
class SearchFragment : Fragment(), BackPressHandable {
    companion object {
        private val TAG = "SearchFragment"
    }

    @Inject lateinit var factory: Factory<SearchResultsViewModel>
    private val composite = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_search, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(activity as BaseActivity) {
            lifecycleComponent.inject(this@SearchFragment)
            setSupportActionBar(toolbar)
        }

        val enteredTextVM = ViewModelProviders.of(activity).get(EnteredTextViewModel::class.java)
        val searchResultsVM = ViewModelProviders.of(activity, factory).get(SearchResultsViewModel::class.java)
        Log.i(TAG, "onViewCreated: Factory: $factory")
        Log.i(TAG, "onViewCreated: VM instance: $searchResultsVM")
        Log.i(TAG, "onViewCreated: Fragment $this is created!")

        composite += enteredTextVM.getEnteredText()
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribe({ Log.i(TAG, "onViewCreated: Loading query: $it") }, Throwable::printStackTrace)

        searchResultsRV.layoutManager = LinearLayoutManager(activity)
        val adapter = CountryNameFlagAdapter(mutableListOf())
        searchResultsRV.adapter = adapter

        composite += searchResultsVM.getSearchResults()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ parseSearchResultResponse(it, adapter) }, Throwable::printStackTrace)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.search_menu, menu)

        val menuItem = menu?.findItem(R.id.search) ?: return
        val searchView = menuItem.actionView as? SearchView ?: return

        searchView.maxWidth = Int.MAX_VALUE

        menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                Log.i(TAG, "onMenuItemActionExpand: Expanding...")
                toolbar.setBackgroundResource(R.color.white)
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                toolbar.setBackgroundResource(R.color.colorPrimary)
                return true
            }
        })
    }

    override fun onBackPressProcessed(): Boolean {
        return false
    }

    override fun onStop() {
        super.onStop()
        composite.clear()
        Log.i(TAG, "onStop: Fragment $this is stopped!")
    }

    private fun parseSearchResultResponse(response: SearchResultState, adapter: CountryNameFlagAdapter) {
        Log.i(TAG, "parseSearchResultResponse: Got new VM state: $response")
        when (response) {
            is SearchResultLoading -> refreshLayout.isRefreshing = response.loading
            is SearchResultError -> Toast.makeText(activity, "${response.errorCode}; Reason: ${response.errorReason}", Toast.LENGTH_LONG).show()
            is SearchResultSuccess -> {
                Toast.makeText(activity, "Complete!", Toast.LENGTH_SHORT).show()
                with(adapter) {
                    countries.clear()
                    countries.addAll(response.data.map { it.name })
                    notifyDataSetChanged()
                }
            }
        }
    }
}

class TextChangedListener(private val textChanged: (String) -> Unit) : TextWatcher {
    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        textChanged(p0?.toString() ?: "")
    }
}