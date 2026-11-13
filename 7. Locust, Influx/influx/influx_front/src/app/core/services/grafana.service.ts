import { Injectable } from "@angular/core";

@Injectable({ providedIn: 'root' })
export class GrafanaService {
    private readonly DASHBOARD_ID = "adpr95k"
    private readonly PANEL_ID = 1
    private readonly REFRESH_TIME = "1s"
    private readonly THEME = "light"
    private readonly SLUG_LABEL = "temperature"
    getGraph(span: string) {

        return `http://localhost:3000/d-solo/${this.DASHBOARD_ID}/${this.SLUG_LABEL}` +
            `?orgId=1&panelId=${this.PANEL_ID}&var-span=${encodeURIComponent(span)}&refresh=${this.REFRESH_TIME}&theme=${this.THEME}`;
    }

}
