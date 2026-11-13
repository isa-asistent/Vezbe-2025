import { HttpClient } from '@angular/common/http';
import { Component, DestroyRef, inject, signal } from '@angular/core';
import { map, Subscription, switchMap } from 'rxjs';
import { ImageService } from '../../../../../core/services/image.service';
import { Image } from '../../../../../core/models/image.model';




@Component({
  selector: 'app-what-to-do',
  templateUrl: './what-to-do-component.html',
  standalone: true,
})
export class WhatToDoComponent {
  private readonly http = inject(HttpClient);
  private readonly service = inject(ImageService);
  private uploadSub?: Subscription;
  private downloadSub?: Subscription;

  readonly dragging = signal(false);
  readonly uploading = signal(false);
  readonly downloading = signal(false);
  readonly error = signal('');
  readonly uploaded = signal<WhatToDoComponent.Uploaded | null>(null);
  readonly result = signal<Image.URL.ForGet | null>(null);

  constructor() {
    inject(DestroyRef).onDestroy(() => {
      this.uploadSub?.unsubscribe();
      this.downloadSub?.unsubscribe();
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.handleFile(input.files?.[0]);
    input.value = ''; // dozvoli da se odabere isti file.
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

  // Prilikom skidanje slike, umesto da se preuzme ceo file, zahtevaj GET potpisani URL.
  download(): void {
    const up = this.uploaded();
    if (!up) return;

    this.downloadSub?.unsubscribe();
    this.error.set('');
    this.downloading.set(true);

    this.downloadSub = this.service.getGetUrl(up.key).subscribe({
      next: (res) => {
        this.result.set({ key: res.key, url: res.url });
        this.downloading.set(false);
      },
      error: () => {
        this.downloading.set(false);
        this.error.set('Download failed. Try again.');
      },
    });
  }

  clear(): void {
    this.uploadSub?.unsubscribe();
    this.downloadSub?.unsubscribe();
    this.uploaded.set(null);
    this.result.set(null);
    this.uploading.set(false);
    this.downloading.set(false);
    this.error.set('');
  }

  onImageError(): void {
    this.error.set('The image could not be loaded.');
  }

  private handleFile(file?: File | null): void {
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      this.error.set('Only image files are supported.');
      return;
    }

    this.error.set('');
    this.send(file);
  }

  private send(file: File): void {
    // Novi upload obustavlja sve prethodne subskripcije.
    this.uploadSub?.unsubscribe();
    this.downloadSub?.unsubscribe();
    this.downloading.set(false);
    this.uploading.set(true);

    this.uploadSub = this.service
      .getPutUrl() // Zahtevaj od servera PUT URL kako bi sproveo upload.
      .pipe(
        // Putem operator switchMap preuzmi odgovornost za upload file-a direktno u skladiste.
        switchMap((put) =>
          this.http
            .put(put.url, file, { headers: { 'Content-Type': file.type } })
            .pipe(map(() => put.key)),
        ),
      )
      .subscribe({
        next: (key) => {
          // Odradi cleanup nakon upload-a.
          this.result.set(null);
          this.uploaded.set({ key, name: file.name });
          this.uploading.set(false);
        },
        error: () => {
          // Sacuvaj sta god da je prethodno uspesno upload-ovano.
          this.uploading.set(false);
          this.error.set('Upload failed. Try again.');
        },
      });
  }
}

// Namespace komponente kako bi se odrzala enkapuslacija i preciznost
namespace WhatToDoComponent {
  // izloze pomocni interfejs koji se koristi u logici komponente.
  export interface Uploaded {
    key: string;
    name: string;
  }
}