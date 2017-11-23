package betrip.kitttn.architecturecomponentstest.vm.search_results;

import android.arch.lifecycle.ViewModel;

import java.net.UnknownHostException;

import betrip.kitttn.architecturecomponentstest.services.CountryNamesLoader;
import betrip.kitttn.architecturecomponentstest.vm.search_results.state.ErrorSearchResultState;
import betrip.kitttn.architecturecomponentstest.vm.search_results.state.InitialSearchResultState;
import betrip.kitttn.architecturecomponentstest.vm.search_results.state.LoadingSearchResultState;
import betrip.kitttn.architecturecomponentstest.vm.search_results.state.SearchResultState;
import betrip.kitttn.architecturecomponentstest.vm.search_results.state.SuccessSearchResultState;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

/**
 * @author kitttn
 */

public class SearchResultsViewModel extends ViewModel {
    private CountryNamesLoader loader;
    private BehaviorSubject<SearchResultState> state = BehaviorSubject.createDefault(new InitialSearchResultState());
    private CompositeDisposable composite = new CompositeDisposable();

    public SearchResultsViewModel(CountryNamesLoader loader) {
        this.loader = loader;
        Disposable d = loader.getCountries()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(it -> state.onNext(new SuccessSearchResultState(it)),
                        Throwable::printStackTrace);

        composite.add(d);
    }

    public Observable<SearchResultState> getSearchResults() {
        return state;
    }

    public void startSearch(String query) {
        Completable completable = query.isEmpty()
                ? loader.fetchAllCountries()
                : loader.fetchCountries(query);

        state.onNext(new LoadingSearchResultState());

        Disposable d = completable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {}, err -> state.onNext(createSearchError(err)));

        composite.add(d);
    }

    private ErrorSearchResultState createSearchError(Throwable error) {
        if (error instanceof UnknownHostException) {
            return new ErrorSearchResultState("No internet connection detected!",
                    ErrorSearchResultState.ErrorCode.NO_NETWORK);
        }

        return new ErrorSearchResultState(error.getMessage(), ErrorSearchResultState.ErrorCode.OTHER);
    }

    @Override protected void onCleared() {
        composite.clear();
        super.onCleared();
    }
}
