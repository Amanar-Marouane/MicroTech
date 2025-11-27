package com.restapi.microtech.repository;

import com.restapi.microtech.entity.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CommandeRepository extends JpaRepository<Commande, Long>, JpaSpecificationExecutor<Commande> {

    @Query("select distinct c from Commande c " +
            "left join fetch c.articles a " +
            "left join fetch a.produit p " +
            "where c.client.id = :clientId order by c.dateCommande desc")
    java.util.List<Commande> findWithDetailsByClientId(@Param("clientId") Long clientId);
}
