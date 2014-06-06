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

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.GridView;
 
/**
 *
 * @author javatechig {@link http://javatechig.com}
 *
 */
@SuppressLint({ "NewApi", "ValidFragment" }) 
public class MainFragment extends Fragment {
	
	
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
    
    public MainFragment(){}
    public MainFragment(String url_to_fetch){
    	url = url_to_fetch;
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_main);        
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        new GetProductInfo().execute();
        return rootView;            
        
    }
 
    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetProductInfo extends AsyncTask<Void, Void, Void> {
 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
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
            
            gridView = (GridView) getView().findViewById(R.id.gridView);
            
            // give imageItem list/array instead of getdata()
            
            customGridAdapter = new GridViewAdapter(getActivity(), R.layout.row_grid, imageItems);
            gridView.setAdapter(customGridAdapter);
            /**
             * Updating parsed JSON data into ListView
             * */              
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