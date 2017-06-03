package kr.soen.networking;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Main4Activity extends AppCompatActivity {

    EditText edit1, edit2;
    TextView resultmsg;

    String userid, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        resultmsg = (TextView)findViewById(R.id.result);


    }
    public void onClick(View v){

        if(v.getId() == R.id.button){
            edit1 = (EditText)findViewById(R.id.edit1);
            edit2 = (EditText)findViewById(R.id.edit2);
            userid = edit1.getText().toString();
            password = edit2.getText().toString();


            if(userid.equals("")||password.equals("")){
                Toast.makeText(getApplicationContext(),"아이디와 비밀번호를 입력하세요.",Toast.LENGTH_LONG).show();
            }else{
                thread.start();

            }



        }
    }

    Handler handler = new Handler();
    Thread thread = new Thread(){
        @Override
        public void run() {

            try{

                URL url = new URL("http://jerry1004.dothome.co.kr/info/login.php");

                //URLConnection request속성 설정
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);

                //URLConnection 의 outputStream에 앱에서 입력받은
                //id와 비밀번호를 전송
                String postData = "userid=" + URLEncoder.encode(userid)
                        +"&password="+URLEncoder.encode(password);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postData.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                //웹페이지에서 전송받은 데이터를 처리하여 결과값을
                //URLConnection 의 inputStream으로 수신한다.
                InputStream inputStream;
                if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK)
                    inputStream = httpURLConnection.getInputStream();
                else
                    inputStream = httpURLConnection.getErrorStream();

                final String result = loginResult(inputStream);


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(result.equals("FAIL"))
                            resultmsg.setText("로그인에 실패했습니다.");
                        else
                            resultmsg.setText(result+"님 로그인 성공");

                    }
                });

                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {

            }
        }
    };

    private String loginResult(InputStream inputStream) {
        StringBuffer buffer = new StringBuffer();

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String str = reader.readLine();
            while(str!=null){
                buffer.append(str);
                str= reader.readLine();
            }reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
