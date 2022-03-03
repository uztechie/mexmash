package uz.techie.mexmash.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.adapter_prize.view.*
import kotlinx.android.synthetic.main.adapter_promocode.view.*
import uz.techie.mexmash.R
import uz.techie.mexmash.models.Point
import uz.techie.mexmash.models.Prize
import uz.techie.mexmash.util.Constants
import uz.techie.mexmash.util.SharedPref
import uz.techie.mexmash.util.Utils

class PromoCodeAdapter:RecyclerView.Adapter<PromoCodeAdapter.PromoCodeViewHolder>() {

    private val diffCallBack = object:DiffUtil.ItemCallback<Point>(){
        override fun areItemsTheSame(oldItem: Point, newItem: Point): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Point, newItem: Point): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallBack)

    inner class PromoCodeViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        fun bind(point: Point) {
            itemView.apply {
                if (Constants.USER_TYPE == Constants.USER_TYPE_SELLER){
                    adapter_promocode_code.text = point.code
                }
                else{
                    adapter_promocode_code.text = point.total_kg.toString() +" "+ context.getString(R.string.kg)
                }


               adapter_promocode_point.text = "${point.point} ${context.getString(R.string.bal)}"
               adapter_promocode_date.text = point.date?.let {
                   Utils.reformatDateFromStringLocaleDividedTime(
                       it
                   )
               }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoCodeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_promocode, parent, false)
        return PromoCodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: PromoCodeViewHolder, position: Int) {
        if (position>-1){
            val prize = differ.currentList[position]
            holder.bind(prize)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var options: RequestOptions = RequestOptions()
        .centerCrop()
        .placeholder(R.drawable.loading_gif)
        .error(R.drawable.no_image)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .priority(Priority.HIGH)


}