/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

package com.opengamma.analytics.financial.interestrate.inflation.provider;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;
import org.threeten.bp.Period;
import org.threeten.bp.ZonedDateTime;

import com.opengamma.analytics.financial.instrument.index.IndexPrice;
import com.opengamma.analytics.financial.instrument.inflation.CouponInflationYearOnYearInterpolationDefinition;
import com.opengamma.analytics.financial.interestrate.inflation.derivative.CouponInflationYearOnYearInterpolation;
import com.opengamma.analytics.financial.provider.calculator.inflation.NetAmountInflationCalculator;
import com.opengamma.analytics.financial.provider.calculator.inflation.PresentValueCurveSensitivityDiscountingInflationCalculator;
import com.opengamma.analytics.financial.provider.calculator.inflation.PresentValueDiscountingInflationCalculator;
import com.opengamma.analytics.financial.provider.description.MulticurveProviderDiscountDataSets;
import com.opengamma.analytics.financial.provider.description.inflation.InflationIssuerProviderDiscount;
import com.opengamma.analytics.financial.provider.description.inflation.InflationProviderInterface;
import com.opengamma.analytics.financial.provider.sensitivity.inflation.MultipleCurrencyInflationSensitivity;
import com.opengamma.analytics.financial.provider.sensitivity.inflation.ParameterSensitivityInflationMulticurveDiscountInterpolatedFDCalculator;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.MultipleCurrencyParameterSensitivity;
import com.opengamma.analytics.financial.provider.sensitivity.parameter.ParameterInflationSensitivityParameterCalculator;
import com.opengamma.analytics.financial.schedule.ScheduleCalculator;
import com.opengamma.analytics.financial.util.AssertSensivityObjects;
import com.opengamma.financial.convention.businessday.BusinessDayConvention;
import com.opengamma.financial.convention.businessday.BusinessDayConventionFactory;
import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.util.money.MultipleCurrencyAmount;
import com.opengamma.util.time.DateUtils;

/**
 * Tests the present value and its sensitivities for year on year with reference index on the first of the month.
 */
public class CouponInflationYearOnYearInterpolationDiscountingMethodTest {

  private static final InflationIssuerProviderDiscount MARKET = MulticurveProviderDiscountDataSets.createMarket1();
  private static final IndexPrice[] PRICE_INDEXES = MARKET.getPriceIndexes().toArray(new IndexPrice[MARKET.getPriceIndexes().size()]);
  private static final IndexPrice PRICE_INDEX_EUR = PRICE_INDEXES[0];
  private static final Calendar CALENDAR_EUR = MulticurveProviderDiscountDataSets.getEURCalendar();
  private static final BusinessDayConvention BUSINESS_DAY = BusinessDayConventionFactory.INSTANCE.getBusinessDayConvention("Modified Following");
  private static final ZonedDateTime START_DATE = DateUtils.getUTCDate(2008, 8, 18);
  private static final Period COUPON_TENOR = Period.ofYears(10);
  private static final ZonedDateTime PAYMENT_DATE = ScheduleCalculator.getAdjustedDate(START_DATE, COUPON_TENOR, BUSINESS_DAY, CALENDAR_EUR);
  private static final ZonedDateTime PAYMENT_DATE_MINUS1 = ScheduleCalculator.getAdjustedDate(START_DATE, Period.ofYears(9), BUSINESS_DAY, CALENDAR_EUR);
  private static final double NOTIONAL = 98765432;
  private static final int MONTH_LAG = 3;
  private static final ZonedDateTime PRICING_DATE = DateUtils.getUTCDate(2011, 8, 3);
  private static final double WEIGHT_START = 1.0 - (PAYMENT_DATE.getDayOfMonth() - 1.) / PAYMENT_DATE.toLocalDate().lengthOfMonth();
  private static final double WEIGHT_END = 1.0 - (PAYMENT_DATE.getDayOfMonth() - 1.) / PAYMENT_DATE.toLocalDate().lengthOfMonth();

  private static final double SHIFT_FD = 1.0E-7;
  private static final double TOLERANCE_PV = 1.0E-2;
  private static final double TOLERANCE_PV_DELTA = 1.0E+2;

  private static final CouponInflationYearOnYearInterpolationDefinition YEAR_ON_YEAR_NO_DEFINITION = CouponInflationYearOnYearInterpolationDefinition.from(PAYMENT_DATE_MINUS1, PAYMENT_DATE, NOTIONAL,
      PRICE_INDEX_EUR, MONTH_LAG, false, WEIGHT_START, WEIGHT_END);
  private static final CouponInflationYearOnYearInterpolation YEAR_ON_YEAR_NO = YEAR_ON_YEAR_NO_DEFINITION.toDerivative(PRICING_DATE);
  private static final CouponInflationYearOnYearInterpolationDefinition YEAR_ON_YEAR_WITH_DEFINITION = CouponInflationYearOnYearInterpolationDefinition.from(PAYMENT_DATE_MINUS1, PAYMENT_DATE,
      NOTIONAL,
      PRICE_INDEX_EUR, MONTH_LAG, true, WEIGHT_START, WEIGHT_END);
  private static final CouponInflationYearOnYearInterpolation YEAR_ON_YEAR_WITH = YEAR_ON_YEAR_WITH_DEFINITION.toDerivative(PRICING_DATE);
  private static final CouponInflationYearOnYearInterpolationDiscountingMethod METHOD = new CouponInflationYearOnYearInterpolationDiscountingMethod();
  private static final PresentValueDiscountingInflationCalculator PVIC = PresentValueDiscountingInflationCalculator.getInstance();
  private static final NetAmountInflationCalculator NAIC = NetAmountInflationCalculator.getInstance();
  private static final PresentValueCurveSensitivityDiscountingInflationCalculator PVCSDC = PresentValueCurveSensitivityDiscountingInflationCalculator.getInstance();
  private static final ParameterInflationSensitivityParameterCalculator<InflationProviderInterface> PSC = new ParameterInflationSensitivityParameterCalculator<>(PVCSDC);
  private static final ParameterSensitivityInflationMulticurveDiscountInterpolatedFDCalculator PS_PV_FDC = new ParameterSensitivityInflationMulticurveDiscountInterpolatedFDCalculator(PVIC, SHIFT_FD);

  @Test
  /**
   * Tests the present value.
   */
  public void presentValueNoNotional() {
    final MultipleCurrencyAmount pv = METHOD.presentValue(YEAR_ON_YEAR_NO, MARKET.getInflationProvider());
    final double df = MARKET.getCurve(YEAR_ON_YEAR_NO.getCurrency()).getDiscountFactor(YEAR_ON_YEAR_NO.getPaymentTime());
    final double finalIndexMonth0 = MARKET.getCurve(PRICE_INDEX_EUR).getPriceIndex(YEAR_ON_YEAR_NO.getReferenceEndTime()[0]);
    final double finalIndexMonth1 = MARKET.getCurve(PRICE_INDEX_EUR).getPriceIndex(YEAR_ON_YEAR_NO.getReferenceEndTime()[1]);
    final double initialIndexMonth0 = MARKET.getCurve(PRICE_INDEX_EUR).getPriceIndex(YEAR_ON_YEAR_NO.getReferenceStartTime()[0]);
    final double initialIndexMonth1 = MARKET.getCurve(PRICE_INDEX_EUR).getPriceIndex(YEAR_ON_YEAR_NO.getReferenceStartTime()[1]);
    final double initialIndex = YEAR_ON_YEAR_NO.getWeightStart() * initialIndexMonth0 + (1 - YEAR_ON_YEAR_NO.getWeightStart()) * initialIndexMonth1;
    final double finalIndex = YEAR_ON_YEAR_NO.getWeightEnd() * finalIndexMonth0 + (1 - YEAR_ON_YEAR_NO.getWeightEnd()) * finalIndexMonth1;
    final double pvExpected = (finalIndex / initialIndex - 1) * df * NOTIONAL;
    assertEquals("Year on year coupon inflation DiscountingMethod: Present value", pvExpected, pv.getAmount(YEAR_ON_YEAR_NO.getCurrency()), TOLERANCE_PV);
  }

  @Test
  /**
   * Tests the net amount.
   */
  public void netAmount() {
    final MultipleCurrencyAmount na = METHOD.netAmount(YEAR_ON_YEAR_NO, MARKET.getInflationProvider());
    final double finalIndexMonth0 = MARKET.getCurve(PRICE_INDEX_EUR).getPriceIndex(YEAR_ON_YEAR_NO.getReferenceEndTime()[0]);
    final double finalIndexMonth1 = MARKET.getCurve(PRICE_INDEX_EUR).getPriceIndex(YEAR_ON_YEAR_NO.getReferenceEndTime()[1]);
    final double initialIndexMonth0 = MARKET.getCurve(PRICE_INDEX_EUR).getPriceIndex(YEAR_ON_YEAR_NO.getReferenceStartTime()[0]);
    final double initialIndexMonth1 = MARKET.getCurve(PRICE_INDEX_EUR).getPriceIndex(YEAR_ON_YEAR_NO.getReferenceStartTime()[1]);
    final double initialIndex = YEAR_ON_YEAR_NO.getWeightStart() * initialIndexMonth0 + (1 - YEAR_ON_YEAR_NO.getWeightStart()) * initialIndexMonth1;
    final double finalIndex = YEAR_ON_YEAR_NO.getWeightEnd() * finalIndexMonth0 + (1 - YEAR_ON_YEAR_NO.getWeightEnd()) * finalIndexMonth1;
    final double naExpected = (finalIndex / initialIndex - 1) * NOTIONAL;
    assertEquals("Year on year coupon inflation DiscountingMethod: Net amount", naExpected, na.getAmount(YEAR_ON_YEAR_NO.getCurrency()), TOLERANCE_PV);
  }

  @Test
  /**
   * Tests the present value: Method vs Calculator.
   */
  public void presentValueMethodVsCalculator() {
    final MultipleCurrencyAmount pvMethod = METHOD.presentValue(YEAR_ON_YEAR_NO, MARKET.getInflationProvider());
    final MultipleCurrencyAmount pvCalculator = YEAR_ON_YEAR_NO.accept(PVIC, MARKET.getInflationProvider());
    assertEquals("Year on year coupon inflation DiscountingMethod: Present value", pvMethod, pvCalculator);
  }

  @Test
  /**
   * Tests the net amount: Method vs Calculator.
   */
  public void netAmountMethodVsCalculator() {
    final MultipleCurrencyAmount naMethod = METHOD.netAmount(YEAR_ON_YEAR_NO, MARKET.getInflationProvider());
    final MultipleCurrencyAmount naCalculator = YEAR_ON_YEAR_NO.accept(NAIC, MARKET.getInflationProvider());
    assertEquals("Year on year coupon inflation DiscountingMethod: Net amount", naMethod, naCalculator);
  }

  @Test
  /**
   * Tests the present value.
   */
  public void presentValueWithNotional() {
    final MultipleCurrencyAmount pv = METHOD.presentValue(YEAR_ON_YEAR_WITH, MARKET.getInflationProvider());
    final double df = MARKET.getCurve(YEAR_ON_YEAR_WITH.getCurrency()).getDiscountFactor(YEAR_ON_YEAR_WITH.getPaymentTime());
    final double finalIndexMonth0 = MARKET.getCurve(PRICE_INDEX_EUR).getPriceIndex(YEAR_ON_YEAR_WITH.getReferenceEndTime()[0]);
    final double finalIndexMonth1 = MARKET.getCurve(PRICE_INDEX_EUR).getPriceIndex(YEAR_ON_YEAR_WITH.getReferenceEndTime()[1]);
    final double initialIndexMonth0 = MARKET.getCurve(PRICE_INDEX_EUR).getPriceIndex(YEAR_ON_YEAR_WITH.getReferenceStartTime()[0]);
    final double initialIndexMonth1 = MARKET.getCurve(PRICE_INDEX_EUR).getPriceIndex(YEAR_ON_YEAR_WITH.getReferenceStartTime()[1]);
    final double initialIndex = YEAR_ON_YEAR_WITH.getWeightStart() * initialIndexMonth0 + (1 - YEAR_ON_YEAR_WITH.getWeightStart()) * initialIndexMonth1;
    final double finalIndex = YEAR_ON_YEAR_WITH.getWeightEnd() * finalIndexMonth0 + (1 - YEAR_ON_YEAR_WITH.getWeightEnd()) * finalIndexMonth1;
    final double pvExpected = (finalIndex / initialIndex) * df * NOTIONAL;
    assertEquals("Year on year coupon inflation DiscountingMethod: Present value", pvExpected, pv.getAmount(YEAR_ON_YEAR_WITH.getCurrency()), TOLERANCE_PV);
  }

  @Test
  /**
   * Tests the present value.
   */
  public void presentValueWithoutNotional() {
    final MultipleCurrencyAmount pv = METHOD.presentValue(YEAR_ON_YEAR_NO, MARKET.getInflationProvider());
    final double df = MARKET.getCurve(YEAR_ON_YEAR_WITH.getCurrency()).getDiscountFactor(YEAR_ON_YEAR_WITH.getPaymentTime());
    final double finalIndexMonth0 = MARKET.getCurve(PRICE_INDEX_EUR).getPriceIndex(YEAR_ON_YEAR_WITH.getReferenceEndTime()[0]);
    final double finalIndexMonth1 = MARKET.getCurve(PRICE_INDEX_EUR).getPriceIndex(YEAR_ON_YEAR_WITH.getReferenceEndTime()[1]);
    final double initialIndexMonth0 = MARKET.getCurve(PRICE_INDEX_EUR).getPriceIndex(YEAR_ON_YEAR_WITH.getReferenceStartTime()[0]);
    final double initialIndexMonth1 = MARKET.getCurve(PRICE_INDEX_EUR).getPriceIndex(YEAR_ON_YEAR_WITH.getReferenceStartTime()[1]);
    final double initialIndex = YEAR_ON_YEAR_WITH.getWeightStart() * initialIndexMonth0 + (1 - YEAR_ON_YEAR_WITH.getWeightStart()) * initialIndexMonth1;
    final double finalIndex = YEAR_ON_YEAR_WITH.getWeightEnd() * finalIndexMonth0 + (1 - YEAR_ON_YEAR_WITH.getWeightEnd()) * finalIndexMonth1;
    final double pvExpected = (finalIndex / initialIndex - 1) * df * NOTIONAL;
    assertEquals("Year on year coupon inflation DiscountingMethod: Present value", pvExpected, pv.getAmount(YEAR_ON_YEAR_WITH.getCurrency()), TOLERANCE_PV);
  }

  @Test
  /**
   * Test the present value curves sensitivity.
   */
  public void presentValueCurveSensitivityWithNotional() {

    final MultipleCurrencyParameterSensitivity pvicsFD = PS_PV_FDC.calculateSensitivity(YEAR_ON_YEAR_WITH, MARKET.getInflationProvider());
    final MultipleCurrencyParameterSensitivity pvicsExact = PSC.calculateSensitivity(YEAR_ON_YEAR_WITH, MARKET.getInflationProvider(), MARKET.getAllNames());

    AssertSensivityObjects.assertEquals("Year on year coupon inflation DiscountingMethod: presentValueCurveSensitivity ", pvicsExact, pvicsFD, TOLERANCE_PV_DELTA);

  }

  @Test
  /**
   * Test the present value curves sensitivity.
   */
  public void presentValueCurveSensitivityNoNotional() {

    final MultipleCurrencyParameterSensitivity pvicsFD = PS_PV_FDC.calculateSensitivity(YEAR_ON_YEAR_NO, MARKET.getInflationProvider());
    final MultipleCurrencyParameterSensitivity pvicsExact = PSC.calculateSensitivity(YEAR_ON_YEAR_NO, MARKET.getInflationProvider(), MARKET.getAllNames());

    AssertSensivityObjects.assertEquals("Year on year coupon inflation DiscountingMethod: presentValueCurveSensitivity ", pvicsExact, pvicsFD, TOLERANCE_PV_DELTA);

  }

  @Test
  public void presentValueMarketSensitivityMethodVsCalculatorNoNotional() {
    final MultipleCurrencyInflationSensitivity pvcisMethod = METHOD.presentValueCurveSensitivity(YEAR_ON_YEAR_NO, MARKET.getInflationProvider());
    final MultipleCurrencyInflationSensitivity pvcisCalculator = YEAR_ON_YEAR_NO.accept(PVCSDC, MARKET.getInflationProvider());
    AssertSensivityObjects.assertEquals("Year on year coupon inflation DiscountingMethod: presentValueMarketSensitivity", pvcisMethod, pvcisCalculator, TOLERANCE_PV_DELTA);
  }

  @Test
  public void presentValueMarketSensitivityMethodVsCalculatorWithNotional() {
    final MultipleCurrencyInflationSensitivity pvcisMethod = METHOD.presentValueCurveSensitivity(YEAR_ON_YEAR_WITH, MARKET.getInflationProvider());
    final MultipleCurrencyInflationSensitivity pvcisCalculator = YEAR_ON_YEAR_WITH.accept(PVCSDC, MARKET.getInflationProvider());
    AssertSensivityObjects.assertEquals("Year on year coupon inflation DiscountingMethod: presentValueMarketSensitivity", pvcisMethod, pvcisCalculator, TOLERANCE_PV_DELTA);
  }

}
