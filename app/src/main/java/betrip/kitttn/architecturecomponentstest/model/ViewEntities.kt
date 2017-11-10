package betrip.kitttn.architecturecomponentstest.model

import android.support.annotation.DrawableRes

/**
 * @author kitttn
 */

data class ViewCountryName(val displayName: String, val realName: String, @DrawableRes val flagId: Int)