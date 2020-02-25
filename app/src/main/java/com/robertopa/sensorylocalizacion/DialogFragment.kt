package com.robertopa.sensorylocalizacion


import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.fragment.app.DialogFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_dialog.*

open class DialogFragment : DialogFragment() {

    companion object {
        const val LATITUD: String = "latitud"
        const val LONGITUD: String = "longitud"
        const val TEXTO_BOTON: String = "button"
        const val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
    }



    private lateinit var gmap: GoogleMap
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    lateinit var mapViewBundle: Bundle

    override fun onCreateDialog(@NonNull savedInstanceState: Bundle?): Dialog {
        val arguments = arguments
        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val layout = inflater.inflate(R.layout.fragment_dialog, null)
        builder.setView(layout)


        if (savedInstanceState != null) {
            mapViewBundle =
                savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)!!
        }

        mapView.onCreate(mapViewBundle)
        mapView.onResume()
        mapView.getMapAsync { googleMap: GoogleMap ->
            gmap = googleMap
            gmap.setMinZoomPreference(12F)
            val ny = LatLng(latitud, longitud)
            gmap.moveCamera(CameraUpdateFactory.newLatLng(ny))
        }

        var boton = "Aceptar"
        if (arguments != null) {
            latitud = arguments.getDouble(LATITUD, 0.0)
            longitud = arguments.getDouble(LONGITUD, 0.0)
            boton = arguments.getString(TEXTO_BOTON, "Aceptar")
        }

        builder.setTitle("Ubicación: ${String.format("%.2f, %.2f", latitud, longitud)}")
        tvLatitud.text = String.format("%.2f", latitud)
        tvLongitud.text = String.format("%.2f", longitud)
        builder.setPositiveButton(boton) { dialogInterface: DialogInterface, _: Int ->
            Log.i("Diálogos", "Aceptar")
            dialogInterface.dismiss()
        }
        return builder.create()
        //return inflater.inflate(R.layout.fragment_dialog, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle =
            outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(
                MAP_VIEW_BUNDLE_KEY,
                mapViewBundle
            )
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }

}
