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

public class AsignaturasDeserializer implements JsonDeserializer<List<Asignatura>> {

    @Override
    public List<Asignatura>deserialize(JsonElement json,
                                     Type type,
                                     JsonDeserializationContext context)
            throws JsonParseException {

        List<Asignatura> asignaturasFinal = new ArrayList<>();
        JsonArray asignaturas = (JsonArray) json;
        for (int i = 0; i < asignaturas.size(); i++) {
            JsonObject actual = (JsonObject) asignaturas.get(i);
            asignaturasFinal.add(new Asignatura(
                    actual.get("alumnoSeccionId").isJsonNull() ? null : actual.get("alumnoSeccionId").getAsInt(),
                    actual.get("codigo").isJsonNull() ? null : actual.get("codigo").getAsString(),
                    actual.get("nombre").isJsonNull() ? null : actual.get("nombre").getAsString(),
                    actual.get("profesor").isJsonNull() ? null : actual.get("profesor").getAsString(),
                    actual.get("tipo").isJsonNull() ? null : actual.get("tipo").getAsString(),
                    actual.get("seccionId").isJsonNull() ? null : actual.get("seccionId").getAsInt(),
                    actual.get("seccion").isJsonNull() ? null : actual.get("seccion").getAsInt()
            ));
        }
        return asignaturasFinal;
    }
}