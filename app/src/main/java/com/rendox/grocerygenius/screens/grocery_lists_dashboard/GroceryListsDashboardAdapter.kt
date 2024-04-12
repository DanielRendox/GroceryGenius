package com.rendox.grocerygenius.screens.grocery_lists_dashboard

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

class GroceryListsDashboardAdapter(
    recyclerView: RecyclerView,
    private var groceryLists: List<GroceryListsDashboardItem>,
    private val updateLists: (List<GroceryListsDashboardItem>) -> Unit,
) : RecyclerView.Adapter<GroceryListsDashboardViewHolder>(), ItemTouchHelperAdapter {

    private val touchHelperCallback = ItemTouchHelperCallback(
        adapter = this,
        onClearView = { updateLists(groceryLists) },
    )
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
    }

    fun updateGroceryLists(newGroceryLists: List<GroceryListsDashboardItem>) {
        val diffCallback = DashboardListDiffUtilCallback(groceryLists, newGroceryLists)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        groceryLists = newGroceryLists
        diffResult.dispatchUpdatesTo(this)
    }
}