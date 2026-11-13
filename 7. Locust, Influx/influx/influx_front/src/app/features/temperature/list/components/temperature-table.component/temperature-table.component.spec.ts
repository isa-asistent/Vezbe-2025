import { ComponentFixture, TestBed } from "@angular/core/testing";
import { TemperatureTableComponent } from "./temperature-table.component";

describe("TemperatureTableComponent", () => {
  let component: TemperatureTableComponent;
  let fixture: ComponentFixture<TemperatureTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TemperatureTableComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TemperatureTableComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
