insert into categoria (nome, descricao) values ('Doces', 'Brigadeiros, bolos, pães de mel e outras sobremesas');
insert into categoria (nome, descricao) values ('Salgados', 'Coxinhas, esfirras, pastéis e salgados assados');
insert into categoria (nome, descricao) values ('Refeições', 'Marmitas e pratos completos');
insert into categoria (nome, descricao) values ('Bebidas', 'Sucos, cafés e bebidas geladas');
insert into categoria (nome, descricao) values ('Lanches naturais', 'Sanduíches naturais, wraps e saladas');

insert into vendedor (nome, email, telefone, nome_negocio, localizacao) values ('Isa', 'isa@unifood.com', '14998453088', 'Doces da Isa', 'Bloco 2');
insert into vendedor (nome, email, telefone, nome_negocio, localizacao) values ('Bruno', 'bruno@unifood.com', '11900000001', 'Cantinho do Bruno', 'Biblioteca');
insert into vendedor (nome, email, telefone, nome_negocio, localizacao) values ('Carol', 'carol@unifood.com', '11900000002', 'Salgados da Carol', 'Bloco 6');
insert into vendedor (nome, email, telefone, nome_negocio, localizacao) values ('Lucas', 'lucas@unifood.com', '11900000003', 'Fit Express', 'Bloco 2');

insert into produto (nome, descricao, preco, imagem_url, disponivel, localizacao, vendedor_id, categoria_id) values ('Brigadeiro gourmet', 'Caixa com 6 unidades, sabores variados.', 12.00, '', true, 'Bloco 5', 1, 1);
insert into produto (nome, descricao, preco, imagem_url, disponivel, localizacao, vendedor_id, categoria_id) values ('Pão de mel', 'Recheado com doce de leite e coberto com chocolate.', 7.50, '', true, 'Biblioteca', 2, 1);
insert into produto (nome, descricao, preco, imagem_url, disponivel, localizacao, vendedor_id, categoria_id) values ('Coxinha de frango', 'Salgado frito na hora, com catupiry.', 6.00, '', false, 'Bloco 6', 3, 2);
insert into produto (nome, descricao, preco, imagem_url, disponivel, localizacao, vendedor_id, categoria_id) values ('Marmita fitness', 'Frango, arroz integral e legumes. Encomende até as 10h.', 18.00, '', true, 'Bloco 2', 4, 3);
insert into produto (nome, descricao, preco, imagem_url, disponivel, localizacao, vendedor_id, categoria_id) values ('Bolo de pote de ninho', 'Pote de 250 ml com creme de leite ninho e morango.', 10.00, '', true, 'Bloco 5', 1, 1);
insert into produto (nome, descricao, preco, imagem_url, disponivel, localizacao, vendedor_id, categoria_id) values ('Suco natural de laranja', 'Copo de 500 ml, feito na hora.', 5.00, '', true, 'Bloco 2', 4, 4);

insert into horario_atendimento (dia, inicio, fim, local, vendedor_id) values ('SEGUNDA', '10:00:00', '12:00:00', 'Bloco 5', 1);
insert into horario_atendimento (dia, inicio, fim, local, vendedor_id) values ('QUARTA', '14:00:00', '16:00:00', 'Biblioteca', 1);
insert into horario_atendimento (dia, inicio, fim, local, vendedor_id) values ('TERCA', '09:00:00', '11:00:00', 'Biblioteca', 2);
insert into horario_atendimento (dia, inicio, fim, local, vendedor_id) values ('QUINTA', '18:30:00', '20:00:00', 'Bloco 6', 3);
insert into horario_atendimento (dia, inicio, fim, local, vendedor_id) values ('SEGUNDA', '11:00:00', '13:00:00', 'Bloco 2', 4);
insert into horario_atendimento (dia, inicio, fim, local, vendedor_id) values ('SEXTA', '11:00:00', '13:00:00', 'Bloco 2', 4);

insert into avaliacao (autor_nome, nota, comentario, data, produto_id) values ('Marina', 5, 'Os melhores brigadeiros do campus!', '2026-11-10', 1);
insert into avaliacao (autor_nome, nota, comentario, data, produto_id) values ('Pedro', 4, 'Muito bons, só achei a caixa pequena.', '2026-11-12', 1);
insert into avaliacao (autor_nome, nota, comentario, data, produto_id) values ('Júlia', 5, 'Recheio caprichado.', '2026-11-11', 2);
insert into avaliacao (autor_nome, nota, comentario, data, produto_id) values ('Rafael', 4, 'Boa porção e bem temperada.', '2026-11-13', 4);

insert into encomenda (cliente_nome, cliente_telefone, quantidade, data_retirada, hora_retirada, observacao, status, valor_total, data_criacao, produto_id) values ('Marina', '14991112222', 2, '2026-11-16', '10:30:00', 'Sabores tradicionais', 'ENTREGUE', 24.00, '2026-11-13 09:15:00', 1);
insert into encomenda (cliente_nome, cliente_telefone, quantidade, data_retirada, hora_retirada, observacao, status, valor_total, data_criacao, produto_id) values ('Pedro', '14993334444', 1, '2026-11-23', '11:00:00', null, 'PENDENTE', 12.00, '2026-11-19 14:00:00', 1);
insert into encomenda (cliente_nome, cliente_telefone, quantidade, data_retirada, hora_retirada, observacao, status, valor_total, data_criacao, produto_id) values ('Rafael', '14995556666', 3, '2026-11-23', '12:00:00', 'Sem cebola', 'CONFIRMADA', 54.00, '2026-11-19 16:30:00', 4);
