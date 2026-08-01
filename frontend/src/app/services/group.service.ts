import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Group } from '../models/group.model';
import { API_BASE_URL } from './api-config';

@Injectable({ providedIn: 'root' })
export class GroupService {

  constructor(private http: HttpClient) {}

  getAll(): Observable<Group[]> {
    return this.http.get<Group[]>(`${API_BASE_URL}/groups`);
  }

  getById(id: number): Observable<Group> {
    return this.http.get<Group>(`${API_BASE_URL}/groups/${id}`);
  }

  create(name: string, memberIds: number[]): Observable<Group> {
    return this.http.post<Group>(`${API_BASE_URL}/groups`, { name, memberIds });
  }
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE_URL}/groups/${id}`);
  }
}
