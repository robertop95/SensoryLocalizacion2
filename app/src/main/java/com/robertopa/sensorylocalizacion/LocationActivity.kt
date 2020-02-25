package com.robertopa.sensorylocalizacion

import android.Manifest
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_location.*

class LocationActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var snackbarMultiplePermissionsListener: SnackbarOnAnyDeniedMultiplePermissionsListener
    private lateinit var adapter: ArrayAdapter<String>
    private var items: ArrayList<String> = ArrayList()
    private lateinit var ubicacionActual: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        buttonLocalizacion.setOnClickListener { checkLocalionPermission() }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        ListaLocal.adapter = adapter

        /*ListaLocal.setOnItemClickListener { _, _, position, _ ->
            /*Toast.makeText(this, "Has hecho click en la localización: ${items[position]}",
                Toast.LENGTH_LONG).show()*/
            val bundle = Bundle()
            items[position]
            bundle.putDouble(DialogFragment.LONGITUD, ubicacionActual.longitude)
            bundle.putDouble(DialogFragment.LATITUD, ubicacionActual.latitude)
            bundle.putString(DialogFragment.TEXTO_BOTON, "Aceptar")

            val dialogView = DialogFragment()
            dialogView.arguments = bundle
            dialogView.show(supportFragmentManager, "error_dialog_mapview")
        }*/
    }

    private fun checkLocalionPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            snackbarMultiplePermissionsListener  =
                SnackbarOnAnyDeniedMultiplePermissionsListener.Builder
                    .with(root, "Es necesario el permiso de la ubicación o de segundo plano.")
                    .withOpenSettingsButton("Ajustes")
                    .withCallback(object: Snackbar.Callback(){
                        override fun onShown(sb: Snackbar?) {
                            // Event handler for when the given Snackbar is visible
                            super.onShown(sb)
                        }

                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            // Event handler for when the given Snackbar has been dismissed
                        }
                    })
                    .build()

        } else {
            snackbarMultiplePermissionsListener  =
                SnackbarOnAnyDeniedMultiplePermissionsListener.Builder
                    .with(root, "Es necesario el permiso de la ubicación.")
                    .withOpenSettingsButton("Ajustes")
                    .withCallback(object: Snackbar.Callback(){
                        override fun onShown(sb: Snackbar?) {
                            // Event handler for when the given Snackbar is visible
                            super.onShown(sb)
                        }

                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            // Event handler for when the given Snackbar has been dismissed
                        }
                    })
                    .build()
        }



        val permission = object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                report?.let {
                    for (permission in report.grantedPermissionResponses) {
                        when (permission.permissionName) {
                            Manifest.permission.ACCESS_FINE_LOCATION ->
                                Toast.makeText(applicationContext, "Permiso concedido Fine",
                                    Toast.LENGTH_LONG).show()
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION ->
                                Toast.makeText(applicationContext, "Permiso concedido Back",
                                    Toast.LENGTH_LONG).show()
                        }
                        fusedLocationClient = LocationServices
                            .getFusedLocationProviderClient(applicationContext)
                        obtenerUltimaUbicacion()
                    }

                    for (permission in report.deniedPermissionResponses) {
                        when (permission.permissionName) {
                            Manifest.permission.ACCESS_FINE_LOCATION -> {
                                if (permission.isPermanentlyDenied) {
                                    Toast.makeText(applicationContext, "Permiso Permanente " +
                                            "denegado Fine",
                                        Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(applicationContext, "Permiso Denegado. Fine",
                                        Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                token?.continuePermissionRequest()
            }

        }

        val composite = CompositeMultiplePermissionsListener(permission,
            snackbarMultiplePermissionsListener)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                .withListener(composite)
                .check()
        } else {
            Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(composite)
                .check()
        }

    }

    private fun obtenerUltimaUbicacion() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    ubicacionActual = location
                    adapter.clear()
                    adapter.insert("lat: ${location.latitude}, long: ${location.longitude}",
                        adapter.count)
                    adapter.notifyDataSetChanged()
                }
            }
    }
}
