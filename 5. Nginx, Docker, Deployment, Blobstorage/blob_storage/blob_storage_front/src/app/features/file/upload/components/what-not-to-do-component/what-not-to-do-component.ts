import { Component, DestroyRef, inject, signal } from '@angular/core';
import { Subscription } from 'rxjs';
import { ImageService } from '../../../../../core/services/image.service';

@Component({
  standalone: true,
  selector: 'app-what-not-to-do',
  templateUrl: './what-not-to-do-component.html',
})
export class WhatNotToDoComponent {
  private readonly service = inject(ImageService);
  private uploadSub?: Subscription;
  private downloadSub?: Subscription;

  readonly dragging = signal(false);
  readonly status = signal<WhatNotToDoComponent.FileStatues
  >('idle');
  readonly error = signal('');
  readonly fileName = signal('');
  readonly imageUrl = signal<string | null>(null);

  constructor() {
    inject(DestroyRef).onDestroy(() => {
      this.uploadSub?.unsubscribe();
      this.downloadSub?.unsubscribe();
      this.revokeImageUrl();
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.handleFile(input.files?.[0]);
    input.value = ''; // dozvoli ponovnu selekciju istog file-a
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.dragging.set(true);
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.dragging.set(false);
    this.handleFile(event.dataTransfer?.files?.[0]);
  }

  /** Preuzima specifiranu sliku direktno putem servera i prezentuje je u komponenti. */
  download(): void {
    const key = this.fileName();
    if (!key) return;

    // Prekini inflight subskripciju
    this.downloadSub?.unsubscribe();
    this.error.set('');
    this.status.set('downloading');


    // skini fajl putem servera
    this.downloadSub = this.service.download(key).subscribe({
      next: (res) => {
        this.revokeImageUrl();
        this.imageUrl.set(URL.createObjectURL(res.file));
        this.status.set('done');
      },
      error: () => {
        this.status.set('error');
        this.error.set('Download failed. Try again.');
      },
    });
  }

  private handleFile(file?: File | null): void {
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      this.error.set('Only image files are supported.');
      return;
    }

    this.error.set('');
    this.upload(file);
  }

  private upload(file: File): void {
    // Upload novog fajla prekida sve prethodne subskripcije
    this.uploadSub?.unsubscribe();
    this.downloadSub?.unsubscribe();
    // Obrisi sliku iz prikaza
    this.revokeImageUrl();
    this.fileName.set('');
    this.status.set('uploading');

    // Otpocni upload
    this.uploadSub = this.service.upload({ file }).subscribe({
      next: (key:string) => {
        
        this.fileName.set(key);
        this.status.set('uploaded');
      },
      error: () => {
        this.status.set('error');
        this.error.set('Upload failed. Try again.');
      },
    });
  }

  private revokeImageUrl(): void {
    const url = this.imageUrl();
    if (url) URL.revokeObjectURL(url);
    this.imageUrl.set(null);
  }
}
// Namespace komponente kako bi se odrzala enkapuslacija i preciznost
namespace WhatNotToDoComponent{
  //izlaze status file-a koji se koristi u logici komponente.
  export type FileStatues = 
    'idle' | 'uploading' | 'uploaded' | 'downloading' | 'done' | 'error'
}