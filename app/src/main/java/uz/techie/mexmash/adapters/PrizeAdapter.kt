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
import uz.techie.mexmash.R
import uz.techie.mexmash.models.Prize
import uz.techie.mexmash.util.Constants
import uz.techie.mexmash.util.SharedPref

class PrizeAdapter(val callback:PrizeAdapterCallback):RecyclerView.Adapter<PrizeAdapter.PrizeViewHolder>() {

    private val diffCallBack = object:DiffUtil.ItemCallback<Prize>(){
        override fun areItemsTheSame(oldItem: Prize, newItem: Prize): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Prize, newItem: Prize): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallBack)

    inner class PrizeViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

        init {
            itemView.setOnClickListener {
                differ.currentList[adapterPosition].image?.let { it1 -> callback.onItemClick(it1) }
            }

            itemView.adapter_prize_image.setOnClickListener {
                differ.currentList[adapterPosition].image?.let { it1 -> callback.onItemClick(it1) }
            }
        }

        fun bind(prize: Prize) {
            itemView.apply {
                adapter_prize_title.text = prize.name
                adapter_prize_point.text = "${prize.point} бал"
                adapter_prize_kg.text = "${prize.kg} кг"


                if (prize.id == -1){
                    adapter_prize_point.text = "${context.getString(R.string.sizda_mavjud)} ${prize.point} бал"
                    adapter_prize_kg.text = "${context.getString(R.string.sizda_mavjud)} ${prize.kg} кг"

                    Glide.with(adapter_prize_image)
                        .load(R.drawable.user)
                        .apply(options)
                        .into(adapter_prize_image)

                    adapter_prize_layout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    adapter_prize_title.setTextColor(ContextCompat.getColor(context, R.color.white))
                    adapter_prize_point.setTextColor(ContextCompat.getColor(context, R.color.white))
                    adapter_prize_kg.setTextColor(ContextCompat.getColor(context, R.color.white))
                    adapter_prize_prize_name.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
                else{
                    Glide.with(adapter_prize_image)
                        .load(prize.image)
                        .apply(options)
                        .into(adapter_prize_image)
                }

                if (Constants.USER_TYPE == Constants.USER_TYPE_DEALER){
                    adapter_prize_kg.visibility = View.VISIBLE
                }
                else{
                    adapter_prize_kg.visibility = View.GONE
                }

                adapter_prize_prize_name.text = prize.level_name
                Glide.with(adapter_prize_cup)
                    .load(prize.level_image)
                    .apply(options)
                    .into(adapter_prize_cup)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrizeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_prize, parent, false)
        return PrizeViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrizeViewHolder, position: Int) {
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
        .placeholder(R.drawable.progress_animation)
        .error(R.drawable.no_image)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .priority(Priority.HIGH)


    interface PrizeAdapterCallback{
        fun onItemClick(url:String)
    }

}
