import { Component, signal } from '@angular/core';
import { FileUploadComponent } from './features/file/upload/file-upload.component';

@Component({
  selector: 'app-root',
  imports: [FileUploadComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('blob_storage_front');
}
