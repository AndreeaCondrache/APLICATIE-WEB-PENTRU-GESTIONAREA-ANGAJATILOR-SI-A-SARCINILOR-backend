package com.andreeacondrache.licenta.repositories;

import com.andreeacondrache.licenta.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {


    List<Comment> findAllByTaskId(Long taskId);
}
