package ru.practicum.sprint13koh8

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.practicum.sprint13koh8.databinding.VCartItemBinding

data class CartItem(
    val id: String,
    val catalogItem: CatalogItem,
    val count: Int,
) {
    val sum: Int
        get() = catalogItem.price.times(count).toInt()
}


class CartItemViewHolder(
    parent: ViewGroup,
    val binding: VCartItemBinding = VCartItemBinding.inflate(
        LayoutInflater.from(
            parent.context
        ), parent, false
    )
) : RecyclerView.ViewHolder(
    binding.root
) {

    fun bind(item: CartItem) {
        binding.root

        Glide
            .with(binding.root.context)
            .load(item.catalogItem.imageUrl)
            .centerCrop()
            .into(binding.image)
        binding.title.text = item.catalogItem.name

        binding.count.text = item.count.toString()
    }

}