package rs.ac.uns.ftn.ci.isa.influx_demo.temperature;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public final class Service {


    private final WriteApiBlocking writeApi;
    private final QueryApi queryApi;

    @Value("${influxdb.bucket}")
    private String influxBucket;

    @Value("${influxdb.org}")
    private String influxOrg;

    public Service(InfluxDBClient influxClient) {
        this.writeApi = influxClient.getWriteApiBlocking();
        queryApi = influxClient.getQueryApi();
    }

    public void create(DTO.Create.Request request) {

        // Construct a data point
        Point point = Point.measurement("temperature")
                .addTag("location", request.location())
                .addField("value", request.value())
                .time(Instant.now(), WritePrecision.NS);

        // Writes to the "default" bucket specified in the config
        writeApi.writePoint(point);
    }

    public List<DTO.Get.Response> getTemperatureMetrics(
            String span) {

        // Build the dynamic Flux query
        String fluxQuery = String.format(
                "from(bucket: \"%s\") " +
                        "  |> range(start: %s) " +
                        "  |> filter(fn: (r) => r[\"_measurement\"] == \"temperature\") " +
                        "  |> filter(fn: (r) => r[\"_field\"] == \"value\")",
                influxBucket, span
        );

        List<FluxTable> tables = queryApi.query(fluxQuery, influxOrg);

        List<DTO.Get.Response> responseList = new ArrayList<>();

        // Parse Flux tables and records into DTOs
        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {

                Object value = record.getValue();
                if (value == null) continue;

                DTO.Get.Response dto = new DTO.Get.Response(

                        (Double) value,
                        (String) record.getValueByKey("location"),
                        record.getTime()
                );

                responseList.add(dto);
            }
        }

        return responseList;
    }

}
