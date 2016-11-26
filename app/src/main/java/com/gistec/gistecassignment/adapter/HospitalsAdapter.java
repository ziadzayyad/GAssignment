package com.gistec.gistecassignment.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.gistec.gistecassignment.R;
import com.gistec.gistecassignment.model.Hospital;

import java.util.ArrayList;
import java.util.HashMap;

public class HospitalsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Hospital> hospitalsArrayList;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;
    private RequestQueue requestQueue;
    private Hospital hospital;

    public HospitalsAdapter(Context context_, ArrayList<Hospital> d) {

        context = context_;
        hospitalsArrayList =d;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        requestQueue = Volley.newRequestQueue(context);

        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }


    public ImageLoader getImageLoader() {
        return imageLoader;
    }


    public int getCount() {
        return hospitalsArrayList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
    //    if(convertView==null) {
            vi = inflater.inflate(R.layout.list_row, null);
            TextView title = (TextView) vi.findViewById(R.id.txtTitle);
            TextView type = (TextView) vi.findViewById(R.id.txtType);
            NetworkImageView thumb_image = (NetworkImageView) vi.findViewById(R.id.list_image); // thumb image
            //hospital = new Hospital();
            hospital = hospitalsArrayList.get(position);
            title.setText(hospital.name);
            type.setText(hospital.type);

            thumb_image.setImageUrl(hospital.imageUrl, imageLoader);

      //  }
        return vi;
    }


}