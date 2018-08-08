package cl.inndev.miutem.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;
import com.nytimes.android.external.store3.middleware.GsonParserFactory;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Picasso;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cl.inndev.miutem.R;
import cl.inndev.miutem.activities.CarreraActivity;
import cl.inndev.miutem.activities.LoginActivity;
import cl.inndev.miutem.activities.MainActivity;
import cl.inndev.miutem.activities.NoticiaActivity;
import cl.inndev.miutem.adapters.NoticiasAdapter;
import cl.inndev.miutem.classes.Asignatura;
import cl.inndev.miutem.classes.Carrera;
import cl.inndev.miutem.classes.Noticia;
import cl.inndev.miutem.interfaces.ApiNoticiasUtem;
import cl.inndev.miutem.interfaces.ApiUtem;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.interfaces.ApiUtem.BASE_URL;


public class InicioFragment extends Fragment {

    private FirebaseAnalytics mFirebaseAnalytics;
    private List<Noticia> mListNoticias;
    private DiscreteScrollView mScrollNoticias;
    private Integer mNoticiasAgregadas = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            provideNoticiasStore()
                    .get(null)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::setNoticias, t -> {
                        t.printStackTrace();
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        Type noticiasType = new TypeToken<List<Noticia>>() {}.getType();
        Reservoir.getAsync("noticias", noticiasType, new ReservoirGetCallback<List<Noticia>>() {
            @Override
            public void onSuccess(List<Noticia> noticias) {
                mListNoticias = noticias;
                setNoticias(mListNoticias);
            }

            @Override
            public void onFailure(Exception e) {
                //getPosts();
            }
        });
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);
        mScrollNoticias = view.findViewById(R.id.scroll_noticias);
        mListNoticias = new ArrayList<>();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAnalytics.setCurrentScreen(getActivity(), InicioFragment.class.getSimpleName(),
                InicioFragment.class.getSimpleName());
        getActivity().setTitle("Inicio");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*
        try {
            Reservoir.delete("noticias");
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    private void setNoticias(List<Noticia> noticias) {
        for (Noticia noticia : noticias) {
            try {
                provideMediaStore()
                        .get(noticia.getFeaturedMedia())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<Noticia.Media>() {
                            @Override
                            public void onSubscribe(Disposable d) {}

                            @Override
                            public void onSuccess(Noticia.Media media) {
                                noticia.setMedia(media);
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        NoticiasAdapter adapter = new NoticiasAdapter(getContext(), noticias);
        adapter.setOnItemClickListener((view, position, noticia) -> {
            Intent intent = new Intent(getActivity(), NoticiaActivity.class);
            intent.putExtra("NOTICIA_ID", noticia.getId());
            startActivity(intent);
        });
        InfiniteScrollAdapter infiniteAdapter = InfiniteScrollAdapter.wrap(adapter);

        mScrollNoticias.setAdapter(infiniteAdapter);
    }

    private void asociarMedia() {

    }

    private Store<List<Noticia>, Integer> provideNoticiasStore() throws IOException {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        ApiNoticiasUtem apiNoticiasUtem = new Retrofit.Builder()
                .baseUrl(ApiNoticiasUtem.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ApiNoticiasUtem.class);

        return StoreBuilder.<Integer, List<Noticia>>key()
                .fetcher(noticiaId -> apiNoticiasUtem.getPosts())
                .open();
    }

    private Store<Noticia.Media, Integer> provideMediaStore() throws IOException {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        ApiNoticiasUtem apiNoticiasUtem = new Retrofit.Builder()
                .baseUrl(ApiNoticiasUtem.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ApiNoticiasUtem.class);

        return StoreBuilder.<Integer, Noticia.Media>key()
                .fetcher(mediaId -> apiNoticiasUtem.getMedia(mediaId))
                .open();
    }


}
