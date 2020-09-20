package com.hse24.app.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import com.hse24.app.R
import com.hse24.app.utils.Hse24Utils

class BasketActivity : AppCompatActivity() {

    private var mFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket)
        //Forcing the
        if (Hse24Utils.isTablet(this)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        initActionBar()

        val fragmentManager = supportFragmentManager
        if (savedInstanceState != null) {
            //Restore the fragment's instance
            mFragment = fragmentManager.getFragment(savedInstanceState, BasketFragment.TAG)
        } else {
            mFragment = BasketFragment()
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mFragment!!, BasketFragment.TAG)
                .commitAllowingStateLoss()
            fragmentManager.executePendingTransactions()
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        //Save the fragment's instance
        supportFragmentManager.putFragment(savedInstanceState, BasketFragment.TAG, mFragment!!)
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
