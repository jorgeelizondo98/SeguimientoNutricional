package com.example.seguimientonutricional.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seguimientonutricional.Comida;
import com.example.seguimientonutricional.ComidaFormsFragment;
import com.example.seguimientonutricional.R;

import java.util.ArrayList;

public class AdapterComida extends RecyclerView.Adapter<AdapterComida.ViewHolder> {

    private Context mContext;
    private ArrayList<Comida> mComidas;


    public AdapterComida(Context mContext, ArrayList<Comida> comidas){
        this.mContext = mContext;
        this.mComidas = comidas;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Comida currComida = mComidas.get(position);

        holder.mTitulo.setText(currComida.getTitulo());
      
        holder.mFecha.setText(currComida.getFecha().toString());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = ((AppCompatActivity)mContext).getSupportFragmentManager();
                Fragment fragment = new ComidaFormsFragment(currComida);
                fm.beginTransaction().replace(R.id.container_home_content,fragment,"comidaForm")
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComidas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView mTitulo;
        private TextView mFecha;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitulo = itemView.findViewById(R.id.titulo);
            mFecha = itemView.findViewById(R.id.fecha);
        }
    }
}
