package com.restapi.microtech.service;

import com.restapi.microtech.contract.OrderItemServiceContract;
import com.restapi.microtech.dto.request.ArticleCommandeRequest;
import com.restapi.microtech.entity.ArticleCommande;
import com.restapi.microtech.entity.Commande;
import com.restapi.microtech.entity.Produit;
import com.restapi.microtech.mapper.ArticleCommandeMapper;
import com.restapi.microtech.repository.ProduitRepository;
import com.restapi.microtech.util.MoneyUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService implements OrderItemServiceContract {

    private final ProduitRepository produitRepository;
    private final ArticleCommandeMapper articleCommandeMapper;

    @Override
    public BuildResult buildArticles(Commande commande, List<ArticleCommandeRequest> items) {
        List<ArticleCommande> articles = new ArrayList<>();
        boolean stockInsuffisant = false;
        BigDecimal sousTotal = BigDecimal.ZERO;

        for (ArticleCommandeRequest itemReq : items) {
            Produit produit = produitRepository.findById(itemReq.getProduitId())
                    .orElseThrow(() -> new EntityNotFoundException("Produit introuvable: " + itemReq.getProduitId()));
            if (itemReq.getQuantite() > produit.getStockDisponible()) {
                stockInsuffisant = true;
            }
            ArticleCommande article = articleCommandeMapper.toEntity(itemReq, produit);
            article.setCommande(commande);
            sousTotal = sousTotal.add(article.getTotalLigne());
            articles.add(article);
        }

        return new BuildResult(articles, MoneyUtil.twoDecimals(sousTotal), stockInsuffisant);
    }
}
