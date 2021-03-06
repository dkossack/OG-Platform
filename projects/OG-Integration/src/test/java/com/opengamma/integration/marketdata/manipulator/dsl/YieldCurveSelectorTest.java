/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.integration.marketdata.manipulator.dsl;

import static org.mockito.Mockito.mock;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

import org.testng.annotations.Test;

import com.opengamma.core.marketdatasnapshot.YieldCurveKey;
import com.opengamma.engine.marketdata.manipulator.MarketDataSelector;
import com.opengamma.engine.marketdata.manipulator.SelectorResolver;
import com.opengamma.engine.marketdata.manipulator.StructureIdentifier;
import com.opengamma.util.money.Currency;

public class YieldCurveSelectorTest {

  private final SelectorResolver _resolver = mock(SelectorResolver.class);

  private static final Scenario SCENARIO = new Scenario("scenario");
  private static final String CALC_CONFIG_NAME = "calcConfigName";

  private static StructureIdentifier structureId(String curveName) {
    return StructureIdentifier.of(new YieldCurveKey(Currency.GBP, curveName));
  }

  /** if no criteria are specified the selector should match any curve */
  @Test
  public void noCriteria() {
    YieldCurveSelector.Builder curve = new YieldCurveSelector.Builder(SCENARIO);
    MarketDataSelector selector = curve.getSelector();
    assertEquals(selector, selector.findMatchingSelector(structureId("curveName1"), CALC_CONFIG_NAME, _resolver));
    assertEquals(selector, selector.findMatchingSelector(structureId("curveName2"), CALC_CONFIG_NAME, _resolver));
  }

  /** match a single name and fail any other names */
  @Test
  public void singleName() {
    String curveName = "curveName";
    String calcConfigName = "calcConfigName";
    YieldCurveSelector.Builder curve = new YieldCurveSelector.Builder(SCENARIO);
    curve.named(curveName);
    MarketDataSelector selector = curve.getSelector();
    assertEquals(selector, selector.findMatchingSelector(structureId(curveName), calcConfigName, _resolver));
    assertNull(selector.findMatchingSelector(structureId("otherName"), calcConfigName, _resolver));
  }

  /** match any one of multiple curve names, fail other names */
  @Test
  public void multipleNames() {
    String curveName1 = "curveName1";
    String curveName2 = "curveName2";
    YieldCurveSelector.Builder curve = new YieldCurveSelector.Builder(SCENARIO);
    curve.named(curveName1, curveName2);
    MarketDataSelector selector = curve.getSelector();
    assertEquals(selector, selector.findMatchingSelector(structureId(curveName1), CALC_CONFIG_NAME, _resolver));
    assertEquals(selector, selector.findMatchingSelector(structureId(curveName2), CALC_CONFIG_NAME, _resolver));
    assertNull(selector.findMatchingSelector(structureId("otherName"), CALC_CONFIG_NAME, _resolver));
  }

  /** don't match if the calc config name doesn't match */
  @Test
  public void calcConfigName() {
    YieldCurveSelector.Builder curve = new YieldCurveSelector.Builder(new Scenario("foo").calculationConfigurations(CALC_CONFIG_NAME));
    MarketDataSelector selector = curve.getSelector();
    assertNull(selector.findMatchingSelector(structureId("curveName"), "otherCalcConfigName", _resolver));
  }

  /** match if the curve name matches a regular expression */
  @Test
  public void nameMatches() {
    String curve3M = "curve3M";
    String curve6M = "curve6M";
    YieldCurveSelector.Builder curve = new YieldCurveSelector.Builder(SCENARIO);
    curve.nameMatches(".*3M");
    MarketDataSelector selector = curve.getSelector();
    assertEquals(selector, selector.findMatchingSelector(structureId(curve3M), CALC_CONFIG_NAME, _resolver));
    assertNull(selector.findMatchingSelector(structureId(curve6M), CALC_CONFIG_NAME, _resolver));
  }

  /** match if the curve name matches a glob */
  @Test
  public void nameLike() {
    String curve3M = "curve3M";
    String curve6M = "curve6M";

    YieldCurveSelector.Builder curve1 = new YieldCurveSelector.Builder(SCENARIO);
    curve1.nameLike("*3M");
    MarketDataSelector selector1 = curve1.getSelector();
    assertEquals(selector1, selector1.findMatchingSelector(structureId(curve3M), CALC_CONFIG_NAME, _resolver));
    assertNull(selector1.findMatchingSelector(structureId(curve6M), CALC_CONFIG_NAME, _resolver));

    YieldCurveSelector.Builder curve2 = new YieldCurveSelector.Builder(SCENARIO);
    curve2.nameLike("curve?M");
    MarketDataSelector selector2 = curve2.getSelector();
    assertEquals(selector2, selector2.findMatchingSelector(structureId(curve3M), CALC_CONFIG_NAME, _resolver));
    assertEquals(selector2, selector2.findMatchingSelector(structureId(curve6M), CALC_CONFIG_NAME, _resolver));
  }

  /** match if the curve currency is specified */
  @Test
  public void singleCurrency() {
    YieldCurveSelector.Builder curve = new YieldCurveSelector.Builder(SCENARIO);
    curve.currencies("GBP");
    MarketDataSelector selector = curve.getSelector();
    String curveName = "curveName";
    StructureIdentifier structure1 = StructureIdentifier.of(new YieldCurveKey(Currency.GBP, curveName));
    StructureIdentifier structure2 = StructureIdentifier.of(new YieldCurveKey(Currency.USD, curveName));
    assertEquals(selector, selector.findMatchingSelector(structure1, CALC_CONFIG_NAME, _resolver));
    assertNull(selector.findMatchingSelector(structure2, CALC_CONFIG_NAME, _resolver));
  }

  /** match if the curve currency matches any of the specified currency codes */
  @Test
  public void multipleCurrencies() {
    YieldCurveSelector.Builder curve = new YieldCurveSelector.Builder(SCENARIO);
    curve.currencies("GBP", "USD");
    MarketDataSelector selector = curve.getSelector();
    String curveName = "curveName";
    StructureIdentifier structure1 = StructureIdentifier.of(new YieldCurveKey(Currency.GBP, curveName));
    StructureIdentifier structure2 = StructureIdentifier.of(new YieldCurveKey(Currency.USD, curveName));
    StructureIdentifier structure3 = StructureIdentifier.of(new YieldCurveKey(Currency.AUD, curveName));
    assertEquals(selector, selector.findMatchingSelector(structure1, CALC_CONFIG_NAME, _resolver));
    assertEquals(selector, selector.findMatchingSelector(structure2, CALC_CONFIG_NAME, _resolver));
    assertNull(selector.findMatchingSelector(structure3, CALC_CONFIG_NAME, _resolver));
  }

  /** match if the curve satisfies all criteria, fail if it fails any of them */
  @Test
  public void multipleCriteria() {
    YieldCurveSelector.Builder curve = new YieldCurveSelector.Builder(SCENARIO);
    String curveName1 = "curveName1";
    String curveName2 = "curveName2";
    String curveName3 = "curveName3";
    curve.named(curveName1, curveName2).currencies("USD", "GBP");
    MarketDataSelector selector = curve.getSelector();
    StructureIdentifier structure1 = StructureIdentifier.of(new YieldCurveKey(Currency.GBP, curveName1));
    StructureIdentifier structure2 = StructureIdentifier.of(new YieldCurveKey(Currency.USD, curveName2));
    StructureIdentifier structure3 = StructureIdentifier.of(new YieldCurveKey(Currency.AUD, curveName1));
    StructureIdentifier structure4 = StructureIdentifier.of(new YieldCurveKey(Currency.USD, curveName3));
    assertEquals(selector, selector.findMatchingSelector(structure1, CALC_CONFIG_NAME, _resolver));
    assertEquals(selector, selector.findMatchingSelector(structure2, CALC_CONFIG_NAME, _resolver));
    assertNull(selector.findMatchingSelector(structure3, CALC_CONFIG_NAME, _resolver));
    assertNull(selector.findMatchingSelector(structure4, CALC_CONFIG_NAME, _resolver));
  }
}
