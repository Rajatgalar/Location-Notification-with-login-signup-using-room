package com.itechnowizard.aplitemapapplication.activities

import android.Manifest
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.itechnowizard.aplitemapapplication.R
import com.itechnowizard.aplitemapapplication.databinding.ActivityHomeBinding
import com.itechnowizard.aplitemapapplication.fragments.DetailsFragment
import com.itechnowizard.aplitemapapplication.fragments.UpdateFragment
import com.itechnowizard.aplitemapapplication.service.LocationService
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "HomeActivity"
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val MY_PERMISSIONS_REQUEST_LOCATION = 1
    private val REQUEST_CHECK_SETTINGS = 2
    private var broadcastReceiver: BroadcastReceiver? = null
    // A reference to the service used to get location updates.
    private var mService: LocationService? = null;
    // Tracks the bound state of the service.
    private var mBound: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermissions()
        loadFragment(DetailsFragment())

        binding.apply {

            bottomNav.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_details -> {
                        loadFragment(DetailsFragment())
                        true
                    }
                    R.id.menu_update -> {
                        loadFragment(UpdateFragment())
                        true
                    }
                }
                true
            }
        }

    }

    private fun checkPermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_LOCATION)
        } else {
            Log.e("MainActivity:","Location Permission Already Granted")
            if (getLocationMode() == 3) {
                Log.e("MainActivity:","Already set High Accuracy Mode")
                initializeService()
            } else {
                Log.e("MainActivity:","Alert Dialog Shown")
                showAlertDialog(this)
            }
        }
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {

                when (intent?.action) {
                    "NotifyUser" -> {
                        try {
                            val name = intent.getStringExtra("pinned_location_name")
                            val lat = intent.getStringExtra("pinned_location_lat")
                            val long = intent.getStringExtra("pinned_location_long")
//                            txt_location_name?.text = "You are around at " + name
//                            txt_location_lat?.text = "Pinned Location Latitude: " + lat
//                            txt_location_long?.text = "Pinned Location Longitude: " + long
                            Log.d(TAG, "onReceive: you are at $lat and lang = $long")
                            Toast.makeText(this@HomeActivity,"lat $lat and long $long",Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(this@HomeActivity,"error",Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction("NotifyUser")
        broadcastReceiver?.let {
            LocalBroadcastManager.getInstance(this).registerReceiver(it, intentFilter)
        }
    }

    override fun onPause() {
        broadcastReceiver?.let {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(it)
        }
        super.onPause()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.e("MainActivity:","Location Permission Granted")
                    if (getLocationMode() == 3) {
                        Log.e("MainActivity:","Already set High Accuracy Mode")
                        initializeService()
                    } else {
                        Log.e("MainActivity:","Alert Dialog Shown")
                        showAlertDialog(this)
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    private fun showAlertDialog(context: Context?) {
        try {
            context?.let {
                val builder = AlertDialog.Builder(it)
                builder.setTitle(it.resources.getString(R.string.app_name))
                    .setMessage("Please select High accuracy Location Mode from Mode Settings")
                    .setPositiveButton(it.resources.getString(android.R.string.ok)) { dialog, which ->
                        dialog.dismiss()
                        startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CHECK_SETTINGS)
                    }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            initializeService()
        }
    }

    // Monitors the state of the connection to the service.
    private var mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder: LocationService.LocalBinder = service as LocationService.LocalBinder
            mService = binder.service
            mBound = true
            // Check that the user hasn't revoked permissions by going to Settings.

            mService?.requestLocationUpdates()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mService = null
            mBound = false
        }
    }

    private fun initializeService(){
        Log.d(TAG, "initializeService: service is initialized")
        bindService(Intent(this, LocationService::class.java),
            mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection)
            mBound = false
        }
        super.onStop()
    }

    private fun getLocationMode(): Int {
        return Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE)
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.commit()
    }
}