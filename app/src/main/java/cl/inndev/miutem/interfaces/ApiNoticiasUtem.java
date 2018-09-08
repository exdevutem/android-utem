package cl.inndev.miutem.interfaces;

import java.util.List;

import cl.inndev.miutem.models.Noticia;
import io.reactivex.Single;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiNoticiasUtem {
    String BASE_URL = "https://www.utem.cl/wp-json/";

    @GET("wp/v2/posts")
    Single<BufferedSource> getPosts();

    @GET("wp/v2/posts/{id}")
    Single<BufferedSource> getPost(
            @Path("id") Integer id
    );

    @GET("wp/v2/media/")
    Call<BufferedSource> getMedias();

    @GET("wp/v2/media/{id}")
    Single<BufferedSource> getMedia(
            @Path("id") String id
    );

}
