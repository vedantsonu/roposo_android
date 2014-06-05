package com.example.roposo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.GridView;
 
/**
 *
 * @author javatechig {@link http://javatechig.com}
 *
 */
public class MainActivity extends Activity {
    private GridView gridView;
    private GridViewAdapter customGridAdapter;
    JSONObject productInfo = null;
    private ProgressDialog pDialog;
    private static String url = "http://www.roposo.com/collection/clothes/tops?start=0&count=40&ajaxflag=true";
    
    // JSON Node names
    private static final String TAG_PRODUCTS = "productsJSON";
    private static final String TAG_IMAGES = "i";
    private static final String TAG_IMAGE_300 = "300x300";
    private static final String TAG_TYPE = "ty";
    
    ArrayList imageItems = new ArrayList();
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
              
        new GetProductInfo().execute();
    }
 
    /*private ArrayList getData() {
        final ArrayList imageItems = new ArrayList();
        // retrieve String drawable array
        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
        for (int i = 0; i < imgs.length(); i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),
                    imgs.getResourceId(i, -1));
            imageItems.add(new ImageItem(bitmap, "Women red ruffle top"));
        }
 
        return imageItems;
 
    }*/
    
    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetProductInfo extends AsyncTask<Void, Void, Void> {
 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
 
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
 
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
 
            Log.d("Response: ", "> " + jsonStr);
 
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                     
                    // Getting JSON Array node
                    productInfo = jsonObj.getJSONObject(TAG_PRODUCTS);
                    Iterator<?> keys = productInfo.keys();

                    while( keys.hasNext() ){
                        String key = (String)keys.next();
                        if( productInfo.get(key) instanceof JSONObject ){
                        	JSONObject c = (JSONObject) productInfo.get(key);
                            
                            String type = c.getString(TAG_TYPE);
                            if(!type.equals("p"))
                            {
                            	continue;
                            }
                            JSONArray images = c.getJSONArray(TAG_IMAGES);
                            String image_url = images.getJSONObject(0).getString(TAG_IMAGE_300);
                            
                            Bitmap image = getBitmapFromURL(image_url.replaceAll("\\\\", ""));
                 
                            ImageItem product = new ImageItem(image,"Women red ruffle top");
                            
                            Log.d("Product: ", type + "  "+ image_url);
     
                            // adding contact to contact list
                            imageItems.add(product);
                        }
                    }                    
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
 
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            
            gridView = (GridView) findViewById(R.id.gridView);
            
            // give imageItem list/array instead of getdata()
            
            customGridAdapter = new GridViewAdapter(MainActivity.this, R.layout.row_grid, imageItems);
            gridView.setAdapter(customGridAdapter);
            /**
             * Updating parsed JSON data into ListView
             * */
            /*ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, contactList,
                    R.layout.list_item, new String[] { TAG_NAME, TAG_EMAIL,
                            TAG_PHONE_MOBILE }, new int[] { R.id.name,
                            R.id.email, R.id.mobile });
 
            setListAdapter(adapter);*/
//            
        }
        
		private Bitmap getBitmapFromURL(String src) {
		    try {
		        URL url = new URL(src);
		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		        connection.setDoInput(true);
		        connection.connect();
		        InputStream input = connection.getInputStream();
		        Bitmap myBitmap = BitmapFactory.decodeStream(input);
		        return myBitmap;
		    } catch (IOException e) {
		        e.printStackTrace();
		        return null;
		    }
		}


 
    }
 
}