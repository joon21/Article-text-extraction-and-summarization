package juneh017.capitalone_challenge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.webkit.URLUtil;
import android.widget.Toast;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static android.support.v4.content.ContextCompat.startActivities;

public class ActionSendReceiver extends BroadcastReceiver {
    String url = null;
    HashMap<String, String> tagDataMap;
    HistoryData history;
    public ActionSendReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        url = intent.getStringExtra(Intent.EXTRA_TEXT);
        if(URLUtil.isValidUrl(url)) {
            Intent newIntent = new Intent();
            newIntent.setAction("juneh017.capitalone_challenge");
            newIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            newIntent.putExtra("validUrl", url);
            context.sendBroadcast(newIntent);
        }
    }
}
