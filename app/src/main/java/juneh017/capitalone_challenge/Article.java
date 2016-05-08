package juneh017.capitalone_challenge;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Juneh017 on 3/24/2016.
 */
public class Article implements Serializable {
    private static final long serialVersionUID = 7526472295622776147L;
    private HashMap<String, String> dataMap = new HashMap<>();
    private String[] keywordSet,
                     entitySet;
    private String  title = null,
                    jsoupText = null,
                    apiText = null,
                    url = null,
                    author = null,
                    sitename = null,
                    siteLogo = null,
                    imageSource = null;


    public Article(String title, String url) {
        this.title = title;
        this.url = url;
    }

    /** setters **/
    public Article setAuthor(String in) {
        author = in;
        return this;
    }
    public Article setSiteName(String in) {
        sitename = in;
        return this;
    }
    public Article setImageSource(String in) {
        imageSource = in;
        return this;
    }
    public Article setSiteLogo(String in) {
        siteLogo = in;
        return this;
    }
    public Article setJsoupText(String in) {
        jsoupText = in;
        return this;
    }
    public Article setApiText(String in) {
        apiText = in;
        return this;
    }
    public Article setKeywordSet(String[] in) {
        keywordSet = in;
        return this;
    }
    public Article setEntitySet(String[] in) {
        entitySet = in;
        return this;
    }

    /** getters **/
    public String getArticleTitle() {
        return title;
    }
    public String getUrl() {
        return url;
    }
    public String getSitename() {
        return sitename;
    }
    public String getAuthor() {
        return author;
    }
    public String getJsoupText() {
        return jsoupText;
    }

    public String getApiText() {
        return apiText;
    }
    public String[] getKeywordSet() {
        return keywordSet;
    }
    public String[] getEntitySet() {
        return entitySet;
    }
    public String getLogo() {
        return siteLogo;
    }
    public String getPic() {
        return imageSource;
    }
    /*public HashMap<String, String> getArticleDataMap() {
        dataMap = new HashMap<>();
        dataMap.put("title", title);
        dataMap.put("url", url);
        dataMap.put("author", author);
        dataMap.put("text", jsoupText);
        dataMap.put("apitext", apiText);
        dataMap.put("sitename", sitename);
        return dataMap;
    }*/



}
