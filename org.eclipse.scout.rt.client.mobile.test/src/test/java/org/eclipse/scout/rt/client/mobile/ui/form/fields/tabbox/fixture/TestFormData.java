/*******************************************************************************
 * Copyright (c) 2015 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.rt.client.mobile.ui.form.fields.tabbox.fixture;

import javax.annotation.Generated;

import org.eclipse.scout.rt.shared.data.form.AbstractFormData;
import org.eclipse.scout.rt.shared.data.form.fields.AbstractValueFieldData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications recommended.
 */
@Generated(value = "org.eclipse.scout.rt.client.mobile.ui.form.fields.tabbox.fixture.TestForm", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class TestFormData extends AbstractFormData {

  private static final long serialVersionUID = 1L;

  public TestFormData() {
  }

  public TemplateExGroupBox getTemplateExGroupBox() {
    return getFieldByClass(TemplateExGroupBox.class);
  }

  public TemplateGroupBox getTemplateGroupBox() {
    return getFieldByClass(TemplateGroupBox.class);
  }

  public TextSimple getTextSimple() {
    return getFieldByClass(TextSimple.class);
  }

  public static class TemplateExGroupBox extends AbstractTemplateGroupBoxData {

    private static final long serialVersionUID = 1L;

    public TemplateExGroupBox() {
    }

    public Text3 getText3() {
      return getFieldByClass(Text3.class);
    }

    public static class Text3 extends AbstractValueFieldData<String> {

      private static final long serialVersionUID = 1L;

      public Text3() {
      }
    }
  }

  public static class TemplateGroupBox extends AbstractTemplateGroupBoxData {

    private static final long serialVersionUID = 1L;

    public TemplateGroupBox() {
    }
  }

  public static class TextSimple extends AbstractValueFieldData<String> {

    private static final long serialVersionUID = 1L;

    public TextSimple() {
    }
  }
}
