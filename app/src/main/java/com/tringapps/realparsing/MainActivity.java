package com.tringapps.realparsing;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String url = "http://www.javaworld.com/index.rss";

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list_item);
        Log.e("TAG","hgvjhnvjhvbjhvjmhjhhnbv=++++++++++++++++++++++++==");

        new LoadXmlFromNetwork().execute();

    }

    private class LoadXmlFromNetwork extends AsyncTask<String,Void,String>{
        private XmlPullParserFactory xmlFactoryObject;
        ArrayList<ChannelItem> itemsArrayList = new ArrayList<>();
        @Override
        protected String doInBackground(String... strings) {


            HttpHandler sh = new HttpHandler();
            InputStream in = sh.loadXmlFromNetwork(url);
            Log.e("TAG","4444444444444444444444444444444444444444444444444444");


            try
            {
                xmlFactoryObject = XmlPullParserFactory.newInstance();
                XmlPullParser myParser;
                myParser = xmlFactoryObject.newPullParser();
                myParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);
//                myParser.setInput(new StringReader());
                myParser.setInput(in,"UTF-8");
                itemsArrayList = xmlParser(myParser);

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

           customAdapter adapter = new customAdapter(MainActivity.this,itemsArrayList);
            listView.setAdapter(adapter);


        }

        private ArrayList<ChannelItem> xmlParser(XmlPullParser myParser) throws XmlPullParserException, IOException {

            String text = null;
            int event = myParser.getEventType();
            ChannelItem item = null;

            while(event != XmlPullParser.END_DOCUMENT)
            {

                String name = myParser.getName();

                switch (event)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (name.equalsIgnoreCase("item")) {
                            item = new ChannelItem();
                        }
                    case XmlPullParser.TEXT:
                        text = myParser.getText();

                        break;
                    case XmlPullParser.END_TAG:

                        if (item != null) {
                            if (name.equalsIgnoreCase("title")) {

                                item.title = text;
                            }
                            else if(name.equalsIgnoreCase("description"))
                            {
                                text = Html.fromHtml(text).toString();
                                item.description = text;
                            }
                            else if(name.equalsIgnoreCase("author"))
                            {
                                text = Html.fromHtml(text).toString();
                                item.author = text;
                            }
                            else if (name.equalsIgnoreCase("media:thumbnail"))
                            {
                                item.url=myParser.getAttributeValue(null,"url");
//                                item.url = text;
                            }
                            else if(name.equalsIgnoreCase("pubDate"))
                            {
                                item.pubDate = text;
                                itemsArrayList.add(item);
                               /* item = null;*/
                            }
                        }

                        break;

                }
                event = myParser.next();
            }
            return itemsArrayList;

        }
    }
}
