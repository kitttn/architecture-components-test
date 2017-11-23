package betrip.kitttn.architecturecomponentstest.vm.selected_country;

import android.arch.lifecycle.ViewModel;

import java.net.UnknownHostException;

import betrip.kitttn.architecturecomponentstest.services.CountryDetailsLoader;
import betrip.kitttn.architecturecomponentstest.vm.selected_country.state.ErrorSelectedCountryState;
import betrip.kitttn.architecturecomponentstest.vm.selected_country.state.LoadingSelectedCountryState;
import betrip.kitttn.architecturecomponentstest.vm.selected_country.state.SelectedCountryState;
import betrip.kitttn.architecturecomponentstest.vm.selected_country.state.SuccessSelectedCountryState;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

/**
 * @author kitttn
 */

public class SelectedCountryViewModel extends ViewModel {
    private PublishProcessor<SelectedCountryState> state = PublishProcessor.create();
    private CompositeDisposable composite = new CompositeDisposable();
    private CountryDetailsLoader loader;

    public SelectedCountryViewModel(CountryDetailsLoader loader) {
        this.loader = loader;
    }

    public void fetchCountryDetails(String countryName) {
        state.onNext(new LoadingSelectedCountryState());

        Disposable d = loader.getCountryDetails(countryName)
                .subscribeOn(Schedulers.io())
                .subscribe(res -> state.onNext(new SuccessSelectedCountryState(res)),
                        err -> state.onNext(createSearchError(err)));

        composite.add(d);
    }

    private ErrorSelectedCountryState createSearchError(Throwable error) {
        if (error instanceof UnknownHostException) {
            return new ErrorSelectedCountryState("No internet connection detected!",
                    ErrorSelectedCountryState.ErrorCode.NO_NETWORK);
        }

        return new ErrorSelectedCountryState(error.getMessage(), ErrorSelectedCountryState.ErrorCode.OTHER);
    }

    public Flowable<SelectedCountryState> getCountryDetails() {
        return state;
    }

    @Override protected void onCleared() {
        composite.clear();
        super.onCleared();
    }
}
