package cl.inndev.miutem.deserializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cl.inndev.miutem.classes.Asignatura;
import cl.inndev.miutem.classes.Carrera;

public class MallaDeserializer implements JsonDeserializer<List<Carrera.Nivel>> {

    @Override
    public List<Carrera.Nivel> deserialize(JsonElement json,
                                           Type type,
                                           JsonDeserializationContext context)
            throws JsonParseException {

        List<Carrera.Nivel> mallaFinal = new ArrayList<>();
        JsonObject malla = (JsonObject) json;
        JsonArray niveles = malla.getAsJsonArray("malla");
        for (int i = 0; i < niveles.size(); i++) {
            List<Asignatura> asignaturasNivel = new ArrayList<>();
            JsonObject nivel = (JsonObject) niveles.get(i);
            JsonArray asignaturas = nivel.getAsJsonArray("asignaturas");

            for (int j = 0; j < asignaturas.size(); j++) {
                JsonObject asignatura = (JsonObject) asignaturas.get(j);

                Asignatura nueva = new Asignatura(
                        asignatura.get("nombre").getAsString(),
                        asignatura.get("codigo").getAsString(),
                        asignatura.get("estado").getAsString(),
                        asignatura.get("tipo").getAsString(),
                        asignatura.get("oportunidades").isJsonNull() ? null : asignatura.get("oportunidades").getAsInt(),
                        asignatura.get("nota").isJsonNull() ? null : asignatura.get("nota").getAsDouble());
                asignaturasNivel.add(nueva);
            }
            Carrera.Nivel nivelActual = new Carrera.Nivel(asignaturasNivel, nivel.get("nivel").isJsonNull() ? null : nivel.get("nivel").getAsString());
            mallaFinal.add(nivelActual);
        }
        return mallaFinal;
    }
}