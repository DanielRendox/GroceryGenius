package com.rendox.grocerygenius.screens.grocery_lists_dashboard

import androidx.recyclerview.widget.DiffUtil

class DashboardListDiffUtilCallback(
    private val oldList: List<GroceryListsDashboardItem>,
    private val newList: List<GroceryListsDashboardItem>,
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