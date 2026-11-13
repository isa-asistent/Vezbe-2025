import { Component, computed, inject, input } from "@angular/core";
import { DomSanitizer } from "@angular/platform-browser";
import { GrafanaService } from "../../../../../core/services/grafana.service";

@Component({
  imports: [],
  selector: "app-temperature-grafana",
  styleUrl: "./temperature-grafana.component.css",
  templateUrl: "./temperature-grafana.component.html",
})
export class TemperatureGrafanaComponent { private sanitizer = inject(DomSanitizer);

  /** Flux range start from the dropdown, e.g. "-1h" */
  span = input.required<string>();

  private readonly grafanaService = inject(GrafanaService);

  // Replace with your dashboard uid, slug and panel id (ideally from environment config)


  readonly src = computed((span = this.span()) => {
    const params = this.grafanaService.getGraph(span) 
    // Safe: the base URL is a constant and span comes from a fixed list
    return this.sanitizer.bypassSecurityTrustResourceUrl(`${params}`);
  });
}
