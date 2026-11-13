package rs.ac.uns.ftn.ci.isa.influx_demo.temperature;

import java.time.Instant;

public class DTO {

    public static class Get {
        public record Request() {
        }

        public record Response(double value, String location, Instant timeStamp) {

        }
    }

    public static class Create {
        public record Request(double value, String location) {

        }
    }


}
