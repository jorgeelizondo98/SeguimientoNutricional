package com.example.seguimientonutricional.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seguimientonutricional.Bebida;
import com.example.seguimientonutricional.BebidaFormsFragment;
import com.example.seguimientonutricional.R;

import java.util.ArrayList;

public class AdapterBebida extends  RecyclerView.Adapter<AdapterBebida.ViewHolder> {



    private Context mContext;
    private ArrayList<Bebida> mBebidas;


    public AdapterBebida(Context mContext, ArrayList<Bebida> bebidas){
        this.mContext = mContext;
        this.mBebidas = bebidas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);
        return new AdapterBebida.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Bebida currBebida = mBebidas.get(position);

        holder.mTitulo.setText(currBebida.getTitulo());

        holder.mFecha.setText(currBebida.getFecha().toString());

        holder.mImageView.setImageResource(R.drawable.cocktail);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = ((AppCompatActivity)mContext).getSupportFragmentManager();
                Fragment fragment = new BebidaFormsFragment(currBebida);
                fm.beginTransaction().replace(R.id.container_home_content,fragment,"bebidaForm")
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mBebidas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView mTitulo;
        private TextView mFecha;
        private ImageView mImageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitulo = itemView.findViewById(R.id.titulo);
            mFecha = itemView.findViewById(R.id.fecha);
            mImageView = itemView.findViewById(R.id.imagen_view_id);
        }
    }

}
