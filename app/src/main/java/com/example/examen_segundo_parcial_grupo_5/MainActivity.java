package com.example.examen_segundo_parcial_grupo_5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.examen_segundo_parcial_grupo_5.Configuracion.Contactos;
import com.example.examen_segundo_parcial_grupo_5.Configuracion.api;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    SignaturePad firma;
    EditText latidud, longitud, telefono, nombre;
    Button salvar, Lista, eliminar;
    Bitmap imagefirma;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nombre = (EditText) findViewById(R.id.txtnombre);
        telefono = (EditText) findViewById(R.id.txttelefono);
        latidud = (EditText) findViewById(R.id.txtlatitud);
        longitud = (EditText) findViewById(R.id.txtxlongitud);
        firma = (SignaturePad) findViewById(R.id.firma);
        salvar = (Button) findViewById(R.id.btnSalvar);
        Lista = (Button) findViewById(R.id.BtnContactos);
        eliminar=(Button) findViewById(R.id.btneliminar);

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validar() != false) {
                    imagefirma = firma.getSignatureBitmap();
                    sendata();
                }


            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firma.clear();
            }
        });

        Lista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Lista_Contactos.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onResume() {
        super.onResume();
        if (!isLocationEnabled()) {
            Toast.makeText(this, "La configuración de ubicación está desactivada, por favor actívala.", Toast.LENGTH_SHORT).show();
            showLocationDialog();
        } else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            checkLocationPermission();
        }
    }

    private void showLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("La configuración de ubicación está desactivada, ¿quieres activarla?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Abre la pantalla de configuración de ubicación
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        // Aquí puedes tomar acciones adicionales si el usuario elige no activar la ubicación
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void sendata() {
        requestQueue = Volley.newRequestQueue(this);
        Contactos cont = new Contactos();

        cont.setNombre(nombre.getText().toString());
        cont.setTelefono(telefono.getText().toString());
        cont.setLatitud(latidud.getText().toString());
        cont.setLongitud(longitud.getText().toString());
        cont.setFirma(ConvertImageBase64());


        JSONObject jsonperson = new JSONObject();

        try {
            jsonperson.put("nombre", cont.getNombre());
            jsonperson.put("telefono", cont.getTelefono());
            jsonperson.put("latitud", cont.getLatitud());
            jsonperson.put("longitud", cont.getLongitud());
            jsonperson.put("firma", cont.getFirma());

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                api.EndpointPost, jsonperson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String mensaje = response.getString("message");
                    Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(request);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        } else {

            getLocation();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {

            }
        }
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            // Usa la latitud y longitud obtenidas aquí
                            latidud.setText(""+latitude);
                            longitud.setText(""+longitude);
                        }
                    }
                });
    }

    private boolean validar() {
        boolean valor=false;

        String nom = nombre.getText().toString().replaceAll("\\s","");
        String tel = telefono.getText().toString().replaceAll("\\s","");
        String lat = latidud.getText().toString().replaceAll("\\s","");
        String lon = longitud.getText().toString().replaceAll("\\s","");

        if(tel.isEmpty()&&nom.isEmpty()&&lat.isEmpty()&&lon.isEmpty()&&firma.isEmpty()){
            Toast.makeText(getApplicationContext(),"LLene todos los campos",Toast.LENGTH_LONG).show();
        } else if (nom.isEmpty()) {
            nombre.setError("Debe llenar este campo");
        } else if (tel.isEmpty()) {
            telefono.setError("Debe llenar este campo");
        } else if (lat.isEmpty()) {
            latidud.setError("Debe llenar este campo");
        } else if (lon.isEmpty()) {
            longitud.setError("Debe llenar este campo");
        } else if (firma.isEmpty()) {
            Toast.makeText(getApplicationContext(),"Debe hacer una firma",Toast.LENGTH_LONG).show();
        } else {
            valor=true;
        }
        return valor;
    }

    private String ConvertImageBase64()
    {
        Bitmap bitmap = imagefirma;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imagearray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imagearray, Base64.DEFAULT);
    }

}