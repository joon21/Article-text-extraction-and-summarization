package juneh017.capitalone_challenge;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class NewsSummarize extends AppCompatActivity {
    ArrayAdapter<String> historyAdapter;
    HistoryData history;
    ArrayList<String> historyList;
    final SpannableString info = new SpannableString(
            "Version 1.0\nMarch 2016\n\nHojoon Lee" +
            "\nUniversity of Maryland\nCollege Park\n" +
            "Computer Science\nJuneh017@gmail.com" +
            "\n\nGitHub:\nhttps://github.com/juneh017/" +
            "CapitalOne_Challenge.git\n");
    String url;
    Article article;
    File historyFile;
    HashMap<String, String> tagDataMap;
    EditText mainEditText;
    ImageView searchButton;
    ListView historyListView;
    int exitCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_summarize);
        this.setTitle("");
        /* main activity onCreate get Views */
        mainEditText = (EditText) findViewById(R.id.urlEditText);
        searchButton = (ImageView) findViewById(R.id.sendButton);
        historyListView = (ListView) findViewById(R.id.historyListView);

        historyFile = new File(getFilesDir(), "history.data");
        historyList = new ArrayList<>();
        history = new HistoryData();
        historyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);


        if(historyFile.exists()) {
            history = FileIO.readFile(historyFile);
            historyList = history.getTitleList();
        } else {
            try {
                FileIO.writeFile(historyFile, history);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        updateHistory();

        historyListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String key = String.valueOf(parent.getItemAtPosition(position));
                        int index = history.getTitleList().indexOf(key);
                        article = history.getArticleMap().get(index);
                        sendIntent(true);
                    }
                }
        );

        Intent i = getIntent();
        url = i.getStringExtra(Intent.EXTRA_TEXT);
        if((url != null) && (URLUtil.isValidUrl(url))) {
            sendIntent(false);
        }


    }

    public void sendOnClick(View view) {
        exitCount = 0;
        url = mainEditText.getText().toString();
        if(url.length() >= 4) {
            if(!url.substring(0, 4).contains("http")) {
                url = "http://" + url;
            }
        }
        sendIntent(false);
    }
    public void urlEditOnClick(View view) {
        url = "";
    }

    public void clearListOnClick(View view) {
        history = history.clearData();
        historyList = history.getTitleList();
        updateHistory();
        Toast.makeText(this, "Data Cleared", Toast.LENGTH_SHORT).show();
        FileIO.writeFile(historyFile, history);
    }

    public void aboutOnClick(View view) {
        Linkify.addLinks(info, Linkify.ALL);
        AlertDialog alertDialog = new AlertDialog.Builder(NewsSummarize.this).create();
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setTitle("Summary");
        alertDialog.setMessage(info);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        ((TextView) alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    /* TODO recycle old arrayadapter for update */
    private void updateHistory() {
        historyAdapter.clear();
        historyAdapter.addAll(historyList);
        historyListView.setAdapter(historyAdapter);
        historyAdapter.notifyDataSetChanged();
    }

    public void sendIntent(boolean isSaved) {
        Intent intent = new Intent(this, Display.class);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

        if(!isSaved) {
            if(!URLUtil.isValidUrl(url)) {
                Toast.makeText(this, "Sorry, Invalid URL :(", Toast.LENGTH_LONG).show();
                return;
            }
            mainEditText.setText("");
            try {
                TagParser parseTagTask = new TagParser(url);
                tagDataMap = parseTagTask.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(tagDataMap != null) {
                intent.putExtra("save", "false");
                intent.putExtra("tagmap", tagDataMap);
            }
        }
        else {
            intent.putExtra("save", "true");
            intent.putExtra("article", article);
        }
        intent.putExtra("history", history);
        intent.putExtra("file", historyFile);
        startActivityForResult(intent, 17);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      /*  if(requestCode == 17) {
            if(resultCode == RESULT_OK) {
                exitCount = 0;


                history = (HistoryData) data.getExtras().get("history");



               *//** history = (HistoryData) received.get("history"); **//*
                //history = FileIO.readFile(historyFile);
                historyList = history.getTitleList();
                updateHistory();
            }
        } else {
            Toast.makeText(this, "Sorry, failed to save article :(", Toast.LENGTH_LONG).show();
        }*/
   }

    @Override
    public void onBackPressed() {
        exitCount++;
        if(exitCount > 1) {
            super.onBackPressed();
        }
        else {
            Toast.makeText(this, "Press back one more time to close", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        history = FileIO.readFile(historyFile);
        historyList = history.getTitleList();
        updateHistory();
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean isFinishing() {
        return super.isFinishing();
    }


}
