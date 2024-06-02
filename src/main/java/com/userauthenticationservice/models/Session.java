package com.userauthenticationservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Session extends BaseModel {

    private String token;

    @Enumerated(EnumType.ORDINAL)
    private SessionState sessionState;

    @ManyToOne
    private User user;
}
