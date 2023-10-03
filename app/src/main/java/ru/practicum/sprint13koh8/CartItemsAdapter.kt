package ru.practicum.sprint13koh8

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class CartItemsAdapter : RecyclerView.Adapter<CartItemViewHolder>() {

    private var items: List<CartItem> = emptyList()
    var onAddCountClickListener: OnCartAddCountClickListener = OnCartAddCountClickListener {}
    var onRemoveCountClickListener: OnCartRemoveCountClickListener =
        OnCartRemoveCountClickListener {}

    fun setItems(newItems: List<CartItem>) {
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        return CartItemViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.binding.addCount.setOnClickListener {
            onAddCountClickListener.onAddCountClick(item)
        }
        holder.binding.removeCount.setOnClickListener {
            onRemoveCountClickListener.onRemoveCountClick(item)
        }
    }
}


fun interface OnCartAddCountClickListener {
    fun onAddCountClick(item: CartItem)
}

fun interface OnCartRemoveCountClickListener {
    fun onRemoveCountClick(item: CartItem)
}