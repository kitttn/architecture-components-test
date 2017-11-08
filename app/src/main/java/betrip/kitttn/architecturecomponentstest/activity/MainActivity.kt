package betrip.kitttn.architecturecomponentstest.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import betrip.kitttn.architecturecomponentstest.R
import betrip.kitttn.architecturecomponentstest.view.SearchFragment

/**
 * @author kitttn
 */

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SearchFragment())
                .commit()
    }
}