package com.seanconroy.fiae.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "quiz_state", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "module_key"})
})
public class QuizState extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public WhitelistUser user;

    @Column(name = "module_key", nullable = false)
    public String moduleKey;

    @Column(name = "module_name")
    public String moduleName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "queue_slugs", nullable = false)
    public List<String> queueSlugs = new ArrayList<>();

    @Column(name = "current_index", nullable = false)
    public int currentIndex;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "results_by_slug", nullable = false)
    public Map<String, String> resultsBySlug = new LinkedHashMap<>();

    @Column(name = "completed", nullable = false)
    public boolean completed = false;

    @Column(name = "revision", nullable = false)
    public long revision;

    @Column(name = "started_at", nullable = false)
    public Instant startedAt;

    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt;
}
