package app.sato.kchan.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.sato.kchan.tasks.fanction.LocationManager

class LocationStockAdapter : RecyclerView.Adapter<LocationStockAdapter.ViewHolder>() {
    val locationManager = LocationManager()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val locationText = view.findViewById<TextView>(R.id.location_stock_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.location_stock_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        locationManager.select(position)
        val location = locationManager.getLocation()!!
        viewHolder.locationText.text = location.getName()
    }

    override fun getItemCount(): Int {
        locationManager.search("", arrayOf("PermanentFlagUp"))
        return locationManager.getLocationNumber()
    }

}