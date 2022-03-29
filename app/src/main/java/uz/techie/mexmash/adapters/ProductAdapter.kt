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
import kotlinx.android.synthetic.main.adapter_product.view.*
import uz.techie.mexmash.R
import uz.techie.mexmash.models.Product
import uz.techie.mexmash.util.Constants
import uz.techie.mexmash.util.Utils

class ProductAdapter:RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var previousPosition = -1
    private var currentPosition = -1
    private var isExpanded = false

    private val diffCallBack = object:DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallBack)

    inner class ProductViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){


        fun bind(product: Product) {
            itemView.apply {
                adapter_product_title.text = Html.fromHtml(product.name?:"")
                adapter_product_short_desc.text = Html.fromHtml(product.desc?:"")
                adapter_product_full_desc.text = Html.fromHtml(product.desc?:"")

                if (Constants.USER_TYPE == Constants.USER_TYPE_SELLER){
                    adapter_product_point.text = "${product.point} ${context.getString(R.string.bal)}"
                }
                else{
                    adapter_product_point.text = "${product.dealer_point} ${context.getString(R.string.bal)}"
                }


                Glide.with(adapter_product_image)
                    .load(product.image)
                    .apply(options)
                    .into(adapter_product_image)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        if (position>-1){
            val product = differ.currentList[position]
            holder.bind(product)


            if (position == currentPosition) {
                holder.itemView.adapter_product_full_desc.visibility = View.VISIBLE
                holder.itemView.adapter_product_short_desc.visibility = View.GONE
                expandItem(holder.itemView.adapter_product_arrow)
            } else {
                holder.itemView.adapter_product_full_desc.visibility = View.GONE
                holder.itemView.adapter_product_short_desc.visibility = View.VISIBLE
                collapseItem(holder.itemView.adapter_product_arrow)
            }
            if (isExpanded) {
                holder.itemView.adapter_product_full_desc.visibility = View.GONE
                holder.itemView.adapter_product_short_desc.visibility = View.VISIBLE
                collapseItem(holder.itemView.adapter_product_arrow)
            }




            holder.itemView.setOnClickListener {

                if (currentPosition >= 0) {
                    val prev: Int = currentPosition
                    notifyItemChanged(prev)
                }
                isExpanded = holder.itemView.adapter_product_full_desc.visibility == View.VISIBLE
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