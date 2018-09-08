package cl.inndev.miutem.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Objects;

import cl.inndev.miutem.R;

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