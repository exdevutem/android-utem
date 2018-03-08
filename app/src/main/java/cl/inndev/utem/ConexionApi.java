package cl.inndev.utem;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by mapache on 07-03-18.
 */

abstract class ConexionApi {
    static abstract class Get extends AsyncTask<String, Void, String> {
        String BASE_URL = "https://api-dirdoc-utem.herokuapp.com/";

        @Override
        protected String doInBackground(String... params) {
            HttpsURLConnection conexion = null;
            BufferedReader reader = null;

            String respuestaJson = null;

            try {
                URL url = new URL(BASE_URL + params[0]);

                conexion = (HttpsURLConnection) url.openConnection();
                conexion.setReadTimeout(10000);
                conexion.setConnectTimeout(15000);
                conexion.setRequestMethod("GET");
                conexion.setRequestProperty("Authorization", "Bearer " + params[1]);

                conexion.connect();

                InputStream inputStream = conexion.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String linea;
                while ((linea = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(linea + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                respuestaJson = buffer.toString();
                return respuestaJson;
            } catch (java.net.SocketTimeoutException e) {
                return null;
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (conexion != null) {
                    conexion.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }
    }

    static abstract class Post extends AsyncTask<String, Void, String> {
        String BASE_URL = "https://api-dirdoc-utem.herokuapp.com/";

        @Override
        protected String doInBackground(String... params) {
            HttpsURLConnection conexion = null;
            BufferedReader reader = null;

            String respuestaJson = null;

            try {
                URL url = new URL(BASE_URL + params[0]);

                conexion = (HttpsURLConnection) url.openConnection();
                conexion.setReadTimeout(10000);
                conexion.setConnectTimeout(15000);
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);

                List<NameValuePair> parametros = new ArrayList<NameValuePair>();
                parametros.add(new BasicNameValuePair("rut", params[1]));
                parametros.add(new BasicNameValuePair("pass", params[2]));

                DataOutputStream dStream = new DataOutputStream(conexion.getOutputStream());
                dStream.writeBytes(getQuery(parametros));
                dStream.flush();
                dStream.close();

                conexion.connect();

                InputStream inputStream = conexion.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String linea;
                while ((linea = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(linea + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                respuestaJson = buffer.toString();
                return respuestaJson;
            } catch (java.net.SocketTimeoutException e) {
                return null;
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (conexion != null) {
                    conexion.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }
    }

    static private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean primero = true;

        for (NameValuePair pair : params) {
            if (primero)
                primero = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
