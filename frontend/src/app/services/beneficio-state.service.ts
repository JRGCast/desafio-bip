import { Injectable, Signal, signal } from '@angular/core';
import { Beneficio, Transacao } from './api.service';

@Injectable({
  providedIn: 'root'
})
export class BeneficioStateService {
  private readonly _beneficios = signal<Beneficio[]>([]);
  private readonly _transacoes = signal<Transacao[]>([]);

  get beneficios(): Signal<Beneficio[]> {
    return this._beneficios.asReadonly()
  }

  setBeneficios(newBeneficios: Beneficio[]): void {
    this._beneficios.set(newBeneficios)
  }

  get transacoes(): Signal<Transacao[]> {
    return this._transacoes.asReadonly()
  }

  setTransacoes(newTransacoes: Transacao[]): void {
    this._transacoes.set(newTransacoes)
  }
}