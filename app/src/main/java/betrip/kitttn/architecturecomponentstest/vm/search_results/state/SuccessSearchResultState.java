package betrip.kitttn.architecturecomponentstest.vm.search_results.state;

import java.util.ArrayList;
import java.util.List;

import betrip.kitttn.architecturecomponentstest.model.entities.CountryName;

/**
 * @author kitttn
 */

public class SuccessSearchResultState implements SearchResultState {
    public List<CountryName> data = new ArrayList<>();

    public SuccessSearchResultState(List<CountryName> data) {
        this.data = data;
    }
}
