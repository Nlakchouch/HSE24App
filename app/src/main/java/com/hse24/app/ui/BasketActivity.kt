package com.hse24.app.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import com.hse24.app.R
import com.hse24.app.utils.UiUtils

class BasketActivity : AppCompatActivity() {

    private var basketFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket)
        //Forcing the LANDSCAPE Orientation in case of a Tablet
        if (UiUtils.isTablet(this)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        Handler(Looper.getMainLooper()).postDelayed({
            initActionBar()
        }, 300)

        val fragmentManager = supportFragmentManager
        if (savedInstanceState != null) {
            //Restore the basket fragment's instance
            basketFragment = fragmentManager.getFragment(savedInstanceState, BasketFragment.TAG)
        } else {
            //Add basket fragment if this is first creation
            basketFragment = BasketFragment()
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, basketFragment!!, BasketFragment.TAG)
                .commitAllowingStateLoss()
            fragmentManager.executePendingTransactions()
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        //Save the fragment's instance
        if(basketFragment != null)
        supportFragmentManager.putFragment(savedInstanceState, BasketFragment.TAG, basketFragment!!)
    }

    private fun initActionBar() {
        val actionBar = supportActionBar ?: return
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setCustomView(R.layout.logo_view)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return true
    }
}
