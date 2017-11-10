package betrip.kitttn.architecturecomponentstest

import android.content.Context
import android.support.annotation.DrawableRes
import android.util.Log
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * @author kitttn
 */

operator fun CompositeDisposable.plusAssign(d: Disposable) {
    this.add(d)
}

@DrawableRes fun Context.getFlagResIdByCode(isoCode: String): Int {
    val id = resources.getIdentifier(isoCode.toLowerCase(), "drawable", this.packageName)
    if (id == 0) {
        Log.w("RESOURCE LOADER", "getResourceId: Resource for $isoCode not found!")
    }
    return id
}