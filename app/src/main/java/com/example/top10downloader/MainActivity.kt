package com.example.top10downloader

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL

class FeedEntry {
    var name: String = ""
    var artist: String = ""
    var imageURL: String = ""
    var releaseDate: String = ""
    var summary: String = ""
    var title : String = ""

}


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private var downloadData: DownloadData? = null

    private var feedURL: String =
        "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private var feedLimit = 10
    private var feedCheckURL = "invalid"
    private val STATE_URL = "feedURL"
    private val STATE_LIMIT = "feedLimit"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate : Called")
        if (savedInstanceState != null) {
            feedLimit = savedInstanceState.getInt(STATE_LIMIT)
            feedURL = savedInstanceState.getString(STATE_URL).toString()
        }

        downloadURL(feedURL.format(feedLimit))
        Log.d(TAG, "onCreate : Done")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_URL, feedURL)
        outState.putInt(STATE_LIMIT, feedLimit)

    }

    private fun downloadURL(feedURL: String) {

        if (feedCheckURL != feedURL) {
            Log.d(TAG, "downloadURL starting Async")
            downloadData = DownloadData(this, xmlListView)
            downloadData?.execute(feedURL.format(feedLimit))
            feedCheckURL = feedURL
            Log.d(TAG, "downloadURL done")
        } else {
            Log.d(TAG, "downloadURL : same feed chosen hence not downloaded again")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feeds_menu, menu)
        if (feedLimit == 10) {
            menu?.findItem(R.id.menu10)?.isChecked = true
        } else {
            menu?.findItem(R.id.menu25)?.isChecked = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuFree -> feedURL =
                "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
            R.id.menuPaid -> feedURL =
                "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
            R.id.menuSongs -> feedURL =
                "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"
            R.id.menu10, R.id.menu25 -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    feedLimit = 35 - feedLimit
                }
            }
            R.id.menuRefresh -> feedCheckURL = "invalid"
            else -> return super.onOptionsItemSelected(item)
        }
        downloadURL(feedURL.format(feedLimit))
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadData?.cancel(true)

    }

    companion object {
        private class DownloadData(val context: Context, val listView: ListView) :
            AsyncTask<String, Void, String>() {

            private val TAG = "DownloadData"

            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
                val parseApplication = ParseApplication()
                parseApplication.parse(result)
//                val arrayAdapter = ArrayAdapter<FeedEntry>(context , R.layout.list_item , parseApplication.application)
//                listView.adapter = arrayAdapter
                val feedAdapter = FeedAdapter(context, R.layout.list_records, parseApplication.application)
                listView.adapter = feedAdapter
            }

            override fun doInBackground(vararg url: String?): String {
//                val rssFeed = downloadXML(url[0])
                val url = URL(url[0])
                val rssFeed = url.readText()
                if (rssFeed.isEmpty()) {
                    Log.d(TAG, "doInBackground : Error Downloading")
                }
                return rssFeed
            }

//            private fun downloadXML(urlPath: String?): String {
//                return URL(urlPath).readText()
//            }

        }
    }
}
