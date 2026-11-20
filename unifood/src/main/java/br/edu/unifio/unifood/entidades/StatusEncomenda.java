package br.edu.unifio.unifood.entidades;

// Ciclo de vida de uma encomenda:
// PENDENTE -> CONFIRMADA -> ENTREGUE
// PENDENTE ou CONFIRMADA -> CANCELADA
public enum StatusEncomenda {
    PENDENTE,
    CONFIRMADA,
    ENTREGUE,
    CANCELADA;

    public boolean podeMudarPara (StatusEncomenda novo) {
        return switch (this) {
            case PENDENTE -> novo == CONFIRMADA || novo == CANCELADA;
            case CONFIRMADA -> novo == ENTREGUE || novo == CANCELADA;
            case ENTREGUE, CANCELADA -> false;
        };
    }

    // Encomenda ainda em andamento (não foi entregue nem cancelada)
    public boolean emAberto () {
        return this == PENDENTE || this == CONFIRMADA;
    }
}
