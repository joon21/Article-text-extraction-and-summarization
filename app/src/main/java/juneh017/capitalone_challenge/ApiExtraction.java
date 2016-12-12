package juneh017.capitalone_challenge;

import android.os.AsyncTask;
import android.widget.Toast;

import com.alchemyapi.api.AlchemyAPI;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;


/**
 * Created by Juneh017 on 3/20/2016.
 */
public class ApiExtraction extends AsyncTask<Void, Void, HashMap<String, String>>{
    private static AlchemyAPI api;
    private final String APIKEY = ""; // API KEY HERE
    HashMap<String, String> map;
    String url;

    public ApiExtraction(String url) {
        api = AlchemyAPI.GetInstanceFromString(APIKEY);
        this.url = url;
    }

    @Override
    protected HashMap<String, String> doInBackground(Void... params) {
        if(api == null) {
            return null;
        }
        map = new HashMap<>();
        addAuthor();
        addText();
        //addImage();
        addKeyWords();
        addEntities();
        return map;
    }

    private void addText() {
        Document document = null;
        try {
            document = api.URLGetText(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(document != null) {
            String temp = docToString(document);
            if(temp != null && temp.replace(" ", "").length() != 0) {
                map.put("text",temp);
            }
        }
    }
    private  void addAuthor() {
        Document document = null;
        try {
            document = api.URLGetAuthor(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(document != null) {
            String temp = docToString(document);
            if(temp != null && temp.replace(" ", "").length() != 0) {
                map.put("author",temp);
            }
        }
    }
    private  void addKeyWords() {
        Document document = null;
        try {
            document = api.URLGetRankedKeywords(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(document != null) {
            map.put("keywords",docToString(document));
        }
    }
   /* private  void addImage() {
        Document document = null;
        try {
            document = api.URLGetImage(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(document != null) {
            map.put("image",docToString(document));
        }
    }*/
    private  void addEntities() {
        Document document = null;
        try {
            document = api.URLGetRankedNamedEntities(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(document != null) {
            map.put("entities",docToString(document));
        }
    }


    private String docToString(Document document) {
        Element root = document.getDocumentElement();
        NodeList items = root.getElementsByTagName("text");
        String result = "";

        for (int i=0;i<items.getLength();i++) {
            Node concept = items.item(i);
            String astring = concept.getChildNodes().item(0).getNodeValue();
            result += ("\n" + astring);
        }
        return result;
    }
}
