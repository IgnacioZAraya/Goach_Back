package com.goach_backend.goach.rest.trainee_routine;
import com.goach_backend.goach.logic.entity.routine.Routine;
import com.goach_backend.goach.logic.entity.routine.RoutineRepository;
import com.goach_backend.goach.logic.entity.trainee_routine.TraineeRoutine;
import com.goach_backend.goach.logic.entity.trainee_routine.TraineeRoutineId;
import com.goach_backend.goach.logic.entity.trainee_routine.TraineeRoutineRepository;
import com.goach_backend.goach.logic.entity.user.User;
import com.goach_backend.goach.logic.entity.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/trainee-routines")
public class TraineeRoutineController {

    private final TraineeRoutineRepository repo;
    private final UserRepository userRepo;
    private final RoutineRepository routineRepo;

    public TraineeRoutineController(
            TraineeRoutineRepository repo,
            UserRepository userRepo,
            RoutineRepository routineRepo
    ) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.routineRepo = routineRepo;
    }

    /* ========= READ ========= */

    @GetMapping("/trainee/{traineeId}")
    public List<TraineeRoutine> listByTrainee(@PathVariable UUID traineeId) {
        return repo.findByIdTraineeId(traineeId);
    }

    @GetMapping("/routine/{routineId}")
    public List<TraineeRoutine> listByRoutine(@PathVariable UUID routineId) {
        return repo.findByIdRoutineId(routineId);
    }

    @GetMapping("/{traineeId}/{routineId}")
    public TraineeRoutine getOne(@PathVariable UUID traineeId, @PathVariable UUID routineId) {
        return repo.findById(new TraineeRoutineId(traineeId, routineId))
                .orElseThrow(() -> new EntityNotFoundException("TraineeRoutine no encontrado"));
    }

    /* ========= CREATE ========= */

    /**
     * Crea la relación. El body debe traer al menos:
     * - trainee.userId
     * - routine.routineId
     * - assignedAt (si es null, se colocará ahora)
     *
     * Se puede enviar también el id embebido; si no viene, se arma con los IDs anteriores.
     */
    @PostMapping
    @Transactional
    public TraineeRoutine create(@Valid @RequestBody TraineeRoutine body) {
        // Resolver IDs desde la entidad recibida
        UUID traineeId;
        UUID routineId;

        if (body.getId() == null) {
            routineId = (body.getRoutine() != null) ? body.getRoutine().getId() : null;
            traineeId = (body.getTrainee() != null) ? body.getTrainee().getId() : null;
            if (traineeId == null || routineId == null) {
                throw new IllegalArgumentException("Debe especificar trainee.userId y routine.routineId");
            }
            body.setId(new TraineeRoutineId(traineeId, routineId));
        } else {
            traineeId = body.getId().getTraineeId();
            routineId  = body.getId().getRoutineId();
        }
        if (repo.existsByIdTraineeIdAndIdRoutineId(traineeId, routineId)) {
            return repo.findById(new TraineeRoutineId(traineeId, routineId)).get();
        }

        // Cargar entidades administradas (garantiza @MapsId y FK válidas)
        User trainee = userRepo.findById(traineeId)
                .orElseThrow(() -> new EntityNotFoundException("Trainee no existe: " + traineeId));
        Routine routine = routineRepo.findById(routineId)
                .orElseThrow(() -> new EntityNotFoundException("Routine no existe: " + routineId));

        body.setTrainee(trainee);
        body.setRoutine(routine);

        if (body.getAssignedAt() == null) {
            body.setAssignedAt(OffsetDateTime.now());
        }

        return repo.save(body);
    }

    /* ========= UPDATE ========= */

    /**
     * Actualiza solo la fecha de asignación usando la entidad como body.
     * El path determina el registro a modificar.
     */
    @PutMapping("/{traineeId}/{routineId}/assigned-at")
    @Transactional
    public TraineeRoutine updateAssignedAt(@PathVariable UUID traineeId,
                                           @PathVariable UUID routineId,
                                           @Valid @RequestBody TraineeRoutine body) {
        TraineeRoutine tr = repo.findById(new TraineeRoutineId(traineeId, routineId))
                .orElseThrow(() -> new EntityNotFoundException("TraineeRoutine no encontrado"));

        if (body.getAssignedAt() == null) {
            throw new IllegalArgumentException("assignedAt es obligatorio para actualizar");
        }
        tr.setAssignedAt(body.getAssignedAt());
        return repo.save(tr);
    }

    /* ========= DELETE ========= */

    @DeleteMapping("/{traineeId}/{routineId}")
    @Transactional
    public void delete(@PathVariable UUID traineeId, @PathVariable UUID routineId) {
        long deleted = repo.deleteByIdTraineeIdAndIdRoutineId(traineeId, routineId);
        if (deleted == 0) throw new EntityNotFoundException("Nada que borrar");
    }
}