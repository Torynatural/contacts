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
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/contacts")
@CrossOrigin(origins = "*") // 允许跨域访问
public class ContactController {

    private final ContactService contactService;

    @Value("${file.upload.dir}") // 上传文件存储在static目录
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
        Contact contact = contactService.getById(id);
        if (contact == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
        return ResponseEntity.ok(contact);
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) MultipartFile avatar
    ) {
        if (name.isEmpty() || phone.isEmpty()) {
            return ResponseEntity.badRequest().body("Name and phone are required");
        }

        Contact contact = new Contact();
        contact.setName(name);
        contact.setPhone(phone);
        contact.setEmail(email);

        if (avatar != null && !avatar.isEmpty()) {
            contact.setAvatar(saveFile(avatar)); // 保存文件
        }

        contactService.save(contact);
        return ResponseEntity.ok(contact);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) MultipartFile avatar
    ) {
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
            existing.setAvatar(saveFile(avatar)); // 保存新文件
        }

        contactService.save(existing);
        return ResponseEntity.ok(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        boolean deleted = contactService.delete(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
        return ResponseEntity.ok("success");
    }

    /**
     * 保存文件到 static/uploads 目录
     * @param file 上传的文件
     * @return 文件的相对路径
     */
    private String saveFile(MultipartFile file) {
        // 1. 获取文件原始名称
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        // 2. 生成唯一文件名
        String fileName = System.currentTimeMillis() + "-" + (int)(Math.random() * 10000) + ext;

        // 3. 动态生成目录 (按日期分目录保存)
        String dateFolder = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String uploadPath = System.getProperty("user.dir") + "/uploads/" + dateFolder; // 动态路径：项目运行目录 + 日期子目录
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs(); // 创建目录
        }

        // 4. 保存文件到动态路径
        File dest = new File(dir, fileName);
        try {
            file.transferTo(dest); // 保存文件
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败", e); // 抛出异常
        }

        // 5. 返回文件的相对访问路径
        return "/uploads/" + dateFolder + "/" + fileName; // 返回前端可访问的路径
    }

}
