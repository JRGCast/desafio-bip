import { describe, it, expect, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ExtratoComponent } from './extrato.component';
import { BeneficioStateService } from '../../services/beneficio-state.service';
import { ITransacao } from '../../services/api.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('ExtratoComponent', () => {
  let component: ExtratoComponent;
  let fixture: ComponentFixture<ExtratoComponent>;
  let mockState: BeneficioStateService;

  const emptyTransacoes: ITransacao[] = [];

  beforeEach(async () => {
    mockState = new BeneficioStateService();
    mockState.setTransacoes(emptyTransacoes);

    await TestBed.configureTestingModule({
      imports: [ExtratoComponent, HttpClientTestingModule],
      providers: [
        { provide: BeneficioStateService, useValue: mockState }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ExtratoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('empty transacoes', () => {
    it('should show empty message when no transacoes', () => {
      const compiled = fixture.nativeElement as HTMLElement;
      
      expect(compiled.querySelector('.empty')).toBeTruthy();
      expect(compiled.querySelector('.empty')?.textContent).toContain('Nenhuma transação encontrada');
    });
  });
});