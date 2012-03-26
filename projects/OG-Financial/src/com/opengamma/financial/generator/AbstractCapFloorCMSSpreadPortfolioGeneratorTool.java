/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.generator;

import com.opengamma.financial.security.capfloor.CapFloorCMSSpreadSecurity;

/**
 * Utility for constructing a random Cap/Floor CMS spread portfolio.
 */
public abstract class AbstractCapFloorCMSSpreadPortfolioGeneratorTool extends AbstractPortfolioGeneratorTool {

  protected abstract AbstractCapFloorCMSSpreadSecurityGenerator createCapFloorCMSSpreadSecurityGenerator();

  @Override
  public PortfolioNodeGenerator createPortfolioNodeGenerator(final int size) {
    final AbstractCapFloorCMSSpreadSecurityGenerator securities = createCapFloorCMSSpreadSecurityGenerator();
    configure(securities);
    final PositionGenerator positions = new SimplePositionGenerator<CapFloorCMSSpreadSecurity>(securities, getSecurityPersister());
    return new LeafPortfolioNodeGenerator(new StaticNameGenerator("Cap/Floor CMS Spread"), positions, size);
  }

}
