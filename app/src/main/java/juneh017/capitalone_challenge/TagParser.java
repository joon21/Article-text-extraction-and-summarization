package juneh017.capitalone_challenge;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.URLUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;



public class TagParser extends AsyncTask<Void, Void, HashMap<String, String>> {
    Document document;
    InputStream inputStream;
    Bitmap siteLogo;
    Uri uri;
    HashMap<String, String> map;
    String  title = null,
            author = null,
            siteName = null,
            img = null,
            text = null,
            url = null,
            favicon = null;


    /** Constructor **/
    public TagParser(String url) {
        this.url = url;
        this.map = new HashMap<>();
        uri = Uri.parse(url);
    }


    @Override
    protected HashMap<String, String> doInBackground(Void... params) {
        map.put("url", url);

        try {
            document = Jsoup.connect(uri.toString()).get();
            if(document.text().isEmpty()) {
                map.put("FAILURE", "Sorry, failed to read article from url :(" );
                return map;
            }
        } catch (IOException e) {
            return null;
        }

        Elements siteNameElements = document.select("[property=og:site_name]");
        Elements authorElements = document.select("author, [class*=author], [property*=author], [name*=author]");
        Elements textElements = document.body().select("p");
        Element favElement = document.select("link[href~=.*\\.(ico|png)]").first();


        title = document.title();
        if(!(title.isEmpty())) {
            map.put("title" , title);
        } else {
            map.put("title" , "No title found.");
        }

        if(siteNameElements != null && !(siteNameElements.isEmpty())) {
            for(Element e : siteNameElements) {
                siteName = e.attr("content");
                if(!((siteName = siteName.replace(" ", "")).isEmpty())) {
                    map.put("sitename", siteName);
                    break;
                }
            }
        }

        if(!(authorElements.isEmpty())) {
            for(Element e : authorElements) {
                if(e.hasAttr("content")) {
                    author = e.attr("content");
                    if(author.length() < 1) {
                        author = e.text();
                    }
                    if(author.length() > 0) {
                        map.put("author", author);
                        break;
                    }
                }
            }
        }

        if(favElement != null) {
            favicon = favElement.attr("href");
            if(!"".equals(favicon)) {
                if(URLUtil.isValidUrl(favicon)) {
                    map.put("sitelogo", favicon);
                } else {
                    map.put("sitelogo", "");
                }
            } else {
                map.put("sitelogo", "");
            }
        }

        /**DOES NOT CHECK EMPTY ELEMENTS**/
        /*description = descriptionElements.text();
        if(!(description.isEmpty())) {
            map.put("description", description);
        }*/

        if(textElements != null && !(textElements.isEmpty())) {
            text = textElements.text();
            map.put("text", text);

            /** TODO check image parse functionality **/
            Elements siblingElements = textElements.first().parent().select("img");
            if(siblingElements != null && !(siblingElements.isEmpty())) {
                for(Element e : siblingElements) {
                    if(e != null && e.hasAttr("src")) {
                        img = e.attr("src");
                        if(URLUtil.isValidUrl(img)) {
                            map.put("image", img);
                            break;
                        }
                    }
                }

            }
        }

        return map;
    }
}
