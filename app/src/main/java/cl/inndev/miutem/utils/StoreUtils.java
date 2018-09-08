package cl.inndev.miutem.utils;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nytimes.android.external.fs3.SourcePersisterFactory;
import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Parser;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;
import com.nytimes.android.external.store3.base.impl.MemoryPolicy;
import com.nytimes.android.external.store3.base.impl.RealStore;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;
import com.nytimes.android.external.store3.middleware.GsonParserFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cl.inndev.miutem.deserializers.CarrerasDeserializer;
import cl.inndev.miutem.deserializers.HorariosDeserializer;
import cl.inndev.miutem.interfaces.ApiUtem;
import cl.inndev.miutem.models.Carrera;
import cl.inndev.miutem.models.Estudiante;
import cl.inndev.miutem.models.Horario;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class StoreUtils extends RealStore {
    public StoreUtils(Fetcher fetcher, Parser parser, Context context) throws IOException {
        super(fetcher, SourcePersisterFactory.create(context.getCacheDir()), parser);
    }

    public static final class BufferedSourceConverterFactory extends Converter.Factory {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                                Retrofit retrofit) {
            if (!BufferedSource.class.equals(type)) {
                return null;
            }
            return (Converter<ResponseBody, BufferedSource>) value -> value.source();
        }
    }

    private static OkHttpClient provideClient(int tiempo, TimeUnit unidad) {
        return new OkHttpClient.Builder()
                .readTimeout(tiempo, unidad)
                .connectTimeout(tiempo, unidad)
                .build();
    }

    public static Gson provideGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();
    }

    private static ApiUtem provideApiUtem() {
        return new Retrofit.Builder()
                .baseUrl(ApiUtem.BASE_URL)
                .addConverterFactory(new BufferedSourceConverterFactory())
                .addConverterFactory(GsonConverterFactory.create(provideGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ApiUtem.class);
    }

    public static Store<Estudiante, BarCode> provideEstudianteStore(Context context, String token) throws IOException {
        return StoreBuilder.<BarCode, BufferedSource, Estudiante>parsedWithKey()
                .fetcher(barCode -> provideApiUtem().getBufferedEstudiante(barCode.getKey(), "Bearer " + token))
                .parser(GsonParserFactory.createSourceParser(provideGson(), Estudiante.class))
                .persister(SourcePersisterFactory.create(context.getCacheDir()))
                //.refreshOnStale()
                .memoryPolicy(MemoryPolicy.builder()
                        .setExpireAfterWrite(10)
                        .setExpireAfterTimeUnit(TimeUnit.MINUTES)
                        .build())
                .open();
    }

    public static Store<List<Carrera>, BarCode> provideCarrerasStore(Context context, String token) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(List.class, new CarrerasDeserializer())
                .create();

        return StoreBuilder.<BarCode, BufferedSource, List<Carrera>>parsedWithKey()
                .fetcher(barCode -> provideApiUtem().getBufferedCarreras(barCode.getKey(), "Bearer " + token))
                .parser(GsonParserFactory.createSourceParser(gson, new TypeToken<List<Carrera>>() {}.getType()))
                .persister(SourcePersisterFactory.create(context.getCacheDir()))
                //.refreshOnStale()
                .memoryPolicy(MemoryPolicy.builder()
                        .setExpireAfterWrite(10)
                        .setExpireAfterTimeUnit(TimeUnit.MINUTES)
                        .build())
                .open();
    }

    public static Store<List<Horario>, BarCode> provideHorariosStore(Context context, String token) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<Horario>>() {}.getType(), new HorariosDeserializer())
                .create();

        return StoreBuilder.<BarCode, BufferedSource, List<Horario>>parsedWithKey()
                .fetcher(barCode -> provideApiUtem().getBufferedHorarios(barCode.getKey(), "Bearer " + token))
                .parser(GsonParserFactory.createSourceParser(gson, new TypeToken<List<Horario>>() {}.getType()))
                .persister(SourcePersisterFactory.create(context.getCacheDir()))
                //.refreshOnStale()
                .memoryPolicy(MemoryPolicy.builder()
                        .setExpireAfterWrite(6)
                        .setExpireAfterTimeUnit(TimeUnit.HOURS)
                        .build())
                .open();
    }
}
