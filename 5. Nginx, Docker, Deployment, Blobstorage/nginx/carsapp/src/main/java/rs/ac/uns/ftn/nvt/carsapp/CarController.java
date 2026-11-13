package rs.ac.uns.ftn.nvt.carsapp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.ftn.nvt.carsapp.dto.GetCarsResponse;
import rs.ac.uns.ftn.nvt.carsapp.model.Car;
import rs.ac.uns.ftn.nvt.carsapp.service.CarService;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/cars")
public class CarController {
    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public ResponseEntity<List<GetCarsResponse>> getAll() {
        return ResponseEntity.ok(carService.getAll());
    }
}
