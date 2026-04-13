import { Component, inject, computed, signal, DestroyRef } from '@angular/core';
import { DecimalPipe, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { switchMap, forkJoin, catchError, retry, EMPTY, timer } from 'rxjs';
import { ApiService, IBeneficio, ITransferenciaRequest } from '../../services/api.service';
import { BeneficioStateService } from '../../services/beneficio-state.service';

/**
 * Componente de redistribuição de valores entre dois benefícios via slider.
 *
 * Reage ao estado global (Signals) e executa transferências via API.
 * Após cada operação bem-sucedida, recarrega o estado global.
 */
@Component({
  selector: 'app-beneficio-slider',
  standalone: true,
  imports: [FormsModule, DecimalPipe, CurrencyPipe],
  templateUrl: './beneficio-slider.component.html',
  styleUrl: './beneficio-slider.component.css'
})
export class BeneficioSliderComponent {
  private apiService = inject(ApiService);
  private state = inject(BeneficioStateService);
  private destroyRef = inject(DestroyRef);

  // Benefícios atuais sincronizados com o estado global
  beneficioA = signal<IBeneficio | null>(null);
  beneficioB = signal<IBeneficio | null>(null);

  /** Soma dos valores dos dois benefícios — base para redistribuição. */
  total = computed(() =>
    Number(this.beneficioA()?.valor || 0) + Number(this.beneficioB()?.valor || 0)
  );

  /** Percentual do slider controlado pelo usuário (0–100). */
  sliderPercentage = signal(50);

  /** Valor calculado do benefício A de acordo com a posição do slider. */
  valorA = computed(() => Math.round(this.total() * (this.sliderPercentage() / 100)));

  /** Valor calculado do benefício B (complemento ao total). */
  valorB = computed(() => this.total() - this.valorA());

  /** Indica se há uma operação de API em andamento. */
  loading = signal(false);

  /** Torna-se `true` após o estado inicial ser carregado do serviço global. */
  componentReady = signal(false);

  /** Mensagem de erro da última operação falha. Vazia quando não há erro. */
  error = signal('');

  /** Mensagem de sucesso temporária. Limpa automaticamente após 3 segundos. */
  successMessage = signal('');

  constructor() {
    // Reage a mudanças no estado global de benefícios
    toObservable(this.state.beneficios).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe((beneficios: IBeneficio[]) => {
      if (beneficios && beneficios.length > 0) {
        this.beneficioA.set(beneficios[0] ?? null);
        this.beneficioB.set(beneficios[1] ?? null);
        this.syncSliderWithState();
        this.componentReady.set(true);
      }
    });
  }

  /** Sincroniza o percentual do slider com os valores atuais dos benefícios. */
  private syncSliderWithState(): void {
    const a = this.beneficioA();
    const t = this.total();
    if (a && t > 0) {
      this.sliderPercentage.set(Math.round((Number(a.valor) / t) * 100));
    }
  }

  /**
   * Monta o request de transferência com base na diferença entre
   * o valor atual e o valor calculado pelo slider.
   * Retorna `null` se não há diferença (nenhuma ação necessária).
   */
  private buildTransferenciaRequest(): ITransferenciaRequest | null {
    const a = this.beneficioA();
    const b = this.beneficioB();
    if (!a || !b) return null;

    const currentA = Number(a.valor);
    const newA = this.valorA();

    if (newA === currentA) return null;

    return newA < currentA
      ? { fromId: a.id, toId: b.id, amount: currentA - newA }
      : { fromId: b.id, toId: a.id, amount: newA - currentA };
  }

  /** Recarrega benefícios e transações no estado global via API. */
  private reloadState() {
    return forkJoin({
      beneficios: this.apiService.getBeneficios().pipe(retry({ count: 3, delay: 1000 })),
      transacoes: this.apiService.getTransacoes().pipe(retry({ count: 3, delay: 1000 }))
    });
  }

  /** Exibe uma mensagem temporária de sucesso por 3 segundos. */
  private showSuccess(message: string): void {
    this.successMessage.set(message);
    timer(3000).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => this.successMessage.set(''));
  }

  onSliderChange(value: number): void {
    this.sliderPercentage.set(value);
  }

  /**
   * Executa a transferência calculada pelo slider.
   * Recarrega o estado global após sucesso.
   */
  alterar(): void {
    const request = this.buildTransferenciaRequest();
    if (!request) return;

    this.loading.set(true);
    this.error.set('');
    this.successMessage.set('');

    this.apiService.transferir(request).pipe(
      switchMap(() => this.reloadState()),
      catchError(err => {
        this.loading.set(false);
        this.error.set('Erro ao realizar transferência. Tente novamente.');
        console.error(err);
        return EMPTY;
      })
    ).subscribe({
      next: ({ beneficios, transacoes }) => {
        this.loading.set(false);
        this.state.setBeneficios(beneficios);
        this.state.setTransacoes(transacoes);
        this.showSuccess('Transferência realizada com sucesso!');
      }
    });
  }

  /**
   * Força o recarregamento dos dados do backend.
   * Útil após falha ou para verificar atualizações.
   */
  refreshData(): void {
    this.loading.set(true);
    this.error.set('');
    this.successMessage.set('');

    this.reloadState().subscribe({
      next: ({ beneficios, transacoes }) => {
        this.loading.set(false);
        this.state.setBeneficios(beneficios);
        this.state.setTransacoes(transacoes);
        this.showSuccess('Dados atualizados com sucesso!');
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Erro ao atualizar dados. Tente novamente.');
      }
    });
  }
}