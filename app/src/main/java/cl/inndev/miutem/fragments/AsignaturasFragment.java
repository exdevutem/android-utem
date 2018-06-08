package cl.inndev.miutem.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cl.inndev.miutem.R;
import cl.inndev.miutem.activities.MainActivity;
import cl.inndev.miutem.adapters.MyRecyclerViewAdapter;
import cl.inndev.miutem.classes.Asignatura;
import cl.inndev.miutem.classes.Estudiante;
import cl.inndev.miutem.interfaces.ApiUtem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.interfaces.ApiUtem.BASE_URL;


public class AsignaturasFragment extends Fragment {

    private FirebaseAnalytics mFirebaseAnalytics;
    private RecyclerView mListAsignaturas;
    private ProgressBar mProgressCargando;
    private AsignaturasAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        View view = inflater.inflate(R.layout.fragment_asignaturas, container, false);
        mListAsignaturas = view.findViewById(R.id.list_asignaturas);
        mProgressCargando = view.findViewById(R.id.progress_cargando);
        ArrayList<String> animalNames = new ArrayList<>();
        animalNames.add("Horse");
        animalNames.add("Cow");
        animalNames.add("Camel");
        animalNames.add("Sheep");
        animalNames.add("Goat");

        // set up the RecyclerView
        mListAsignaturas.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new AsignaturasAdapter(getContext(), animalNames);
        mListAsignaturas.setAdapter(adapter);

        // getAsignaturas();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        // Set title bar
        mFirebaseAnalytics.setCurrentScreen(getActivity(), AsignaturasFragment.class.getSimpleName(),
                AsignaturasFragment.class.getSimpleName());
        ((MainActivity) getActivity()).setActionBarTitle("Asignaturas");

    }

    private void getAsignaturas() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiUtem restClient = retrofit.create(ApiUtem.class);

        Map<String, String> credenciales = Estudiante.getCredenciales(getContext());

        Call<ArrayList<Asignatura>> call = restClient.getAsignaturas(credenciales.get("rut"), credenciales.get("token"));

        call.enqueue(new Callback<ArrayList<Asignatura>>() {
            @Override
            public void onResponse(Call<ArrayList<Asignatura>> call, Response<ArrayList<Asignatura>> response) {
                switch (response.code()) {
                    case 200:
                        break;
                    default:
                        Toast.makeText(getContext(), "Error desconocido", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Asignatura>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static class AsignaturasAdapter extends RecyclerView.Adapter<AsignaturasAdapter.ViewHolder> {

        private List<String> mData;
        private LayoutInflater mInflater;
        private AsignaturasAdapter.ItemClickListener mClickListener;

        // data is passed into the constructor
        public AsignaturasAdapter(Context context, List<String> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        // inflates the row layout from xml when needed
        @Override
        public AsignaturasAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item, parent, false);
            return new AsignaturasAdapter.ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(AsignaturasAdapter.ViewHolder holder, int position) {
            String animal = mData.get(position);
            holder.myTextView.setText(animal);
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
        }


        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView myTextView;

            ViewHolder(View itemView) {
                super(itemView);
                myTextView = itemView.findViewById(R.id.text_asignatura_nombre);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            }
        }

        // convenience method for getting data at click position
        String getItem(int id) {
            return mData.get(id);
        }

        // allows clicks events to be caught
        public void setClickListener(AsignaturasAdapter.ItemClickListener itemClickListener) {
            this.mClickListener = itemClickListener;
        }

        // parent activity will implement this method to respond to click events
        public interface ItemClickListener {
            void onItemClick(View view, int position);
        }
    }
}

