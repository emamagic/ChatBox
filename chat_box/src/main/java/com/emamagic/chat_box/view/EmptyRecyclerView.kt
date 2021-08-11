package com.emamagic.chat_box.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class EmptyRecyclerView(context: Context, attrs: AttributeSet? = null) : RecyclerView(context, attrs) {
    private var emptyView: View? = null

    private val observer: AdapterDataObserver =
        object : AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkIfEmpty()
            }
        }

    internal fun checkIfEmpty() {
        emptyView?.visibility = if ((adapter?.itemCount ?: 0) > 0) View.GONE else View.VISIBLE
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        val oldAdapter = getAdapter()
        oldAdapter?.unregisterAdapterDataObserver(observer)
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(observer)
    }

    fun setEmptyView(emptyView: View?) {
        this.emptyView = emptyView
        checkIfEmpty()
    }
}