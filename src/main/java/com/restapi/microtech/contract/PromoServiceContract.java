package com.restapi.microtech.contract;

import java.math.BigDecimal;

public interface PromoServiceContract {
    /**
     * Validates promo code format and returns the promo discount amount based on subtotal.
     * If code is null/blank, returns BigDecimal.ZERO.
     * Throws BusinessRuleException if format invalid.
     */
    BigDecimal computePromoDiscount(String promoCode, BigDecimal sousTotal);
}
