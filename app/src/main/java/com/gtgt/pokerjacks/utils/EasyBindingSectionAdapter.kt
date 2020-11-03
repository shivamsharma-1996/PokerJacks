package com.gtgt.pokerjacks.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gtgt.pokerjacks.R
import com.gtgt.pokerjacks.extensions.runOnMain
import kotlin.concurrent.thread

abstract class EasyBindingSectionAdapter<T, U : ViewDataBinding, V : ViewDataBinding>(
    private val headerLayouId: Int,
    private val itemLayoutId: Int,
    private val isHeader: (T) -> Boolean,
    private val diffCallback: DiffUtil.ItemCallback<T>,
    private val isLoading: ((T) -> Boolean)? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var currentList = listOf<T>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            0 -> HeaderHolder(
                DataBindingUtil.inflate<U>(
                    LayoutInflater.from(parent.context),
                    headerLayouId, parent, false
                )
            )
            1 -> ItemHolder(
                DataBindingUtil.inflate<V>(
                    LayoutInflater.from(parent.context),
                    itemLayoutId, parent, false
                )
            )
            else -> LoadingHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.progressbar_list,
                    parent,
                    false
                )
            )
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            if (holder is HeaderHolder<*>) {
                onBindHeaderHolder(holder as HeaderHolder<U>, position)
            } else if (holder is ItemHolder<*>) {
                onBindItemHolder(holder as ItemHolder<V>, position)
            }
        } catch (ex: Exception) {

        }
    }

    abstract fun onBindHeaderHolder(holder: HeaderHolder<U>, position: Int)
    abstract fun onBindItemHolder(holder: ItemHolder<V>, position: Int)
    class LoadingHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getItemViewType(position: Int): Int {
        try {
            val currentItem = getItem(position)

            isLoading?.invoke(currentItem).let {
                if (it != null && it) {
                    return -1
                }
            }

            return if (isHeader(currentItem)) {
                0
            } else {
                1
            }
        } catch (ex: Exception) {
            return -1
        }
    }

    class HeaderHolder<U : ViewDataBinding>(val binding: U) : RecyclerView.ViewHolder(binding.root)
    class ItemHolder<V : ViewDataBinding>(val binding: V) : RecyclerView.ViewHolder(binding.root)


    fun getItemAt(position: Int) = currentList[position]
    fun getItem(position: Int) = currentList[position]

    fun onItemRangeMoved(callback: () -> Unit) {
        registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                callback()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                callback()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                callback()
            }
        })
    }

    override fun getItemCount() = currentList.size


    fun submitList(newList: List<T>?) {
        if (newList != null) {
            if (currentList.isEmpty()) {
                currentList = newList
                notifyDataSetChanged()
            } else {
                thread {
                    val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                            try {
                                diffCallback.areItemsTheSame(
                                    currentList[oldItemPosition],
                                    newList[newItemPosition]
                                )
                            } catch (ex: java.lang.Exception) {
                                false
                            }

                        override fun areContentsTheSame(
                            oldItemPosition: Int,
                            newItemPosition: Int
                        ) =
                            try {
                                diffCallback.areContentsTheSame(
                                    currentList[oldItemPosition],
                                    newList[newItemPosition]
                                )
                            } catch (ex: Exception) {
                                false
                            }

                        override fun getOldListSize() = currentList.size

                        override fun getNewListSize() = newList.size

                    })
                    runOnMain {
                        currentList = newList
                        result.dispatchUpdatesTo(this)
                    }
                }
            }
        }
    }

    fun addList(list: List<T>) {
        val newList = ArrayList(currentList)
        newList.addAll(list)
        submitList(newList)
    }

    fun onCurrentListChanged(previousList: List<T>, currentList: List<T>) {}
}