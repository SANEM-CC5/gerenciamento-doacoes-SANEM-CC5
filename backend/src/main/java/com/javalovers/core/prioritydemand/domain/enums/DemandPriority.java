package com.javalovers.core.prioritydemand.domain.enums;

/** Nível de prioridade de uma demanda (RF20). O peso define a ordenação: maior = mais urgente. */
public enum DemandPriority {
    HIGH(3),
    MEDIUM(2),
    LOW(1);

    private final int weight;

    DemandPriority(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
