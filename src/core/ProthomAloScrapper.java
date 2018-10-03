package core;

import com.jaunt.*;
import java.util.ArrayList;

public class ProthomAloScrapper extends NewsScrapper{

    ProthomAloScrapper() {
        this.targetAddress = "https://en.prothomalo.com/";
        this.headlineTags = new ArrayList<>();
        addHeadlineTags();
    }

    // get all the Element object related with the news heading
    // in general, anchor tags <a>
    @Override
    void addHeadlineTags() {
        UserAgent userAgent = new UserAgent();

        try {
            userAgent.visit(this.targetAddress);
            Elements newsWidget = userAgent.doc.findEvery("<div class='news_widget widget'>");

            for (Element ol: newsWidget) {
                Elements headlines = ol.findEvery("<h2 class='title'>").findEvery("<a>");

                // add the tags
                for (Element h: headlines) {
                    this.headlineTags.add(h);

                    // output each headline with it's content link
                    System.out.println(h.getChildText());
                    System.out.println("Link: " + this.getNewsLink(h) + "\n");
                }
            }

        } catch (ResponseException e) {
            e.printStackTrace();
        }
    }

    // get the link to actual news content from anchor tag href
    @Override
    String getNewsLink(Element news) {
        try {
            return news.getAt("href");
        } catch (NotFound notFound) {
            notFound.printStackTrace();
        }
        return "";
    }

    // print the headlines from headlineTags
    void printHeadlines() {
        for (Element h : headlineTags)
            System.out.println(h.getChildText());
    }


    public static void main(String args[]) {
        ProthomAloScrapper sc = new ProthomAloScrapper();
        // sc.printHeadlines();
    }


}
