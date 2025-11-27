package com.restapi.microtech.contract;

import com.restapi.microtech.dto.request.ArticleCommandeRequest;
import com.restapi.microtech.entity.ArticleCommande;
import com.restapi.microtech.entity.Commande;

import java.math.BigDecimal;
import java.util.List;

public interface OrderItemServiceContract {

    class BuildResult {
        private final List<ArticleCommande> articles;
        private final BigDecimal sousTotal;
        private final boolean stockInsuffisant;

        public BuildResult(List<ArticleCommande> articles, BigDecimal sousTotal, boolean stockInsuffisant) {
            this.articles = articles;
            this.sousTotal = sousTotal;
            this.stockInsuffisant = stockInsuffisant;
        }

        public List<ArticleCommande> getArticles() { return articles; }
        public BigDecimal getSousTotal() { return sousTotal; }
        public boolean isStockInsuffisant() { return stockInsuffisant; }
    }

    /**
     * Builds and validates order items for the given order, returns articles list, subtotal and stock flag.
     * Throws EntityNotFoundException for missing products.
     */
    BuildResult buildArticles(Commande commande, List<ArticleCommandeRequest> items);
}
