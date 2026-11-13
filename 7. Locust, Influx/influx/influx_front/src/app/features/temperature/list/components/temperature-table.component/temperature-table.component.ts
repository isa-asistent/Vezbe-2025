import { Component, computed, input } from "@angular/core";
import { Temperature } from "../../../../../core/models/temperature.model";
import { DatePipe, DecimalPipe } from "@angular/common";
import { FormsModule } from "@angular/forms";

@Component({
  imports: [DatePipe, FormsModule,DecimalPipe],
  selector: "app-temperature-table",
  styleUrl: "./temperature-table.component.css",
  templateUrl: "./temperature-table.component.html",
})
export class TemperatureTableComponent {
  readings = input.required<Temperature.ForGet[]>();

  // Most recent first
  readonly sorted = computed(() =>
    [...this.readings()].sort(
      (a, b) => new Date(b.timeStamp).getTime() - new Date(a.timeStamp).getTime()
    )
  );
}
