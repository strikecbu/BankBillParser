package com.andy.bankbillparser.model;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardTransaction extends BasicTransaction {
    private String card;

    @Override
    public String toString() {
        return "CardTransaction{" +
                "card='" + card + '\'' +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", date='" + date + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                '}';
    }
}
