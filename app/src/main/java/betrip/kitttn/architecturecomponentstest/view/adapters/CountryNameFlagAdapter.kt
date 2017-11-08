package betrip.kitttn.architecturecomponentstest.view.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import betrip.kitttn.architecturecomponentstest.R

/**
 * @author kitttn
 */

data class CountryNameFlagVH(private val view: View) : RecyclerView.ViewHolder(view) {
    private val nameTxt by lazy { view.findViewById<TextView>(R.id.countryNameTxt) }
    fun bind(country: String) {
        nameTxt.text = country
    }
}

class CountryNameFlagAdapter(val countries: MutableList<String>) :
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