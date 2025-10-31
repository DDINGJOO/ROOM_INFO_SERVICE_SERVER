package com.teambind.springproject.repository;

import com.teambind.springproject.entity.attribute.StringBased.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
}
