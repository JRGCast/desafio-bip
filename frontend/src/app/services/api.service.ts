import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface IBeneficio {
  id: number;
  nome: string;
  descricao: string | null;
  valor: number;
  ativo: boolean;
  version: number;
}

export interface ITransacao {
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

export interface ITransferenciaRequest {
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

  getBeneficios(): Observable<IBeneficio[]> {
    return this.http.get<IBeneficio[]>(`${this.baseUrl}/beneficios`);
  }

  getTransacoes(): Observable<ITransacao[]> {
    return this.http.get<ITransacao[]>(`${this.baseUrl}/transacoes`);
  }

  transferir(request: ITransferenciaRequest): Observable<ITransacao> {
    return this.http.post<ITransacao>(`${this.baseUrl}/beneficios/transferencia`, request);
  }
}