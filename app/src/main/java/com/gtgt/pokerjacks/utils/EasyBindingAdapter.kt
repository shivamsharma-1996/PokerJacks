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

abstract class EasyBindingAdapter<T, U : ViewDataBinding>(
    private val layoutId: Int,
    private val diffCallback: DiffUtil.ItemCallback<T>? = null,
    private val isLoading: ((T) -> Boolean)? = null,
    private val onUpdated: ((Boolean) -> Unit)? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var currentList = listOf<T>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == 0) Holder<U>(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                layoutId, parent, false
            )
        )
        else LoadingHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.progressbar_list,
                parent,
                false
            )
        )

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

    class Holder<U : ViewDataBinding>(val binding: U) : RecyclerView.ViewHolder(binding.root)
    class LoadingHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    abstract fun onBindViewHolder(holder: Holder<U>, position: Int)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            if (holder is Holder<*>) {
                onBindViewHolder(holder as Holder<U>, position)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun getItemViewType(position: Int): Int {
        try {
            val currentItem = getItem(position)

            isLoading?.invoke(currentItem).let {
                if (it != null && it) {
                    return -1
                }
            }
        } catch (ex: java.lang.Exception) {

        }
        return 0
    }

    open fun submitList(newList: List<T>?) {
        if (newList != null) {
            if (diffCallback == null || currentList.isEmpty()) {
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
                        result.dispatchUpdatesTo(this)
                        if (onUpdated != null) {
                            val haveLengthChanges =
                                if (currentList == newList) {
                                    var haveChanges = false
                                    currentList.forEachIndexed { index, item ->
                                        if (item != newList[index]) {
                                            haveChanges = true
                                            return@forEachIndexed
                                        }
                                    }
                                    haveChanges
                                } else {
                                    true
                                }
                            onUpdated.invoke(haveLengthChanges)
                        }
                        currentList = newList
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