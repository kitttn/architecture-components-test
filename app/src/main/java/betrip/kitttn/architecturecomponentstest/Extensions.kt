package betrip.kitttn.architecturecomponentstest

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * @author kitttn
 */

operator fun CompositeDisposable.plusAssign(d: Disposable) {
    this.add(d)
}