package com.example.contacts.service;

import com.example.contacts.entity.Contact;
import com.example.contacts.repository.ContactRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {
    private final ContactRepository repository;

    public ContactService(ContactRepository repository) {
        this.repository = repository;
    }

    public List<Contact> list(String q) {
        if (q == null || q.isEmpty()) {
            return repository.findAll();
        }
        return repository.findByNameContainingOrPhoneContaining(q, q);
    }

    public Contact getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public Contact save(Contact c) {
        return repository.save(c);
    }

    public boolean delete(Integer id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
