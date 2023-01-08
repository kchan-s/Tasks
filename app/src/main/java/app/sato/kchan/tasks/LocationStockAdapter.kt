package app.sato.kchan.tasks

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LocationStockAdapter: RecyclerView.Adapter<LocationStockAdapter.ViewHolder>(){
    val locationData = listOf("土佐山田駅", "高知工科大学", "高知駅")

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val locationText: TextView
        init {
            locationText = view.findViewById(R.id.location_stock_text)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.location_stock_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = locationData[position]
        viewHolder.locationText.text = item
    }

    override fun getItemCount() = locationData.size

}