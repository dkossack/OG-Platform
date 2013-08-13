/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.option;

import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

/**
 * The fixed strike lookback payoff style.
 */
@BeanDefinition
public class FixedStrikeLookbackPayoffStyle extends PayoffStyle {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * Creates an instance.
   */
  public FixedStrikeLookbackPayoffStyle() {
  }

  //-------------------------------------------------------------------------
  @Override
  public <T> T accept(PayoffStyleVisitor<T> visitor) {
    return visitor.visitFixedStrikeLookbackPayoffStyle(this);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code FixedStrikeLookbackPayoffStyle}.
   * @return the meta-bean, not null
   */
  public static FixedStrikeLookbackPayoffStyle.Meta meta() {
    return FixedStrikeLookbackPayoffStyle.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(FixedStrikeLookbackPayoffStyle.Meta.INSTANCE);
  }

  @Override
  public FixedStrikeLookbackPayoffStyle.Meta metaBean() {
    return FixedStrikeLookbackPayoffStyle.Meta.INSTANCE;
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
   * The meta-bean for {@code FixedStrikeLookbackPayoffStyle}.
   */
  public static class Meta extends PayoffStyle.Meta {
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
    public BeanBuilder<? extends FixedStrikeLookbackPayoffStyle> builder() {
      return new DirectBeanBuilder<FixedStrikeLookbackPayoffStyle>(new FixedStrikeLookbackPayoffStyle());
    }

    @Override
    public Class<? extends FixedStrikeLookbackPayoffStyle> beanType() {
      return FixedStrikeLookbackPayoffStyle.class;
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