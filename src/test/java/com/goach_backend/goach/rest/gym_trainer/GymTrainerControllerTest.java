package com.goach_backend.goach.rest.gym_trainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goach_backend.goach.logic.entity.auth.JwtAuthenticationFilter;
import com.goach_backend.goach.logic.entity.gym.Gym;
import com.goach_backend.goach.logic.entity.gym.GymRepository;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainer.GymTrainer;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainer.GymTrainerId;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainer.GymTrainerRepository;
import com.goach_backend.goach.logic.entity.user.User;
import com.goach_backend.goach.logic.entity.user.UserRepository;
import com.goach_backend.goach.logic.enums.AssocStatus;
import com.goach_backend.goach.logic.enums.MembershipState;
import com.goach_backend.goach.rest.gym_trainer.gym_trainer.GymTrainerController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = GymTrainerController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class GymTrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GymRepository gymRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private GymTrainerRepository gymTrainerRepository;

    /**
     * shouldReturnGymTrainerRelation
     * <p>
     * Verifica que el controlador pueda retornar correctamente
     * una relación existente entre un gimnasio y un entrenador
     * cuando ambos identificadores son válidos.
     * Se espera una respuesta HTTP 200 (OK).
     */
    @Test
    void shouldReturnGymTrainerRelation() throws Exception {
        UUID gymId = UUID.randomUUID();
        UUID trainerId = UUID.randomUUID();

        GymTrainer relation = new GymTrainer();
        when(gymTrainerRepository.findById(new GymTrainerId(gymId, trainerId)))
                .thenReturn(Optional.of(relation));

        mockMvc.perform(get("/gyms/{gymId}/trainers/{trainerId}", gymId, trainerId))
                .andExpect(status().isOk());
    }

    /**
     * shouldFailWhenRelationNotFound
     * <p>
     * Prueba un escenario negativo donde se solicita una relación
     * gym-trainer inexistente.
     * El repositorio retorna vacío y el controlador lanza una excepción,
     * resultando en una respuesta HTTP 500.
     */
    @Test
    void shouldFailWhenRelationNotFound() throws Exception {
        UUID gymId = UUID.randomUUID();
        UUID trainerId = UUID.randomUUID();

        when(gymTrainerRepository.findById(new GymTrainerId(gymId, trainerId)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/gyms/{gymId}/trainers/{trainerId}", gymId, trainerId))
                .andExpect(status().isInternalServerError());
    }

    /**
     * shouldCreateGymTrainerRelation
     * <p>
     * Valida el flujo positivo de creación de una relación entre
     * un gimnasio y un entrenador con datos válidos.
     * Se mockean las dependencias necesarias y se espera
     * una respuesta HTTP 201 (Created).
     */
    @Test
    void shouldCreateGymTrainerRelation() throws Exception {
        UUID gymId = UUID.randomUUID();

        Gym gym = new Gym();
        User trainer = new User();
        trainer.setId(UUID.randomUUID());
        trainer.setEmail("trainer@test.com");

        GymTrainer body = new GymTrainer();
        body.setTrainer(trainer);
        body.setAssociateStatus(AssocStatus.ACTIVE);
        body.setGymPaymentStatus(MembershipState.UTD);

        when(gymRepository.findById(gymId)).thenReturn(Optional.of(gym));
        when(userRepository.findByEmail(trainer.getEmail()))
                .thenReturn(Optional.of(trainer));
        when(gymTrainerRepository.save(org.mockito.ArgumentMatchers.any(GymTrainer.class)))
                .thenReturn(new GymTrainer(gym, trainer));

        mockMvc.perform(post("/gyms/{gymId}/trainers", gymId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    /**
     * shouldFailWhenGymNotFound
     * <p>
     * Prueba un escenario negativo donde se intenta crear una relación
     * gym-trainer utilizando un gimnasio inexistente.
     * El sistema debe fallar de forma controlada y responder con HTTP 500.
     */
    @Test
    void shouldFailWhenGymNotFound() throws Exception {
        UUID gymId = UUID.randomUUID();

        GymTrainer body = new GymTrainer();

        when(gymRepository.findById(gymId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/gyms/{gymId}/trainers", gymId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isInternalServerError());
    }

    /**
     * shouldUpdateGymTrainer
     * <p>
     * Verifica que una relación gym-trainer existente pueda ser actualizada
     * correctamente mediante una petición PUT.
     * Se valida que el controlador procese los cambios y responda con HTTP 200 (OK).
     */
    @Test
    void shouldUpdateGymTrainer() throws Exception {
        UUID gymId = UUID.randomUUID();
        UUID trainerId = UUID.randomUUID();

        GymTrainer existing = new GymTrainer();
        existing.setAssociateStatus(AssocStatus.INACTIVE);

        when(gymTrainerRepository.findById(new GymTrainerId(gymId, trainerId)))
                .thenReturn(Optional.of(existing));

        GymTrainer body = new GymTrainer();
        body.setAssociateStatus(AssocStatus.ACTIVE);

        mockMvc.perform(put("/gyms/{gymId}/trainers/{trainerId}", gymId, trainerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    /**
     * shouldFailUpdateWhenRelationNotFound
     * <p>
     * Prueba un escenario negativo donde se intenta actualizar una relación
     * gym-trainer que no existe.
     * El repositorio retorna vacío y el sistema responde con HTTP 500.
     */
    @Test
    void shouldFailUpdateWhenRelationNotFound() throws Exception {
        UUID gymId = UUID.randomUUID();
        UUID trainerId = UUID.randomUUID();

        when(gymTrainerRepository.findById(new GymTrainerId(gymId, trainerId)))
                .thenReturn(Optional.empty());

        GymTrainer body = new GymTrainer();

        mockMvc.perform(put("/gyms/{gymId}/trainers/{trainerId}", gymId, trainerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isInternalServerError());
    }

    /**
     * shouldDeleteGymTrainer
     * <p>
     * Verifica que una relación gym-trainer pueda ser eliminada correctamente.
     * Independientemente de la existencia previa, el endpoint debe responder
     * con HTTP 204 (No Content).
     */
    @Test
    void shouldDeleteGymTrainer() throws Exception {
        UUID gymId = UUID.randomUUID();
        UUID trainerId = UUID.randomUUID();

        mockMvc.perform(delete("/gyms/{gymId}/trainers/{trainerId}", gymId, trainerId))
                .andExpect(status().isNoContent());
    }

    /**
     * deleteShouldAlwaysReturnNoContent
     * <p>
     * Valida que el endpoint de eliminación de relaciones gym-trainer
     * siempre retorne una respuesta HTTP 204 (No Content),
     * garantizando un comportamiento idempotente.
     */
    @Test
    void deleteShouldAlwaysReturnNoContent() throws Exception {
        UUID gymId = UUID.randomUUID();
        UUID trainerId = UUID.randomUUID();

        mockMvc.perform(delete("/gyms/{gymId}/trainers/{trainerId}", gymId, trainerId))
                .andExpect(status().isNoContent());
    }
}