package cl.inndev.miutem.interfaces;

import java.util.List;

import cl.inndev.miutem.classes.Noticia;
import io.reactivex.Single;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiNoticiasUtem {
    String BASE_URL = "https://www.utem.cl/wp-json/";

    @GET("wp/v2/posts")
    Single<List<Noticia>> getPosts();

    @GET("wp/v2/posts/{id}")
    Single<BufferedSource> getPost(
            @Path("id") Integer id
    );

    @GET("wp/v2/media/")
    Call<List<Noticia.Media>> getMedias();

    @GET("wp/v2/media/{id}")
    Single<Noticia.Media> getMedia(
            @Path("id") Integer id
    );

}
