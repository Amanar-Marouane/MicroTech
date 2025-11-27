package com.restapi.microtech.contract;

import com.restapi.microtech.dto.request.ProduitRequest;
import com.restapi.microtech.dto.response.ProduitResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductServiceContract {
    ProduitResponse create(ProduitRequest request);

    ProduitResponse getById(Long id);

    ProduitResponse update(Long id, ProduitRequest request);

    void delete(Long id);

    Page<ProduitResponse> search(String nom,
                                 java.math.BigDecimal prixMin,
                                 java.math.BigDecimal prixMax,
                                 Integer stockMin,
                                 Integer stockMax,
                                 Pageable pageable);
}
