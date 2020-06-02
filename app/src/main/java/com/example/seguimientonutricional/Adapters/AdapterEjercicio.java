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

import com.example.seguimientonutricional.Ejercicio;
import com.example.seguimientonutricional.EjercicioFormsFragment;
import com.example.seguimientonutricional.FormsLifeCyle;
import com.example.seguimientonutricional.R;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterEjercicio extends RecyclerView.Adapter<AdapterEjercicio.ViewHolder> {

    private Context mContext;
    private ArrayList<Ejercicio> mEjercicios;
    private Fragment parent;

    public AdapterEjercicio(Context mContext, ArrayList<Ejercicio> ejercicios,Fragment parent){
        this.mContext = mContext;
        this.mEjercicios = ejercicios;
        this.parent = parent;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);
        return new AdapterEjercicio.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Ejercicio currEjercicio = mEjercicios.get(position);

        holder.mTitulo.setText(currEjercicio.getTitulo());

        Calendar cal = Calendar.getInstance();
        cal.setTime(currEjercicio.getFecha());
        Integer hour = cal.get(Calendar.HOUR_OF_DAY);
        Integer minutes = cal.get(Calendar.MINUTE);
        holder.mFecha.setText(hour.toString() + ":" + minutes.toString());

        holder.mImageView.setImageResource(R.drawable.caminadora);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new EjercicioFormsFragment(currEjercicio);
                FormsLifeCyle fragmentHome = (FormsLifeCyle) parent;
                fragmentHome.onFormsOpened();
                parent.getChildFragmentManager().beginTransaction()
                        .replace(R.id.container_home_content,fragment,"ejercicioForm")
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mEjercicios.size();
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
