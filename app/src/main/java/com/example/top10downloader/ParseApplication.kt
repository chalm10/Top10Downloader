package com.example.top10downloader

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

class ParseApplication {
    private val TAG = "ParseApplication"
    val application = ArrayList<FeedEntry>()

    fun parse(xmlData: String): Boolean {
//        Log.d(TAG, "parse fn called with $xmlData")
        var status = true
        var inEntry = false
        var inTitle = false
        var textValue = ""

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            xpp.setInput(xmlData.reader())
            var eventType = xpp.eventType
            var currentRecord = FeedEntry()
            while (eventType != XmlPullParser.END_DOCUMENT) {

                val tagName = xpp.name?.toLowerCase()
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        Log.d(TAG, "parse: Start tag for $tagName")
                        if (tagName == "entry") {
                            inEntry = true
                        }
                        else if (tagName == "title"  && !inEntry){
                            inTitle = true
                        }
                    }

                    XmlPullParser.TEXT -> textValue = xpp.text

                    XmlPullParser.END_TAG -> {
                        Log.d(TAG, "parse : Ending tag for " + tagName)
                        if (inEntry) {
                            when (tagName) {
                                "name" -> currentRecord.name = textValue
                                "artist" -> currentRecord.artist = textValue
                                "releasedate" -> currentRecord.releaseDate = textValue
                                "summary" -> currentRecord.summary = textValue
                                "image" -> currentRecord.imageURL = textValue
                                "entry" -> {
                                    application.add(currentRecord)
                                    inEntry = false
                                    currentRecord = FeedEntry()     //creates a new object
                                }
                            }
                        }
                        else if (inTitle){
                            currentRecord.title = textValue
                            inTitle = false
                        }
                    }
                }
                eventType = xpp.next()  //nothing else to do
            }
        } catch (e: Exception) {
            status = false
            e.printStackTrace()
        }
        return status
    }
}