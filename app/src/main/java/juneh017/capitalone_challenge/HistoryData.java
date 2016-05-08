package juneh017.capitalone_challenge;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Juneh017 on 3/17/2016.
 */
public class HistoryData implements Serializable {
    private static final long serialVersionUID = 7526472295622776147L;
    private ArrayList<Article> articles;
    private ArrayList<String> titleList;
    private HashMap<Integer, String> urlMap;
    private HashMap<Integer, Article> articleMap;

    public HistoryData() {
        articles = new ArrayList<>();
        titleList = new ArrayList<>();
        urlMap = new HashMap<>();
        articleMap = new HashMap<>();
    }

    public HistoryData addArticle(Article article) {
        String title = article.getArticleTitle();
        String url = article.getUrl();
        if(!(titleList.contains(title))) {
            titleList.add(title);
            int index = titleList.indexOf(title);
            urlMap.put(index, url);
            articleMap.put(index, article);
        }
        return this;
    }
    /*public  void removeArticle(String title) {
        if(titleList.contains(title)) {
            titleList.remove(title);
            int index = titleList.indexOf(title);
            descriptionMap.remove(index);
            String url = urlMap.get(index);
            urlMap.remove(index);
            articleMap.remove(url);
        }
    }*/

    public ArrayList<String> getTitleList() {
        return titleList;
    }
    public HashMap<Integer, String> getUrlMap() {
        return urlMap;
    }
    public HashMap<Integer, Article> getArticleMap() {
        return articleMap;
    }
    public HistoryData clearData() {
        return new HistoryData();
    }


}
