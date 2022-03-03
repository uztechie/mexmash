package uz.techie.mexmash.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_region.view.*
import uz.techie.mexmash.databinding.AdapterRegionBinding
import uz.techie.mexmash.models.District
import uz.techie.mexmash.models.Quarter

class QuarterAdapter(val callBack: QuarterAdapterCallBack):RecyclerView.Adapter<QuarterAdapter.RegionViewHolder>() {

   private val diffCallBack = object : DiffUtil.ItemCallback<Quarter>(){
        override fun areItemsTheSame(oldItem: Quarter, newItem: Quarter): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Quarter, newItem: Quarter): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallBack)


    inner class RegionViewHolder(binding:AdapterRegionBinding):RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                callBack.onItemClick(differ.currentList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        val binding = AdapterRegionBinding.inflate(LayoutInflater.from(parent.context))
        return RegionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        if (position>=0){
            holder.itemView.adapter_region_title.text = differ.currentList[position].name
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface QuarterAdapterCallBack{
        fun onItemClick(quarter:Quarter)
    }


}