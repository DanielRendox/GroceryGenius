package com.rendox.grocerygenius.ui.helpers

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.rendox.grocerygenius.screens.grocery_lists_dashboard.recyclerview.GroceryListAdderItemViewHolder

class DragHandleReorderItemTouchHelperCallback(
    private val onItemMove: (fromPosition: Int, toPosition: Int) -> Unit,
    private val onClearView: () -> Unit,
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
) {
    override fun isLongPressDragEnabled() = false

    override fun isItemViewSwipeEnabled() = false

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (target is GroceryListAdderItemViewHolder) return false
        onItemMove(
            viewHolder.bindingAdapterPosition,
            target.bindingAdapterPosition
        )
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        onClearView()
        super.clearView(recyclerView, viewHolder)
    }
}