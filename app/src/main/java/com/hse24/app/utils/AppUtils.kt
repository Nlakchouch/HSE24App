@file:Suppress("DEPRECATION")

package com.hse24.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.util.Log

import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * Created by LAKCHOUCH NAOUFAL on 01/09/20.
 */

object AppUtils {

    private const val TAG = "AppUtils"

    /**This method is used to check if the user is connected to WIFI network
     */
    fun isConnectedToWiFi(context: Context): Boolean {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connManager.activeNetworkInfo
        // connected to wifi
        return activeNetwork != null && activeNetwork.type == ConnectivityManager.TYPE_WIFI
    }

    /**This method is used to check if the user is connected to Mobile network
     */
    fun isConnectedToMobile(context: Context): Boolean {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connManager.activeNetworkInfo
        // connected to mobile data
        return activeNetwork != null && activeNetwork.type == ConnectivityManager.TYPE_MOBILE
    }

    /**This method is used to check if the user is connected to internet or not
     */
    fun isNetworkConnected(context: Context): Boolean {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    /**This method is used to connect to the internet and to get Json
     * from different Json WebServices
     */
    @Throws(IOException::class)
    fun readFromUrl(url: String?): String {
        Log.v(TAG, url.toString())
        val manifest = StringBuilder()
        val input: InputStream
        val mURL = URL(url)
        val urlConnection = mURL.openConnection() as HttpURLConnection
        urlConnection.setRequestProperty("accept", "application/json")
        urlConnection.setRequestProperty("appDevice", "ANDROID_PHONE")
        urlConnection.setRequestProperty("locale", "de_DE")
        try {
            input = BufferedInputStream(urlConnection.inputStream)
            val rd = BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8), 8)
            var cur: String?
            try {
                while (rd.readLine().also { cur = it } != null) {
                    manifest.append(cur)
                }
                input.close()
            } catch (e1: IOException) {
                Log.d(TAG, "Failed reading url $url", e1)
            }
            urlConnection.disconnect()
        } catch (e1: Exception) {
            Log.d(TAG, "Failed reading url $url", e1)
        } finally {
            urlConnection.disconnect()
        }
        return manifest.toString()
    }

    /** Using AssetManager to get a bitmap from a file
     */
    fun getBitmapFromAsset(context: Context, filePath: String?): Bitmap? {
        val assetManager = context.assets
        val istr: InputStream
        var bitmap: Bitmap? = null
        try {
            istr = assetManager.open(filePath!!)
            bitmap = BitmapFactory.decodeStream(istr)
        } catch (e: IOException) {
            // handle exception
        }
        return bitmap
    }

}