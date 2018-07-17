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

import cl.inndev.miutem.classes.Carrera;

public class CarrerasDeserializer implements JsonDeserializer<List<Carrera>> {

    @Override
    public List<Carrera> deserialize(JsonElement json,
                                     Type type,
                                     JsonDeserializationContext context)
            throws JsonParseException {

        List<Carrera> carrerasFinal = new ArrayList<>();
        JsonArray carreras = (JsonArray) json;
        for (int i = 0; i < carreras.size(); i++) {
            JsonObject actual = (JsonObject) carreras.get(i);
            JsonObject carrera = actual.getAsJsonObject("carrera");
            JsonObject plan = actual.getAsJsonObject("plan");
            JsonObject inicio = actual.getAsJsonObject("semestreInicio");
            JsonObject termino = actual.getAsJsonObject("semestreTermino");
            Carrera objeto = new Carrera();
            objeto.setmId(actual.get("_id").getAsInt());
            objeto.setEstado(actual.get("estado").getAsString());
            objeto.setCarreraId(carrera.get("_id").getAsInt());
            objeto.setNombre(carrera.get("nombre").getAsString());
            objeto.setCodigo(carrera.get("codigo").getAsString());
            objeto.setSemestreInicioId(inicio.get("_id").getAsInt());
            objeto.setAnioInicio(inicio.get("anio").getAsInt());
            objeto.setSemestreInicio(inicio.get("semestre").getAsInt());
            if (!termino.get("_id").isJsonNull()) {
                objeto.setSemestreTerminoId(termino.get("_id").getAsInt());
                objeto.setAnioTermino(termino.get("anio").getAsInt());
                objeto.setSemestreTermino(termino.get("semestre").getAsInt());
            }
            carrerasFinal.add(objeto);
        }
        return carrerasFinal;
    }
}