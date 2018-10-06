package core;

import com.jaunt.*;
import java.util.HashMap;

abstract class NewsScrapper {

    public String targetAddress;
    public HashMap<String, String> headlineTags;

    abstract HashMap<String, String> addHeadlineTags();
    abstract String getNewsLink(Element news);

    public String getTargetAddress() {
        return targetAddress;
    }
}
