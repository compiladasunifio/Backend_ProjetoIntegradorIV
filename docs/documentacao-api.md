# UniFood — Documentação da API

URL base: `http://localhost:8080`
Formato: JSON (`Content-Type: application/json`)
Autenticação: **HTTP Basic** em todas as rotas.

## Usuários de teste

| Usuário | Senha | Perfil | O que pode fazer |
|---|---|---|---|
| `admin` | `admin123` | ADMIN | Tudo, inclusive categorias e exclusão de vendedores e encomendas |
| `vendedor` | `vendedor123` | VENDEDOR | Vendedores (criar/editar), produtos, horários, painel, confirmar e entregar encomendas |
| `cliente` | `cliente123` | CLIENTE | Avaliações, criar/editar encomendas |

Qualquer usuário autenticado pode fazer consultas (`GET`, exceto o painel) e cancelar encomendas.

- **401 Unauthorized**: requisição sem credenciais ou com usuário/senha errados.
- **403 Forbidden**: usuário autenticado, mas o perfil não pode fazer a operação (ex.: `cliente` criando produto).

## Formato dos erros

```json
{
  "dataHora": "2026-11-20T18:30:00.123",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "Existem campos inválidos na requisição",
  "caminho": "/vendedores",
  "campos": {
    "email": "Informe um e-mail válido",
    "telefone": "O telefone deve ter 10 ou 11 dígitos (DDD + número), somente números"
  }
}
```

| Status | Quando acontece |
|---|---|
| 400 | Bean Validation (`@Valid`), JSON mal formatado, parâmetro com tipo errado, horário de fim antes do início, relacionamento sem id |
| 401 | Sem login ou senha incorreta |
| 403 | Perfil sem permissão |
| 404 | Registro não encontrado (`RecursoNaoEncontradoExcecao`) |
| 409 | Regra de negócio violada (`RegraNegocioExcecao`) |

Relacionamentos são enviados só com o id: `"vendedor": {"id": 1}`.
Datas: `aaaa-mm-dd`. Horas: `HH:mm`. Dias: `SEGUNDA`, `TERCA`, `QUARTA`, `QUINTA`, `SEXTA`, `SABADO`.

---

## Categorias — `/categorias`

| Método | Rota | Perfil | Descrição | Sucesso |
|---|---|---|---|---|
| GET | `/categorias` | todos | Lista por nome | 200 |
| GET | `/categorias/{id}` | todos | Busca por id | 200 |
| GET | `/categorias/{id}/produtos` | todos | Produtos da categoria | 200 |
| POST | `/categorias` | ADMIN | Cria | 201 |
| PUT | `/categorias/{id}` | ADMIN | Atualiza | 200 |
| DELETE | `/categorias/{id}` | ADMIN | Exclui | 204 |

```json
{ "nome": "Cafés", "descricao": "Cafés e bebidas quentes" }
```

Regras: nome único (409); não exclui categoria com produtos (409).

## Vendedores — `/vendedores`

| Método | Rota | Perfil | Descrição | Sucesso |
|---|---|---|---|---|
| GET | `/vendedores` | todos | Lista por nome do negócio | 200 |
| GET | `/vendedores/busca?negocio=doces` | todos | Busca pelo nome do negócio | 200 |
| GET | `/vendedores/{id}` | todos | Busca por id | 200 |
| GET | `/vendedores/{id}/produtos` | todos | Produtos do vendedor | 200 |
| GET | `/vendedores/{id}/horarios` | todos | Horários em ordem de dia e hora | 200 |
| GET | `/vendedores/{id}/encomendas` | todos | Agenda de retiradas | 200 |
| GET | `/vendedores/{id}/painel` | VENDEDOR, ADMIN | Indicadores do negócio | 200 |
| POST | `/vendedores` | VENDEDOR, ADMIN | Cria | 201 |
| PUT | `/vendedores/{id}` | VENDEDOR, ADMIN | Atualiza | 200 |
| DELETE | `/vendedores/{id}` | ADMIN | Exclui com produtos, avaliações e horários | 204 |

```json
{
  "nome": "Ana",
  "email": "ana@unifood.com",
  "telefone": "14997776655",
  "nomeNegocio": "Café da Ana",
  "localizacao": "Bloco 3"
}
```

Regras: e-mail único, sem diferenciar maiúsculas (409); não exclui vendedor com encomendas pendentes ou confirmadas (409).

Resposta do painel:

```json
{
  "vendedorId": 1,
  "nomeNegocio": "Doces da Isa",
  "totalProdutos": 2,
  "produtosDisponiveis": 2,
  "notaMedia": 4.5,
  "totalAvaliacoes": 2,
  "horasAtendimentoSemana": 4.0,
  "encomendasPendentes": 1,
  "encomendasConfirmadas": 0,
  "encomendasEntregues": 1,
  "faturamentoEntregue": 24.00
}
```
(valores com os dados iniciais do `import.sql`)

## Produtos — `/produtos`

| Método | Rota | Perfil | Descrição | Sucesso |
|---|---|---|---|---|
| GET | `/produtos` | todos | Lista por nome | 200 |
| GET | `/produtos/disponiveis` | todos | Só os disponíveis (catálogo) | 200 |
| GET | `/produtos/busca?nome=bri` | todos | Busca por parte do nome | 200 |
| GET | `/produtos/faixa-preco?minimo=5&maximo=12` | todos | Disponíveis na faixa de preço (JPQL) | 200 |
| GET | `/produtos/{id}` | todos | Busca por id | 200 |
| GET | `/produtos/{id}/avaliacoes` | todos | Avaliações do produto | 200 |
| POST | `/produtos` | VENDEDOR, ADMIN | Cria | 201 |
| PUT | `/produtos/{id}` | VENDEDOR, ADMIN | Atualiza | 200 |
| PATCH | `/produtos/{id}/disponibilidade?disponivel=false` | VENDEDOR, ADMIN | Liga/desliga a disponibilidade | 200 |
| DELETE | `/produtos/{id}` | VENDEDOR, ADMIN | Exclui com avaliações e encomendas finalizadas | 204 |

```json
{
  "nome": "Cappuccino",
  "descricao": "Copo de 300 ml",
  "preco": 8.50,
  "imagemUrl": "",
  "disponivel": true,
  "localizacao": "Bloco 3",
  "vendedor": { "id": 5 },
  "categoria": { "id": 6 }
}
```

Regras: sem `localizacao`, usa a do vendedor; vendedor e categoria precisam existir (404); não exclui produto com encomendas pendentes ou confirmadas (409).

## Horários de atendimento — `/horarios`

| Método | Rota | Perfil | Descrição | Sucesso |
|---|---|---|---|---|
| GET | `/horarios` | todos | Lista | 200 |
| GET | `/horarios/{id}` | todos | Busca por id | 200 |
| POST | `/horarios` | VENDEDOR, ADMIN | Cria | 201 |
| PUT | `/horarios/{id}` | VENDEDOR, ADMIN | Atualiza (mesmo vendedor) | 200 |
| DELETE | `/horarios/{id}` | VENDEDOR, ADMIN | Exclui | 204 |

```json
{ "dia": "TERCA", "inicio": "07:30", "fim": "09:30", "local": "Bloco 3", "vendedor": { "id": 5 } }
```

Regras: `fim` depois de `inicio` (400); o vendedor não pode ter dois horários sobrepostos no mesmo dia (409).

## Avaliações — `/avaliacoes`

| Método | Rota | Perfil | Descrição | Sucesso |
|---|---|---|---|---|
| GET | `/avaliacoes` | todos | Lista (mais recentes primeiro) | 200 |
| GET | `/avaliacoes/{id}` | todos | Busca por id | 200 |
| POST | `/avaliacoes` | CLIENTE, ADMIN | Publica | 201 |
| PUT | `/avaliacoes/{id}` | CLIENTE, ADMIN | Edita autor, nota e comentário | 200 |
| DELETE | `/avaliacoes/{id}` | CLIENTE, ADMIN | Exclui | 204 |

```json
{ "autorNome": "Beatriz", "nota": 5, "comentario": "Chegou quentinho!", "produto": { "id": 1 } }
```

Regras: nota de 1 a 5 (400); a `data` é preenchida pelo servidor.

## Encomendas — `/encomendas` (funcionalidade específica do PI)

O comprador encomenda um produto para retirar com o vendedor no campus, em um dia e horário em que o vendedor atende.

| Método | Rota | Perfil | Descrição | Sucesso |
|---|---|---|---|---|
| GET | `/encomendas` | todos | Lista por data de retirada | 200 |
| GET | `/encomendas/status/{status}` | todos | Filtra por status | 200 |
| GET | `/encomendas/{id}` | todos | Busca por id | 200 |
| POST | `/encomendas` | CLIENTE, ADMIN | Cria (status PENDENTE) | 201 |
| PUT | `/encomendas/{id}` | CLIENTE, ADMIN | Edita encomenda PENDENTE | 200 |
| PATCH | `/encomendas/{id}/confirmar` | VENDEDOR, ADMIN | PENDENTE → CONFIRMADA | 200 |
| PATCH | `/encomendas/{id}/entregar` | VENDEDOR, ADMIN | CONFIRMADA → ENTREGUE | 200 |
| PATCH | `/encomendas/{id}/cancelar` | todos | PENDENTE/CONFIRMADA → CANCELADA | 200 |
| DELETE | `/encomendas/{id}` | ADMIN | Exclui encomenda finalizada | 204 |

```json
{
  "clienteNome": "Beatriz",
  "clienteTelefone": "14991234567",
  "quantidade": 2,
  "dataRetirada": "2026-12-07",
  "horaRetirada": "10:30",
  "observacao": "Sabores sortidos",
  "produto": { "id": 1 }
}
```

`status`, `valorTotal` e `dataCriacao` são calculados pelo servidor (se vierem no JSON, são ignorados).

Regras:
1. Produto indisponível não aceita encomenda (409).
2. A retirada precisa cair dentro de um horário de atendimento do vendedor; domingo não tem atendimento (409).
3. `valorTotal = preço do produto × quantidade`, recalculado na edição.
4. Só encomendas PENDENTES podem ser editadas (409).
5. O status segue o fluxo PENDENTE → CONFIRMADA → ENTREGUE, com cancelamento possível antes da entrega (409 para qualquer outra transição).
6. Só encomendas ENTREGUES ou CANCELADAS podem ser excluídas (409).
7. Quantidade de 1 a 20 e data de retirada a partir de hoje (400).

## Consultas personalizadas (Spring Data JPA)

| Repositório | Método | Tipo |
|---|---|---|
| `ProdutoRepositorio` | `findByDisponivelTrueOrderByNome` | nome de método |
| `ProdutoRepositorio` | `findByNomeContainingIgnoreCaseOrderByNome` | nome de método |
| `ProdutoRepositorio` | `buscarDisponiveisPorFaixaDePreco` | `@Query` JPQL |
| `VendedorRepositorio` | `findByNomeNegocioContainingIgnoreCaseOrderByNomeNegocio` | nome de método |
| `HorarioAtendimentoRepositorio` | `contarConflitos` | `@Query` JPQL |
| `HorarioAtendimentoRepositorio` | `existsByVendedorIdAndDiaAndInicioLessThanEqualAndFimGreaterThanEqual` | nome de método |
| `AvaliacaoRepositorio` | `calcularMediaPorVendedor` | `@Query` JPQL |
| `EncomendaRepositorio` | `somarValorPorVendedorEStatus` | `@Query` JPQL |
| `EncomendaRepositorio` | `findByStatusOrderByDataRetiradaAscHoraRetiradaAsc` | nome de método |
