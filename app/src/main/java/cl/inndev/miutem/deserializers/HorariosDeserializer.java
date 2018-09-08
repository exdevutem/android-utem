package cl.inndev.miutem.deserializers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cl.inndev.miutem.models.Asignatura;
import cl.inndev.miutem.models.Carrera;
import cl.inndev.miutem.models.Horario;

public class HorariosDeserializer implements JsonDeserializer<List<Horario>> {

    private class Container {
        @SerializedName("carrera")
        @Expose
        private Carrera carrera;
        @SerializedName("asignaturas")
        @Expose
        private List<Asignatura> asignaturas;

        public Carrera getCarrera() {
            return carrera;
        }

        public void setCarrera(Carrera carrera) {
            this.carrera = carrera;
        }

        public List<Asignatura> getAsignaturas() {
            return asignaturas;
        }

        public void setAsignaturas(List<Asignatura> asignaturas) {
            this.asignaturas = asignaturas;
        }
    }

    @Override
    public List<Horario> deserialize(JsonElement json,
                                          Type type,
                                          JsonDeserializationContext context) throws JsonParseException {

        JsonArray jsonArray = json.getAsJsonArray();
        List<Horario> horarios = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject elemento = (JsonObject) jsonArray.get(i);
            Container container = new Gson().fromJson(elemento.toString(), Container.class);

            JsonObject semana = elemento.getAsJsonObject("horario");

            List<List<Asignatura>> horario = new ArrayList<>();

            for (Map.Entry<String, JsonElement> dia : semana.entrySet()) {
                // Dias
                JsonArray diaObject = (JsonArray) dia.getValue();
                List<Asignatura> diaHorario = new ArrayList<>();
                for (int j = 0; j < diaObject.size(); j++) {
                    // Bloques
                    JsonObject periodo = (JsonObject) diaObject.get(j);
                    JsonArray bloques = periodo.getAsJsonArray("bloques");
                    if (!bloques.get(0).isJsonNull()) {
                        JsonObject bloque = (JsonObject) bloques.get(0);
                        Boolean encontrado = false;
                        for (Asignatura asignatura : container.getAsignaturas()) {
                            if (asignatura.getCodigo().equalsIgnoreCase(bloque.get("codigoAsignatura").getAsString())) {
                                if (asignatura.getSeccion().getNumero() == bloque.get("seccionAsignatura").getAsInt()) {
                                    encontrado = true;
                                    Asignatura nuevaAsignatura = new Asignatura(asignatura);
                                    nuevaAsignatura.setCodigo(bloque.get("codigoAsignatura").getAsString());
                                    nuevaAsignatura.setSala(bloque.get("sala").getAsString());
                                    diaHorario.add(nuevaAsignatura);
                                }
                            }
                        }
                        if (!encontrado) {
                            diaHorario.add(null);
                        }
                    } else {
                        diaHorario.add(null);
                    }
                }
                horario.add(diaHorario);
            }
            horarios.add(new Horario(container.getCarrera(), container.getAsignaturas(), horario));
        }

        return horarios;
    }
}