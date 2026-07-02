package com.seanconroy.fiae.repository;

import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.hibernate.orm.panache.PanacheRepository;


import com.seanconroy.fiae.entity.QuizSession;
import com.seanconroy.fiae.entity.QuizSessionCard;

@ApplicationScoped
public class QuizSessionCardRepository implements PanacheRepository<QuizSessionCard> {


    public List<QuizSessionCard> findBySession(QuizSession session) {
        return list("session", session);
    }
}
