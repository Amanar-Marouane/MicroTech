package com.restapi.microtech.repository;

import com.restapi.microtech.entity.ArticleCommande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleCommandeRepository extends JpaRepository<ArticleCommande, Long> {
}
