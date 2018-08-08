package cl.inndev.miutem.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.XMLReader;

import java.util.List;
import java.util.Objects;

import cl.inndev.miutem.R;
import cl.inndev.miutem.classes.Noticia;
import cl.inndev.miutem.interfaces.ApiNoticiasUtem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NoticiaActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsing;
    private ImageView mImageCover;
    private LinearLayout mLayoutCuerpo;
    private TextView mTextSubtitulo;
    public Integer mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticia);

        mId = getIntent().getIntExtra("NOTICIA_ID", -1);
        collapsing = findViewById(R.id.collapsing);
        mImageCover = findViewById(R.id.image_cover);
        mLayoutCuerpo = findViewById(R.id.layout_cuerpo);
        mTextSubtitulo = findViewById(R.id.text_subtitulo);

        if (mId == -1) {
            finish();
        }

        initToolbar();
        //getPost(mId);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void procesarHtml(Document html) {
        Elements etiquetas = html.getAllElements();
        for (Element etiqueta : etiquetas) {
            if (etiqueta.tagName().equalsIgnoreCase("p")) {
                TextView parrafo = new TextView(NoticiaActivity.this);
                parrafo.setText(Html.fromHtml(etiqueta.html()));
                float scale = getResources().getDisplayMetrics().density;
                int _24dp = (int) (24 * scale + 0.5f);
                int _8dp = (int) (8 * scale + 0.5f);
                parrafo.setPadding(_24dp, _8dp, _24dp, _8dp);
                parrafo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                mLayoutCuerpo.addView(parrafo);
            } else if (etiqueta.tagName().equalsIgnoreCase("img")) {
                ImageView imagen = new ImageView(NoticiaActivity.this);
                float scale = getResources().getDisplayMetrics().density;
                int _8dp = (int) (8 * scale + 0.5f);
                imagen.setPadding(0, _8dp, 0, _8dp);
                imagen.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                imagen.setAdjustViewBounds(true);
                Picasso.get().load(etiqueta.attr("src")).into(imagen);
                mLayoutCuerpo.addView(imagen);
            }
        }

    }

}