package com.goach_backend.goach.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goach_backend.goach.logic.entity.role.RoleEnum;
import com.goach_backend.goach.logic.entity.routine.Routine;
import com.goach_backend.goach.logic.entity.routine.RoutineRepository;
import com.goach_backend.goach.logic.entity.set_exercise.Set;
import com.goach_backend.goach.logic.entity.set_exercise.SetExercise;
import com.goach_backend.goach.logic.entity.set_exercise.SetExerciseRepository;
import com.goach_backend.goach.logic.entity.set_exercise.SetRepository;
import com.goach_backend.goach.logic.entity.stats.StatsRepository;
import com.goach_backend.goach.logic.entity.trainee_routine.TraineeRoutineRepository;
import com.goach_backend.goach.logic.entity.user.User;
import com.goach_backend.goach.logic.entity.user.UserRepository;
import com.goach_backend.goach.logic.entity.workout_sessions.WorkoutSession;
import com.goach_backend.goach.logic.entity.workout_sessions.WorkoutSessionRepository;
import com.goach_backend.goach.logic.entity.auth.JwtAuthenticationFilter;
import com.goach_backend.goach.rest.routine.RoutineRestController;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = RoutineRestController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class RoutineRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoutineRepository routineRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private TraineeRoutineRepository traineeRoutineRepository;

    @MockitoBean
    private SetRepository setRepository;

    @MockitoBean
    private StatsRepository statsRepository;

    @MockitoBean
    private WorkoutSessionRepository workoutSessionRepository;

    @MockitoBean
    private SetExerciseRepository setExerciseRepository;

    //shouldReturnAllRoutines
    //Verifica que el endpoint GET /routine devuelva correctamente la lista de rutinas registradas.
    //El test simula que el repositorio devuelve una rutina y valida que la respuesta sea 200 OK.
    //También confirma que se llamó al método findAll() del repositorio.

    @Test
    void shouldReturnAllRoutines() throws Exception {
        Routine routine = new Routine();
        routine.setName("Push Day");

        when(routineRepository.findAll()).thenReturn(List.of(routine));

        mockMvc.perform(get("/routine"))
                .andExpect(status().isOk());

        verify(routineRepository).findAll();
    }

    //shouldReturnRoutineById
    //Verifica que el endpoint GET /routine/{routineId} devuelva correctamente una rutina cuando existe.
    //El test simula que el repositorio encuentra la rutina por su id y valida que la respuesta sea 200 OK.
    //También confirma que se llamó al método findById(routineId).

    @Test
    void shouldReturnRoutineById() throws Exception {
        UUID routineId = UUID.randomUUID();

        Routine routine = new Routine();
        routine.setId(routineId);
        routine.setName("Push Day");

        when(routineRepository.findById(routineId)).thenReturn(Optional.of(routine));

        mockMvc.perform(get("/routine/{routineId}", routineId))
                .andExpect(status().isOk());

        verify(routineRepository).findById(routineId);
    }

    //shouldFailWhenRoutineNotFoundById
    //Verifica que el endpoint GET /routine/{routineId} falle cuando la rutina no existe.
    //El test simula que el repositorio no encuentra la rutina y valida que la respuesta sea 404 Not Found.

    @Test
    void shouldFailWhenRoutineNotFoundById() throws Exception {
        UUID routineId = UUID.randomUUID();

        when(routineRepository.findById(routineId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/routine/{routineId}", routineId))
                .andExpect(status().isNotFound());
    }

    //shouldReturnRoutinesByName
    //Verifica que el endpoint GET /routine/filterByName/{name} filtre rutinas por nombre correctamente.
    //El test simula que el repositorio devuelve una rutina que coincide con el filtro y valida que la respuesta sea 200 OK.
    //También confirma que se llamó al método findRoutineByName("Leg").

    @Test
    void shouldReturnRoutinesByName() throws Exception {
        Routine routine = new Routine();
        routine.setName("Leg Day");

        when(routineRepository.findRoutineByName("Leg"))
                .thenReturn(List.of(routine));

        mockMvc.perform(get("/routine/filterByName/{name}", "Leg"))
                .andExpect(status().isOk());

        verify(routineRepository).findRoutineByName("Leg");
    }

    //shouldCreateRoutine
    //Verifica que el endpoint POST /routine cree una rutina correctamente cuando el usuario asignado es un entrenador válido.
    //El test simula que el usuario existe y tiene rol TRAINER, envía la rutina en formato JSON y valida que la respuesta sea 201 Created.
    //También confirma que se llamó al método save() del repositorio.

    @Test
    void shouldCreateRoutine() throws Exception {
        UUID trainerId = UUID.randomUUID();

        User trainer = new User();
        trainer.setId(trainerId);
        trainer.setRole(RoleEnum.TRAINER);

        Routine body = new Routine();
        body.setName("Upper Body");
        body.setTrainer(trainer);

        when(userRepository.findById(trainerId)).thenReturn(Optional.of(trainer));
        when(routineRepository.save(any(Routine.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/routine")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());

        verify(routineRepository).save(any(Routine.class));
    }

    //shouldFailCreateWhenUserIsNotTrainer
    //Verifica que el endpoint POST /routine falle cuando el usuario asignado no tiene rol de entrenador.
    //El test simula que el usuario existe pero tiene rol TRAINEE y valida que la respuesta sea 404 Not Found.

    @Test
    void shouldFailCreateWhenUserIsNotTrainer() throws Exception {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setRole(RoleEnum.TRAINEE);

        Routine body = new Routine();
        body.setName("Upper Body");
        body.setTrainer(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/routine")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    //shouldUpdateRoutine
    //Verifica que el endpoint PUT /routine/{id} actualice correctamente una rutina existente.
    //El test simula que la rutina existe, que el entrenador asignado es válido, envía los nuevos datos y valida que la respuesta sea 200 OK.
    //También confirma que se llamó al método save() del repositorio.

    @Test
    void shouldUpdateRoutine() throws Exception {
        UUID routineId = UUID.randomUUID();
        UUID trainerId = UUID.randomUUID();

        Routine existing = new Routine();
        existing.setId(routineId);
        existing.setName("Old Routine");

        User trainer = new User();
        trainer.setId(trainerId);
        trainer.setRole(RoleEnum.TRAINER);

        Routine body = new Routine();
        body.setId(routineId);
        body.setName("Updated Routine");
        body.setDescription("Updated Description");
        body.setTrainer(trainer);

        when(routineRepository.findById(routineId)).thenReturn(Optional.of(existing));
        when(userRepository.findById(trainerId)).thenReturn(Optional.of(trainer));
        when(routineRepository.save(any(Routine.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/routine/{id}", trainerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        verify(routineRepository).save(any(Routine.class));
    }

    //shouldDeleteRoutine
    //Verifica que el endpoint DELETE /routine/{id} elimine correctamente una rutina y todos sus datos relacionados.
    //El test simula que la rutina existe junto con sus sets, ejercicios de set y sesiones de entrenamiento asociadas, y valida que la respuesta sea 200 OK.
    //También confirma que se eliminan los ejercicios de set, los sets, las estadísticas del entrenamiento, las sesiones de entrenamiento y finalmente la rutina.

    @Test
    void shouldDeleteRoutine() throws Exception {
        UUID routineId = UUID.randomUUID();
        UUID setId = UUID.randomUUID();
        UUID workoutId = UUID.randomUUID();

        Routine routine = new Routine();
        routine.setId(routineId);

        Set set = new Set();
        set.setId(setId);

        SetExercise setExercise = new SetExercise();

        WorkoutSession workoutSession = new WorkoutSession();
        workoutSession.setId(workoutId);

        when(routineRepository.findById(routineId)).thenReturn(Optional.of(routine));
        when(setRepository.findByRoutine_IdOrderBySetNumberAsc(routineId)).thenReturn(List.of(set));
        when(setExerciseRepository.findBySet_IdOrderByOrderIndexAsc(setId)).thenReturn(List.of(setExercise));
        when(workoutSessionRepository.findByRoutine_Id(routineId)).thenReturn(List.of(workoutSession));

        mockMvc.perform(delete("/routine/{id}", routineId))
                .andExpect(status().isOk());

        verify(setExerciseRepository).delete(setExercise);
        verify(setRepository).delete(set);
        verify(statsRepository).deleteByWorkout_Id(workoutId);
        verify(workoutSessionRepository).delete(workoutSession);
        verify(routineRepository).delete(routine);
    }
}