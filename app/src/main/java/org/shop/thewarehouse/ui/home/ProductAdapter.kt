package org.shop.thewarehouse.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.shop.thewarehouse.R
import org.shop.thewarehouse.data.model.Product
import org.shop.thewarehouse.databinding.ListItemProductBinding

class ProductAdapter(private val shopInterface: ShopInterface) :
    ListAdapter<Product, ProductAdapter.ViewHolder>(ProductDiffCallback) {

    class ViewHolder(private val binding: ListItemProductBinding,val shopInterface: ShopInterface) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {

            binding.run {
                this.product = product

                executePendingBindings()
            }
            itemView.setOnClickListener{
                shopInterface.onItemClick(product)
            }

        }

    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = ListItemProductBinding.inflate(layoutInflater, viewGroup, false)
        return ViewHolder(binding,shopInterface)

    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    interface ShopInterface{
        fun addItem(product: Product)
        fun onItemClick(product: Product)

    }
}

object ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}