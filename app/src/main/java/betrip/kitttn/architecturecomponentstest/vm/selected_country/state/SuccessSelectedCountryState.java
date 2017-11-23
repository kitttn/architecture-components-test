package betrip.kitttn.architecturecomponentstest.vm.selected_country.state;

import betrip.kitttn.architecturecomponentstest.model.entities.CountryDetails;

/**
 * @author kitttn
 */

public class SuccessSelectedCountryState implements SelectedCountryState {
    public CountryDetails country;

    public SuccessSelectedCountryState(CountryDetails country) {
        this.country = country;
    }
}
