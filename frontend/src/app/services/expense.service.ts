import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Expense, ExpenseRequest } from '../models/expense.model';
import { API_BASE_URL } from './api-config';

@Injectable({ providedIn: 'root' })
export class ExpenseService {

  constructor(private http: HttpClient) {}

  getForGroup(groupId: number): Observable<Expense[]> {
    return this.http.get<Expense[]>(`${API_BASE_URL}/groups/${groupId}/expenses`);
  }

  add(groupId: number, request: ExpenseRequest): Observable<Expense> {
    return this.http.post<Expense>(`${API_BASE_URL}/groups/${groupId}/expenses`, request);
  }
}
