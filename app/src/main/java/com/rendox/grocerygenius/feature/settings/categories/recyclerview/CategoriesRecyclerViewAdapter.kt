package com.rendox.grocerygenius.feature.settings.categories.recyclerview

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.rendox.grocerygenius.model.Category
import com.rendox.grocerygenius.ui.helpers.DragHandleReorderItemTouchHelperCallback
import java.util.Collections

class CategoriesRecyclerViewAdapter(
    recyclerView: RecyclerView,
    var categories: List<Category>,
    private val updateLists: (List<Category>) -> Unit,
) : RecyclerView.Adapter<CategoryViewHolder>() {

    private val touchHelperCallback = DragHandleReorderItemTouchHelperCallback(
        onItemMove = { fromPosition, toPosition ->
            onItemMove(fromPosition, toPosition)
        },
        onClearView = { updateLists(categories) },
    )
    private val touchHelper = ItemTouchHelper(touchHelperCallback)

    init {
        touchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = CategoryViewHolder(
        composeView = ComposeView(parent.context),
        onDrag = { touchHelper.startDrag(it) },
    )

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int
    ) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size

    private fun onItemMove(fromPosition: Int, toPosition: Int) {
        categories = this.categories.toMutableList().also {
            Collections.swap(it, fromPosition, toPosition)
        }
        notifyItemMoved(fromPosition, toPosition)
    }
}