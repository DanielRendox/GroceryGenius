package com.rendox.grocerygenius.screens.grocery_lists_dashboard.recyclerview

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.rendox.grocerygenius.model.GroceryList
import com.rendox.grocerygenius.ui.helpers.DragHandleReorderItemTouchHelperCallback
import java.util.Collections

class DashboardRecyclerViewAdapter(
    recyclerView: RecyclerView,
    private var groceryLists: List<GroceryList>,
    private val updateLists: (List<GroceryList>) -> Unit,
) : RecyclerView.Adapter<DashboardItemViewHolder>() {

    private val touchHelperCallback = DragHandleReorderItemTouchHelperCallback(
        onItemMove = { fromPosition, toPosition ->
            onItemMove(fromPosition, toPosition)
        },
        onClearView = { updateLists(groceryLists) },
    )
    private val touchHelper = ItemTouchHelper(touchHelperCallback)

    init {
        touchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DashboardItemViewHolder {
        return DashboardItemViewHolder(
            composeView = ComposeView(parent.context),
            onDrag = { touchHelper.startDrag(it) },
        )
    }

    override fun onBindViewHolder(holder: DashboardItemViewHolder, position: Int) {
        return holder.bind(
            groceryList = groceryLists[position]
        )
    }

    override fun getItemCount() = groceryLists.size

    private fun onItemMove(fromPosition: Int, toPosition: Int) {
        groceryLists = this.groceryLists.toMutableList().also {
            Collections.swap(it, fromPosition, toPosition)
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    fun updateGroceryLists(newGroceryLists: List<GroceryList>) {
        val diffCallback = DashboardDiffUtilCallback(groceryLists, newGroceryLists)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        groceryLists = newGroceryLists
        diffResult.dispatchUpdatesTo(this)
    }
}