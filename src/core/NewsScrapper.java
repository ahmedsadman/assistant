package core;

import com.jaunt.*;

import java.util.ArrayList;

abstract class NewsScrapper {

    String targetAddress;
    ArrayList<Element> headlineTags;

    abstract void addHeadlineTags();
    abstract String getNewsLink(Element news);

    public String getTargetAddress() {
        return targetAddress;
    }
}
