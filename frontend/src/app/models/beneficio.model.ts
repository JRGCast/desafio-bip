/**
 * Representa um benefício empresarial cadastrado no sistema.
 */
export interface IBeneficio {
  id: number;
  nome: string;
  descricao: string | null;
  valor: number;
  ativo: boolean;
  version: number;
}

/**
 * Representa uma transação de transferência entre dois benefícios.
 */
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

/**
 * Payload para solicitar uma transferência entre dois benefícios.
 */
export interface ITransferenciaRequest {
  fromId: number;
  toId: number;
  amount: number;
}
