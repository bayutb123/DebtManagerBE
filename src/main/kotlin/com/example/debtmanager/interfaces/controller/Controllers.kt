package com.example.debtmanager.interfaces.controller

import com.example.debtmanager.application.dto.*
import com.example.debtmanager.application.usecase.*
import com.example.debtmanager.interfaces.request.*
import com.example.debtmanager.interfaces.response.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/auth")
class AuthController(private val authenticateUseCase: AuthenticateUseCase) {
    @PostMapping("/google")
    fun google(@Valid @RequestBody request: GoogleAuthRequest): AuthResponse {
        val result = authenticateUseCase.execute(GoogleAuthCommand(request.idToken))
        return AuthResponse(result.token, result.user.toResponse())
    }
}

@RestController
@RequestMapping("/contacts")
class ContactController(private val useCase: ContactUseCase) {
    @GetMapping
    fun list(): List<ContactResponse> = useCase.list().map { it.toResponse() }

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): ContactResponse = useCase.get(id).toResponse()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: ContactRequest): ContactResponse = useCase.create(
        ContactCommand(request.name, request.phone, request.notes)
    ).toResponse()

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody request: ContactRequest): ContactResponse = useCase.update(
        id, ContactCommand(request.name, request.phone, request.notes)
    ).toResponse()

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID) { useCase.delete(id) }
}

@RestController
@RequestMapping("/debts")
class DebtController(private val useCase: DebtUseCase) {
    @GetMapping
    fun list(): List<DebtResponse> = useCase.list().map { it.toResponse() }

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): DebtResponse = useCase.get(id).toResponse()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: DebtRequest): DebtResponse = useCase.create(
        DebtCommand(request.contactId, request.title, request.amount, request.dueDate, request.notes)
    ).toResponse()

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody request: DebtRequest): DebtResponse = useCase.update(
        id, DebtCommand(request.contactId, request.title, request.amount, request.dueDate, request.notes)
    ).toResponse()

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID) { useCase.delete(id) }

    @PostMapping("/{id}/payment")
    fun pay(@PathVariable id: UUID, @Valid @RequestBody request: PaymentRequest): DebtResponse = useCase.pay(
        id, PaymentCommand(request.amount, request.date, request.note)
    ).toResponse()
}

@RestController
@RequestMapping("/receivables")
class ReceivableController(private val useCase: ReceivableUseCase) {
    @GetMapping
    fun list(): List<ReceivableResponse> = useCase.list().map { it.toResponse() }

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): ReceivableResponse = useCase.get(id).toResponse()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: ReceivableRequest): ReceivableResponse = useCase.create(
        ReceivableCommand(request.contactId, request.title, request.amount, request.dueDate, request.notes)
    ).toResponse()

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody request: ReceivableRequest): ReceivableResponse = useCase.update(
        id, ReceivableCommand(request.contactId, request.title, request.amount, request.dueDate, request.notes)
    ).toResponse()

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID) { useCase.delete(id) }

    @PostMapping("/{id}/payment")
    fun pay(@PathVariable id: UUID, @Valid @RequestBody request: PaymentRequest): ReceivableResponse = useCase.pay(
        id, PaymentCommand(request.amount, request.date, request.note)
    ).toResponse()
}

@RestController
@RequestMapping("/transactions")
class TransactionController(private val useCase: TransactionUseCase) {
    @GetMapping
    fun list(): List<TransactionResponse> = useCase.list().map { it.toResponse() }

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): TransactionResponse = useCase.get(id).toResponse()
}

@RestController
@RequestMapping("/dashboard")
class DashboardController(private val useCase: DashboardUseCase) {
    @GetMapping("/summary")
    fun summary(): DashboardSummaryResponse {
        val summary = useCase.summary()
        return DashboardSummaryResponse(
            totalDebt = summary.totalDebt,
            totalReceivable = summary.totalReceivable,
            netBalance = summary.netBalance,
            recentTransactions = summary.recentTransactions.map { it.toResponse() }
        )
    }
}