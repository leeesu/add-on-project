package com.onpurple.repository;


import com.onpurple.model.Img;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImgRepository extends JpaRepository<Img, Long> {
    List<Img> findByPost_Id(Long id);
    List<Img> deleteByPost_Id(Long id);
    List<Img> findByUser_id(Long id);

    List<Img> findByUser_Id(Long id);
    List<Img> deleteByUser_id(Long id);
}
