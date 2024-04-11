package com.rendox.grocerygenius.screens.grocery_lists_dashboard

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

class GroceryListsDashboardAdapter(
    recyclerView: RecyclerView,
    private var groceryLists: List<GroceryListsDashboardItem>,
    private val onMoveItem: (Int, Int) -> Unit,
) : RecyclerView.Adapter<GroceryListsDashboardViewHolder>(), ItemTouchHelperAdapter {

    private val touchHelperCallback = ItemTouchHelperCallback(this)
    private val touchHelper = ItemTouchHelper(touchHelperCallback)

    init {
        touchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroceryListsDashboardViewHolder {
        return GroceryListsDashboardViewHolder(
            composeView = ComposeView(parent.context),
            onDrag = { touchHelper.startDrag(it) },
        )
    }

    override fun onBindViewHolder(holder: GroceryListsDashboardViewHolder, position: Int) {
        return holder.bind(
            groceryList = groceryLists[position]
        )
    }

    override fun getItemCount() = groceryLists.size

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        groceryLists = this.groceryLists.toMutableList().also {
            Collections.swap(it, fromPosition, toPosition)
        }
        notifyItemMoved(fromPosition, toPosition)
        onMoveItem(fromPosition, toPosition)
    }

    fun updateGroceryLists(newGroceryLists: List<GroceryListsDashboardItem>) {
        groceryLists = newGroceryLists
        notifyDataSetChanged()
    }
}