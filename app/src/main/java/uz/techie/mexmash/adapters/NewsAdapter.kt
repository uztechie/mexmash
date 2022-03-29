package uz.techie.mexmash.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.adapter_news.view.*
import uz.techie.mexmash.R
import uz.techie.mexmash.models.News
import uz.techie.mexmash.util.Utils

class NewsAdapter:RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private var previousPosition = -1
    private var currentPosition = -1
    private var isExpanded = false

    private val diffCallBack = object:DiffUtil.ItemCallback<News>(){
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallBack)

    inner class NewsViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

        init {
            itemView.adapter_news_arrow.setOnClickListener {

            }
        }

        fun bind(news: News) {
            itemView.apply {
                adapter_news_title.text = Html.fromHtml(news.title?:"")
                adapter_news_short_desc.text = Html.fromHtml(news.desc?:"")
                adapter_news_full_desc.text = Html.fromHtml(news.desc?:"")
                adapter_news_date.text = Utils.reformatDateFromStringLocale(news.created_at)


                Glide.with(adapter_news_image)
                    .load(news.image)
                    .apply(options)
                    .into(adapter_news_image)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        if (position>-1){
            val news = differ.currentList[position]
            holder.bind(news)


            if (position == currentPosition) {
                holder.itemView.adapter_news_full_desc.visibility = View.VISIBLE
                holder.itemView.adapter_news_short_desc.visibility = View.GONE
                expandItem(holder.itemView.adapter_news_arrow)
            } else {
                holder.itemView.adapter_news_full_desc.visibility = View.GONE
                holder.itemView.adapter_news_short_desc.visibility = View.VISIBLE
                collapseItem(holder.itemView.adapter_news_arrow)
            }
            if (isExpanded) {
                holder.itemView.adapter_news_full_desc.visibility = View.GONE
                holder.itemView.adapter_news_short_desc.visibility = View.VISIBLE
                collapseItem(holder.itemView.adapter_news_arrow)
            }




            holder.itemView.setOnClickListener {

                if (currentPosition >= 0) {
                    val prev: Int = currentPosition
                    notifyItemChanged(prev)
                }
                isExpanded = holder.itemView.adapter_news_full_desc.visibility == View.VISIBLE
                currentPosition = position
                notifyItemChanged(currentPosition)

            }


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


    private fun expandItem(view: View) {
        val rotateAnimation = RotateAnimation(
            0.0f,
            180.0f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotateAnimation.interpolator = DecelerateInterpolator()
        rotateAnimation.repeatCount = 0
        rotateAnimation.duration = 500
        rotateAnimation.fillAfter = true
        view.startAnimation(rotateAnimation)
    }

    private fun collapseItem(view: View) {
        val rotateAnimation = RotateAnimation(
            180.0f,
            0.0f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotateAnimation.interpolator = DecelerateInterpolator()
        rotateAnimation.repeatCount = 0
        rotateAnimation.duration = 500
        rotateAnimation.fillAfter = true
        view.startAnimation(rotateAnimation)
    }
}