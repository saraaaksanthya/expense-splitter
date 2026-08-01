import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Person } from '../models/person.model';
import { API_BASE_URL } from './api-config';

@Injectable({ providedIn: 'root' })
export class PersonService {

  constructor(private http: HttpClient) {}

  getAll(): Observable<Person[]> {
    return this.http.get<Person[]>(`${API_BASE_URL}/people`);
  }

  create(person: Person): Observable<Person> {
    return this.http.post<Person>(`${API_BASE_URL}/people`, person);
  }
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE_URL}/people/${id}`);
  }
}
