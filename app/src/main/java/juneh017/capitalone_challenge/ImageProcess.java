package juneh017.capitalone_challenge;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Juneh017 on 3/18/2016.
 */
public class ImageProcess extends AsyncTask<Void, Void, Bitmap> {
    InputStream inputStream;
    String imgSrc;
    Bitmap siteLogo;

   public ImageProcess(String imgSrc) {
       this.imgSrc = imgSrc;
   }


    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            inputStream = new java.net.URL(imgSrc).openStream();
            siteLogo =  BitmapFactory.decodeStream(inputStream);
            return siteLogo;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /* For put image on Intent */
    private byte[] imgToByteArray(Bitmap image) {
        byte[] byteArray = null;
        ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();;
        if(image != null) {
            image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayStream);
            byteArray = byteArrayStream.toByteArray();
        }

        return byteArray;
    }
}
