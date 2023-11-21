package com.example.examen_segundo_parcial_grupo_5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.examen_segundo_parcial_grupo_5.Configuracion.Contactos;
import com.example.examen_segundo_parcial_grupo_5.Configuracion.api;

import org.json.JSONObject;

public class Actualizar extends AppCompatActivity {
    private RequestQueue requestQueue;
    EditText nombre,telefono,latitud,logitud;
    ImageView imagen;

    String id;

    Button atras,actualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar);
        nombre = (EditText) findViewById(R.id.txtANombre);
        telefono = (EditText) findViewById(R.id.txtATel);
        latitud = (EditText) findViewById(R.id.txtlAlat);
        logitud = (EditText) findViewById(R.id.txtAlong);
        imagen  =(ImageView) findViewById(R.id.image);

        atras= (Button) findViewById(R.id.btnEAtras);
        actualizar=(Button) findViewById(R.id.btnActualizar);

        id=getIntent().getStringExtra("id");
        nombre.setText(getIntent().getStringExtra("nombre"));
        telefono.setText(getIntent().getStringExtra("telefono"));
        latitud.setText(getIntent().getStringExtra("latitud"));
        logitud.setText(getIntent().getStringExtra("Longitud"));
        imagen.setImageBitmap(revelar(getIntent().getStringExtra("base64")));

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validar()!=false){
                    actualizarD();
                }
            }
        });

        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Lista_Contactos.class);
                startActivity(intent);
            }
        });

    }

    private void actualizarD() {
        requestQueue = Volley.newRequestQueue(this);
        Contactos cont = new Contactos();

        cont.setId(id);
        cont.setNombre(nombre.getText().toString());
        cont.setTelefono(telefono.getText().toString());
        cont.setLatitud(latitud.getText().toString());
        cont.setLongitud(logitud.getText().toString());

        JSONObject jsonperson = new JSONObject();

        try {
            jsonperson.put("id",cont.getId());
            jsonperson.put("nombre", cont.getNombre());
            jsonperson.put("telefono", cont.getTelefono());
            jsonperson.put("latitud", cont.getLatitud());
            jsonperson.put("longitud", cont.getLongitud());

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                api.EndpointPut, jsonperson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String mensaje = response.getString("Actualizado");
                    Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"actualizado", Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(request);

    }

    public Bitmap revelar(String blob){

        byte[] decodedBytes = Base64.decode(blob, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }
    private boolean validar() {
        boolean valor=false;

        String nom = nombre.getText().toString().replaceAll("\\s","");
        String tel = telefono.getText().toString().replaceAll("\\s","");
        String lat = latitud.getText().toString().replaceAll("\\s","");
        String lon = logitud.getText().toString().replaceAll("\\s","");

        if(tel.isEmpty()&&nom.isEmpty()&&lat.isEmpty()&&lon.isEmpty()){
            Toast.makeText(getApplicationContext(),"LLene todos los campos",Toast.LENGTH_LONG).show();
        } else if (nom.isEmpty()) {
            nombre.setError("Debe llenar este campo");
        } else if (tel.isEmpty()) {
            telefono.setError("Debe llenar este campo");
        } else if (lat.isEmpty()) {
            latitud.setError("Debe llenar este campo");
        } else if (lon.isEmpty()) {
            logitud.setError("Debe llenar este campo");
        }
        else {
            valor=true;
        }
        return valor;
    }
}