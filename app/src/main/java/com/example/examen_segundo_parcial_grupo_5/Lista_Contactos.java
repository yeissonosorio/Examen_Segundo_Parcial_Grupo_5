package com.example.examen_segundo_parcial_grupo_5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.examen_segundo_parcial_grupo_5.Configuracion.Firma;
import com.example.examen_segundo_parcial_grupo_5.Configuracion.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class Lista_Contactos extends AppCompatActivity {

    private RequestQueue requestQueue;

    String id,nombre,latitud,longitud,telefono,base64;

    RecyclerView lista;

    ArrayList<String> listaD;

    EditText busqueda;

    List<Firma> data = new ArrayList<>();

    Button Eliminar,Actualizar,atras;

    int toque=0;
    int previousPosition = 1;
    int count=1;
    long previousMil=0;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contactos);

        lista = (RecyclerView) findViewById(R.id.lista);
        busqueda =(EditText) findViewById(R.id.txtbusqueda);
        Eliminar =(Button) findViewById(R.id.BtnEliminar);
        Actualizar=(Button) findViewById(R.id.btnactualizar);
        atras = (Button) findViewById(R.id.btnAtras);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        lista.setLayoutManager(layoutManager);

        Eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toque==1){
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Lista_Contactos.this);
                    builder.setTitle("Eliminar contacto");
                    builder.setMessage("¿Estás seguro de que deseas eliminar a " +nombre + " ?");
                    builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            eliminar();
                            finish();
                            startActivity(getIntent());
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    androidx.appcompat.app.AlertDialog dialog = builder.create();
                    dialog.show();

                }else {
                    Toast.makeText(getApplicationContext(),"Precione un contacto para Eliminar",Toast.LENGTH_LONG).show();
                }
            }
        });

        Actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toque==1){
                    Intent intent = new Intent(getApplicationContext(), com.example.examen_segundo_parcial_grupo_5.Actualizar.class);
                    intent.putExtra("id",id);
                    intent.putExtra("nombre",nombre);
                    intent.putExtra("latitud",latitud);
                    intent.putExtra("telefono",telefono);
                    intent.putExtra("Longitud",latitud);
                    intent.putExtra("base64",base64);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Precione un contacto para Actualizar",Toast.LENGTH_LONG).show();
                }
            }
        });

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });


        getinfo();

    }

    public  void eliminar(){
        requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, api.EndpointDelt+"?id="+id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String mensaje = null;
                try {
                    mensaje = response.getString("eliminado");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void getinfo(){
        requestQueue = Volley.newRequestQueue(this);

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET,api.EndpointGet, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Respuesta", response.toString());
                try {
                    Obtener(response);
                    MyAdapter adp = new MyAdapter(data);
                    adp.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Firma item) {
                            int poscision = data.indexOf(item);

                            if(previousPosition==poscision)
                            {
                                count++;
                                if(count==2 && System.currentTimeMillis()-previousMil<=1000)
                                {
                                    //Toast.makeText(getApplicationContext(), "Doble Click ",Toast.LENGTH_LONG).show();
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                                    alertDialogBuilder.setTitle("Acción");
                                    alertDialogBuilder
                                            .setMessage("¿Desea ver la ubicacion del contacto de "+data.get(poscision).getNombre()+"?")
                                            .setCancelable(false)
                                            .setPositiveButton("SI",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {

                                                    try{
                                                            Intent intent = new Intent(getApplicationContext(),Mapa.class);
                                                            intent.putExtra("nombre",data.get(poscision).getNombre());
                                                            intent.putExtra("latitud",data.get(poscision).getLatitud());
                                                            intent.putExtra("Longitud",data.get(poscision).getLongitud());
                                                            startActivity(intent);
                                                    }catch (Exception ex){
                                                        ex.toString();
                                                    }
                                                }
                                            })
                                            .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                    count=1;
                                    toque=0;
                                }
                                else {
                                    toque=1;
                                    id=data.get(poscision).getId();
                                    nombre=data.get(poscision).getNombre();
                                    telefono=data.get(poscision).getTelefono();
                                    latitud= data.get(poscision).getLatitud();
                                    longitud=data.get(poscision).getLongitud();
                                    base64= data.get(poscision).getConver64();
                                }
                            }
                            else
                            {
                                toque=1;
                                id=data.get(poscision).getId();
                                nombre=data.get(poscision).getNombre();
                                telefono=data.get(poscision).getTelefono();
                                latitud= data.get(poscision).getLatitud();
                                longitud=data.get(poscision).getLongitud();
                                base64= data.get(poscision).getConver64();
                                previousPosition=poscision;
                                count=1;
                                previousMil=System.currentTimeMillis();

                            }
                        }
                    });
                    lista.setAdapter(adp);
                    busqueda.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if(s.toString().length()>0) {
                                adp.eliminarElementosNoCoincidentes(s.toString());
                            }else{
                                getinfo();
                            }                        }
                    });

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Respuesta", error.toString());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void Obtener(String response) throws JSONException {
        JSONArray jsonArray = new JSONArray(response);

        listaD = new ArrayList<>();



        for (int i = 0;i<jsonArray.length();i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String id = jsonObject.getString("id");
            String nombre = jsonObject.getString("nombre");
            String telefono = jsonObject.getString("telefono");
            String latitud =  jsonObject.getString("latitud");
            String longitud =  jsonObject.getString("longitud");
            String firma =  jsonObject.getString("firma");
            data.add(new Firma(id,nombre,telefono,latitud,longitud,revelar(firma),firma));

        }

    }
    public Bitmap revelar(String blob){

        byte[] decodedBytes = Base64.decode(blob, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }

}