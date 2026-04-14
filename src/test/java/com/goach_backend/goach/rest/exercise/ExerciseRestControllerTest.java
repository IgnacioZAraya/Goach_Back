package com.goach_backend.goach.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goach_backend.goach.logic.entity.exercise.Exercise;
import com.goach_backend.goach.logic.entity.exercise.ExerciseRepository;
import com.goach_backend.goach.logic.entity.auth.JwtAuthenticationFilter;
import com.goach_backend.goach.rest.exercise.ExerciseRestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ExerciseRestController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class ExerciseRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExerciseRepository exerciseRepository;

    //shouldReturnAllExercises
    //Verifica que el endpoint GET /exercise responda correctamente cuando existen ejercicios registrados.
    //El test simula que el repositorio devuelve una lista con un ejercicio y valida que la respuesta sea 200 OK.
    //También confirma que se llamó al método findAll() del repositorio.

    @Test
    void shouldReturnAllExercises() throws Exception {
        Exercise exercise = new Exercise();
        exercise.setName("Bench Press");

        when(exerciseRepository.findAll()).thenReturn(List.of(exercise));

        mockMvc.perform(get("/exercise"))
                .andExpect(status().isOk());

        verify(exerciseRepository).findAll();
    }

    //shouldReturnEmptyExerciseList
    //Verifica que el endpoint GET /exercise responda correctamente cuando no hay ejercicios registrados.
    //El test simula que el repositorio devuelve una lista vacía y valida que la respuesta sea 200 OK.
    //También confirma que se llamó al método findAll() del repositorio.

    @Test
    void shouldReturnEmptyExerciseList() throws Exception {
        when(exerciseRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/exercise"))
                .andExpect(status().isOk());

        verify(exerciseRepository).findAll();
    }

    //shouldReturnExercisesByName
    //Verifica que el endpoint GET /exercise/filterByName/{name} filtre ejercicios por nombre correctamente.
    //El test simula que el repositorio devuelve un ejercicio con el nombre solicitado y valida que la respuesta sea 200 OK.
    //También confirma que se llamó al método findExerciseByName("Squat").

    @Test
    void shouldReturnExercisesByName() throws Exception {
        Exercise exercise = new Exercise();
        exercise.setName("Squat");

        when(exerciseRepository.findExerciseByName("Squat"))
                .thenReturn(List.of(exercise));

        mockMvc.perform(get("/exercise/filterByName/{name}", "Squat"))
                .andExpect(status().isOk());

        verify(exerciseRepository).findExerciseByName("Squat");
    }

    //shouldCreateExercise
    //Verifica que el endpoint POST /exercise cree un nuevo ejercicio cuando no existe uno con el mismo nombre.
    //El test simula que no hay ejercicios repetidos, envía un ejercicio en formato JSON y valida que la respuesta sea 201 Created.
    //También confirma que se llamó al método save() del repositorio.

    @Test
    void shouldCreateExercise() throws Exception {
        Exercise body = new Exercise();
        body.setName("Deadlift");

        when(exerciseRepository.findExerciseByName("Deadlift")).thenReturn(List.of());
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(body);

        mockMvc.perform(post("/exercise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());

        verify(exerciseRepository).save(any(Exercise.class));
    }

    //shouldFailCreateWhenExerciseAlreadyExists
    //Verifica que el endpoint POST /exercise no permita crear un ejercicio si ya existe otro con el mismo nombre.
    //El test simula que el repositorio ya contiene un ejercicio llamado igual y valida que la respuesta sea 409 Conflict.
    //También confirma que no se llamó al método save().

    @Test
    void shouldFailCreateWhenExerciseAlreadyExists() throws Exception {
        Exercise existing = new Exercise();
        existing.setName("Deadlift");

        Exercise body = new Exercise();
        body.setName("Deadlift");

        when(exerciseRepository.findExerciseByName("Deadlift"))
                .thenReturn(List.of(existing));

        mockMvc.perform(post("/exercise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict());

        verify(exerciseRepository, never()).save(any(Exercise.class));
    }

    //shouldUpdateExercise
    //Verifica que el endpoint PUT /exercise/{exerciseId} actualice un ejercicio existente correctamente.
    //El test simula que el ejercicio existe en el repositorio, envía nuevos datos y valida que la respuesta sea 201 Created.
    //También confirma que se llamó al método save() del repositorio.

    @Test
    void shouldUpdateExercise() throws Exception {
        UUID exerciseId = UUID.randomUUID();

        Exercise existing = new Exercise();
        existing.setName("Old Name");
        existing.setDescription("Old Desc");

        Exercise body = new Exercise();
        body.setName("New Name");
        body.setDescription("New Desc");

        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(existing));
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/exercise/{exerciseId}", exerciseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());

        verify(exerciseRepository).save(any(Exercise.class));
    }

    //shouldFailUpdateWhenExerciseNotFound
    //Verifica que el endpoint PUT /exercise/{exerciseId} falle cuando el ejercicio que se quiere actualizar no existe.
    //El test simula que el repositorio no encuentra el ejercicio por su id y valida que la respuesta sea 500 Internal Server Error.

    @Test
    void shouldFailUpdateWhenExerciseNotFound() throws Exception {
        UUID exerciseId = UUID.randomUUID();

        Exercise body = new Exercise();
        body.setName("New Name");

        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/exercise/{exerciseId}", exerciseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isInternalServerError());
    }

    //shouldDeleteExercise
    //Verifica que el endpoint DELETE /exercise/{exerciseId} elimine correctamente un ejercicio existente.
    //El test simula que el ejercicio existe en el repositorio y valida que la respuesta sea 204 No Content.
    //También confirma que se llamó al método delete() del repositorio.
    //
    //Si quieres, también te lo puedo acomodar en formato de comentarios para ponerlos directamente encima de cada método de prueba.

    @Test
    void shouldDeleteExercise() throws Exception {
        UUID exerciseId = UUID.randomUUID();

        Exercise existing = new Exercise();
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(existing));

        mockMvc.perform(delete("/exercise/{exerciseId}", exerciseId))
                .andExpect(status().isNoContent());

        verify(exerciseRepository).delete(existing);
    }
}