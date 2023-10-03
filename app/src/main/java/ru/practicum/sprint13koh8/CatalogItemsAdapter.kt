package ru.practicum.sprint13koh8

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class CatalogItemsAdapter : RecyclerView.Adapter<CatalogItemViewHolder>() {

    private var items: List<CatalogItemViewData> = emptyList()
    var onAddToCartClickListener: OnAddToCartClickListener = OnAddToCartClickListener {}
    var onAddCountClickListener: OnAddCountClickListener = OnAddCountClickListener {}
    var onRemoveCountClickListener: OnRemoveCountClickListener = OnRemoveCountClickListener {}

    fun setItems(newItems: List<CatalogItemViewData>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return items.size
            }

            override fun getNewListSize(): Int {
                return newItems.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition].id == newItems[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition] == newItems[newItemPosition]
            }

        })
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogItemViewHolder {
        return CatalogItemViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CatalogItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.binding.addToCart.setOnClickListener {
            onAddToCartClickListener.onAddToCartClick(item)
        }
        holder.binding.addCount.setOnClickListener {
            onAddCountClickListener.onAddCountClick(item)
        }
        holder.binding.removeCount.setOnClickListener {
            onRemoveCountClickListener.onRemoveCountClick(item)
        }
    }
}

fun interface OnAddToCartClickListener {
    fun onAddToCartClick(item: CatalogItemViewData)
}

fun interface OnAddCountClickListener {
    fun onAddCountClick(item: CatalogItemViewData)
}

fun interface OnRemoveCountClickListener {
    fun onRemoveCountClick(item: CatalogItemViewData)
}