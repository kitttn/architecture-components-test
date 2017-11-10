package betrip.kitttn.architecturecomponentstest.activity

import android.os.Bundle
import betrip.kitttn.architecturecomponentstest.R
import betrip.kitttn.architecturecomponentstest.view.SearchFragment

/**
 * @author kitttn
 */

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SearchFragment())
                .commit()
    }
}