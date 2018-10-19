package core;

import com.jaunt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ProthomAloScrapper extends NewsScrapper{

    public ProthomAloScrapper() {
        this.targetAddress = "https://en.prothomalo.com/";
        this.headlineTags = addHeadlineTags();
    }

    // get all the Element object related with the news heading
    // in general, anchor tags <a>
    @Override
    HashMap<String, String> addHeadlineTags() {
        UserAgent userAgent = new UserAgent();
        HashMap<String, String> headlineTags2 = new HashMap<>();

        try {
            userAgent.visit(this.targetAddress);
            Elements newsWidget = userAgent.doc.findEvery("<div class='news_widget widget'>");

            for (Element ol: newsWidget) {
                Elements headlines = ol.findEvery("<h2 class='title'>").findEvery("<a>");

                // add the tags
                for (Element h: headlines) {
                    headlineTags2.put(h.getChildText(), this.getNewsLink(h));
                }
            }

        } catch (ResponseException e) {
            e.printStackTrace();
        }

        return headlineTags2;
    }

    public HashMap<String, String> getHeadlineTags() {
        return this.headlineTags;
    }

    // get the link to actual news content from anchor tag href
    @Override
    public String getNewsLink(Element news) {
        try {
            return news.getAt("href");
        } catch (NotFound notFound) {
            notFound.printStackTrace();
        }
        return "";
    }

    public static void main(String args[]) {
        ProthomAloScrapper sc = new ProthomAloScrapper();
        // sc.printHeadlines();
    }

}
