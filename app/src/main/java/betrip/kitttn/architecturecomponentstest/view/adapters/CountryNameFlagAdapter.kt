package betrip.kitttn.architecturecomponentstest.view.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import betrip.kitttn.architecturecomponentstest.R
import betrip.kitttn.architecturecomponentstest.model.ViewCountryName

/**
 * @author kitttn
 */

data class CountryNameFlagVH(private val view: View) : RecyclerView.ViewHolder(view) {
    private val nameTxt by lazy { view.findViewById<TextView>(R.id.countryNameTxt) }
    private val flag by lazy { view.findViewById<ImageView>(R.id.countryNameFlag) }
    fun bind(country: ViewCountryName) {
        nameTxt.text = country.name
    }
}

class CountryNameFlagAdapter(val countries: MutableList<ViewCountryName>) :
        RecyclerView.Adapter<CountryNameFlagVH>() {

    override fun onBindViewHolder(holder: CountryNameFlagVH?, position: Int) {
        val country = countries[position]
        holder?.bind(country)
    }

    override fun getItemCount() = countries.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CountryNameFlagVH {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.view_country_name_flag, parent, false)
        return CountryNameFlagVH(view)
    }

}