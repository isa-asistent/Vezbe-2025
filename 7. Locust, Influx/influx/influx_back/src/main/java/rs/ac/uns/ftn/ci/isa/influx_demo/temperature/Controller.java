package rs.ac.uns.ftn.ci.isa.influx_demo.temperature;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/temperature")
@CrossOrigin(origins = "http://localhost:4200")
public final class Controller {
    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<DTO.Get.Response>> getInSpan(@RequestParam(defaultValue = "-1h") String span) {
        var response = service.getTemperatureMetrics(span);
        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody DTO.Create.Request request){
        service.create(request);
        return ResponseEntity.created(URI.create("")).build();
    }
}
