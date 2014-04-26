package com.brassorange.eventapp.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

	public static String SPLASH_URL = "";
	public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    static {
        // Add 3 sample items.
        //addItem(new DummyItem("1", "Item 1"));
        //addItem(new DummyItem("2", "Item 2"));
        //addItem(new DummyItem("3", "Item 3"));
    }

    public static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public String id;
        public String title;
        public String summary;
        public String content;

        public DummyItem(String id, String title) {
            this.id = id;
            this.title = title;
            Log.d(getClass().getSimpleName(), "DummyItem " + id + " " + title);
        }

        public void setSummary (String summary) {
        	this.summary = summary;
        }

        public void setContent (String content) {
        	this.content = content;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
