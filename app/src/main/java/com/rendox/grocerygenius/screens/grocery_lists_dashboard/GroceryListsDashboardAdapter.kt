package com.rendox.grocerygenius.screens.grocery_lists_dashboard

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView

class GroceryListsDashboardAdapter(
    private val groceryLists: List<GroceryListsDashboardItem>,
) : RecyclerView.Adapter<GroceryListsDashboardViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroceryListsDashboardViewHolder {
        return GroceryListsDashboardViewHolder(
            ComposeView(parent.context)
        )
    }

    override fun onBindViewHolder(holder: GroceryListsDashboardViewHolder, position: Int) {
        return holder.bind(
            groceryList = groceryLists[position]
        )
    }

    override fun getItemCount(): Int {
        return groceryLists.size
    }
}