package com.hrms.db.repositories.Expense_Management;

import com.pesu.expensesubsystem.entity.Receipt;
import java.util.List;
import java.util.Optional;

public interface ReceiptRepository {
    Receipt save(Receipt receipt);
    Optional<Receipt> findById(Long receiptId);
    List<Receipt> findByClaimId(Long claimId);
    void delete(Long receiptId);
    void deleteByClaimId(Long claimId);
}
