/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.swap;

import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.util.money.Currency;

/**
 * A notional that holds the security used as a notional, such as an index or equity.
 */
@BeanDefinition
public class InterestRateNotional extends Notional {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * The currency.
   */
  @PropertyDefinition(validate = "notNull")
  private Currency _currency;
  /**
   * The amount.
   */
  @PropertyDefinition
  private double _amount;

  /**
   * Creates an instance.
   */
  private InterestRateNotional() {
  }

  /**
   * Creates an instance.
   * 
   * @param currency  the currency, not null
   * @param amount  the amount
   */
  public InterestRateNotional(Currency currency, double amount) {
    setCurrency(currency);
    setAmount(amount);
  }

  //-------------------------------------------------------------------------
  @Override
  public <T> T accept(NotionalVisitor<T> visitor) {
    return visitor.visitInterestRateNotional(this);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code InterestRateNotional}.
   * @return the meta-bean, not null
   */
  public static InterestRateNotional.Meta meta() {
    return InterestRateNotional.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(InterestRateNotional.Meta.INSTANCE);
  }

  @Override
  public InterestRateNotional.Meta metaBean() {
    return InterestRateNotional.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 575402001:  // currency
        return getCurrency();
      case -1413853096:  // amount
        return getAmount();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 575402001:  // currency
        setCurrency((Currency) newValue);
        return;
      case -1413853096:  // amount
        setAmount((Double) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  protected void validate() {
    JodaBeanUtils.notNull(_currency, "currency");
    super.validate();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      InterestRateNotional other = (InterestRateNotional) obj;
      return JodaBeanUtils.equal(getCurrency(), other.getCurrency()) &&
          JodaBeanUtils.equal(getAmount(), other.getAmount()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getCurrency());
    hash += hash * 31 + JodaBeanUtils.hashCode(getAmount());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the currency.
   * @return the value of the property, not null
   */
  public Currency getCurrency() {
    return _currency;
  }

  /**
   * Sets the currency.
   * @param currency  the new value of the property, not null
   */
  public void setCurrency(Currency currency) {
    JodaBeanUtils.notNull(currency, "currency");
    this._currency = currency;
  }

  /**
   * Gets the the {@code currency} property.
   * @return the property, not null
   */
  public final Property<Currency> currency() {
    return metaBean().currency().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the amount.
   * @return the value of the property
   */
  public double getAmount() {
    return _amount;
  }

  /**
   * Sets the amount.
   * @param amount  the new value of the property
   */
  public void setAmount(double amount) {
    this._amount = amount;
  }

  /**
   * Gets the the {@code amount} property.
   * @return the property, not null
   */
  public final Property<Double> amount() {
    return metaBean().amount().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code InterestRateNotional}.
   */
  public static class Meta extends Notional.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code currency} property.
     */
    private final MetaProperty<Currency> _currency = DirectMetaProperty.ofReadWrite(
        this, "currency", InterestRateNotional.class, Currency.class);
    /**
     * The meta-property for the {@code amount} property.
     */
    private final MetaProperty<Double> _amount = DirectMetaProperty.ofReadWrite(
        this, "amount", InterestRateNotional.class, Double.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
      this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "currency",
        "amount");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 575402001:  // currency
          return _currency;
        case -1413853096:  // amount
          return _amount;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends InterestRateNotional> builder() {
      return new DirectBeanBuilder<InterestRateNotional>(new InterestRateNotional());
    }

    @Override
    public Class<? extends InterestRateNotional> beanType() {
      return InterestRateNotional.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code currency} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Currency> currency() {
      return _currency;
    }

    /**
     * The meta-property for the {@code amount} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Double> amount() {
      return _amount;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
