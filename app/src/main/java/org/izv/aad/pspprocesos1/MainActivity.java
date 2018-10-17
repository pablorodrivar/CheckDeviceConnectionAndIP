package org.izv.aad.pspprocesos1;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
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
        if(linea.contains("inet addr")){
            ips.add(linea.substring(linea.indexOf("192"), linea.indexOf("Bcast")));
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                /*Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String cadena = "ifconfig";
                        Runtime rt = Runtime.getRuntime();
                        try{
                            proceso = rt.exec(cadena);
                            InputStream is = proceso.getInputStream();
                            InputStreamReader isr = new InputStreamReader(is);  //CLASE FILTRO
                            BufferedReader br = new BufferedReader(isr);
                            String linea;
                            while((linea = br.readLine()) != null) {
                                textView.append(linea + "\n");
                            }
                            proceso.waitFor();
                        }catch(IOException |InterruptedException|IllegalThreadStateException e){
                            System.out.println(e.toString());
                        }
                    }
                });
                th.start();*/
            }
        });
    }
}
