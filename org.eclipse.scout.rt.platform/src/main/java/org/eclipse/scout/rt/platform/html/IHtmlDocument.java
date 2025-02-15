/*
 * Copyright (c) 2010-2017 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package org.eclipse.scout.rt.platform.html;

public interface IHtmlDocument extends IHtmlElement {

  String HTML5_DOCTYPE = "<!DOCTYPE html>";

  IHtmlDocument doctype(String type);

  /**
   * @return HTML document with HTML5 doctype {@value IHtmlDocument#HTML5_DOCTYPE}
   */
  IHtmlDocument doctype();

}
