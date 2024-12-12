package com.example.contacts.repository;

import com.example.contacts.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    List<Contact> findByNameContainingOrPhoneContaining(String name, String phone);
}
