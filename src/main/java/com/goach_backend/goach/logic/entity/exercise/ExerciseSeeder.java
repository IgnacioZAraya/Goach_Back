package com.goach_backend.goach.logic.entity.exercise;

import com.goach_backend.goach.logic.entity.muscle_group.MuscleGroupEnum;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ExerciseSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final ExerciseRepository exerciseRepository;

    public ExerciseSeeder(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createMainExercises();
    }

    private void createMainExercises() {
        if (exerciseRepository.count() == 0) {
            List<Exercise> mainExercises = Arrays.asList(
                    createExercise("Squat", MuscleGroupEnum.QUADRICEPS,
                            "A compound lower-body exercise that targets the quadriceps, glutes, and hamstrings. Performed by bending the knees and lowering the hips before returning to a standing position."),
                    createExercise("Deadlift", MuscleGroupEnum.GLUTES,
                            "A full-body lift that primarily engages the glutes, hamstrings, and lower back by lifting a weighted barbell from the ground to hip height."),
                    createExercise("Bench Press", MuscleGroupEnum.CHEST,
                            "A classic upper-body exercise that targets the chest, shoulders, and triceps by pressing a barbell or dumbbells away from the chest."),
                    createExercise("Pull-Up", MuscleGroupEnum.BACK_LATS,
                            "A bodyweight exercise focusing on the latissimus dorsi, performed by pulling the body upward until the chin passes the bar."),
                    createExercise("Overhead Press", MuscleGroupEnum.FRONT_DELTOIDS,
                            "An upper-body exercise that develops the shoulders and triceps by pressing a barbell or dumbbells overhead from shoulder level."),
                    createExercise("Plank", MuscleGroupEnum.ABS_RECTUS,
                            "A core stabilization exercise that strengthens the abdominal and lower back muscles by maintaining a straight body position supported by forearms and toes."),
                    createExercise("Lunge", MuscleGroupEnum.QUADRICEPS,
                            "A unilateral leg exercise that builds strength and balance in the quadriceps and glutes by stepping forward and lowering the hips."),
                    createExercise("Push-Up", MuscleGroupEnum.CHEST,
                            "A bodyweight movement that develops the chest, shoulders, triceps, and core by lowering and raising the body from the floor."),
                    createExercise("Bent-Over Row", MuscleGroupEnum.BACK_UPPER,
                            "A pulling exercise that strengthens the upper back and biceps by rowing a barbell or dumbbell toward the torso."),
                    createExercise("Bicep Curl", MuscleGroupEnum.BICEPS,
                            "An isolation exercise that targets the biceps by curling a dumbbell or barbell upward toward the shoulders.")
            );
            exerciseRepository.saveAll(mainExercises);
        }
    }

    private Exercise createExercise(String name, MuscleGroupEnum muscleGroup, String description) {
        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setMuscleGroup(muscleGroup);
        exercise.setDescription(description);
        return exercise;
    }
}
