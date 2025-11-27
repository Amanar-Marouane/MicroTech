package com.restapi.microtech.service;

import com.restapi.microtech.contract.PromoServiceContract;
import com.restapi.microtech.exception.BusinessRuleException;
import com.restapi.microtech.util.MoneyUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@Service
public class PromoService implements PromoServiceContract {

    private static final Pattern PROMO_PATTERN = Pattern.compile("PROMO-[A-Z0-9]{4}");

    @Override
    public BigDecimal computePromoDiscount(String promoCode, BigDecimal sousTotal) {
        if (promoCode == null || promoCode.isBlank()) {
            return BigDecimal.ZERO;
        }
        if (!PROMO_PATTERN.matcher(promoCode).matches()) {
            throw new BusinessRuleException("Code promo invalide: format attendu PROMO-XXXX");
        }
        if (sousTotal == null) return BigDecimal.ZERO;
        return MoneyUtil.twoDecimals(sousTotal.multiply(new BigDecimal("0.05")));
    }
}
