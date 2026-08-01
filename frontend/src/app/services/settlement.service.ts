import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Balance, Settlement } from '../models/settlement.model';
import { API_BASE_URL } from './api-config';

@Injectable({ providedIn: 'root' })
export class SettlementService {

  constructor(private http: HttpClient) {}

  getBalances(groupId: number): Observable<Balance[]> {
    return this.http.get<Balance[]>(`${API_BASE_URL}/groups/${groupId}/balances`);
  }

  getSettlements(groupId: number): Observable<Settlement[]> {
    return this.http.get<Settlement[]>(`${API_BASE_URL}/groups/${groupId}/settle`);
  }
}
