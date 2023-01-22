package app.sato.kchan.tasks

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import app.sato.kchan.tasks.databinding.MapActivityBinding
import app.sato.kchan.tasks.fanction.Location
import app.sato.kchan.tasks.fanction.LocationManager
import app.sato.kchan.tasks.fanction.NoteManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*


class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        const val ADDRESS_RESULT = "address"
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: MapActivityBinding

    lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    var locationCallback: LocationCallback? = null

    var position = -1
    var first = true
    var mFirst = true // marker生成が初めてか
    var newLocation = LatLng(0.0, 0.0) // 登録されているなら読み込み
    lateinit var marker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTheme()
        binding = MapActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        position = intent.getIntExtra("position", -1)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val toolbar = binding.mapToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // 戻るボタン
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home-> {
                val addressLine = doReverseGeoCoding(newLocation.latitude, newLocation.longitude).get(0).getAddressLine(0).toString()
                val address = addressLine.split(" ")
                println(address[1])
                val intent = Intent()
                intent.putExtra(ADDRESS_RESULT, address[1])
                setResult(RESULT_OK, intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        checkPermission()

        if (position != -1) {
            val nm = NoteManager()
            nm.selectByTempId(position.toString())
            val n = nm.getNote()
            val noteLocation = n.getNoticeLocation()

            if (noteLocation != null) {
                val coordinate = doGeoCoding(noteLocation.getAddress())
                newLocation = LatLng(coordinate[0].latitude, coordinate[0].longitude)
                marker = mMap.addMarker(
                    MarkerOptions().position(newLocation).title(noteLocation.getAddress())
                )!!
                mFirst = false
            }
        }

        mMap.setOnMapLongClickListener { longpushLocation: LatLng ->
            if (!mFirst) marker.remove()
            else mFirst = false
            newLocation = LatLng(longpushLocation.latitude, longpushLocation.longitude)
            marker = mMap.addMarker(MarkerOptions().position(newLocation).title("" + longpushLocation.latitude + " :" + longpushLocation.longitude))!!
        }
    }

    //パーミッションの状態を確認する
    private fun checkPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED){
            myLocationEnable()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }
    }

    //requestPeermissionsのコールバック
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"許可されました",Toast.LENGTH_SHORT).show()
            myLocationEnable()
        }else{
            Toast.makeText(this,"拒否されました",Toast.LENGTH_SHORT).show()
        }
    }

    //自分の位置情報をオンにする
    private fun myLocationEnable(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //許可されていない
            return
        }else{
            mMap.isMyLocationEnabled = true

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    var currentLatLog = LatLng(p0.lastLocation!!.latitude, p0.lastLocation!!.longitude)
                    if (first) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLog))
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(mMap.maxZoomLevel - 5))
                        first = false
                    }
                }
            }

            val locationRequest = LocationRequest.create().apply {
                interval = 0
                fastestInterval = 0
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback as LocationCallback,mainLooper)
        }
    }

    private fun doGeoCoding(query: String): MutableList<Address> {
        val gcoder = Geocoder(this, Locale.getDefault())
        return gcoder.getFromLocationName(query, 1)
    }

    private fun doReverseGeoCoding(lat: Double, lng: Double) : MutableList<Address>{
        val gcoder = Geocoder(this, Locale.getDefault())
        return gcoder.getFromLocation(lat, lng, 1)
    }

    private fun loadTheme() {
        val cPreferences = getSharedPreferences("themeData", MODE_PRIVATE)
        setTheme(cPreferences.getInt("theme", R.style.Theme_TaSks_Turquoise))
    }
}