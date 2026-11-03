package rs.ac.uns.ftn.nvt.carsapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.uns.ftn.nvt.carsapp.model.Car;

public interface ICarRepository extends JpaRepository<Car, Long> {
}
