package com.tringapps.realparsing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by geethu on 2/12/16.
 */
public class customAdapter extends BaseAdapter {


    ArrayList<ChannelItem> arraytListOfItems;
    Context context;
    LayoutInflater inflater;
    Bitmap[] bitmap;
    private LruCache<String, Bitmap> mMemoryCache;

    public customAdapter(MainActivity mainActivity, ArrayList<ChannelItem> itemsArrayList) {

        context = mainActivity;
        arraytListOfItems = itemsArrayList;
        bitmap = new Bitmap[arraytListOfItems.size()];

        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 4;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;

            }


        };

    }

    @Override
    public int getCount() {
        return arraytListOfItems.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        MyHolderClass holderTag;

        if(view == null)
        {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.substitute,null);
            holderTag = new MyHolderClass(view);
            view.setTag(holderTag);

        }
        else
        {
            holderTag = (MyHolderClass) view.getTag();
        }


        holderTag.title.setText(arraytListOfItems.get(i).title);
        holderTag.description.setText(arraytListOfItems.get(i).description.trim());
        holderTag.author.setText(arraytListOfItems.get(i).author.trim());
        holderTag.pubDate.setText(arraytListOfItems.get(i).pubDate);
//        holderTag.image.setImageResource(R.mipmap.ic_launcher);
        holderTag.image.setTag("TAG" + i);

//        new downloadImage(arraytListOfItems.get(i).url, holderTag.image,i).execute();


        new downloadImage(arraytListOfItems.get(i).url, i).execute();


        if(bitmap[i] != null)
        {
            holderTag.image.setImageBitmap(bitmap[i]);
        }
        else
        {
            holderTag.image.setImageResource(R.mipmap.ic_launcher);
        }

        return view;
    }

    private class MyHolderClass {
        TextView title;
        TextView description;
        TextView author;
        TextView pubDate;
        ImageView image;

        public MyHolderClass(View view) {

            title = (TextView) view.findViewById(R.id.titleView);
            description = (TextView) view.findViewById(R.id.descriptionView);
            author = (TextView) view.findViewById(R.id.authorView);
            pubDate = (TextView) view.findViewById(R.id.pubDateView);
            image = (ImageView) view.findViewById(R.id.imageView);


        }

    }
    private void notifyChanged() {
        notifyDataSetChanged();
    }

    private class downloadImage extends AsyncTask<String, Void, Bitmap> {

        String url = null;
        ImageView view;
//        Bitmap bitmap;
        InputStream in;
        int position;
        public downloadImage(String requrl, ImageView image, int i) {

            url = requrl;
            view = image;
            position = i;

        }

        public downloadImage(String reqUrl,int i) {

            url = reqUrl;
            position = i;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {

            if(getBitmapFromMemCache(url) == null) {
                HttpHandler sh = new HttpHandler();
                in = sh.loadXmlFromNetwork(url);
                bitmap[position] = BitmapFactory.decodeStream(in);
                putBitmapToMemCache(url,bitmap[position]);
            }
            else
            {
                bitmap[position] = getBitmapFromMemCache(url);
            }

            return null;


//            return bitmap[position];
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            notifyChanged();
            /*if(view.getTag().toString().equalsIgnoreCase("TAG" + position)) {
                view.setImageBitmap(bitmap);
            }*/

        }
    }

    public void putBitmapToMemCache(String key, Bitmap bitmap)
    {
        mMemoryCache.put(key,bitmap);
    }
    public Bitmap getBitmapFromMemCache(String key)
    {
        return mMemoryCache.get(key);
    }

}
