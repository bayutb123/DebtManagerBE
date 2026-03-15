package com.example.debtmanager.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "contacts")
class ContactEntity(
    @Column(name = "user_id", nullable = false)
    var userId: UUID,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var phone: String,
    @Column(nullable = false)
    var notes: String
) : BaseEntity()