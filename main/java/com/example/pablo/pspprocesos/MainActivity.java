package com.example.pablo.pspprocesos;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MITAG";
    private android.widget.TextView textView2;
    private android.widget.TextView textView4;
    private android.widget.Button button;
    private Process proceso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        checkInternet();
        checkIP();

        setEventsHandler();
    }

    public void checkInternet(){
        CheckInternet check = new CheckInternet();
        check.execute();
    }

    public void checkIP(){
        CheckIP check = new CheckIP();
        check.execute();
    }

    private class CheckIP extends AsyncTask<Integer, Integer, String> {

        CheckIP(){}

        @Override
        protected String doInBackground(Integer... integers) {
            Runtime rt = Runtime.getRuntime();
            String ip = "";
            try{
                proceso = rt.exec("ifconfig");
                InputStream is = proceso.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                final BufferedReader br = new BufferedReader(isr);
                String linea;
                while((linea = br.readLine()) != null ){
                    if(linea.contains("wlan0")){
                        linea = br.readLine();
                        ip = linea.substring(linea.indexOf("192"), linea.indexOf("Bcast"));
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            return ip;
        }

        @Override
        protected void onPostExecute(String ip) {
            super.onPostExecute(ip);
            textView2.setText(ip);
        }
    }

    private class CheckInternet  extends AsyncTask<Integer, Integer, Integer>{

        CheckInternet(){}

        @Override
        protected Integer doInBackground(Integer... integers) {
            int i = 0;
            Runtime rt = Runtime.getRuntime();
            try{
                proceso = rt.exec("/system/bin/ping -c 1 8.8.8.8");
                i = proceso.waitFor();
            }catch (IOException e){
                e.printStackTrace();
            }catch (InterruptedException ignore)
            {
                ignore.printStackTrace();
                System.out.println(" Exception:"+ignore);
            }
            return i;
        }

        @Override
        protected void onPostExecute(Integer i) {
            super.onPostExecute(i);
            if(i!=0){
                textView4.setText(R.string.connected);
                textView4.setTextColor(getResources().getColor(R.color.green));
            }else {
                textView4.setText(R.string.disconnected);
                textView4.setTextColor(getResources().getColor(R.color.red));
            }
        }
    }

    public void init(){
        this.textView4 = findViewById(R.id.textView4);
        this.textView2 = findViewById(R.id.textView2);
        this.button = findViewById(R.id.button);

        setEventsHandler();
    }

    public void setEventsHandler(){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInternet();
                checkIP();
            }
        });
    }
}

/*public class MainActivity extends AppCompatActivity {
    final static private String TAG = "MITAG";
    private Button start, stop;
    private TextView textView;
    private Process proceso;
    private boolean conectado = true;
    private List<String> ips;

    private void init(){
        start = findViewById(R.id.start);
        textView = findViewById(R.id.textView);
        ips = new ArrayList<String>();

        setEventsHandler();
    }

    private void checkResultado(String linea){
        if(linea.endsWith("time 0ms")){
            conectado = false;
        }
    }

    private void setListaIPs(String linea){
        if(linea.contains("wlan0")){
            //ips.add(linea.substring(linea.indexOf("192"), linea.indexOf("Bcast")));
            ips.add(linea.substring(20,35));
        }
    }

    private void mostrarLista(){
        textView.post(new Runnable() {
            @Override
            public void run() {
                textView.append("Las IPS son: " + ips.toString() + "\n");
            }
        });
    }

    private void mostrarResultado(final String linea){
        textView.post(new Runnable() { //SE PUEDE HACER CON RUNONUITHREAD
            @Override
            public void run() {
                textView.append(linea + "\n");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void removeLista(){
        ips.clear();
    }

    public void setEventsHandler(){
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeLista();
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String ping = "/system/bin/ping -c 1 8.8.8.8";
                        Runtime rt = Runtime.getRuntime();
                        try{
                            proceso = rt.exec(ping);
                            InputStream is = proceso.getInputStream();
                            InputStreamReader isr = new InputStreamReader(is);  //CLASE FILTRO
                            BufferedReader br = new BufferedReader(isr);
                            String linea;
                            while((linea = br.readLine()) != null) {
                                //textView.append(linea + "\n");  //DESDE UNA HEBRA YO NO PUEDO ESCRIBIR EN LA INTERFAZ DE USUARIO
                                mostrarResultado(linea);
                                checkResultado(linea);
                            }
                            if(conectado){
                                mostrarResultado(getText(R.string.connected).toString());
                            } else {
                                mostrarResultado(getText(R.string.not_connected).toString());
                            }

                            proceso.waitFor();
                        }catch(IOException |InterruptedException|IllegalThreadStateException e){
                            System.out.println(e.toString());
                        }
                    }
                });
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String ping = "ifconfig";
                        Runtime rt = Runtime.getRuntime();
                        try{
                            proceso = rt.exec(ping);
                            InputStream is = proceso.getInputStream();
                            InputStreamReader isr = new InputStreamReader(is);  //CLASE FILTRO
                            BufferedReader br = new BufferedReader(isr);
                            String linea;
                            while((linea = br.readLine()) != null) {
                                try {
                                    setListaIPs(linea);
                                }catch(Exception e){
                                    Log.v(TAG, e.toString());
                                }
                            }
                            mostrarLista();
                            proceso.waitFor();

                        }catch(IOException |InterruptedException|IllegalThreadStateException e){
                            System.out.println(e.toString());
                        }
                    }
                });
            }
        });
    }
}*/

