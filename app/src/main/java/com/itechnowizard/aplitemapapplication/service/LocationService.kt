package com.itechnowizard.aplitemapapplication.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.itechnowizard.aplitemapapplication.R
import com.itechnowizard.aplitemapapplication.activities.HomeActivity


private const val TAG = "LocationService"

class LocationService : Service() {

    private val EXTRA_STARTED_FROM_NOTIFICATION = "started_from_notification"
    private val mBinder = LocalBinder()
    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 5000
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
    private val NOTIFICATION_ID = 12345678
    private var mChangingConfiguration = false
    private var mLocationRequest: LocationRequest? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null
    private var mServiceHandler: Handler? = null
    private var currentLocation: Location? = null

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "onCreate: ")
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                Log.d(TAG, "onLocationResult: ")
                p0.let { it ->
                    it.lastLocation.let {
                        if (it != null) {
                            onNewLocation(it)
                            Log.d(TAG, "onLocationResult: ${it.longitude}")
                        }
                    }
                }
            }
        }

        createLocationRequest()
        getLastLocation()

        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        mServiceHandler = Handler(handlerThread.looper)

    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.e(TAG, "in onBind()")
        stopForeground(true)
        mChangingConfiguration = false
        return mBinder
    }

    override fun onRebind(intent: Intent?) {
        Log.i(TAG, "in onRebind()")
        stopForeground(true)
        mChangingConfiguration = false
        super.onRebind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Location Service started")
        val startedFromNotification =
            intent?.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION, false)

        if (startedFromNotification!!) {
            removeLocationUpdates()
            stopSelf()
        }
        return Service.START_NOT_STICKY
    }

    inner class LocalBinder : Binder() {
        internal val service: LocationService
            get() = this@LocationService
    }


    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "Last client unbound from service")

        if (!mChangingConfiguration) {
            Log.i(TAG, "Starting foreground service")

            startForeground(NOTIFICATION_ID, serviceNotification())
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        mServiceHandler?.removeCallbacksAndMessages(null)
    }

    fun requestLocationUpdates() {
        Log.e(TAG, "Requesting location updates")
        startService(Intent(applicationContext, LocationService::class.java))
        try {
            mLocationRequest?.let {
                mLocationCallback?.let { it1 ->
                    mFusedLocationClient?.requestLocationUpdates(
                        it,
                        it1,
                        Looper.myLooper()
                    )
                }
            }
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission. Could not request updates. $unlikely")
        }

    }

    private fun removeLocationUpdates() {
        Log.e(TAG, "Removing location updates")
        try {
            mLocationCallback?.let { mFusedLocationClient?.removeLocationUpdates(it) }
            stopSelf()
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission. Could not remove updates. $unlikely")
        }
    }

    private fun getLastLocation() {
        try {
            mFusedLocationClient?.lastLocation
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        currentLocation = task.result
                    } else {
                        Log.e(TAG, "Failed to get location.")
                    }
                }
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission.$unlikely")
        }

    }


    fun onNewLocation(location: Location) {
        currentLocation = location
        if(isAppinBackground()) {
//        sendNotification(this, currentLocation!!)
            serviceNotification()
        }
        else{
            Log.e(TAG, "App In Foreground " + currentLocation!!.provider)
            val pushNotification = Intent("NotifyUser")
            pushNotification.putExtra("pinned_location_name", currentLocation!!.provider)
            pushNotification.putExtra("pinned_location_lat", currentLocation!!.latitude.toString())
            pushNotification.putExtra("pinned_location_long", currentLocation!!.longitude.toString())
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
            sendNotification(this,currentLocation!!)
        }
    }

    private fun isAppinBackground(): Boolean{
        var isInBackground = true

        try {
            val am = getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            am?.let {
                val runningProcesses = it.runningAppProcesses
                for (processInfo in runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (activeProcess in processInfo.pkgList) {
                            if (activeProcess == getApplicationContext().packageName) {
                                isInBackground = false
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        return isInBackground
    }

    private fun sendNotification(context: Context, location : Location) {
        try {
            Log.d(TAG, "sendNotification: inside notification")
            val mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d(TAG, "sendNotification: inside O")
                val name = context.getString(R.string.app_name)
                val mChannel =
                    NotificationChannel("channel_01", name, NotificationManager.IMPORTANCE_HIGH)

                mNotificationManager?.createNotificationChannel(mChannel)
            }

            val notificationIntent = Intent(context, HomeActivity::class.java)

            val stackBuilder = TaskStackBuilder.create(context)

            stackBuilder.addParentStack(HomeActivity::class.java)

            stackBuilder.addNextIntent(notificationIntent)

            val notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

            val builder = NotificationCompat.Builder(context)

            builder.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle("Your lat : ${location.latitude} & long : ${location.longitude}")
                .setContentIntent(notificationPendingIntent)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId("channel_01") // Channel ID
            } else {
                builder.priority = Notification.PRIORITY_HIGH
            }

            builder.setAutoCancel(true)

            mNotificationManager?.notify(0, builder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun serviceNotification(): Notification {
        val mNotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as? NotificationManager

        mNotificationManager?.cancel(0)

        val intent = Intent(this, LocationService::class.java)

        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My App"
            val mChannel = NotificationChannel(
                "location_service_channel",
                name,
                NotificationManager.IMPORTANCE_HIGH
            )

            mNotificationManager?.createNotificationChannel(mChannel)
        }

        val builder = NotificationCompat.Builder(this)
            .setContentTitle("Location Service")
            .setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Your lat : ${currentLocation!!.latitude} & long : ${currentLocation!!.longitude}")
            .setWhen(System.currentTimeMillis())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("location_service_channel") // Channel ID
        } else {
            builder.priority = Notification.PRIORITY_HIGH
        }

        mNotificationManager?.notify(NOTIFICATION_ID, builder.build())

        return builder.build()
    }

    private fun createLocationRequest() {
        Log.d(TAG, "createLocationRequest: ")
        mLocationRequest = LocationRequest()
        mLocationRequest?.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest?.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        Log.e(TAG, "createLocationRequest")
    }

}