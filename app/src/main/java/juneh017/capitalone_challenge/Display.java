package juneh017.capitalone_challenge;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alchemyapi.api.AlchemyAPI;

import net.sf.classifier4J.Utilities;

import org.w3c.dom.Document;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Display extends AppCompatActivity {
    /** Default length of summarized article **/
    int numberOfLines = 7, maxLen = 15;

    boolean isParsedByAPIService = true;
    Menu menu;
    Bitmap logo, pic;
    HistoryData history;
    File historyFile, abbrListFile;
    HashMap<String, String> dataMap,
                            map = null;
    ImageView siteLogoImgView,
              articleImgView;
    TextView displayTextView,
             displayTitleView,
             displayAuthorView,
             displaySiteNameView;
    MenuItem apiToggleButton;
    String[] keywordSet,
             entitySet;
    String  apiText,
            jsoupText,
            imageSource,
            siteLogo,
            sourceName = "API parser",
            failMessage =  "Sorry, failed to read article from url :(",
            title = null,
            url = null,
            urlSimple = null,
            author = null,
            siteName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        displaySiteNameView = (TextView) findViewById(R.id.displaySiteNameView);
        displayTitleView = (TextView) findViewById(R.id.displayTitleView);
        displayAuthorView = (TextView) findViewById(R.id.displayAuthorNameView);
        displayTextView = (TextView) findViewById(R.id.displayTextView);
        siteLogoImgView = (ImageView) findViewById(R.id.displaySiteImageView);
        articleImgView = (ImageView) findViewById(R.id.displayArticleImageView);
        siteLogoImgView.setImageResource(R.mipmap.ic_launcher);

        Bundle received = getIntent().getExtras();
        if(received != null) {
            history = (HistoryData) received.get("history");
            historyFile = (File) received.get("file");
        } else {
            return;
        }


        String state = (String) received.get("save");
        if((state == null) || (state.isEmpty())) {
            displayTitleView.setText(failMessage);
            return;
        }
        if(state.equals("true")) {
            Article article = (Article) received.get("article");
            url = article.getUrl();
            title = article.getArticleTitle();
            author = article.getAuthor();
            siteName = article.getSitename();
            siteLogo = article.getLogo();
            imageSource = article.getPic();
            jsoupText = article.getJsoupText();
            apiText = article.getApiText();
            keywordSet = article.getKeywordSet();
            entitySet = article.getEntitySet();
        }
        else {

            dataMap = (HashMap<String, String>) received.get("tagmap");

            /** jsoup parser calls **/
            for(Map.Entry<String, String> e : dataMap.entrySet()) {
                if(e.getKey().equals("FAILURE")) {
                    displayTitleView.setText(failMessage);
                    return;
                }
                else if(e.getKey().equals("title")) {
                    title = e.getValue();
                }
                else if(e.getKey().equals("url")) {
                    url = e.getValue();
                    int start = url.indexOf('/');
                    if(start >= 0) {
                        urlSimple = url.substring(start + 2, url.length());
                        int end = urlSimple.indexOf('/');
                        if(end >= 0) {
                            urlSimple = urlSimple.substring(0, end);
                        }
                    }
                }
                else if(e.getKey().equals("author")) {
                    author = e.getValue();
                }
                else if(e.getKey().equals("text")) {
                    jsoupText = e.getValue();
                }
                else if(e.getKey().equals("sitename")) {
                    siteName = e.getValue();
                }
                else if(e.getKey().equals("sitelogo")) {
                    siteLogo = e.getValue();
                }
                else if(e.getKey().equals("image")) {
                    imageSource = e.getValue();
                }
            }

            /** AlchemyAPI calls **/
            map = null;
            ApiExtraction apiConnectionTask = new ApiExtraction(url);
            try {
                map = apiConnectionTask.execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(map != null) {
                for(Map.Entry<String, String> entry : map.entrySet()) {
                    if(entry.getKey().equals("text")) {
                        apiText = entry.getValue();
                    }
                    else if(entry.getKey().equals("keywords")) {
                        keywordSet = entry.getValue().split("\n");
                    }
                    else if(entry.getKey().equals("entities")) {
                        entitySet = entry.getValue().split("\n");
                    }
                    else if(entry.getKey().equals("author")) {
                        String temp = entry.getValue();
                        if(temp.length() > author.length()) {
                            author = temp;
                        }
                    }
                }
            }

            /** Initialize uninitialized variables **/
            if(author == null) { author = ""; }
            if(apiText == null) { apiText = "<br><b>Can't see any text?<br>Try extraction with another parser</b>"; }
            if(jsoupText == null) { jsoupText = "<br><b>Can't see any text?<br>Try extraction with another parser</b>"; }
            if(siteName == null || siteName.length() == 0) { siteName = urlSimple; }

            /** Serialize current article **/
            Article article = new Article(title, url);
            article.setSiteName(siteName)
                    .setSiteLogo(siteLogo)
                    .setAuthor(author)
                    .setImageSource(imageSource)
                    .setKeywordSet(keywordSet)
                    .setEntitySet(entitySet)
                    .setApiText(apiText)
                    .setJsoupText(jsoupText);
            history = history.addArticle(article);

        }

        if(siteLogo != null && (!siteLogo.isEmpty())) {
            try {
                logo = new ImageProcess(siteLogo).execute().get();
                if(logo != null) {
                    siteLogoImgView.setImageBitmap(logo);
                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
        if(imageSource != null && (!imageSource.isEmpty())) {
            try {
                pic = new ImageProcess(imageSource).execute().get();
                if(pic != null) {
                    articleImgView.setImageBitmap(pic);
                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }

        displayTitleView.setText(title);
        displaySiteNameView.setText(siteName);
        displayAuthorView.setText(Html.fromHtml(author));
        displayAuthorView.setMovementMethod(LinkMovementMethod.getInstance());
        displayTextView.setText(Html.fromHtml(getNewLinesText()));
        displayTextView.setMovementMethod(LinkMovementMethod.getInstance());

        FileIO.writeFile(historyFile, history);

      /*  Intent responseIntent = new Intent();
        responseIntent.putExtra("history", history);
        setResult(RESULT_OK, responseIntent);*/
    }

    private String getNewLinesText() {
        /** CALL TEXT SUMMARY TASK **/
        String resultText = "", textSource;
        String[] returnedArray;
        try {
            if(isParsedByAPIService) {
                textSource = apiText;
            } else  {
                textSource = jsoupText;
            }
            InputStream abbrevFileStream = getResources().openRawResource(R.raw.abbrev);
            resultText = new SummaryTask(title, textSource,
                    keywordSet, entitySet,
                    numberOfLines, abbrevFileStream)
                    .execute()
                    .get();
            abbrevFileStream.close();
            /*if((entitySet != null) && (keywordSet != null)) {
            } else {
                resultText = textSource;
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultText;

    }

    /** Adds menu items to menu and inflate menu **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        for (int j = 0; j < menu.size(); j++) {
            MenuItem item = menu.getItem(j);
            item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        this.menu = menu;
        apiToggleButton = menu.findItem(R.id.menu_toggle_parser);
        return super.onCreateOptionsMenu(menu);
    }

    /** Adds a line to summary **/
    public void setLineAddOnClick(MenuItem item) {
        if(numberOfLines < maxLen) {
            numberOfLines++;
            displayTextView.setText(Html.fromHtml(getNewLinesText()));
            displayTextView.setMovementMethod(LinkMovementMethod.getInstance());
            Toast.makeText(this, "Line added", Toast.LENGTH_SHORT).show();
        }
    }
    /** Removes a line from summary **/
    public void setLineRemoveOnClick(MenuItem item) {
        if(numberOfLines > 1) {
            numberOfLines--;
            displayTextView.setText(Html.fromHtml(getNewLinesText()));
            displayTextView.setMovementMethod(LinkMovementMethod.getInstance());
            Toast.makeText(this, "Line removed", Toast.LENGTH_SHORT).show();
        }
    }

    public void setToggleOnClick(MenuItem item) {
        String spanHTML;
        numberOfLines = 5;
        if(isParsedByAPIService) {
            isParsedByAPIService = false;
            sourceName = "jsoup parser";
            apiToggleButton.setIcon(R.drawable.btn_off_custom);
            if((spanHTML = getNewLinesText()) != null) {
                displayTextView.setText(Html.fromHtml(spanHTML));
            }
            displayTextView.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            isParsedByAPIService = true;
            sourceName = "API parser";
            apiToggleButton.setIcon(R.drawable.btn_on_custom);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                this.invalidateOptionsMenu();
            }
            if((spanHTML = getNewLinesText()) != null) {
                displayTextView.setText(Html.fromHtml(spanHTML));
            }
            displayTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        Toast.makeText(this, "Extracted by " + sourceName, Toast.LENGTH_LONG).show();
    }

    public void fullOnClick(View view) {
        if(URLUtil.isValidUrl(url)) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } else {
            Toast.makeText(this, "Sorry, Invalid URL :(", Toast.LENGTH_LONG).show();
        }
    }

    public void backOnClick(View view) {
        finish();
        super.onBackPressed();
    }

    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }


}
