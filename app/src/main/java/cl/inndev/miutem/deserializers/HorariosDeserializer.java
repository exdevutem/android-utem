package cl.inndev.miutem.deserializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cl.inndev.miutem.classes.Asignatura;
import cl.inndev.miutem.classes.Estudiante;

public class HorariosDeserializer implements JsonDeserializer<Estudiante.Horario> {

    @Override
    public Estudiante.Horario deserialize(JsonElement json,
                                          Type type,
                                          JsonDeserializationContext context) throws JsonParseException {
        Map<String, Asignatura> cursadas = new HashMap<>();
        JsonArray carreras = (JsonArray) json;
        // TODO: Agregar la posibilidad de multiples horarios
        JsonObject carrera = (JsonObject) carreras.get(0);
        JsonArray asignaturas = carrera.getAsJsonArray("asignaturas");
        for (int j = 0; j < asignaturas.size(); j++) {
            JsonObject asignatura = (JsonObject) asignaturas.get(j);
            Asignatura nueva = new Asignatura(asignatura.get("nombre").getAsString(),
                    asignatura.get("tipo").getAsString(),
                    asignatura.get("profesor").getAsString(),
                    asignatura.get("seccion").getAsInt());
            cursadas.put(asignatura.get("codigo").getAsString() + "/" + nueva.getSeccion(),
                    nueva);
        }


        JsonObject semana = carrera.getAsJsonObject("horario");
        List<List<Asignatura>> horario = new ArrayList<>();
        for (Map.Entry<String, JsonElement> dia : semana.entrySet()) {
            JsonArray dias = (JsonArray) dia.getValue();
            List<Asignatura> diaHorario = new ArrayList<>();
            for (int j = 0; j < dias.size(); j++) {
                JsonObject periodo = (JsonObject) dias.get(j);
                JsonArray bloques = periodo.getAsJsonArray("bloques");
                if (!bloques.get(0).isJsonNull()) {
                    JsonObject bloque = (JsonObject) bloques.get(0);
                    Asignatura asignatura = cursadas.get(
                            bloque.get("codigoAsignatura").getAsString() + "/" +
                                    bloque.get("seccionAsignatura"));
                    asignatura.setCodigo(bloque.get("codigoAsignatura").getAsString());
                    asignatura.setSala(bloque.get("sala").getAsString());
                    diaHorario.add(asignatura);
                } else {
                    diaHorario.add(null);
                }
            }
            horario.add(diaHorario);
        }

        return new Estudiante.Horario(horario);
    }
}