package com.example.jacob.test;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jcraft.jsch.*;
import java.util.*;
import java.io.*;
import android.widget.TextView;


public class MainActivity extends Activity {
    Button button;
    TextView usr_nm;
    EditText usr;
    EditText password;
    EditText host1;
    String host;
    String user;
    String pass;
    String sendCom;
    EditText command;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("working");
        button = (Button) findViewById(R.id.btn);
        host1 = (EditText) findViewById(R.id.hostInput);
        command = (EditText) findViewById(R.id.comm);
        usr = (EditText) findViewById(R.id.user);
        password = (EditText) findViewById(R.id.pass);
        usr_nm = (TextView) findViewById(R.id.textView2);


        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                user = usr.getText().toString();
                pass = password.getText().toString();
                sendCom = command.getText().toString();
                host = host1.getText().toString();
                new loadsomestuff().execute(host);
            }
        });


    }


    public class loadsomestuff extends AsyncTask<String, Integer, String> {

        String asd;

        @Override
        protected String doInBackground(String... arg0) {


            JSch jsch = new JSch();
            Properties props = new Properties();
            props.put("StrictHostKeyChecking", "no");

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            config.put("compression.s2c", "zlib,none");
            config.put("compression.c2s", "zlib,none");

            Session session;
            try {
                session = jsch.getSession(user, host, 22);
                session.setConfig(config);
                session.setPassword(pass);
                session.connect();
            } catch (JSchException e) {
                asd = "NOT_Executed";
                System.out.println("NOT_executed with" + user + pass);
                e.printStackTrace();
                return "NOT_Executed";
            }


            ChannelExec channel= null;

            try {
                channel = (ChannelExec) session.openChannel("exec");
                channel.setPty(true);
                channel.setPtyType("VT100");

                System.out.println(channel);
            } catch (JSchException e) {
                e.printStackTrace();
            }

            assert ((ChannelExec)channel) != null;
            channel.setCommand("echo " + pass + " | sudo -S " + sendCom);
            System.out.println(channel);


            InputStream in= null;
            try {
                in = channel.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            OutputStream out= null;
            try {
                out = channel.getOutputStream();
                System.out.println(out);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ((ChannelExec)channel).setErrStream(System.err);

            try {
                channel.connect();
            } catch (JSchException e1) {
                e1.printStackTrace();
            }

            byte[] tmp=new byte[1024];
            while(true){
                try {
                    while(in.available()>0){
                        int i=in.read(tmp, 0, 1024);
                        if(i<0)break;
                        System.out.print(new String(tmp, 0, i));

                        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        //PrintStream ps = new PrintStream(baos);


                        asd = new String(tmp, 0, i);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(channel.isClosed()){
                    System.out.println("exit-status: "+ channel.getExitStatus());
                    break;
                }
                try{Thread.sleep(1000);}catch(Exception ee){}
            }
            channel.disconnect();
            session.disconnect();

            Context context = getApplicationContext();
            CharSequence text = "Connected to Pi";
            //int duration = Toast.LENGTH_SHORT;
            //Toast toast = Toast.makeText(context, text, duration);
            //toast.show();
            System.out.println("Final output");

            return asd;
        }

        @Override
        protected void onPostExecute(String abc) {
            usr_nm.setText(asd);

        }

    }

}