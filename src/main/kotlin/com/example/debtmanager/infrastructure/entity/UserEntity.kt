package com.example.debtmanager.infrastructure.entity

import com.example.debtmanager.domain.model.Provider
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class UserEntity(
    @Column(nullable = false, unique = true)
    var email: String,
    @Column(nullable = false)
    var name: String,
    @Column(name = "picture_url", nullable = false)
    var pictureUrl: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var provider: Provider
) : BaseEntity()