import { HttpClient, HttpResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable, tap } from 'rxjs';
import { Image } from '../models/image.model';

@Injectable({
  providedIn: 'root',
})
export class ImageService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/images';
  private readonly whatToDoAPI = `${this.apiUrl}/whatToDo`
  private readonly whatNotToDoAPI = `${this.apiUrl}/whatNotToDo`

  /**
   * Metoda vrsi skidanje slike kroz server.
   * @param keyName Naziv kljuca u skladistu.
   * @returns Observablue tipa {@link Image.Raw.ForGet} koja sadrzi sliku kao {@link Blob}.
   */
  public download(keyName: string): Observable<Image.Raw.ForGet> {
    return this.http.get(`${this.whatNotToDoAPI}/${keyName}`, {
      responseType: 'blob',
    })
      .pipe(
        map((response) => ({
          file: response,
          key: keyName
        }))
      )

  }
  /**
   * Metoda vrsi upload slike kao Blob-a.
   * @param request Tip zahteva koji sadrzi sliku kao {@link Blob} vrednost.
   * @returns 
   */
  public upload(request: Image.Raw.ForCreate): Observable<string> {
    const formData = new FormData();
    formData.append('file', request.file);

    return this.http
      .post<void>(this.whatNotToDoAPI, formData, { observe: 'response' })
      .pipe(
        map((res) => {
          const location = res.headers.get('Location');
          if (!location) throw new Error('Missing Location header');
          return location;
        }),
      );
  }
  /**
   * Metoda vraca get url, gde ce se pomenuti url iskoristi za dobavljanje slike.
   * @param key Naziv kljuca u bucketu
   * @returns  Observablue tipa {@link Image.URL.ForGet} koja sadrzi url za dobavljanje slike.
   */
  public getGetUrl(key: string): Observable<Image.URL.ForGet> {
    return this.http.get(`${this.whatToDoAPI}/url/${key}`, {
      responseType: 'text',
    }).pipe(map((url) => (
      {
        key,
        url
      }
    ))
    )
  }

  /**
   * Metoda vraca put url, gde ce klijentski kod preuzeti odredjenu kolicinu odgovornosti za upload slike.
   * @returns Observablu tipa {@link Image.URL.ForPut} koja sadrzi put url
   */
  public getPutUrl(): Observable<Image.URL.ForPut> {
    return this.http.put<Image.URL.ForPut>(`${this.whatToDoAPI}/url`, {})
  }
}
