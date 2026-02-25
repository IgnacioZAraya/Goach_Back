package com.goach_backend.goach.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goach_backend.goach.logic.entity.gym.Gym;
import com.goach_backend.goach.logic.entity.gym.GymRepository;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainee.GymTrainee;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainee.GymTraineeRepository;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainer.GymTrainer;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainer.GymTrainerRepository;
import com.goach_backend.goach.logic.entity.user.User;
import com.goach_backend.goach.logic.entity.user.UserRepository;
import com.goach_backend.goach.logic.entity.auth.JwtAuthenticationFilter;
import com.goach_backend.goach.rest.gym.GymRestController;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = GymRestController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class GymRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GymRepository gymRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private GymTraineeRepository gymTraineeRepository;

    @MockitoBean
    private GymTrainerRepository gymTrainerRepository;

    //shouldReturnAllGyms
    //Verifica que el endpoint GET /gym devuelva correctamente la lista de gimnasios registrados.
    //El test simula que el repositorio devuelve un gimnasio y valida que la respuesta sea 200 OK.
    //También confirma que se llamó al método findAll() del repositorio.

    @Test
    void shouldReturnAllGyms() throws Exception {
        Gym gym = new Gym();
        gym.setName("Gold's Gym");

        when(gymRepository.findAll()).thenReturn(List.of(gym));

        mockMvc.perform(get("/gym"))
                .andExpect(status().isOk());

        verify(gymRepository).findAll();
    }

    //shouldReturnGymsByName
    //Verifica que el endpoint GET /gym/filterByName/{name} filtre gimnasios por nombre correctamente.
    //El test simula que el repositorio devuelve un gimnasio cuyo nombre coincide con el filtro y valida que la respuesta sea 200 OK.
    //También confirma que se llamó al método findGymByName("Power").

    @Test
    void shouldReturnGymsByName() throws Exception {
        Gym gym = new Gym();
        gym.setName("Power House");

        when(gymRepository.findGymByName("Power"))
                .thenReturn(List.of(gym));

        mockMvc.perform(get("/gym/filterByName/{name}", "Power"))
                .andExpect(status().isOk());

        verify(gymRepository).findGymByName("Power");
    }

    //shouldReturnEmptyGymPopulationWhenGymNotFound
    //Verifica que el endpoint GET /gym/{ownerId} responda con datos vacíos cuando no existe un gimnasio asociado al dueño indicado.
    //El test simula que el repositorio no encuentra el gimnasio y valida que la respuesta sea 200 OK.
    //También verifica que el campo totalPopulation sea 0 y que el campo name sea una cadena vacía.

    @Test
    void shouldReturnEmptyGymPopulationWhenGymNotFound() throws Exception {
        UUID ownerId = UUID.randomUUID();

        when(gymRepository.findByOwner_Id(ownerId)).thenReturn(null);

        mockMvc.perform(get("/gym/{ownerId}", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPopulation").value(0))
                .andExpect(jsonPath("$.name").value(""));

        verify(gymRepository).findByOwner_Id(ownerId);
    }

    //shouldReturnGymPopulation
    //Verifica que el endpoint GET /gym/{ownerId} calcule correctamente la población total de un gimnasio.
    //El test simula un gimnasio con un entrenador y un trainee asociados, y valida que la respuesta sea 200 OK.
    //También verifica que el campo totalPopulation sea 2.
    //Además, confirma que se consultaron los repositorios de entrenadores y trainees por el id del gimnasio.

    @Test
    void shouldReturnGymPopulation() throws Exception {
        UUID ownerId = UUID.randomUUID();
        UUID gymId = UUID.randomUUID();

        User owner = new User();
        owner.setId(ownerId);
        owner.setName("Owner");
        owner.setEmail("owner@test.com");

        Gym gym = new Gym();
        gym.setId(gymId);
        gym.setName("Goach Gym");
        gym.setOwner(owner);

        User trainer = new User();
        trainer.setId(UUID.randomUUID());
        trainer.setName("Trainer");
        trainer.setEmail("trainer@test.com");

        User trainee = new User();
        trainee.setId(UUID.randomUUID());
        trainee.setName("Trainee");
        trainee.setEmail("trainee@test.com");

        GymTrainer gymTrainer = new GymTrainer();
        gymTrainer.setTrainer(trainer);

        GymTrainee gymTrainee = new GymTrainee();
        gymTrainee.setTrainee(trainee);

        when(gymRepository.findByOwner_Id(ownerId)).thenReturn(gym);
        when(gymTrainerRepository.findByGym_Id(gymId)).thenReturn(List.of(gymTrainer));
        when(gymTraineeRepository.findByGym_Id(gymId)).thenReturn(List.of(gymTrainee));

        mockMvc.perform(get("/gym/{ownerId}", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPopulation").value(2));

        verify(gymTrainerRepository).findByGym_Id(gymId);
        verify(gymTraineeRepository).findByGym_Id(gymId);
    }

    //shouldCreateGym
    //Verifica que el endpoint POST /gym cree un gimnasio correctamente cuando el dueño existe.
    //El test simula que el usuario dueño está registrado, envía los datos del gimnasio en formato JSON y valida que la respuesta sea 201 Created.
    //También confirma que se llamó al método save() del repositorio.

    @Test
    void shouldCreateGym() throws Exception {
        UUID ownerId = UUID.randomUUID();

        User owner = new User();
        owner.setId(ownerId);

        Gym body = new Gym();
        body.setName("New Gym");
        body.setOwner(owner);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(gymRepository.save(any(Gym.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/gym")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());

        verify(gymRepository).save(any(Gym.class));
    }

    //shouldFailCreateWhenOwnerIsMissing
    //Verifica que el endpoint POST /gym falle cuando no se envía un dueño en la solicitud.
    //El test envía un gimnasio sin información del propietario y valida que la respuesta sea 400 Bad Request.

    @Test
    void shouldFailCreateWhenOwnerIsMissing() throws Exception {
        Gym body = new Gym();
        body.setName("New Gym");

        mockMvc.perform(post("/gym")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    //shouldFailCreateWhenOwnerNotFound
    //Verifica que el endpoint POST /gym falle cuando el dueño indicado no existe en la base de datos.
    //El test simula que el repositorio de usuarios no encuentra al dueño y valida que la respuesta sea 404 Not Found.

    @Test
    void shouldFailCreateWhenOwnerNotFound() throws Exception {
        UUID ownerId = UUID.randomUUID();

        User owner = new User();
        owner.setId(ownerId);

        Gym body = new Gym();
        body.setName("New Gym");
        body.setOwner(owner);

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/gym")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    //shouldUpdateGym
    //Verifica que el endpoint PUT /gym/{gymId} actualice correctamente un gimnasio existente.
    //El test simula que el gimnasio existe, que el nuevo dueño también existe, envía los nuevos datos y valida que la respuesta sea 200 OK.
    //También confirma que se llamó al método save() del repositorio.

    @Test
    void shouldUpdateGym() throws Exception {
        UUID gymId = UUID.randomUUID();
        UUID newOwnerId = UUID.randomUUID();

        User newOwner = new User();
        newOwner.setId(newOwnerId);

        Gym existingGym = new Gym();
        existingGym.setId(gymId);
        existingGym.setName("Old Gym");

        Gym body = new Gym();
        body.setName("Updated Gym");
        body.setOwner(newOwner);

        when(gymRepository.findById(gymId)).thenReturn(Optional.of(existingGym));
        when(userRepository.findById(newOwnerId)).thenReturn(Optional.of(newOwner));
        when(gymRepository.save(any(Gym.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/gym/{gymId}", gymId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        verify(gymRepository).save(any(Gym.class));
    }
}