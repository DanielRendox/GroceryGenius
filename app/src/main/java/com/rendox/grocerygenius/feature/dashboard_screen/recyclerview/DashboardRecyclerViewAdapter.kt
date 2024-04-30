package com.rendox.grocerygenius.feature.dashboard_screen.recyclerview

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
    var groceryLists: List<GroceryList>,
    private val updateLists: (List<GroceryList>) -> Unit,
    private val onItemClicked: (String) -> Unit,
    private val onAdderItemClicked: () -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

    override fun getItemViewType(position: Int): Int {
        return if (position == groceryLists.size) {
            VIEW_TYPE_ADDER
        } else {
            VIEW_TYPE_GROCERY_LIST
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder = when(viewType) {
        VIEW_TYPE_GROCERY_LIST -> DashboardItemViewHolder(
            composeView = ComposeView(parent.context),
            onDrag = { touchHelper.startDrag(it) },
            onViewClicked = onItemClicked,
        )
        VIEW_TYPE_ADDER -> GroceryListAdderItemViewHolder(
            composeView = ComposeView(parent.context),
            onViewClicked = onAdderItemClicked,
        )

        else -> throw IllegalArgumentException("Unknown view type: $viewType")
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) = when(holder) {
        is DashboardItemViewHolder -> holder.bind(groceryLists[position])
        is GroceryListAdderItemViewHolder -> holder.bind()
        else -> throw IllegalArgumentException("Unknown view holder: $holder")
    }

    override fun getItemCount() = groceryLists.size + 1

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

    companion object {
        private const val VIEW_TYPE_GROCERY_LIST = 0
        private const val VIEW_TYPE_ADDER = 1
    }
}