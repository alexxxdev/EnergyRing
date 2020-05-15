package cn.vove7.energy_ring.ui.adapter

import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.vove7.energy_ring.R
import cn.vove7.energy_ring.util.AppInfo
import kotlinx.android.synthetic.main.list_item_app.view.*

/**
 * # AppListAdapter
 *
 * @author Vove
 * 2020/5/14
 */
class AppListAdapter(val list: List<AppInfo>, title: String, val pm: PackageManager) : RecyclerView.Adapter<AppListAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_app, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(list[position], this)
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: AppInfo, adapter: AppListAdapter) = itemView.run {
            icon_view.setImageDrawable(item.getIcon(adapter.pm))
            title_view.text = item.getName(adapter.pm)
            setOnClickListener {


            }
        }
    }
}