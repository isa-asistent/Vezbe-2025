package rs.ac.uns.ftn.nvt.carsapp.dto;

import jakarta.persistence.Column;

public record GetCarsResponse(
        Long id,
        String make,

        String model,

        String year,

        Double price,
        String serverName
) {

}
