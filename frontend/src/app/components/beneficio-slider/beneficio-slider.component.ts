import { Component, inject, computed, signal, OnInit, DestroyRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { switchMap, forkJoin, catchError, retry, EMPTY } from 'rxjs';
import { ApiService, Beneficio } from '../../services/api.service';
import { BeneficioStateService } from '../../services/beneficio-state.service';

@Component({
  selector: 'app-beneficio-slider',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './beneficio-slider.component.html',
  styleUrl: './beneficio-slider.component.css'
})
export class BeneficioSliderComponent implements OnInit {
  private apiService = inject(ApiService);
  private state = inject(BeneficioStateService);
  private destroyRef = inject(DestroyRef);

  // Simple signals - managed via subscription
  beneficioA = signal<Beneficio | null>(null);
  beneficioB = signal<Beneficio | null>(null);

  // Total computed from local signals
  total = computed(() => 
    Number(this.beneficioA()?.valor || 0) + Number(this.beneficioB()?.valor || 0)
  );

  // User input
  sliderPercentage = signal(50);

  // Display values computed from total + user input
  valorA = computed(() => Math.round(this.total() * (this.sliderPercentage() / 100)));
  valorB = computed(() => this.total() - this.valorA());

  // Only true during API calls
  loading = signal(false);
  // Shows loading state before state is loaded
  componentReady = signal(false);
  error = signal('');
  refreshFailed = signal(false);
  successMessage = signal('');

  constructor() {
    toObservable(this.state.beneficios as any).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe((beneficios: any) => {
      if (beneficios && beneficios.length > 0) {
        this.beneficioA.set(beneficios[0] || null);
        this.beneficioB.set(beneficios[1] || null);
        this.syncSliderWithState();
        this.componentReady.set(true);
      }
    });
  }

  ngOnInit(): void {
    // Initialization handled in constructor
  }

  private syncSliderWithState(): void {
    const a = this.beneficioA();
    const t = this.total();
    if (a && t > 0) {
      this.sliderPercentage.set(Math.round((Number(a.valor) / t) * 100));
    }
  }

  onSliderChange(updatedValue: number): void {
    this.sliderPercentage.set(updatedValue);
  }

  alterar(): void {
    const a = this.beneficioA();
    const b = this.beneficioB();
    if (!a || !b) return;

    const currentA = Number(a.valor);
    const newA = this.valorA();
    const diff = Math.abs(newA - currentA);

    if (diff === 0) {
      return;
    }

    let fromId: number;
    let toId: number;
    let amount: number;

    if (newA < currentA) {
      fromId = a.id;
      toId = b.id;
      amount = currentA - newA;
    } else {
      fromId = b.id;
      toId = a.id;
      amount = newA - currentA;
    }

    this.refreshFailed.set(false);
    this.loading.set(true);
    this.error.set('');
    this.successMessage.set('');

    this.apiService.transferir({ fromId, toId, amount }).pipe(
      switchMap(() => forkJoin({
        beneficios: this.apiService.getBeneficios().pipe(
          retry({ count: 3, delay: 1000 })
        ),
        transacoes: this.apiService.getTransacoes().pipe(
          retry({ count: 3, delay: 1000 })
        )
      })),
      catchError(err => {
        this.loading.set(false);
        this.error.set('Erro ao realizar transferência. Tente novamente.');
        console.error(err);
        return EMPTY;
      })
    ).subscribe({
      next: ({ beneficios, transacoes }) => {
        this.loading.set(false);
        this.refreshFailed.set(false);
        this.state.setBeneficios(beneficios);
        this.state.setTransacoes(transacoes);
        
        this.successMessage.set('Transferência realizada com sucesso!');
        setTimeout(() => this.successMessage.set(''), 3000);
      },
      error: () => {
        this.refreshFailed.set(true);
        this.loading.set(false);
      }
    });
  }

  refreshData(): void {
    this.refreshFailed.set(false);
    this.loading.set(true);
    this.error.set('');
    this.successMessage.set('');

    forkJoin({
      beneficios: this.apiService.getBeneficios().pipe(
        retry({ count: 3, delay: 1000 })
      ),
      transacoes: this.apiService.getTransacoes().pipe(
        retry({ count: 3, delay: 1000 })
      )
    }).subscribe({
      next: ({ beneficios, transacoes }) => {
        this.loading.set(false);
        this.refreshFailed.set(false);
        this.state.setBeneficios(beneficios);
        this.state.setTransacoes(transacoes);
        
        this.successMessage.set('Dados atualizados com sucesso!');
        setTimeout(() => this.successMessage.set(''), 3000);
      },
      error: () => {
        this.refreshFailed.set(true);
        this.loading.set(false);
      }
    });
  }
}