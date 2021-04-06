package com.example.top10downloader

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ViewHolder(v: View) {
    val tvName: TextView = v.findViewById(R.id.tvName)
    val tvArtist: TextView = v.findViewById(R.id.tvArtist)
    val tvSummary: TextView = v.findViewById(R.id.tvSummary)
    val tvDate : TextView = v.findViewById((R.id.tvDate))
    val tvTitle : TextView = v.findViewById(R.id.tvTitle)
}


class FeedAdapter(
    context: Context,
    private val resource: Int,
    private val application: List<FeedEntry>
) : ArrayAdapter<FeedEntry>(context, resource) {

    private val TAG = "FeedAdapter"
    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return application.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            view = inflater.inflate(resource, parent , false)
            Log.d(TAG, "getView() : new view created")
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            Log.d(TAG, "getView() : view reused on scrolling")
            viewHolder = view.tag as ViewHolder
        }


        val currentApp = application[position]

        viewHolder.tvName.text = currentApp.name
        viewHolder.tvArtist.text = currentApp.artist
        viewHolder.tvSummary.text = currentApp.summary
        viewHolder.tvDate.text = currentApp.releaseDate
        viewHolder.tvTitle.text = currentApp.title

        return view
    }
}