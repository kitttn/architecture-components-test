package betrip.kitttn.architecturecomponentstest.view;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import betrip.kitttn.architecturecomponentstest.R;
import betrip.kitttn.architecturecomponentstest.activity.BaseActivity;
import betrip.kitttn.architecturecomponentstest.activity.MainActivity;
import betrip.kitttn.architecturecomponentstest.di.modules.Factory;
import betrip.kitttn.architecturecomponentstest.model.ViewCountryName;
import betrip.kitttn.architecturecomponentstest.model.entities.CountryName;
import betrip.kitttn.architecturecomponentstest.view.adapters.CountryNameFlagAdapter;
import betrip.kitttn.architecturecomponentstest.view.adapters.Handler;
import betrip.kitttn.architecturecomponentstest.vm.EnteredTextViewModel;
import betrip.kitttn.architecturecomponentstest.vm.search_results.SearchResultsViewModel;
import betrip.kitttn.architecturecomponentstest.vm.search_results.state.ErrorSearchResultState;
import betrip.kitttn.architecturecomponentstest.vm.search_results.state.InitialSearchResultState;
import betrip.kitttn.architecturecomponentstest.vm.search_results.state.LoadingSearchResultState;
import betrip.kitttn.architecturecomponentstest.vm.search_results.state.SearchResultState;
import betrip.kitttn.architecturecomponentstest.vm.search_results.state.SuccessSearchResultState;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author kitttn
 */

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    @Inject Factory<SearchResultsViewModel> factory;
    private CompositeDisposable composite = new CompositeDisposable();
    private CountryNameFlagAdapter adapter;
    private SearchView searchView;
    private MenuItem menuItem;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.searchResultsRV) RecyclerView results;
    @BindView(R.id.refreshLayout) SwipeRefreshLayout refreshLayout;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BaseActivity activity = ((BaseActivity) getActivity());
        if (activity != null && activity.lifecycleComponent != null) {
            activity.lifecycleComponent.inject(this);
            activity.setSupportActionBar(toolbar);
        }

        adapter = new CountryNameFlagAdapter(this::openDetailsFragment);
        results.setLayoutManager(new LinearLayoutManager(activity));
        results.setAdapter(adapter);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        menuItem = menu.findItem(R.id.search);
        searchView = ((SearchView) menuItem.getActionView());
        searchView.setMaxWidth(Integer.MAX_VALUE);
        bindViewModel();
    }

    private void openDetailsFragment(String countryName) {
        ((MainActivity) getActivity()).openCountryDetails(countryName);
    }

    private void bindViewModel() {
        EnteredTextViewModel textVm = ViewModelProviders.of(getActivity()).get(EnteredTextViewModel.class);
        SearchResultsViewModel searchResultsVM =
                ViewModelProviders.of(getActivity(), factory).get(SearchResultsViewModel.class);

        refreshLayout.setOnRefreshListener(() -> searchResultsVM.startSearch(textVm.getLastQuery()));

        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override public boolean onMenuItemActionExpand(MenuItem menuItem) {
                toolbar.setBackgroundResource(R.color.white);
                return true;
            }

            @Override public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                toolbar.setBackgroundResource(R.color.colorPrimary);
                return true;
            }
        });

        Disposable d = searchResultsVM.getSearchResults()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(it -> parseSearchResultResponse(it, searchResultsVM), Throwable::printStackTrace);

        composite.add(d);

        d = textVm.getEnteredText()
                .observeOn(Schedulers.io())
                .debounce(400, TimeUnit.MILLISECONDS, Schedulers.io())
                .subscribe(searchResultsVM::startSearch, Throwable::printStackTrace);

        composite.add(d);

        String query = textVm.getLastQuery();
        if (!query.isEmpty()) {
            menuItem.expandActionView();
            searchView.setQuery(query, false);
        }

        searchView.setOnQueryTextListener(new QueryListener(textVm::textChanged));
    }

    private void unbindViewModel() {
        menuItem.setOnActionExpandListener(null);
        searchView.setOnQueryTextListener(null);

        menuItem = null;
        searchView = null;

        composite.clear();
    }

    @Override public void onDestroy() {
        unbindViewModel();
        super.onDestroy();
        Log.i(TAG, "onDestroy: Fragment $this destroyed!");
    }

    @Override public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: Fragment $this is stopped!");
    }

    private void parseSearchResultResponse(SearchResultState response, SearchResultsViewModel searchVm) {
        Log.i(TAG, "parseSearchResultResponse: Frag id: " + this + "; Got new VM state: " + response);
        BaseActivity activity = ((BaseActivity) getActivity());

        refreshLayout.setRefreshing(false);
        if (response instanceof InitialSearchResultState)
            searchVm.startSearch("");

        if (response instanceof LoadingSearchResultState)
            refreshLayout.setRefreshing(true);

        if (response instanceof ErrorSearchResultState) {
            ErrorSearchResultState.ErrorCode code = ((ErrorSearchResultState) response).error;
            if (code == ErrorSearchResultState.ErrorCode.NO_NETWORK)
                Toast.makeText(activity, "No network connection found :(", Toast.LENGTH_LONG).show();
        }

        if (response instanceof SuccessSearchResultState) {
            List<CountryName> countries = ((SuccessSearchResultState) response).data;
            List<ViewCountryName> resulting = new ArrayList<>();
            for (CountryName country : countries) {
                String name = getString(R.string.country_name_template, country.name, country.nativeName);
                resulting.add(new ViewCountryName(name, country.name, activity.getFlagIdByCode(country.isoCode)));
            }
            adapter.replace(resulting);
        }
    }
}

class QueryListener implements SearchView.OnQueryTextListener {
    private Handler<String> textChanged;

    public QueryListener(Handler<String> textChanged) {
        this.textChanged = textChanged;
    }

    @Override public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override public boolean onQueryTextChange(String newText) {
        if (newText == null)
            return true;

        textChanged.invoke(newText);
        return true;
    }
}