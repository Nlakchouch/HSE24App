package com.hse24.app.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import com.hse24.app.R
import com.hse24.app.utils.UiUtils

class ProductDetailsActivity : AppCompatActivity() {

    private var selectedSku: String? = ""
    private var productDetailFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        //Forcing the LANDSCAPE Orientation in case of a Tablet
        if (UiUtils.isTablet(this)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        //Customizing ActionBar
        initActionBar()

        selectedSku = intent.getStringExtra("sku")

        val fragmentManager = supportFragmentManager
        if (savedInstanceState != null) {
            //Restore the product detail fragment's instance
            productDetailFragment = fragmentManager.getFragment(savedInstanceState, ProductDetailsFragment.TAG)
            selectedSku = savedInstanceState.getString("sku")

        } else {
            //Add product detail fragment if this is first creation
            productDetailFragment = ProductDetailsFragment.newInstance(selectedSku)
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, productDetailFragment!!, ProductDetailsFragment.TAG)
                .commitAllowingStateLoss()
            fragmentManager.executePendingTransactions()
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        //Save the fragment's instance
        if(productDetailFragment != null)
        supportFragmentManager.putFragment(savedInstanceState, ProductDetailsFragment.TAG, productDetailFragment!!)

        savedInstanceState.putString("sku", selectedSku)
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
