package com.billbharat.sales.service.impl;

import com.billbharat.sales.dto.request.SaleRequest;
import com.billbharat.sales.dto.response.SaleResponse;
import com.billbharat.sales.entity.Commission;
import com.billbharat.sales.entity.Coupon;
import com.billbharat.sales.entity.Sale;
import com.billbharat.sales.exception.BadRequestException;
import com.billbharat.sales.exception.ResourceNotFoundException;
import com.billbharat.sales.repository.CommissionRepository;
import com.billbharat.sales.repository.CouponRepository;
import com.billbharat.sales.repository.SaleRepository;
import com.billbharat.sales.service.SalesService;
import com.billbharat.sales.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SalesServiceImpl implements SalesService {

    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.05"); // 5% commission

    private final SaleRepository saleRepository;
    private final CouponRepository couponRepository;
    private final CommissionRepository commissionRepository;

    @Override
    @Transactional
    public SaleResponse createSale(UUID userId, SaleRequest request) {
        BigDecimal amount = request.getAmount();
        BigDecimal discountAmount = BigDecimal.ZERO;
        UUID couponId = null;

        // Apply coupon if provided
        if (StringUtils.hasText(request.getCouponCode())) {
            Coupon coupon = couponRepository.findByCodeAndIsActiveTrue(request.getCouponCode())
                    .orElseThrow(() -> new BadRequestException("Invalid or expired coupon code"));

            // Validate coupon
            LocalDateTime now = LocalDateTime.now();
            if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
                throw new BadRequestException("Coupon not yet valid");
            }
            if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
                throw new BadRequestException("Coupon has expired");
            }
            if (coupon.getMinOrderValue() != null && amount.compareTo(coupon.getMinOrderValue()) < 0) {
                throw new BadRequestException("Order amount too low for this coupon");
            }
            if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
                throw new BadRequestException("Coupon usage limit reached");
            }

            // Calculate discount
            if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
                discountAmount = amount.multiply(coupon.getDiscountValue()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                if (coupon.getMaxDiscount() != null && discountAmount.compareTo(coupon.getMaxDiscount()) > 0) {
                    discountAmount = coupon.getMaxDiscount();
                }
            } else {
                discountAmount = coupon.getDiscountValue();
            }

            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);
            couponId = coupon.getId();
        }

        BigDecimal finalAmount = amount.subtract(discountAmount);
        String invoiceNumber = "INV-" + System.currentTimeMillis();

        Sale sale = Sale.builder()
                .userId(userId)
                .merchantId(StringUtils.hasText(request.getMerchantId())
                        ? UUID.fromString(request.getMerchantId()) : null)
                .couponId(couponId)
                .amount(amount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .saleTime(DateUtil.now())
                .invoiceNumber(invoiceNumber)
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : Sale.PaymentMethod.CASH)
                .status(Sale.Status.COMPLETED)
                .productDetails(request.getProductDetails())
                .notes(request.getNotes())
                .build();

        Sale savedSale = saleRepository.save(sale);

        // Calculate and store commission
        BigDecimal commissionAmount = finalAmount.multiply(COMMISSION_RATE).setScale(2, RoundingMode.HALF_UP);
        Commission commission = Commission.builder()
                .userId(userId)
                .saleId(savedSale.getId())
                .commissionType(Commission.CommissionType.SALE)
                .baseAmount(finalAmount)
                .commissionRate(COMMISSION_RATE.multiply(new BigDecimal("100")))
                .commissionAmount(commissionAmount)
                .status(Commission.Status.PENDING)
                .build();
        commissionRepository.save(commission);

        return SaleResponse.fromEntity(savedSale);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SaleResponse> getSales(UUID userId, Pageable pageable) {
        return saleRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(SaleResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponse getSaleById(UUID saleId) {
        return saleRepository.findById(saleId)
                .map(SaleResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", "id", saleId));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> validateCoupon(String couponCode) {
        Coupon coupon = couponRepository.findByCodeAndIsActiveTrue(couponCode)
                .orElseThrow(() -> new BadRequestException("Invalid or expired coupon code"));

        Map<String, Object> result = new HashMap<>();
        result.put("code", coupon.getCode());
        result.put("description", coupon.getDescription());
        result.put("discountType", coupon.getDiscountType().name());
        result.put("discountValue", coupon.getDiscountValue());
        result.put("minOrderValue", coupon.getMinOrderValue());
        result.put("maxDiscount", coupon.getMaxDiscount());
        result.put("isValid", true);
        return result;
    }
}
