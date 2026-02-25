package com.smart.smartcontactmanager.repositories;



import com.smart.smartcontactmanager.entities.contact;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContactRepository extends JpaRepository<contact, Integer> {
    List<contact> findByUser_Id(int userId);
}