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
package org.eclipse.scout.rt.shared.extension.dto.fixture;

import java.math.BigDecimal;

import javax.annotation.Generated;

import org.eclipse.scout.commons.annotations.Extends;
import org.eclipse.scout.rt.shared.data.form.fields.AbstractFormFieldData;
import org.eclipse.scout.rt.shared.data.form.fields.AbstractValueFieldData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications recommended.
 */
@Extends(OrigFormData.class)
@Generated(value = "org.eclipse.scout.rt.shared.extension.dto.fixture.SingleFormExtension", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class SingleFormExtensionData extends AbstractFormFieldData {

  private static final long serialVersionUID = 1L;

  public SingleFormExtensionData() {
  }

  public SecondBigDecimal getSecondBigDecimal() {
    return getFieldByClass(SecondBigDecimal.class);
  }

  public static class SecondBigDecimal extends AbstractValueFieldData<BigDecimal> {

    private static final long serialVersionUID = 1L;

    public SecondBigDecimal() {
    }
  }
}
