/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.component.factory.infrastructure;


import com.opengamma.component.ComponentInfo;
import com.opengamma.component.ComponentRepository;
import com.opengamma.component.factory.AbstractComponentFactory;
import com.opengamma.util.jms.JmsConnector;
import com.opengamma.util.jms.JmsConnectorFactoryBean;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import javax.jms.ConnectionFactory;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Component Factory for a shared JmsConnector.
 *
 * A client broker URI must be specified
 *
 * If no ConnectionFactory is provided, it will default to pooled ActiveMQ implementation with some sensible defaults.
 *
 * This class can be inherited from and protected methods overriden if necessary.
 *
 */
@BeanDefinition
public class JMSConnectorComponentFactory extends AbstractComponentFactory {

  @PropertyDefinition(validate = "notNull")
  private String _classifier;

  @PropertyDefinition(validate = "notNull")
  private String _clientBrokerUri;

  @PropertyDefinition
  private ConnectionFactory _connectionFactory;

  @Override
  public void init(ComponentRepository repo, LinkedHashMap<String, String> configuration) throws Exception {

    final ComponentInfo info = new ComponentInfo(JmsConnector.class, getClassifier());
    final JmsConnector component = initJmsConnector();
    repo.registerComponent(info, component);

  }

  protected JmsConnector initJmsConnector() throws Exception {

    initDefaults();

    JmsConnectorFactoryBean factoryBean = new JmsConnectorFactoryBean();
    factoryBean.setName("StandardJms");
    factoryBean.setConnectionFactory(getConnectionFactory());
    factoryBean.setClientBrokerUri(new URI(getClientBrokerUri()));
    factoryBean.afterPropertiesSet();

    return factoryBean.getObjectCreating();

  }

  protected void initDefaults() {
    if (getConnectionFactory() == null) {
      setConnectionFactory(defaultToActiveMQConnectionFactory());
    }
  }

  protected ActiveMQConnectionFactory initActiveMQConnectionFactory() {
    final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(getClientBrokerUri());
    connectionFactory.setWatchTopicAdvisories(false);
    return connectionFactory;
  }

  protected PooledConnectionFactory initPooledConnectionFactory() {
    return new PooledConnectionFactory(initActiveMQConnectionFactory());
  }

  protected ConnectionFactory defaultToActiveMQConnectionFactory() {
    return initPooledConnectionFactory();
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code JMSConnectorComponentFactory}.
   * @return the meta-bean, not null
   */
  public static JMSConnectorComponentFactory.Meta meta() {
    return JMSConnectorComponentFactory.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(JMSConnectorComponentFactory.Meta.INSTANCE);
  }

  @Override
  public JMSConnectorComponentFactory.Meta metaBean() {
    return JMSConnectorComponentFactory.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -281470431:  // classifier
        return getClassifier();
      case -1176216760:  // clientBrokerUri
        return getClientBrokerUri();
      case 1966765132:  // connectionFactory
        return getConnectionFactory();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -281470431:  // classifier
        setClassifier((String) newValue);
        return;
      case -1176216760:  // clientBrokerUri
        setClientBrokerUri((String) newValue);
        return;
      case 1966765132:  // connectionFactory
        setConnectionFactory((ConnectionFactory) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  protected void validate() {
    JodaBeanUtils.notNull(_classifier, "classifier");
    JodaBeanUtils.notNull(_clientBrokerUri, "clientBrokerUri");
    super.validate();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      JMSConnectorComponentFactory other = (JMSConnectorComponentFactory) obj;
      return JodaBeanUtils.equal(getClassifier(), other.getClassifier()) &&
          JodaBeanUtils.equal(getClientBrokerUri(), other.getClientBrokerUri()) &&
          JodaBeanUtils.equal(getConnectionFactory(), other.getConnectionFactory()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getClassifier());
    hash += hash * 31 + JodaBeanUtils.hashCode(getClientBrokerUri());
    hash += hash * 31 + JodaBeanUtils.hashCode(getConnectionFactory());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the classifier.
   * @return the value of the property, not null
   */
  public String getClassifier() {
    return _classifier;
  }

  /**
   * Sets the classifier.
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
   * Gets the clientBrokerUri.
   * @return the value of the property, not null
   */
  public String getClientBrokerUri() {
    return _clientBrokerUri;
  }

  /**
   * Sets the clientBrokerUri.
   * @param clientBrokerUri  the new value of the property, not null
   */
  public void setClientBrokerUri(String clientBrokerUri) {
    JodaBeanUtils.notNull(clientBrokerUri, "clientBrokerUri");
    this._clientBrokerUri = clientBrokerUri;
  }

  /**
   * Gets the the {@code clientBrokerUri} property.
   * @return the property, not null
   */
  public final Property<String> clientBrokerUri() {
    return metaBean().clientBrokerUri().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the connectionFactory.
   * @return the value of the property
   */
  public ConnectionFactory getConnectionFactory() {
    return _connectionFactory;
  }

  /**
   * Sets the connectionFactory.
   * @param connectionFactory  the new value of the property
   */
  public void setConnectionFactory(ConnectionFactory connectionFactory) {
    this._connectionFactory = connectionFactory;
  }

  /**
   * Gets the the {@code connectionFactory} property.
   * @return the property, not null
   */
  public final Property<ConnectionFactory> connectionFactory() {
    return metaBean().connectionFactory().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code JMSConnectorComponentFactory}.
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
        this, "classifier", JMSConnectorComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code clientBrokerUri} property.
     */
    private final MetaProperty<String> _clientBrokerUri = DirectMetaProperty.ofReadWrite(
        this, "clientBrokerUri", JMSConnectorComponentFactory.class, String.class);
    /**
     * The meta-property for the {@code connectionFactory} property.
     */
    private final MetaProperty<ConnectionFactory> _connectionFactory = DirectMetaProperty.ofReadWrite(
        this, "connectionFactory", JMSConnectorComponentFactory.class, ConnectionFactory.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "classifier",
        "clientBrokerUri",
        "connectionFactory");

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
        case -1176216760:  // clientBrokerUri
          return _clientBrokerUri;
        case 1966765132:  // connectionFactory
          return _connectionFactory;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends JMSConnectorComponentFactory> builder() {
      return new DirectBeanBuilder<JMSConnectorComponentFactory>(new JMSConnectorComponentFactory());
    }

    @Override
    public Class<? extends JMSConnectorComponentFactory> beanType() {
      return JMSConnectorComponentFactory.class;
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
     * The meta-property for the {@code clientBrokerUri} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> clientBrokerUri() {
      return _clientBrokerUri;
    }

    /**
     * The meta-property for the {@code connectionFactory} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ConnectionFactory> connectionFactory() {
      return _connectionFactory;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
