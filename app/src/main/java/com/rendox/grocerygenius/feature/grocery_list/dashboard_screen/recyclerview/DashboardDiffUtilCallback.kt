package com.rendox.grocerygenius.feature.grocery_list.dashboard_screen.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.rendox.grocerygenius.model.GroceryList

class DashboardDiffUtilCallback(
    private val oldList: List<GroceryList>,
    private val newList: List<GroceryList>,
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int,
    ) = oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int,
    ) = oldList[oldItemPosition] == newList[newItemPosition]
}