import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Beneficio {
  id: number;
  nome: string;
  descricao: string | null;
  valor: number;
  ativo: boolean;
  version: number;
}

export interface Transacao {
  id: number;
  fromId: number;
  toId: number;
  fromNome: string;
  toNome: string;
  amount: number;
  fromValorAnterior: number;
  toValorAnterior: number;
  criadoEm: string;
  status: string;
}

export interface TransferenciaRequest {
  fromId: number;
  toId: number;
  amount: number;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/v1';

  getBeneficios(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(`${this.baseUrl}/beneficios`);
  }

  getTransacoes(): Observable<Transacao[]> {
    return this.http.get<Transacao[]>(`${this.baseUrl}/transacoes`);
  }

  transferir(request: TransferenciaRequest): Observable<Transacao> {
    return this.http.post<Transacao>(`${this.baseUrl}/beneficios/transferencia`, request);
  }
}