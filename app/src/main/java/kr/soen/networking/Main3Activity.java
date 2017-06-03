package kr.soen.networking;

import android.os.Handler;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Main3Activity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> arr= new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        listView = (ListView)findViewById(R.id.listview);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,arr);
    }

    public void onClick(View view){
        if(view.getId() == R.id.button){
            thread.start();
        }
    }


    Handler handler = new Handler();
    Thread thread = new Thread(){
        @Override
        public void run() {
            try{

                //원하는 RSS사이트 접속해서 InputStream으로 가져오기
                URL url = new URL("https://news.google.com/news?cf=all&hl=ko&pz=1&ned=kr&topic=m&output=rss");
                HttpURLConnection urlConnection =
                        (HttpURLConnection)url.openConnection();
                if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    int itemCount = readData(urlConnection.getInputStream());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            adapter.notifyDataSetChanged();
                            listView.setAdapter(adapter);

                        }
                    });
                    urlConnection.disconnect();
                }

            }catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
        }

        //RSS문서를 parsing할 수 있도록 읽어옴
        int readData(InputStream is){
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            try{
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                Document document = builder.parse(is);
                int datacount = parseDocument(document);
                return datacount;

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }

        //Document Element 에서 Tagname 'item'의 NodeList를 생성한다.
        private int parseDocument(Document doc){
            Element docEle = doc.getDocumentElement();
            NodeList nodelist = docEle.getElementsByTagName("item");
            int count = 0;
            if((nodelist != null) && (nodelist.getLength() > 0)){
                for(int i = 0 ; i < nodelist.getLength(); i++){
                    String newsItem = getTagData(nodelist, i);
                    if(newsItem != null){
                        arr.add(newsItem);
                        count++;
                    }
                }
            }
            return count;
        }

        //item 엘리먼트 아래의 각 항목들의 값을 추출한다.
        private String getTagData(NodeList nodeList, int index){

            String newsItem = null;

            try{
                Element entry = (Element) nodeList.item(index);
                Element title = (Element)entry.getElementsByTagName("title").item(0);
                Element pubDate = (Element)entry.getElementsByTagName("pubDate").item(0);
                String pubDateValue = pubDate.getFirstChild().getNodeValue();

                String titleValue = null;
                if(title != null ){
                    Node firstChild = title.getFirstChild();
                    if(firstChild !=null)
                        titleValue = firstChild.getNodeValue();
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
                Date date = new Date();
                newsItem = titleValue + "-"+simpleDateFormat.format(date.parse(pubDateValue));


            }catch (DOMException e){
                e.printStackTrace();
            }
            return newsItem;
        }
    };
}
