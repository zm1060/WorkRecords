package com.benjaminwan.ocr.ncnn

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.benjaminwan.ocr.ncnn.models.Records
import org.w3c.dom.Text

interface OnItemClickListener {

    fun onclick(v: View, position: Int)

}

class RecordsListAdapter : ListAdapter<Records, RecordsListAdapter.RecordsViewHolder>(RecordssComparator()) {

    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordsViewHolder {
        return RecordsViewHolder.create(parent)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onBindViewHolder(holder: RecordsViewHolder, position: Int) {
        val current = getItem(position)
        val str = current.id.toString()+","+current.date +","+ current.about_address + ","+current.detail_address +","+current.area+","+current.length+","+current.status
        holder.bind(str)

        holder.itemView.findViewById<Button>(R.id.button_look).setOnClickListener {

                Log.d("test","点击了第${position}个条目")
                var intent= Intent()
                intent.setClass(it.context , RecordActivity::class.java)
                intent.putExtra("key",str)
                it.context.startActivity(intent)

        }

    }

    class RecordsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val view_about_address: TextView = itemView.findViewById(R.id.textView2)
        private val view_date: TextView = itemView.findViewById(R.id.textView3)
        private val view_status: TextView = itemView.findViewById(R.id.textView4)
        fun bind(text: String?) {

            var delimiter = ","

            val parts = text?.split(delimiter)

            view_about_address.text = parts?.get(2) ?: ""
            view_date.text = parts?.get(1) ?: ""
            view_status.text  = parts?.get(6) ?: ""

        }

        companion object {
            fun create(parent: ViewGroup): RecordsViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.record_item, parent, false)
                return RecordsViewHolder(view)
            }
        }
    }



    class RecordssComparator : DiffUtil.ItemCallback<Records>() {
        override fun areItemsTheSame(oldItem: Records, newItem: Records): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Records, newItem: Records): Boolean {
            return oldItem.about_address == newItem.about_address && oldItem.status == newItem.status && oldItem.date == newItem.date && oldItem.length == newItem.length && oldItem.area == newItem.area && oldItem.detail_address == newItem.detail_address && oldItem.status == newItem.status
        }
    }
}
