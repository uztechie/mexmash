package uz.techie.mexmash.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.adapter_slider.view.*
import uz.techie.mexmash.R
import uz.techie.mexmash.models.Slider

class SliderAdapter:RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    private val diffCallBack = object:DiffUtil.ItemCallback<Slider>(){
        override fun areItemsTheSame(oldItem: Slider, newItem: Slider): Boolean {
            return oldItem.image == newItem.image
        }
        override fun areContentsTheSame(oldItem: Slider, newItem: Slider): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallBack)

    inner class SliderViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_slider, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        if (position>-1){
            val slider = differ.currentList[position]
            Glide.with(holder.itemView.adapter_slider_image)
                .load(slider.image)
                .into(holder.itemView.adapter_slider_image)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}