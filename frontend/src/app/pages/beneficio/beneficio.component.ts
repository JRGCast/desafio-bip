import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { BeneficioStateService } from '../../services/beneficio-state.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-beneficio',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="container">
      <header class="header">
        <h1>Gestão de Benefícios</h1>
      </header>
      <nav class="nav">
        <a routerLink="gerenciar" routerLinkActive="active">Gerenciar</a>
        <a routerLink="extrato" routerLinkActive="active">Extrato</a>
      </nav>
      <main class="content">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [`
    .container {
      min-height: 100vh;
      background: #f5f5f5;
    }
    .header {
      background: #fff;
      padding: 16px 24px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    }
    .header h1 {
      margin: 0;
      font-size: 20px;
      color: #333;
      text-align: center;
    }
    .nav {
      display: flex;
      background: #fff;
      border-bottom: 1px solid #e0e0e0;
    }
    .nav a {
      flex: 1;
      padding: 14px 24px;
      font-size: 15px;
      font-weight: 500;
      color: #666;
      text-decoration: none;
      text-align: center;
      border-bottom: 2px solid transparent;
      transition: all 0.2s;
    }
    .nav a:hover {
      color: #333;
      background: #fafafa;
    }
    .nav a.active {
      color: #4caf50;
      border-bottom-color: #4caf50;
    }
    .content {
      padding: 24px 16px;
    }
  `]
})
export class BeneficioComponent implements OnInit {
  private apiService = inject(ApiService);
  private state = inject(BeneficioStateService);
  protected fetchedData = signal(false)

  ngOnInit(): void {
    this.fetchNSetBoth()
  }

  private fetchNSetBoth(): void {
    forkJoin({
      beneficiosFetch$: this.apiService.getBeneficios(),
      transacoesFetch$: this.apiService.getTransacoes()
    }).subscribe(({ beneficiosFetch$, transacoesFetch$ }) => {
      this.state.setBeneficios(beneficiosFetch$)
      this.state.setTransacoes(transacoesFetch$)
      this.fetchedData.set(true)
    })
  }
}