/*******************************************************************************
 * Copyright (c) 2014-2015 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
scout.ValueFieldAdapter = function() {
  scout.ValueFieldAdapter.parent.call(this);
};
scout.inherits(scout.ValueFieldAdapter, scout.FormFieldAdapter);

scout.ValueFieldAdapter.prototype._onWidgetDisplayTextChanged = function(event) {
  this._send('displayTextChanged', {
    displayText: event.displayText,
    whileTyping: event.whileTyping
  }, {
    showBusyIndicator: !event.whileTyping
  });
};

scout.ValueFieldAdapter.prototype._onWidgetEvent = function(event) {
  if (event.type === 'displayTextChanged') {
    this._onWidgetDisplayTextChanged(event);
  } else {
    scout.ValueFieldAdapter.parent.prototype._onWidgetEvent.call(this, event);
  }
};
