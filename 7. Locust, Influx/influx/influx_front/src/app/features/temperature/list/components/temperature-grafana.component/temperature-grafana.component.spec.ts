import { ComponentFixture, TestBed } from "@angular/core/testing";
import { TemperatureGrafanaComponent } from "./temperature-grafana.component";

describe("TemperatureGrafanaComponent", () => {
  let component: TemperatureGrafanaComponent;
  let fixture: ComponentFixture<TemperatureGrafanaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TemperatureGrafanaComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TemperatureGrafanaComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
