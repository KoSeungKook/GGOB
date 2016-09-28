package com.example.administrator.library_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {

    private ScrollView scrollview;
    private TextView textview;
    private EditText edittext;
    private EditText edittext_spec;
    private EditText edittext_max;
    private EditText edittext_min;
    private TextView textview_LED;
    private CheckBox checkBox_Server;

    private PrintWriter wr = null;
    private BufferedReader br = null;
    private BackThread thread;
    private Socket socket;
    private String serverip = "192.168.9.43";

    private String Readtext;
    private Message message;
    private boolean Thread_State = false;
    private int max =0, min = 0;
    private int led_check = 0;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scrollview = (ScrollView) findViewById(R.id.scrollView);
        textview = (TextView) findViewById(R.id.viewText);
        edittext = (EditText) findViewById(R.id.sendText);
        edittext_spec = (EditText) findViewById(R.id.specText);
        edittext_max = (EditText) findViewById(R.id.maxText);
        edittext_min = (EditText) findViewById(R.id.minText);
        textview_LED = (TextView) findViewById(R.id.textview_led);
        checkBox_Server = (CheckBox) findViewById(R.id.checkBox_server);

        textview.setMovementMethod(new ScrollingMovementMethod());
    }

    private void Connectbt_Clieck()
    {
        if (Thread_State == false) {
            thread = new BackThread();  // 작업스레드 생성
            thread.setDaemon(true);  // 메인스레드와 종료 동기화
            thread.start();     // 작업스레드 시작 -> run() 이 작업스레드로 실행됨
            Thread_State = true;
        }
    }

    private void Closetbt_Clieck() {

        if (Thread_State == true) {

            try {
                if (socket.isConnected()) {
                    socket.close();
                    textview.append("연결 종료 \r\n");
                    TCP_Write("[END]");
                }
                Thread_State = false;
                TCP_Open(false);
            } catch (Exception e) {
                Readtext = e.toString();
            }
        }
    }



    public void connBtnClick (View view) {
        switch (view.getId()) {
            case R.id.connBtn: {
                Connectbt_Clieck();
            }
            break;
            case R.id.closeBtn: {
                Closetbt_Clieck();
            }
            break;
            case R.id.sendBtn: {
                String SendMessage = "[LED] [WRITE] " + edittext.getText().toString();
                TCP_Write(SendMessage);

                Intent intent = new Intent(this, ledActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.valueBtn: {
                String SendMessage = "[LED] [READ] S";
                TCP_Write(SendMessage);
               // scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
            break;
            case R.id.specBtn: {
                String SendMessage = "[LED] [READ] L";
                TCP_Write(SendMessage);
                // scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
            break;


        }
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            /**
             * 넘겨받은 what값을 이용해 실행할 작업을 분류합니다
             */

            if (msg.what == 0) {
                textview.append("연결 실패 \r\n");
                checkBox_Server.setChecked(false);
            } else if (msg.what == 1) {
                textview.append("연결 성공 \r\n");
                checkBox_Server.setChecked(true);
            } else if (msg.what == 2) {
                if (Readtext.contains("[READ] S")) {

                    String[] Division = Readtext.split("@");

                    int led_check = Integer.parseInt(Division[4]);
                    if (led_check == 1) {
                        textview_LED.setBackgroundColor(Color.RED);
                    } else {
                        textview_LED.setBackgroundColor(Color.GREEN);
                    }
                    textview_LED.setText("LED\n" + Division[3]);

                }
                if (Readtext.contains("[READ] L")) {
                    String[] Division = Readtext.split("@");
                    edittext_spec.setText(Division[3]);
                    edittext_max.setText(Division[4]);
                    edittext_min.setText(Division[5]);
                    }


                textview.append("[PC에서 받은 내용] : " + Readtext + "\r\n\r\n");
            }

        }
    };

    class BackThread extends Thread {  // Thread 를 상속받은 작업스레드 생성
        @Override
        public void run () {
            boolean Thread_Run = true;
            String TCP_Result = "";

            try {
                while (Thread_State) {
                    if (Thread_Run == true) {
                        TCP_Result = TCP_Open(true);
                        message = Message.obtain();

                        if (TCP_Result == "true") message.what = 1;
                        else message.what = 0;

                        handler.sendMessage(message);
                        Thread_Run = false;
                    } else {
                        TCP_Result = TCP_Read();
                        if (TCP_Result == "true") {
                        }
                        message = Message.obtain();
                        message.what = 2;
                        handler.sendMessage(message);
                    }
                }
            } catch (Exception e) {
                Readtext = e.toString();
                message.what = 2;
                handler.sendMessage(message);
                Thread_State = false;
            }

        }

    } // end class BackThread

    public String TCP_Open (Boolean Flag) {
        String Result = "false";
        try {
            if (Flag == true) {
                socket = new Socket(serverip, 8088);

                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                wr = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                 Result = "true";
            } else {
                socket.close();
            }
        } catch (Exception e) {
            Result = e.toString();
        }

        return Result;
    }

    public void TCP_Write (String Message) {
        wr.println(Message); //한글은 깨짐. 숫자로 테스트 해보니깐 됫음.
    }

    public String TCP_Read () {
        String result = "false";
        try {
            Readtext = br.readLine();
            result = "true";
        } catch (Exception e) {
            Readtext = e.toString();
        }

        return result;
    }

}
