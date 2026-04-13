import { Component, inject } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { BeneficioStateService } from '../../services/beneficio-state.service';

@Component({
  selector: 'app-extrato',
  standalone: true,
  imports: [DatePipe, DecimalPipe],
  templateUrl: './extrato.component.html',
  styleUrl: './extrato.component.css'
})
export class ExtratoComponent {
  private state = inject(BeneficioStateService);

  transacoes = this.state.transacoes;
}