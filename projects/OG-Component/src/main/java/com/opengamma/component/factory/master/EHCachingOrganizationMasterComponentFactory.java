/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.component.factory.master;

import java.util.LinkedHashMap;
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

import com.opengamma.component.ComponentInfo;
import com.opengamma.component.ComponentRepository;
import com.opengamma.component.factory.AbstractComponentFactory;
import com.opengamma.component.factory.ComponentInfoAttributes;
import com.opengamma.master.orgs.OrganisationMaster;
import com.opengamma.master.orgs.impl.DataOrganisationMasterResource;
import com.opengamma.master.orgs.impl.EHCachingOrganisationMaster;
import com.opengamma.master.orgs.impl.RemoteOrganisationMaster;
import com.opengamma.util.ehcache.EHCacheUtils;

/**
 * Component factory for the combined organization master.
 * <p>
 * This factory creates a combined organization master from an underlying and user master.
 */
@BeanDefinition
public class EHCachingOrganizationMasterComponentFactory extends AbstractComponentFactory {

  /**
   * The classifier that the factory should publish under.
   */
  @PropertyDefinition(validate = "notNull")
  private String _classifier;
  /**
   * The flag determining whether the component should be published by REST (default true).
   */
  @PropertyDefinition
  private boolean _publishRest = true;
  /**
   * The underlying organization master.
   */
  @PropertyDefinition(validate = "notNull")
  private OrganisationMaster _underlying;


  //-------------------------------------------------------------------------
  @Override
  public void init(ComponentRepository repo, LinkedHashMap<String, String> organizationuration) {

    OrganisationMaster master = new EHCachingOrganisationMaster(getClassifier(),
                                                      _underlying,
                                                      EHCacheUtils.createCacheManager()); // actually retrieves existing

    // register
    ComponentInfo info = new ComponentInfo(OrganisationMaster.class, getClassifier());
    info.addAttribute(ComponentInfoAttributes.LEVEL, 2);
    info.addAttribute(ComponentInfoAttributes.REMOTE_CLIENT_JAVA, RemoteOrganisationMaster.class);
    repo.registerComponent(info, master);
    if (isPublishRest()) {
      repo.getRestComponents().publish(info, new DataOrganisationMasterResource(master));
    }
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code EHCachingOrganizationMasterComponentFactory}.
   * @return the meta-bean, not null
   */
  public static EHCachingOrganizationMasterComponentFactory.Meta meta() {
    return EHCachingOrganizationMasterComponentFactory.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(EHCachingOrganizationMasterComponentFactory.Meta.INSTANCE);
  }

  @Override
  public EHCachingOrganizationMasterComponentFactory.Meta metaBean() {
    return EHCachingOrganizationMasterComponentFactory.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -281470431:  // classifier
        return getClassifier();
      case -614707837:  // publishRest
        return isPublishRest();
      case -1770633379:  // underlying
        return getUnderlying();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -281470431:  // classifier
        setClassifier((String) newValue);
        return;
      case -614707837:  // publishRest
        setPublishRest((Boolean) newValue);
        return;
      case -1770633379:  // underlying
        setUnderlying((OrganisationMaster) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  protected void validate() {
    JodaBeanUtils.notNull(_classifier, "classifier");
    JodaBeanUtils.notNull(_underlying, "underlying");
    super.validate();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      EHCachingOrganizationMasterComponentFactory other = (EHCachingOrganizationMasterComponentFactory) obj;
      return JodaBeanUtils.equal(getClassifier(), other.getClassifier()) &&
          JodaBeanUtils.equal(isPublishRest(), other.isPublishRest()) &&
          JodaBeanUtils.equal(getUnderlying(), other.getUnderlying()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getClassifier());
    hash += hash * 31 + JodaBeanUtils.hashCode(isPublishRest());
    hash += hash * 31 + JodaBeanUtils.hashCode(getUnderlying());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the classifier that the factory should publish under.
   * @return the value of the property, not null
   */
  public String getClassifier() {
    return _classifier;
  }

  /**
   * Sets the classifier that the factory should publish under.
   * @param classifier  the new value of the property, not null
   */
  public void setClassifier(String classifier) {
    JodaBeanUtils.notNull(classifier, "classifier");
    this._classifier = classifier;
  }

  /**
   * Gets the the {@code classifier} property.
   * @return the property, not null
   */
  public final Property<String> classifier() {
    return metaBean().classifier().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the flag determining whether the component should be published by REST (default true).
   * @return the value of the property
   */
  public boolean isPublishRest() {
    return _publishRest;
  }

  /**
   * Sets the flag determining whether the component should be published by REST (default true).
   * @param publishRest  the new value of the property
   */
  public void setPublishRest(boolean publishRest) {
    this._publishRest = publishRest;
  }

  /**
   * Gets the the {@code publishRest} property.
   * @return the property, not null
   */
  public final Property<Boolean> publishRest() {
    return metaBean().publishRest().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the underlying organization master.
   * @return the value of the property, not null
   */
  public OrganisationMaster getUnderlying() {
    return _underlying;
  }

  /**
   * Sets the underlying organization master.
   * @param underlying  the new value of the property, not null
   */
  public void setUnderlying(OrganisationMaster underlying) {
    JodaBeanUtils.notNull(underlying, "underlying");
    this._underlying = underlying;
  }

  /**
   * Gets the the {@code underlying} property.
   * @return the property, not null
   */
  public final Property<OrganisationMaster> underlying() {
    return metaBean().underlying().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code EHCachingOrganizationMasterComponentFactory}.
   */
  public static class Meta extends AbstractComponentFactory.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code classifier} property.
     */
    private final MetaProperty<String> _classifier = DirectMetaProperty.ofReadWrite(
        this, "classifier", EHCachingOrganizationMasterComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code publishRest} property.
     */
    private final MetaProperty<Boolean> _publishRest = DirectMetaProperty.ofReadWrite(
        this, "publishRest", EHCachingOrganizationMasterComponentFactory.class, Boolean.TYPE);
    /**
     * The meta-property for the {@code underlying} property.
     */
    private final MetaProperty<OrganisationMaster> _underlying = DirectMetaProperty.ofReadWrite(
        this, "underlying", EHCachingOrganizationMasterComponentFactory.class, OrganisationMaster.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "classifier",
        "publishRest",
        "underlying");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -281470431:  // classifier
          return _classifier;
        case -614707837:  // publishRest
          return _publishRest;
        case -1770633379:  // underlying
          return _underlying;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends EHCachingOrganizationMasterComponentFactory> builder() {
      return new DirectBeanBuilder<EHCachingOrganizationMasterComponentFactory>(new EHCachingOrganizationMasterComponentFactory());
    }

    @Override
    public Class<? extends EHCachingOrganizationMasterComponentFactory> beanType() {
      return EHCachingOrganizationMasterComponentFactory.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code classifier} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> classifier() {
      return _classifier;
    }

    /**
     * The meta-property for the {@code publishRest} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Boolean> publishRest() {
      return _publishRest;
    }

    /**
     * The meta-property for the {@code underlying} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<OrganisationMaster> underlying() {
      return _underlying;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
