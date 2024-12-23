package edu.skku.cs.team13

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView


class CheckableSpinnerAdapter internal constructor(
    private val context: Context,
    private val headerText: String,
    private val allItems: List<SpinnerItem>,
    private val selectedItems: MutableSet<SpinnerItem>
) : BaseAdapter() {
    internal class SpinnerItem(val key: String? = null, val value: String? = null, val txt: String)

    override fun getCount(): Int {
        return allItems.size + 1
    }

    override fun getItem(position: Int): Any? {
        return if (position < 1) {
            null
        } else {
            allItems[position - 1]
        }
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        val generatedView: View
        if (view != null && view.tag is ViewHolder) {
            holder = view.tag as ViewHolder
            generatedView = view
        } else {
            val layoutInflator = LayoutInflater.from(context)
            generatedView = layoutInflator.inflate(R.layout.checkable_spinner_item, parent, false)
            holder = ViewHolder(
                generatedView.findViewById(R.id.text),
                generatedView.findViewById(R.id.checkbox)
            )
            generatedView.tag = holder
        }
        if (position < 1) {
            holder.mCheckBox.setVisibility(View.GONE)
            holder.mTextView.setText(headerText)
            holder.mTextView.isClickable = false
        } else {
            val listPos = position - 1
            holder.mCheckBox.visibility = View.VISIBLE
            holder.mTextView.text = allItems[listPos].txt
            val item = allItems[listPos]
            val isSel = selectedItems.contains(item)
            holder.mCheckBox.setOnCheckedChangeListener(null)
            holder.mCheckBox.isChecked = isSel
            holder.mCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedItems.add(item)
                } else {
                    selectedItems.remove(item)
                }
            }
            holder.mTextView.setOnClickListener { holder.mCheckBox.toggle() }
        }
        return generatedView
    }

    inner class ViewHolder(val mTextView: TextView, val mCheckBox: CheckBox)
}