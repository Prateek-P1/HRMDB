package com.hrms.db.repositories.Expense_Management;

import com.hrms.db.config.DatabaseConnection;
import com.pesu.expensesubsystem.entity.Receipt;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ExpenseReceiptRepositoryImpl implements ReceiptRepository {

    @Override
    public Receipt save(Receipt receipt) {
        if (receipt == null) throw new IllegalArgumentException("receipt is null");
        if (receipt.getClaimId() == null) throw new IllegalArgumentException("claimId is null");

        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            com.hrms.db.entities.Receipt entity = new com.hrms.db.entities.Receipt();
            entity.setClaimId(String.valueOf(receipt.getClaimId()));
            entity.setFilePath(receipt.getFilePath());
            entity.setFileName(receipt.getFileName());
            entity.setUploadDate(receipt.getUploadDate());
            session.persist(entity);

            tx.commit();
            return ExpenseRepoMapper.toDtoReceipt(entity);
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public Optional<Receipt> findById(Long receiptId) {
        if (receiptId == null) return Optional.empty();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            com.hrms.db.entities.Receipt entity = session.get(com.hrms.db.entities.Receipt.class, receiptId);
            return Optional.ofNullable(ExpenseRepoMapper.toDtoReceipt(entity));
        }
    }

    @Override
    public List<Receipt> findByClaimId(Long claimId) {
        if (claimId == null) return Collections.emptyList();
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            List<com.hrms.db.entities.Receipt> entities = session
                    .createQuery("FROM Receipt r WHERE r.claimId = :cid ORDER BY r.uploadDate DESC", com.hrms.db.entities.Receipt.class)
                    .setParameter("cid", String.valueOf(claimId))
                    .getResultList();
            List<Receipt> out = new ArrayList<>(entities.size());
            for (com.hrms.db.entities.Receipt r : entities) out.add(ExpenseRepoMapper.toDtoReceipt(r));
            return out;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public void delete(Long receiptId) {
        if (receiptId == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            com.hrms.db.entities.Receipt entity = session.get(com.hrms.db.entities.Receipt.class, receiptId);
            if (entity != null) session.remove(entity);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }

    @Override
    public void deleteByClaimId(Long claimId) {
        if (claimId == null) return;
        Transaction tx = null;
        try (Session session = DatabaseConnection.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            List<com.hrms.db.entities.Receipt> entities = session
                    .createQuery("FROM Receipt r WHERE r.claimId = :cid", com.hrms.db.entities.Receipt.class)
                    .setParameter("cid", String.valueOf(claimId))
                    .getResultList();
            for (com.hrms.db.entities.Receipt r : entities) session.remove(r);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
            throw ex;
        }
    }
}
