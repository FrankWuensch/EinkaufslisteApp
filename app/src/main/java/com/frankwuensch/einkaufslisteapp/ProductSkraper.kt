package com.frankwuensch.einkaufslisteapp

import android.util.Log
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.a

/**
 * A 'suspend' function performs its work without blocking the thread.
 * It is the correct way to handle network operations in Kotlin.
 *
 * This function scrapes a given URL for all hyperlink (`<a>`) tags and
 * returns a list of the URLs found in their `href` attributes.
 *
 * @param url The website URL to scrape.
 * @return A list of all link URLs found on the page.
 */
suspend fun fetchAllLinks(url: String): List<String> {
    Log.d("Scraping", "Scraping has started for URL: $url")

    // skrape() is a library function that handles the network request.
    // It must be called from a coroutine or another suspend function.
    val scrapedLinks = skrape(HttpFetcher) {
        request {
            this.url = url
            // It's good practice to set a timeout for network requests.
            timeout = 15000 // 15 seconds
        }
        response {
            htmlDocument {
                // relaxed = true helps parse HTML that might not be perfectly structured.
                relaxed = true

                // Select all 'a' (hyperlink) elements on the page.
                a {
                    // From all the 'a' elements found...
                    findAll {
                        // ...create a list by mapping each element to its 'href' attribute value.
                        // This extracts the actual URL string.
                        map { it.attribute("href") }
                    }
                }
            }
        }
    }
    Log.d("Scraping", "Scraping finished. Found ${scrapedLinks.size} links.")
    return scrapedLinks
}
