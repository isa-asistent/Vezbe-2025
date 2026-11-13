import { Component, computed, inject, model, signal } from "@angular/core";
import { TemperatureApiService } from "../../../core/services/temperature.api.service";
import { Temperature } from "../../../core/models/temperature.model";
import { switchMap, tap } from "rxjs";
import { FormsModule } from "@angular/forms";
import { CommonModule, DatePipe } from "@angular/common";
import { TemperatureTableComponent } from "./components/temperature-table.component/temperature-table.component";
import { TemperatureGraphComponent } from "./components/temperature-graph.component/temperature-graph.component";
import { TemperatureGrafanaComponent } from "./components/temperature-grafana.component/temperature-grafana.component";

@Component({
  imports: [FormsModule,
     CommonModule,
      TemperatureTableComponent,
       TemperatureGraphComponent,
      TemperatureGrafanaComponent],
  selector: "app-list",
  templateUrl: "./list.component.html",
})
export class ListComponent {
  private readonly service = inject(TemperatureApiService)

  readonly spans = [
            { label: 'Last minute', value: '-1m' },

        { label: 'Last 5 minutes', value: '-5m' },

    { label: 'Last 15 minutes', value: '-15m' },
    { label: 'Last hour', value: '-1h' },
    { label: 'Last 6 hours', value: '-6h' },
    { label: 'Last 24 hours', value: '-24h' },
    { label: 'Last 7 days', value: '-7d' },
    { label: 'Last 30 days', value: '-30d' },
  ];

  readonly location = 'Office';
  span = model('-1h');
  readonly views = [
    { label: 'Table', value: 'table' },
    { label: 'Graph', value: 'graph' },
        { label: 'Grafana', value: 'grafana' },
  ] as const;
  readonly view = signal<'table' | 'graph' | 'grafana'>('table');

  private readings = signal<Temperature.ForGet[]>([]);

  constructor() {
    this.load();
  }

  load() {
    const span = this.span()
    this.service.get({ span })
      .pipe(
        tap(
          data => this.readings.set(data))
      )
      .subscribe();
  }

  createReading() {
    const body: Temperature.ForCreate = {
      value: +(Math.random() * 50 - 10).toFixed(1), // -10 to 40
      location: this.location,
    };

    this.service.create(body)
      .pipe(
        switchMap(() => this.service.get(
          { span: this.span() }
        )
        ),
        tap(data => this.readings.set(data))
      )
      .subscribe();
  }
}