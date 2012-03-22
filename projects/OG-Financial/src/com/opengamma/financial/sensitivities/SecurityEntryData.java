/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.sensitivities;

import java.util.Map;

import javax.time.calendar.LocalDate;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.id.ExternalId;
import com.opengamma.util.money.Currency;

@BeanDefinition
public class SecurityEntryData extends DirectBean {
  public static final String SECURITY_SCHEME = "EXTERNAL_SENSITIVITIES_SECURITY";
  @PropertyDefinition
  private ExternalId _id;
  @PropertyDefinition
  private Currency _currency;
  @PropertyDefinition
  private LocalDate _maturityDate;
  @PropertyDefinition
  private ExternalId _factorSetId;
  
  public static final String EXTERNAL_SENSITIVITIES_SECURITY_TYPE = "EXTERNAL_SENSITIVITIES_SECURITY";
  
  public SecurityEntryData() {
  }
  
  public SecurityEntryData(ExternalId id, Currency currency, LocalDate  maturityDate, ExternalId factorSetId) {
    setId(id);
    setCurrency(currency);
    setMaturityDate(maturityDate);
    setFactorSetId(factorSetId);
  }
  
  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code SecurityEntryData}.
   * @return the meta-bean, not null
   */
  public static SecurityEntryData.Meta meta() {
    return SecurityEntryData.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(SecurityEntryData.Meta.INSTANCE);
  }

  @Override
  public SecurityEntryData.Meta metaBean() {
    return SecurityEntryData.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 3355:  // id
        return getId();
      case 575402001:  // currency
        return getCurrency();
      case -414641441:  // maturityDate
        return getMaturityDate();
      case 42976526:  // factorSetId
        return getFactorSetId();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 3355:  // id
        setId((ExternalId) newValue);
        return;
      case 575402001:  // currency
        setCurrency((Currency) newValue);
        return;
      case -414641441:  // maturityDate
        setMaturityDate((LocalDate) newValue);
        return;
      case 42976526:  // factorSetId
        setFactorSetId((ExternalId) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      SecurityEntryData other = (SecurityEntryData) obj;
      return JodaBeanUtils.equal(getId(), other.getId()) &&
          JodaBeanUtils.equal(getCurrency(), other.getCurrency()) &&
          JodaBeanUtils.equal(getMaturityDate(), other.getMaturityDate()) &&
          JodaBeanUtils.equal(getFactorSetId(), other.getFactorSetId());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getId());
    hash += hash * 31 + JodaBeanUtils.hashCode(getCurrency());
    hash += hash * 31 + JodaBeanUtils.hashCode(getMaturityDate());
    hash += hash * 31 + JodaBeanUtils.hashCode(getFactorSetId());
    return hash;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the id.
   * @return the value of the property
   */
  public ExternalId getId() {
    return _id;
  }

  /**
   * Sets the id.
   * @param id  the new value of the property
   */
  public void setId(ExternalId id) {
    this._id = id;
  }

  /**
   * Gets the the {@code id} property.
   * @return the property, not null
   */
  public final Property<ExternalId> id() {
    return metaBean().id().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the currency.
   * @return the value of the property
   */
  public Currency getCurrency() {
    return _currency;
  }

  /**
   * Sets the currency.
   * @param currency  the new value of the property
   */
  public void setCurrency(Currency currency) {
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
   * Gets the maturityDate.
   * @return the value of the property
   */
  public LocalDate getMaturityDate() {
    return _maturityDate;
  }

  /**
   * Sets the maturityDate.
   * @param maturityDate  the new value of the property
   */
  public void setMaturityDate(LocalDate maturityDate) {
    this._maturityDate = maturityDate;
  }

  /**
   * Gets the the {@code maturityDate} property.
   * @return the property, not null
   */
  public final Property<LocalDate> maturityDate() {
    return metaBean().maturityDate().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the factorSetId.
   * @return the value of the property
   */
  public ExternalId getFactorSetId() {
    return _factorSetId;
  }

  /**
   * Sets the factorSetId.
   * @param factorSetId  the new value of the property
   */
  public void setFactorSetId(ExternalId factorSetId) {
    this._factorSetId = factorSetId;
  }

  /**
   * Gets the the {@code factorSetId} property.
   * @return the property, not null
   */
  public final Property<ExternalId> factorSetId() {
    return metaBean().factorSetId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code SecurityEntryData}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code id} property.
     */
    private final MetaProperty<ExternalId> _id = DirectMetaProperty.ofReadWrite(
        this, "id", SecurityEntryData.class, ExternalId.class);
    /**
     * The meta-property for the {@code currency} property.
     */
    private final MetaProperty<Currency> _currency = DirectMetaProperty.ofReadWrite(
        this, "currency", SecurityEntryData.class, Currency.class);
    /**
     * The meta-property for the {@code maturityDate} property.
     */
    private final MetaProperty<LocalDate> _maturityDate = DirectMetaProperty.ofReadWrite(
        this, "maturityDate", SecurityEntryData.class, LocalDate.class);
    /**
     * The meta-property for the {@code factorSetId} property.
     */
    private final MetaProperty<ExternalId> _factorSetId = DirectMetaProperty.ofReadWrite(
        this, "factorSetId", SecurityEntryData.class, ExternalId.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "id",
        "currency",
        "maturityDate",
        "factorSetId");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3355:  // id
          return _id;
        case 575402001:  // currency
          return _currency;
        case -414641441:  // maturityDate
          return _maturityDate;
        case 42976526:  // factorSetId
          return _factorSetId;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends SecurityEntryData> builder() {
      return new DirectBeanBuilder<SecurityEntryData>(new SecurityEntryData());
    }

    @Override
    public Class<? extends SecurityEntryData> beanType() {
      return SecurityEntryData.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code id} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExternalId> id() {
      return _id;
    }

    /**
     * The meta-property for the {@code currency} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Currency> currency() {
      return _currency;
    }

    /**
     * The meta-property for the {@code maturityDate} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<LocalDate> maturityDate() {
      return _maturityDate;
    }

    /**
     * The meta-property for the {@code factorSetId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExternalId> factorSetId() {
      return _factorSetId;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
