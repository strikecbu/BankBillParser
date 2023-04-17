package com.andy.bankbillparser.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BasicTransaction {
    protected String source;
    protected String destination;
    protected String date;
    protected String description;
    protected Long amount;
}
