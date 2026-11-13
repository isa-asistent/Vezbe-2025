import { ComponentFixture, TestBed } from "@angular/core/testing";
import { TemperatureGraphComponent } from "./temperature-graph.component";

describe("TemperatureGraphComponent", () => {
  let component: TemperatureGraphComponent;
  let fixture: ComponentFixture<TemperatureGraphComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TemperatureGraphComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TemperatureGraphComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
