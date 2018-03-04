package com.example.edblack.runtimepermissionswithdexter

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import com.karumi.dexter.PermissionToken

import android.support.v7.app.AlertDialog
import android.provider.MediaStore
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.*
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btn_camera.setOnClickListener {
            requestCameraPermission()
        }

        btn_location.setOnClickListener {
            requestLocationPermission()
        }

        btn_Storage.setOnClickListener {
            requestStoragePermission()
        }
    }

    // Requesting permission to use the Camera
    private fun requestCameraPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        openCamera()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                        token!!.continuePermissionRequest()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {

                        if (response!!.isPermanentlyDenied) {
                            showSettingsDialog()
                        }
                    }

                }).check()
    }

    private fun requestLocationPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        Toast.makeText(applicationContext, "Location granted", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                        token!!.continuePermissionRequest()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        if (response!!.isPermanentlyDenied) {
                            showSettingsDialog()
                        }
                    }

                }).check()
    }


    private fun requestStoragePermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            Toast.makeText(applicationContext, "All permissions are granted", Toast.LENGTH_SHORT).show()
                        }

                        if (report.isAnyPermissionPermanentlyDenied) {
                            showSettingsDialog()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                        token!!.continuePermissionRequest()
                    }

                })
                .withErrorListener { Toast.makeText(applicationContext, "Error occurred", Toast.LENGTH_SHORT).show() }
                .onSameThread()
                .check()
    }

//    .withErrorListener(object : PermissionRequestErrorListener {
//        override fun onError(error: DexterError?) {
//            Toast.makeText(applicationContext, "Error occured", Toast.LENGTH_SHORT).show()
//        }
//
//    })

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permission Needed")
        builder.setMessage("This app needs your permission to run this feature. You can grant it in the app settings.")
        builder.setPositiveButton("SETTINGS") { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 100)
    }


}

