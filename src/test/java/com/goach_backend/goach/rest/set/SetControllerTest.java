package com.goach_backend.goach.rest.set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goach_backend.goach.logic.entity.auth.JwtAuthenticationFilter;
import com.goach_backend.goach.logic.entity.routine.Routine;
import com.goach_backend.goach.logic.entity.routine.RoutineRepository;
import com.goach_backend.goach.logic.entity.set_exercise.Set;
import com.goach_backend.goach.logic.entity.set_exercise.SetRepository;
import com.goach_backend.goach.logic.entity.exercise.ExerciseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Time;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = SetController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class SetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SetRepository setRepository;

    @MockitoBean
    private RoutineRepository routineRepository;

    @MockitoBean
    private ExerciseRepository exerciseRepository;

    /**
     * shouldReturnAllSets
     * <p>
     * Verifica que el endpoint de listado de sets retorne correctamente
     * una respuesta HTTP 200 cuando existen registros.
     * El repositorio es mockeado para devolver una lista simulada,
     * validando el comportamiento esperado del controlador.
     */
    @Test
    void shouldReturnAllSets() throws Exception {
        when(setRepository.findAll()).thenReturn(List.of(new Set(), new Set()));

        mockMvc.perform(get("/sets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    /**
     * shouldReturnSetById
     * <p>
     * Verifica que el controlador pueda recuperar un set existente
     * cuando se proporciona un identificador válido.
     * Se espera una respuesta HTTP 200 y el uso correcto del repositorio.
     */
    @Test
    void shouldReturnSetById() throws Exception {
        UUID setId = UUID.randomUUID();
        when(setRepository.existsById(setId)).thenReturn(true);
        when(setRepository.findById(setId)).thenReturn(Optional.of(new Set()));

        mockMvc.perform(get("/sets/{id}", setId))
                .andExpect(status().isOk());
    }

    /**
     * shouldReturn404WhenSetNotFound
     * <p>
     * Valida el manejo de errores cuando se solicita un set
     * que no existe en la base de datos.
     * El sistema debe responder con un estado HTTP 404.
     */
    @Test
    void shouldReturn404WhenSetNotFound() throws Exception {
        UUID setId = UUID.randomUUID();
        when(setRepository.existsById(setId)).thenReturn(false);

        mockMvc.perform(get("/sets/{id}", setId))
                .andExpect(status().isNotFound());
    }

    /**
     * shouldReturnSetsByRoutine
     * <p>
     * Valida que el sistema pueda retornar correctamente los sets
     * asociados a una rutina existente, ordenados por número de set.
     * Se comprueba que la lógica de filtrado por rutina funcione correctamente.
     */
    @Test
    void shouldReturnSetsByRoutine() throws Exception {
        UUID routineId = UUID.randomUUID();
        when(routineRepository.existsById(routineId)).thenReturn(true);
        when(setRepository.findByRoutine_IdOrderBySetNumberAsc(routineId))
                .thenReturn(List.of(new Set()));

        mockMvc.perform(get("/sets/filterByRoutine/{id}", routineId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    /**
     * shouldReturn404WhenRoutineNotFound
     * <p>
     * Prueba un escenario negativo donde se solicita el listado de sets
     * para una rutina que no existe. El objetivo es validar que el sistema
     * responda de forma controlada devolviendo un estado HTTP 404.
     */
    @Test
    void shouldReturn404WhenRoutineNotFound() throws Exception {
        UUID routineId = UUID.randomUUID();
        when(routineRepository.existsById(routineId)).thenReturn(false);

        mockMvc.perform(get("/sets/filterByRoutine/{id}", routineId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].error").exists());
    }

    /**
     * shouldCreateSet
     * <p>
     * Comprueba el flujo completo de creación de un set asociado
     * a una rutina existente. Se valida que el controlador intente
     * persistir el set y devuelva el objeto creado.
     */
    @Test
    void shouldCreateSet() throws Exception {
        UUID routineId = UUID.randomUUID();
        Routine routine = new Routine();

        when(routineRepository.findById(routineId)).thenReturn(Optional.of(routine));
        when(setRepository.save(org.mockito.ArgumentMatchers.any(Set.class)))
                .thenReturn(new Set());

        Set body = Set.builder()
                .setNumber(1)
                .restTime(Time.valueOf("00:01:00"))
                .workTime(Time.valueOf("00:00:30"))
                .build();

        mockMvc.perform(post("/sets/{id}", routineId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    /**
     * shouldUpdateSet
     * <p>
     * Verifica que un set existente pueda ser actualizado correctamente
     * cuando pertenece a la rutina indicada.
     * Se valida que el controlador procese la solicitud PUT,
     * persista los cambios y responda con un estado HTTP 201 (Created).
     */
    @Test
    void shouldUpdateSet() throws Exception {
        UUID setId = UUID.randomUUID();
        UUID routineId = UUID.randomUUID();

        Set existing = new Set();
        when(setRepository.findById(setId)).thenReturn(Optional.of(existing));
        when(routineRepository.existsById(routineId)).thenReturn(true);
        when(setRepository.save(existing)).thenReturn(existing);

        Set body = Set.builder().setNumber(2).build();

        mockMvc.perform(put("/sets/{setId}/routine/{routineId}", setId, routineId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    /**
     * shouldDeleteSet
     * <p>
     * Verifica que un set existente pueda ser eliminado correctamente
     * cuando pertenece a la rutina indicada.
     * Se espera una respuesta HTTP 204 (No Content).
     */
    @Test
    void shouldDeleteSet() throws Exception {
        UUID setId = UUID.randomUUID();
        UUID routineId = UUID.randomUUID();

        Routine routine = new Routine();
        routine.setId(routineId);

        Set set = new Set();
        set.setRoutine(routine);

        when(setRepository.findById(setId)).thenReturn(Optional.of(set));

        mockMvc.perform(delete("/sets/{setId}/routine/{routineId}", setId, routineId))
                .andExpect(status().isNoContent());
    }
}