package com.example.debtmanager.application.service

import com.example.debtmanager.application.dto.ContactCommand
import com.example.debtmanager.common.exception.NotFoundException
import com.example.debtmanager.common.utils.SecurityUtils
import com.example.debtmanager.domain.model.Contact
import com.example.debtmanager.domain.repository.ContactRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class ContactService(private val contactRepository: ContactRepository) {
    fun list(): List<Contact> = contactRepository.findAllByUserId(SecurityUtils.currentUserId())

    fun get(id: UUID): Contact = contactRepository.findByIdAndUserId(id, SecurityUtils.currentUserId())
        ?: throw NotFoundException("Contact not found")

    fun create(command: ContactCommand): Contact {
        val userId = SecurityUtils.currentUserId()
        val contact = Contact(
            id = UUID.randomUUID(),
            userId = userId,
            name = command.name,
            phone = command.phone,
            notes = command.notes,
            createdAt = Instant.now()
        )
        return contactRepository.save(contact)
    }

    fun update(id: UUID, command: ContactCommand): Contact {
        val existing = get(id)
        val updated = existing.copy(name = command.name, phone = command.phone, notes = command.notes)
        return contactRepository.save(updated)
    }

    fun delete(id: UUID) {
        get(id) // ensure ownership
        contactRepository.delete(id)
    }
}