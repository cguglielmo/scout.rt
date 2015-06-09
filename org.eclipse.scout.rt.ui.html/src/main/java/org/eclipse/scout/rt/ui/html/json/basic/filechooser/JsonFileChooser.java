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
package org.eclipse.scout.rt.ui.html.json.basic.filechooser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.scout.commons.FileUtility;
import org.eclipse.scout.commons.resource.BinaryResource;
import org.eclipse.scout.rt.client.ui.basic.filechooser.FileChooserEvent;
import org.eclipse.scout.rt.client.ui.basic.filechooser.FileChooserListener;
import org.eclipse.scout.rt.client.ui.basic.filechooser.IFileChooser;
import org.eclipse.scout.rt.ui.html.IUiSession;
import org.eclipse.scout.rt.ui.html.json.AbstractJsonAdapter;
import org.eclipse.scout.rt.ui.html.json.IJsonAdapter;
import org.eclipse.scout.rt.ui.html.json.JsonEvent;
import org.eclipse.scout.rt.ui.html.res.IBinaryResourceConsumer;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonFileChooser<T extends IFileChooser> extends AbstractJsonAdapter<T> implements IBinaryResourceConsumer {

  private static final String EVENT_CLOSED = "closed";
  // UI events
  private static final String EVENT_CANCEL = "cancel";

  private FileChooserListener m_fileChooserListener;

  public JsonFileChooser(T model, IUiSession uiSession, String id, IJsonAdapter<?> parent) {
    super(model, uiSession, id, parent);
  }

  @Override
  public String getObjectType() {
    return "FileChooser";
  }

  @Override
  protected void attachModel() {
    super.attachModel();
    if (m_fileChooserListener != null) {
      throw new IllegalStateException();
    }
    m_fileChooserListener = new P_FileChooserListener();
    getModel().addFileChooserListener(m_fileChooserListener);
  }

  @Override
  protected void detachModel() {
    super.detachModel();
    if (m_fileChooserListener == null) {
      throw new IllegalStateException();
    }
    getModel().removeFileChooserListener(m_fileChooserListener);
    m_fileChooserListener = null;
  }

  @Override
  public JSONObject toJson() {
    JSONObject json = super.toJson();
    putProperty(json, "multiSelect", getModel().isMultiSelect());
    putProperty(json, "contentTypes", new JSONArray(getFileExtensionsAsContentTypes()));
    return json;
  }

  protected Set<String> getFileExtensionsAsContentTypes() {
    Set<String> contentTypes = new HashSet<>();
    for (String extension : getModel().getFileExtensions()) {
      String contentType = FileUtility.getContentTypeForExtension(extension);
      if (contentType != null) {
        contentTypes.add(contentType);
      }
    }
    return contentTypes;
  }

  protected void handleModelFileChooserEvent(FileChooserEvent event) {
    switch (event.getType()) {
      case FileChooserEvent.TYPE_CLOSED:
        handleModelClosed();
        break;
      default:
        // NOP
    }
  }

  protected void handleModelClosed() {
    dispose();
    addActionEvent(EVENT_CLOSED);
  }

  @Override
  public void handleUiEvent(JsonEvent event) {
    if (EVENT_CANCEL.equals(event.getType())) {
      handleUiCancel(event);
    }
    else {
      super.handleUiEvent(event);
    }
  }

  protected void handleUiCancel(JsonEvent event) {
    getModel().getUIFacade().setResultFromUI(null);
  }

  @Override
  public void consumeBinaryResource(List<BinaryResource> binaryResources) {
    getModel().getUIFacade().setResultFromUI(binaryResources);
  }

  private class P_FileChooserListener implements FileChooserListener {

    @Override
    public void fileChooserChanged(FileChooserEvent e) {
      handleModelFileChooserEvent(e);
    }
  }
}
