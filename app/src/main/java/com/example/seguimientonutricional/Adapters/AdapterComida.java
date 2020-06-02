package com.example.seguimientonutricional.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seguimientonutricional.Comida;
import com.example.seguimientonutricional.ComidaFormsFragment;
import com.example.seguimientonutricional.FormsLifeCyle;
import com.example.seguimientonutricional.R;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterComida extends RecyclerView.Adapter<AdapterComida.ViewHolder> {

    private Context mContext;
    private ArrayList<Comida> mComidas;
    private Fragment parent;


    public AdapterComida(Context mContext, ArrayList<Comida> comidas, Fragment parent){
        this.mContext = mContext;
        this.mComidas = comidas;
        this.parent = parent;
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

        Calendar cal = Calendar.getInstance();
        cal.setTime(currComida.getFecha());
        Integer hour = cal.get(Calendar.HOUR_OF_DAY);
        Integer minutes = cal.get(Calendar.MINUTE);
        holder.mFecha.setText(hour.toString() + ":" + minutes.toString());



        holder.mImageView.setImageResource(R.drawable.salad);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ComidaFormsFragment(currComida);
                FormsLifeCyle fragmentHome = (FormsLifeCyle) parent;
                fragmentHome.onFormsOpened();
                parent.getChildFragmentManager().beginTransaction()
                        .replace(R.id.container_home_content,fragment,"comidaForm")
                        .addToBackStack(null)
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
        private ImageView mImageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitulo = itemView.findViewById(R.id.titulo);
            mFecha = itemView.findViewById(R.id.fecha);
            mImageView = itemView.findViewById(R.id.imagen_view_id);
        }
    }
}
