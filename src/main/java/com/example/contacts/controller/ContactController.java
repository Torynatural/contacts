package com.example.contacts.controller;

import com.example.contacts.entity.Contact;
import com.example.contacts.service.ContactService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/contacts")
@CrossOrigin(origins = "*") // 允许跨域访问，如需限制请根据实际情况配置
public class ContactController {

    private final ContactService contactService;

    @Value("${file.upload.dir:src/main/resources/static/uploads}")
    private String uploadDir;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(value = "q", required = false) String q) {
        return ResponseEntity.ok(contactService.list(q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        Contact c = contactService.getById(id);
        if (c == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
        return ResponseEntity.ok(c);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestParam String name,
                                    @RequestParam String phone,
                                    @RequestParam(required = false) String email,
                                    @RequestParam(required = false) MultipartFile avatar) {
        if (name.isEmpty() || phone.isEmpty()) {
            return ResponseEntity.badRequest().body("Name and phone are required");
        }

        Contact c = new Contact();
        c.setName(name);
        c.setPhone(phone);
        c.setEmail(email);

        if (avatar != null && !avatar.isEmpty()) {
            c.setAvatar(saveFile(avatar));
        }

        contactService.save(c);
        return ResponseEntity.ok("success");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @RequestParam String name,
                                    @RequestParam String phone,
                                    @RequestParam(required = false) String email,
                                    @RequestParam(required = false) MultipartFile avatar) {
        Contact existing = contactService.getById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
        if (name.isEmpty() || phone.isEmpty()) {
            return ResponseEntity.badRequest().body("Name and phone are required");
        }

        existing.setName(name);
        existing.setPhone(phone);
        existing.setEmail(email);

        if (avatar != null && !avatar.isEmpty()) {
            existing.setAvatar(saveFile(avatar));
        }

        contactService.save(existing);
        return ResponseEntity.ok("success");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        boolean deleted = contactService.delete(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
        return ResponseEntity.ok("success");
    }

    private String saveFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        String fileName = System.currentTimeMillis() + "-" + (int)(Math.random()*10000) + ext;
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File dest = new File(dir, fileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "/uploads/" + fileName; // 返回可供前端访问的相对路径
    }
}
