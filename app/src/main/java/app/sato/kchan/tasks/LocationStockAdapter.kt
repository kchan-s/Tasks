package app.sato.kchan.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LocationStockAdapter: RecyclerView.Adapter<LocationStockAdapter.ViewHolder>(){
    companion object{
        val locationData = mutableListOf(mutableListOf("土佐山田駅", "33.607133", "133.685047"),
            mutableListOf("高知工科大学", "33.620917", "133.719833"),
            mutableListOf("高知駅", "33.567153", "133.543661"))
    }

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
        viewHolder.locationText.text = item[0]
    }

    override fun getItemCount() = locationData.size

}