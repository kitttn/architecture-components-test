package betrip.kitttn.architecturecomponentstest.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import betrip.kitttn.architecturecomponentstest.R;
import betrip.kitttn.architecturecomponentstest.model.ViewCountryName;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author kitttn
 */

public class CountryNameFlagAdapter extends RecyclerView.Adapter<CountryNameFlagAdapter.CountryNameViewHolder> {
    private List<ViewCountryName> countryNames = new ArrayList<>();
    private Handler<String> handler;

    public CountryNameFlagAdapter(Handler<String> handler) {
        this.handler = handler;
    }

    @Override
    public CountryNameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_country_name_flag, parent, false);
        return new CountryNameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CountryNameViewHolder holder, int position) {
        ViewCountryName country = countryNames.get(position);
        holder.nameTxt.setText(country.getDisplayName());
        holder.flagImg.setImageResource(country.getFlagId());
        holder.view.setOnClickListener(v -> handler.invoke(country.getRealName()));
    }

    public void replace(List<ViewCountryName> newCountries) {
        this.countryNames.clear();
        this.countryNames.addAll(newCountries);
        notifyDataSetChanged();
    }

    @Override public int getItemCount() {
        return countryNames.size();
    }

    class CountryNameViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.countryNameTxt) TextView nameTxt;
        @BindView(R.id.countryNameFlag) ImageView flagImg;
        View view;

        public CountryNameViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}