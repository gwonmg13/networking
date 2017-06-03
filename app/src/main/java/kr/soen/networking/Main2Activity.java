package kr.soen.networking;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Main2Activity extends AppCompatActivity {
    EditText et;
    TextView tv;
    String urlstr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        et = (EditText)findViewById(R.id.et);
        tv = (TextView)findViewById(R.id.textView);

    }


    public void onClick(View v){
        if(v.getId()==R.id.button){
                thread.start();

        }
    }

    Handler handler = new Handler();
    Thread thread = new Thread(){
        @Override
        public void run() {
            try{

                urlstr = et.getText().toString();

                URL url = new URL(urlstr);
                HttpURLConnection urlConnection =
                        (HttpURLConnection)url.openConnection();

                if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                    final String data = readData(urlConnection.getInputStream());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(data);
                        }
                    });
                    urlConnection.disconnect();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally{

            }
        }

        String readData(InputStream is){
            String data = "";
            Scanner s= new Scanner(is);
            while(s.hasNext())
                data+= s.nextLine()+"\n";
            s.close();;
            return data;
        }
    };


}
