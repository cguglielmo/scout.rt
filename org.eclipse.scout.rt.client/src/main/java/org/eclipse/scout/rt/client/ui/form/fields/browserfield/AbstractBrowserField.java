/*******************************************************************************
 * Copyright (c) 2010 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.rt.client.ui.form.fields.browserfield;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.scout.commons.annotations.ClassId;
import org.eclipse.scout.commons.annotations.ConfigOperation;
import org.eclipse.scout.commons.annotations.ConfigProperty;
import org.eclipse.scout.commons.annotations.Order;
import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.commons.logger.IScoutLogger;
import org.eclipse.scout.commons.logger.ScoutLogManager;
import org.eclipse.scout.commons.resource.BinaryResource;
import org.eclipse.scout.rt.client.extension.ui.form.fields.IFormFieldExtension;
import org.eclipse.scout.rt.client.extension.ui.form.fields.browserfield.BrowserFieldChains.BrowserFieldAcceptLocationChangeChain;
import org.eclipse.scout.rt.client.extension.ui.form.fields.browserfield.BrowserFieldChains.BrowserFieldLocationChangedChain;
import org.eclipse.scout.rt.client.extension.ui.form.fields.browserfield.IBrowserFieldExtension;
import org.eclipse.scout.rt.client.ui.form.fields.AbstractFormField;
import org.eclipse.scout.rt.client.ui.form.fields.AbstractValueField;
import org.eclipse.scout.rt.shared.services.common.file.RemoteFile;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

// FIXME AWE: change value of BrowserField to URL. When we call setBinaryLocation() on the BrowserField
// we must:
// 1. create a download-URL for that resource
// 2. set the location (=value) to that download-URL
//
// When we change the location (=value) of the BrowserField we must also set the binaryResource to null.
// Talk to SME and PBN for more infos

@ClassId("6402e68c-abd1-42b8-8da2-b4a12f910c98")
public abstract class AbstractBrowserField extends AbstractValueField<RemoteFile> implements IBrowserField {
  private static final IScoutLogger LOG = ScoutLogManager.getLogger(AbstractBrowserField.class);

  private IBrowserFieldUIFacade m_uiFacade;

  public AbstractBrowserField() {
    this(true);
  }

  public AbstractBrowserField(boolean callInitializer) {
    super(callInitializer);
  }

  @ConfigProperty(ConfigProperty.BOOLEAN)
  @Order(250)
  protected boolean getConfiguredScrollBarEnabled() {
    return false;
  }

  @Override
  @Order(210)
  @ConfigProperty(ConfigProperty.BOOLEAN)
  protected boolean getConfiguredAutoAddDefaultMenus() {
    return false;
  }

  @Order(220)
  @ConfigProperty(ConfigProperty.OBJECT)
  protected Set<SandboxValues> getConfiguredSandbox() {
    return fullSandbox();
  }

  /**
   * This callback is invoked before the link is followed, it can be used as handler and vetoer. The default returns
   * true.<br>
   *
   * @return true to accept this location, false to prevent the browser from going to that location (equal to browser
   *         esc/stop button)
   * @param location
   * @param path
   *          may be null for locations like about:blank or javascript:... {@link URL#getPath()}
   * @param local
   *          true if the url is not a valid external url but a local model url
   *          (http://local/...)
   */
  @ConfigOperation
  @Order(230)
  protected boolean execAcceptLocationChange(String location, String path, boolean local) throws ProcessingException {
    return true;
  }

  /**
   * This callback is invoked after the link was followed, thus it is already at that location
   * <p>
   * The default does noting.
   *
   * @param location
   * @param path
   *          may be null for locations like about:blank or javascript:... {@link URL#getPath()}
   * @param local
   *          true if the url is not a valid external url but a local model url
   *          (http://local/...)
   */
  @ConfigOperation
  @Order(230)
  protected void execLocationChanged(String location, String path, boolean local) throws ProcessingException {
  }

  @Override
  protected void initConfig() {
    m_uiFacade = new P_UIFacade();
    super.initConfig();
    setScrollBarEnabled(getConfiguredScrollBarEnabled());
    setSandbox(getConfiguredSandbox());
  }

  @Override
  protected void applySearchInternal(SearchFilter search) {
    //nop
  }

  @Override
  public void doLocationChange(String location) throws ProcessingException {
    if (getUIFacade().fireBeforeLocationChangedFromUI(location)) {
      getUIFacade().fireAfterLocationChangedFromUI(location);
    }
  }

  @Override
  public void setLocation(String location) {
    propertySupport.setProperty(PROP_LOCATION, location);
  }

  @Override
  public String getLocation() {
    return (String) propertySupport.getProperty(PROP_LOCATION);
  }

  @Override
  public IBrowserFieldUIFacade getUIFacade() {
    return m_uiFacade;
  }

  protected void setScrollBarEnabled(boolean scrollBarEnabled) {
    propertySupport.setProperty(PROP_SCROLLBARS_ENABLED, scrollBarEnabled);
  }

  @Override
  public boolean isScrollBarEnabled() {
    return propertySupport.getPropertyBool(PROP_SCROLLBARS_ENABLED);
  }

  private class P_UIFacade implements IBrowserFieldUIFacade {

    @Override
    public boolean fireBeforeLocationChangedFromUI(String location) {
      try {
        URL url = null;
        try {
          url = new URL(location);
        }
        catch (Exception t) {
          //nop
        }
        return interceptAcceptLocationChange(location, url != null ? url.getPath() : null, url != null && "local".equals(url.getHost()));
      }
      catch (Exception t) {
        LOG.error("location: " + location, t);
      }
      return false;
    }

    @Override
    public void fireAfterLocationChangedFromUI(String location) {
      try {
        URL url = null;
        try {
          url = new URL(location);
        }
        catch (Exception t) {
          //nop
        }
        interceptLocationChanged(location, url != null ? url.getPath() : null, url != null && "local".equals(url.getHost()));
      }
      catch (Exception t) {
        LOG.error("location: " + location, t);
      }
    }
  }

  protected final void interceptLocationChanged(String location, String path, boolean local) throws ProcessingException {
    List<? extends IFormFieldExtension<? extends AbstractFormField>> extensions = getAllExtensions();
    BrowserFieldLocationChangedChain chain = new BrowserFieldLocationChangedChain(extensions);
    chain.execLocationChanged(location, path, local);
  }

  protected final boolean interceptAcceptLocationChange(String location, String path, boolean local) throws ProcessingException {
    List<? extends IFormFieldExtension<? extends AbstractFormField>> extensions = getAllExtensions();
    BrowserFieldAcceptLocationChangeChain chain = new BrowserFieldAcceptLocationChangeChain(extensions);
    return chain.execAcceptLocationChange(location, path, local);
  }

  protected static class LocalBrowserFieldExtension<OWNER extends AbstractBrowserField> extends LocalValueFieldExtension<RemoteFile, OWNER> implements IBrowserFieldExtension<OWNER> {

    public LocalBrowserFieldExtension(OWNER owner) {
      super(owner);
    }

    @Override
    public void execLocationChanged(BrowserFieldLocationChangedChain chain, String location, String path, boolean local) throws ProcessingException {
      getOwner().execLocationChanged(location, path, local);
    }

    @Override
    public boolean execAcceptLocationChange(BrowserFieldAcceptLocationChangeChain chain, String location, String path, boolean local) throws ProcessingException {
      return getOwner().execAcceptLocationChange(location, path, local);
    }
  }

  @Override
  protected IBrowserFieldExtension<? extends AbstractBrowserField> createLocalExtension() {
    return new LocalBrowserFieldExtension<AbstractBrowserField>(this);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Set<SandboxValues> getSandbox() {
    return (Set<SandboxValues>) propertySupport.getProperty(PROP_SANDBOX);
  }

  @Override
  public void setSandbox(Set<SandboxValues> sandboxValues) {
    propertySupport.setProperty(PROP_SANDBOX, sandboxValues);
  }

  protected Set<SandboxValues> fullSandbox() {
    Set<SandboxValues> set = new HashSet<>();
    for (SandboxValues sandboxValue : SandboxValues.values()) {
      set.add(sandboxValue);
    }
    return set;
  }

  @Override
  public BinaryResource getBinaryResource() {
    return (BinaryResource) propertySupport.getProperty(PROP_BINARY_RESOURCE);
  }

  @Override
  public void setBinaryResource(BinaryResource binaryResource) {
    propertySupport.setProperty(PROP_BINARY_RESOURCE, binaryResource);
  }

}
