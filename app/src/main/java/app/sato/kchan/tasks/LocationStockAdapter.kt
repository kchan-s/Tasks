package app.sato.kchan.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.sato.kchan.tasks.fanction.LocationManager

class LocationStockAdapter: RecyclerView.Adapter<LocationStockAdapter.ViewHolder>(){

    val lm = LocationManager()

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val locationText = view.findViewById<TextView>(R.id.location_stock_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.location_stock_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        lm.selectByTempId(position.toString())
        val location = lm.getLocation()
        if (location.isPermanent()) viewHolder.locationText.text = location.getName()
    }

    override fun getItemCount(): Int{
        return lm.getLocationNumber()
//        return 1
    }

}