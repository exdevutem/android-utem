package cl.inndev.utem;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        TextView nombre = (TextView) findViewById(R.id.nombreText);
        TextView rut = (TextView) findViewById(R.id.rutText);
        TextView correoUtem = (TextView) findViewById(R.id.correoUtemText);
        TextView ingreso = (TextView) findViewById(R.id.ingresoText);
        TextView matricula = (TextView) findViewById(R.id.matriculaText);
        TextView carreras = (TextView) findViewById(R.id.carrerasText);
        TextView edad = (TextView) findViewById(R.id.edadText);
        TextView correoPersonal = (TextView) findViewById(R.id.correoPersonalText);
        TextView fijo = (TextView) findViewById(R.id.fijoText);
        TextView movil = (TextView) findViewById(R.id.movilText);
        TextView direccion = (TextView) findViewById(R.id.direccionText);

        SharedPreferences usuario = getSharedPreferences("usuario", MODE_PRIVATE);

        //Toast.makeText(this, usuario.getString("nombre", null), Toast.LENGTH_SHORT).show();

        new DownloadImageTask((CircleImageView) findViewById(R.id.profile_image))
                .execute(usuario.getString("foto-url", null));

        nombre.setText(usuario.getString("nombre", "Usuario desconocido"));
        rut.setText(usuario.getString("rut", "Sin RUT"));
        correoUtem.setText(usuario.getString("correo-utem", "No cargó el correo"));
        ingreso.setText(Integer.toString(usuario.getInt("anio-ingreso", 0)));
        matricula.setText(Integer.toString(usuario.getInt("ultima-matricula", 0)));
        carreras.setText(Integer.toString(usuario.getInt("carreras-cursadas", 0)));
        if (usuario.contains("edad")) {
            edad.setText(Integer.toString(usuario.getInt("edad", 0)));
        }

        if (usuario.contains("correo-personal")) {
            correoPersonal.setText(usuario.getString("correo-personal", null));
        }

        if (usuario.contains("telefono-fijo")) {
            fijo.setText(Long.toString(usuario.getLong("telefono-fijo", 0)));
        }

        if (usuario.contains("telefono-movil")) {
            fijo.setText(Long.toString(usuario.getLong("telefono-movil", 0)));
        }

        if (usuario.contains("direccion")) {
            direccion.setText(usuario.getString("direccion", null));
        }

        /*
        //get the spinner from the xml.
        Spinner dropdown = (Spinner) findViewById(R.id.spinner1);
        //create a list of items for the spinner.
        String[] items = new String[]{"Masculino", "Femenino", "Prefiero no especificar"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        */


    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        CircleImageView bmImage;

        public DownloadImageTask(CircleImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
