import { Component, signal } from '@angular/core';
import { WhatNotToDoComponent } from './components/what-not-to-do-component/what-not-to-do-component';
import { WhatToDoComponent } from './components/what-to-do-component/what-to-do-component';

@Component({
  selector: 'app-file-upload',
  imports: [WhatNotToDoComponent, WhatToDoComponent],
  templateUrl:'./file-upload.component.html'
})
export class FileUploadComponent {
  readonly active = signal<'dont' | 'do'>('dont');
}