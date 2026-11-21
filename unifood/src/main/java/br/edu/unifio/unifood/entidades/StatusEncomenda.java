package br.edu.unifio.unifood.entidades;

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

    public boolean emAberto () {
        return this == PENDENTE || this == CONFIRMADA;
    }
}
