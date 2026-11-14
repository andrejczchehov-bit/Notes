package com.example.mynotes.data

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

/**
 * Парсер RSS-ленты Lenta.ru на базе XmlPullParser (Лабораторная работа №10).
 */
class RssParser {
    fun parse(xmlString: String): List<Post> {
        val posts = mutableListOf<Post>()
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(StringReader(xmlString))

        var eventType = parser.eventType
        var currentTitle = ""
        var currentDescription = ""
        var currentImageUrl = ""
        var inItem = false
        var idCounter = 1

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val name = parser.name
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (name.equals("item", ignoreCase = true)) {
                        inItem = true
                        currentTitle = ""
                        currentDescription = ""
                        currentImageUrl = ""
                    } else if (inItem) {
                        if (name.equals("title", ignoreCase = true)) {
                            currentTitle = parser.nextText() ?: ""
                        } else if (name.equals("description", ignoreCase = true)) {
                            currentDescription = parser.nextText() ?: ""
                        } else if (name.equals("enclosure", ignoreCase = true)) {
                            currentImageUrl = parser.getAttributeValue(null, "url") ?: ""
                        } else if (name.equals("image", ignoreCase = true) && currentImageUrl.isBlank()) {
                            // Иногда картинка в теге image или media:content
                            currentImageUrl = parser.getAttributeValue(null, "url") ?: ""
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (name.equals("item", ignoreCase = true)) {
                        inItem = false
                        val cleanTitle = currentTitle.trim()
                        val cleanDesc = currentDescription.trim().ifBlank { cleanTitle }
                        val cleanImage = currentImageUrl.trim().ifBlank {
                            "https://picsum.photos/id/${(idCounter % 50) + 10}/400/300"
                        }
                        if (cleanTitle.isNotBlank()) {
                            posts.add(
                                Post(
                                    id = idCounter++,
                                    title = cleanTitle,
                                    body = cleanDesc,
                                    imageUrl = cleanImage
                                )
                            )
                        }
                    }
                }
            }
            eventType = parser.next()
        }
        return posts
    }
}
