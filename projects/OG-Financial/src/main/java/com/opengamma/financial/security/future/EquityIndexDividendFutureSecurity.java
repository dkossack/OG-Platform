/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.future;

import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.threeten.bp.ZonedDateTime;

import com.opengamma.financial.security.FinancialSecurityVisitor;
import com.opengamma.id.ExternalId;
import com.opengamma.util.money.Currency;
import com.opengamma.util.time.Expiry;

/**
 * A security for equity index dividend futures.
 */
@BeanDefinition
public class EquityIndexDividendFutureSecurity extends EquityFutureSecurity {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  EquityIndexDividendFutureSecurity() { //For builder
    super();
  }

  public EquityIndexDividendFutureSecurity(Expiry expiry, String tradingExchange, String settlementExchange, Currency currency, double unitAmount,
      ZonedDateTime settlementDate, ExternalId underlyingIdentifier, String category) {
    super(expiry, tradingExchange, settlementExchange, currency, unitAmount, settlementDate, underlyingIdentifier, category);
  }

  //-------------------------------------------------------------------------
  @Override
  public <T> T accept(FinancialSecurityVisitor<T> visitor) {
    return visitor.visitEquityIndexDividendFutureSecurity(this);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code EquityIndexDividendFutureSecurity}.
   * @return the meta-bean, not null
   */
  public static EquityIndexDividendFutureSecurity.Meta meta() {
    return EquityIndexDividendFutureSecurity.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(EquityIndexDividendFutureSecurity.Meta.INSTANCE);
  }

  @Override
  public EquityIndexDividendFutureSecurity.Meta metaBean() {
    return EquityIndexDividendFutureSecurity.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      return super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code EquityIndexDividendFutureSecurity}.
   */
  public static class Meta extends EquityFutureSecurity.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap());

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    public BeanBuilder<? extends EquityIndexDividendFutureSecurity> builder() {
      return new DirectBeanBuilder<EquityIndexDividendFutureSecurity>(new EquityIndexDividendFutureSecurity());
    }

    @Override
    public Class<? extends EquityIndexDividendFutureSecurity> beanType() {
      return EquityIndexDividendFutureSecurity.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}