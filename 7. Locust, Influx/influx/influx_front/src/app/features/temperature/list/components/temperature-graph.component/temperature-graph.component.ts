import { Component, computed, input } from "@angular/core";
import { Temperature } from "../../../../../core/models/temperature.model";
import { DatePipe } from "@angular/common";

@Component({
  imports: [DatePipe],
  selector: "app-temperature-graph",
  styleUrl: "./temperature-graph.component.css",
  templateUrl: "./temperature-graph.component.html",
})
export class TemperatureGraphComponent { readings = input.required<Temperature.ForGet[]>();

  readonly width = 600;
  readonly height = 300;
  private readonly pad = { top: 16, right: 16, bottom: 32, left: 40 };

  readonly chart = computed(() => {
    // Oldest -> newest, left to right
    const data = [...this.readings()].sort(
      (a, b) => new Date(a.timeStamp).getTime() - new Date(b.timeStamp).getTime()
    );
    if (!data.length) return null;

    const { width, height, pad } = this;
    const innerW = width - pad.left - pad.right;
    const innerH = height - pad.top - pad.bottom;

    const times = data.map((d) => new Date(d.timeStamp).getTime());
    const values = data.map((d) => d.value);
    const tMin = Math.min(...times);
    const tMax = Math.max(...times);
    const vMin = Math.floor(Math.min(...values) - 1);
    const vMax = Math.ceil(Math.max(...values) + 1);

    const x = (t: number) =>
      tMax === tMin ? pad.left + innerW / 2 : pad.left + ((t - tMin) / (tMax - tMin)) * innerW;
    const y = (v: number) => pad.top + innerH - ((v - vMin) / (vMax - vMin)) * innerH;

    const points = data.map((d, i) => ({
      x: x(times[i]),
      y: y(d.value),
      value: d.value,
      timeStamp: d.timeStamp,
    }));

    const ticks = Array.from({ length: 5 }, (_, i) => {
      const v = vMin + ((vMax - vMin) * i) / 4;
      return { y: y(v), label: v.toFixed(0) };
    });

    return {
      points,
      ticks,
      path: points.map((p, i) => `${i ? 'L' : 'M'}${p.x},${p.y}`).join(' '),
      start: data[0].timeStamp,
      end: data[data.length - 1].timeStamp,
      left: pad.left,
      right: width - pad.right,
      bottom: height - 8,
    };
  });
}