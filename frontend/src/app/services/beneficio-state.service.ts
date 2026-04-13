import { Injectable, Signal, signal } from '@angular/core';
import { IBeneficio, ITransacao } from './api.service';

@Injectable({
  providedIn: 'root'
})
export class BeneficioStateService {
  private readonly _beneficios = signal<IBeneficio[]>([]);
  private readonly _transacoes = signal<ITransacao[]>([]);

  get beneficios(): Signal<IBeneficio[]> {
    return this._beneficios.asReadonly()
  }

  setBeneficios(newBeneficios: IBeneficio[]): void {
    this._beneficios.set(newBeneficios)
  }

  get transacoes(): Signal<ITransacao[]> {
    return this._transacoes.asReadonly()
  }

  setTransacoes(newTransacoes: ITransacao[]): void {
    this._transacoes.set(newTransacoes)
  }
}