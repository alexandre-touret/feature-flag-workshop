import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Instrument} from '../models/instrument.model';
//TODO: Uncomment
//import { OpenFeature } from '@openfeature/web-sdk';

@Injectable({
  providedIn: 'root'
})
export class InstrumentService {
  private http = inject(HttpClient);
  private apiUrl = '/api/instruments';

  getInstruments(): Observable<Instrument[]> {
    return this.http.get<Instrument[]>(this.apiUrl);
  }

  getInstrument(id: number): Observable<Instrument> {
    return this.http.get<Instrument>(`${this.apiUrl}/${id}`);
  }

  createInstrument(instrument: Instrument): Observable<{instrumentId: string}> {
    return this.http.post<{instrumentId: string}>(this.apiUrl, instrument);
  }

  updateInstrument(id: number, instrument: Instrument): Observable<Instrument> {
    return this.http.put<Instrument>(`${this.apiUrl}/${id}`, instrument);
  }

  deleteInstrument(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  search(query: string): Observable<Instrument[]> {
    return this.http.get<Instrument[]>(`${this.apiUrl}/search`, { params: { q: query } });
  }
}
