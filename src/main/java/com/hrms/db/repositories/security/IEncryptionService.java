package com.hrms.db.repositories.security;

public interface IEncryptionService {
    String encrypt(String plainText);

    String decrypt(String cipherText);

    String mask(String plainText);
}
