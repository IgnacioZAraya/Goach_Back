package com.goach_backend.goach.rest.gym;

import com.goach_backend.goach.logic.entity.exercise.Exercise;
import com.goach_backend.goach.logic.entity.gym.Gym;
import com.goach_backend.goach.logic.entity.gym.GymRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/gym")
public class GymRestController {
    @Autowired
    private GymRepository gymRepository;

    @GetMapping
    public List<Gym> getAllGym(){
        return gymRepository.findAll();
    }
    @GetMapping("/filterByName/{name}")
    public List<Gym> getGymByName(@PathVariable String name) {
        return gymRepository.findGymByName(name);
    }
}
