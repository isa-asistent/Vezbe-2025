import { Component, signal } from '@angular/core';
import { ListComponent } from './features/temperature/list/list.component';

@Component({
  imports: [ ListComponent],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  protected readonly title = signal('influx_front');
}
