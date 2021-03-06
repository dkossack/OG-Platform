/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.provider.description.interestrate;

import com.opengamma.analytics.financial.model.interestrate.definition.HullWhiteOneFactorPiecewiseConstantParameters;
import com.opengamma.util.money.Currency;
import com.opengamma.util.tuple.Pair;

/**
 * Interface for Hull-White parameters provider for one issuer/currency.
 */
public interface HullWhiteIssuerProviderInterface extends ParameterIssuerProviderInterface {

  /**
   * Create a new copy of the provider.
   * @return The bundle.
   */
  @Override
  HullWhiteIssuerProviderInterface copy();

  /**
   * Returns the Hull-White one factor model parameters.
   * @return The parameters.
   */
  HullWhiteOneFactorPiecewiseConstantParameters getHullWhiteParameters();

  /**
   * Returns the issuer/currency for which the Hull-White parameters are valid (Hull-White on the discounting curve).
   * @return The issuer/currency.
   */
  Pair<String, Currency> getHullWhiteIssuerCurrency();

}
