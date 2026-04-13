import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment as env} from '../environments/environment'
import { IBeneficio, ITransacao, ITransferenciaRequest } from '../models/beneficio.model';

// Re-exported so existing imports like:
//   import { IBeneficio } from '../../services/api.service'
// continue to compile without changes.
export type { IBeneficio, ITransacao, ITransferenciaRequest };

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private http = inject(HttpClient);
  private BASE_URL = env.baseUrl;

  /** Retorna todos os benefícios ativos. */
  getBeneficios(): Observable<IBeneficio[]> {
    return this.http.get<IBeneficio[]>(`${this.BASE_URL}/beneficios`);
  }

  /** Retorna o extrato global de todas as transferências. */
  getTransacoes(): Observable<ITransacao[]> {
    return this.http.get<ITransacao[]>(`${this.BASE_URL}/transacoes`);
  }

  /** Executa uma transferência de valor entre dois benefícios. */
  transferir(request: ITransferenciaRequest): Observable<ITransacao> {
    return this.http.post<ITransacao>(`${this.BASE_URL}/beneficios/transferencia`, request);
  }
}