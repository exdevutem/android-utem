package cl.inndev.miutem.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nytimes.android.external.fs3.SourcePersisterFactory;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.MemoryPolicy;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;
import com.nytimes.android.external.store3.middleware.GsonParserFactory;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cl.inndev.miutem.R;
import cl.inndev.miutem.activities.NoticiaActivity;
import cl.inndev.miutem.adapters.NoticiasAdapter;
import cl.inndev.miutem.models.Carrera;
import cl.inndev.miutem.models.Noticia;
import cl.inndev.miutem.interfaces.ApiNoticiasUtem;
import cl.inndev.miutem.utils.StoreUtils;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static cl.inndev.miutem.utils.StoreUtils.provideGson;


public class InicioFragment extends Fragment {

    private FirebaseAnalytics mFirebaseAnalytics;
    private List<Noticia> mListNoticias;
    private DiscreteScrollView mScrollNoticias;
    private Integer mNoticiasAgregadas = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarCode barCode = new BarCode(Noticia.class.getSimpleName() + "s", "general");
        /*
        try {
            provideNoticiasStore().get(barCode).subscribe(noticias -> {
                setNoticias(noticias);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            BarCode barCode = new BarCode(Noticia.Media.class.getSimpleName(), noticia.getFeaturedMedia().toString());
            try {
                provideMediaStore().get(barCode)
                        .subscribeOn(Schedulers.io());
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


    private Store<List<Noticia>, BarCode> provideNoticiasStore() throws IOException {
        ApiNoticiasUtem apiNoticiasUtem = new Retrofit.Builder()
                .baseUrl(ApiNoticiasUtem.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(provideGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ApiNoticiasUtem.class);

        return StoreBuilder.<BarCode, BufferedSource, List<Noticia>>parsedWithKey()
                .fetcher(noticiaId -> apiNoticiasUtem.getPosts())
                .parser(GsonParserFactory.createSourceParser(provideGson(), new TypeToken<List<Carrera>>() {}.getType()))
                .persister(SourcePersisterFactory.create(getContext().getCacheDir()))
                //.refreshOnStale()
                .networkBeforeStale()
                .memoryPolicy(MemoryPolicy.builder()
                        .setExpireAfterWrite(10)
                        .setExpireAfterTimeUnit(TimeUnit.MINUTES)
                        .build())
                .open();
    }

    private Store<Noticia.Media, BarCode> provideMediaStore() throws IOException {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();

        ApiNoticiasUtem apiNoticiasUtem = new Retrofit.Builder()
                .baseUrl(ApiNoticiasUtem.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ApiNoticiasUtem.class);

        return StoreBuilder.<BarCode, BufferedSource, Noticia.Media>parsedWithKey()
                .fetcher(barCode -> apiNoticiasUtem.getMedia(barCode.getKey()))
                .parser(GsonParserFactory.createSourceParser(provideGson(), Noticia.Media.class))
                .persister(SourcePersisterFactory.create(getContext().getCacheDir()))
                //.refreshOnStale()
                .networkBeforeStale()
                .memoryPolicy(MemoryPolicy.builder()
                        .setExpireAfterWrite(10)
                        .setExpireAfterTimeUnit(TimeUnit.MINUTES)
                        .build())
                .open();
    }


}
