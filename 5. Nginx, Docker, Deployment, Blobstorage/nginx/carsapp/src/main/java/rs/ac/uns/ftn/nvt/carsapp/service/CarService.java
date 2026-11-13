package rs.ac.uns.ftn.nvt.carsapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.nvt.carsapp.dto.GetCarsResponse;
import rs.ac.uns.ftn.nvt.carsapp.model.Car;
import rs.ac.uns.ftn.nvt.carsapp.repository.ICarRepository;

import java.util.List;

@Service
public class CarService {
    @Value("${spring.application.name:Default-App}")
    private String appName;
    private final ICarRepository carRepository;

    public CarService(ICarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<GetCarsResponse> getAll() {
        return carRepository.findAll().stream()
                .map(c -> new GetCarsResponse(
                        c.getId(),
                        c.getMake(),
                        c.getModel(),
                        c.getYear(),
                        c.getPrice(),
                        appName
                )).toList();
    }
}
