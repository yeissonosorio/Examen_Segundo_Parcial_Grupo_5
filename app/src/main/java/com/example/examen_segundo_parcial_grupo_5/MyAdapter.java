package com.example.examen_segundo_parcial_grupo_5;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.examen_segundo_parcial_grupo_5.Configuracion.Firma;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Firma> listaOriginal;

    private OnItemClickListener clickListener;

    public MyAdapter(List<Firma> data) {
        this.listaOriginal=new ArrayList<>(data);
        
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Firma currentItem = listaOriginal.get(position);
        TextView nom = (TextView)holder.itemView.findViewById(R.id.textView);
        nom.setText(currentItem.getNombre());
        ImageView imageView =(ImageView) holder.itemView.findViewById(R.id.imageView);
        imageView.setImageBitmap(currentItem.getFirma());
        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onItemClick(currentItem);
            }
        });

    }
    @Override
    public int getItemCount() {
        return listaOriginal.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(Firma item);
    }

    public void eliminarTodosLosElementos() {
        listaOriginal.clear(); // Elimina todos los elementos de la lista
        notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
    }

    public void eliminarElementosNoCoincidentes(String textoABuscar) {
        ArrayList<Firma> elementosARetener = new ArrayList<>();
        for (Firma elemento : listaOriginal) {
            if (elemento.getNombre().contains(textoABuscar)) {
                elementosARetener.add(elemento);
            }
        }
        listaOriginal.retainAll(elementosARetener);
        notifyDataSetChanged();
    }


}