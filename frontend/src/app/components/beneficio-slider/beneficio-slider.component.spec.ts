import { describe, it, expect, vi, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { BeneficioSliderComponent } from './beneficio-slider.component';
import { ApiService } from '../../services/api.service';
import { BeneficioStateService } from '../../services/beneficio-state.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { IBeneficio, ITransacao } from '../../services/api.service';

describe('BeneficioSliderComponent', () => {
  let component: BeneficioSliderComponent;
  let fixture: ComponentFixture<BeneficioSliderComponent>;
  let mockApiService: ApiService;
  let stateService: BeneficioStateService;

  const mockBeneficioA: IBeneficio = {
    id: 1,
    nome: 'Beneficio A',
    descricao: 'Desc A',
    valor: 1000,
    ativo: true,
    version: 0
  };

  const mockBeneficioB: IBeneficio = {
    id: 2,
    nome: 'Beneficio B',
    descricao: 'Desc B',
    valor: 500,
    ativo: true,
    version: 0
  };

  const mockTransacao: ITransacao = {
    id: 1,
    fromId: 1,
    toId: 2,
    fromNome: 'Beneficio A',
    toNome: 'Beneficio B',
    amount: 250,
    fromValorAnterior: 1000,
    toValorAnterior: 500,
    criadoEm: '2024-01-01T00:00:00Z',
    status: 'SUCESSO'
  };

  beforeEach(async () => {
    stateService = new BeneficioStateService();
    stateService.setBeneficios([mockBeneficioA, mockBeneficioB]);

    await TestBed.configureTestingModule({
      imports: [BeneficioSliderComponent, HttpClientTestingModule],
      providers: [
        { provide: BeneficioStateService, useValue: stateService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(BeneficioSliderComponent);
    component = fixture.componentInstance;
    mockApiService = TestBed.inject(ApiService);
    fixture.detectChanges();
  });

  describe('alterar() - transfer benefits', () => {
    it('should show success message after successful transfer', async () => {
      vi.spyOn(mockApiService, 'transferir').mockReturnValue(of(mockTransacao));
      vi.spyOn(mockApiService, 'getBeneficios').mockReturnValue(of([mockBeneficioA, mockBeneficioB]));
      vi.spyOn(mockApiService, 'getTransacoes').mockReturnValue(of([mockTransacao]));

      component.sliderPercentage.set(25);
      component.alterar();
      fixture.detectChanges();

      expect(component.successMessage()).toBe('Transferência realizada com sucesso!');
    });

    it('should show error message after failed transfer', async () => {
      vi.spyOn(mockApiService, 'transferir').mockReturnValue(throwError(() => new Error('Transfer failed')));

      component.sliderPercentage.set(25);
      component.alterar();
      fixture.detectChanges();

      expect(component.error()).toBe('Erro ao realizar transferência. Tente novamente.');
    });
  });

  describe('refreshData() - refresh data', () => {
    it('should show success message after successful refresh', async () => {
      vi.spyOn(mockApiService, 'getBeneficios').mockReturnValue(of([mockBeneficioA, mockBeneficioB]));
      vi.spyOn(mockApiService, 'getTransacoes').mockReturnValue(of([mockTransacao]));

      component.refreshData();
      fixture.detectChanges();

      expect(component.successMessage()).toBe('Dados atualizados com sucesso!');
    });
  });
});