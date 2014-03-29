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
package org.eclipse.scout.rt.ui.json.desktop;

import static org.eclipse.scout.rt.ui.json.testing.JsonTestUtility.extractEventsFromResponse;

import java.util.List;

import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.rt.client.ui.basic.table.ITable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.ui.json.JsonEvent;
import org.eclipse.scout.rt.ui.json.JsonResponse;
import org.eclipse.scout.rt.ui.json.desktop.JsonDesktopTable;
import org.eclipse.scout.rt.ui.json.fixtures.JsonSessionMock;
import org.eclipse.scout.rt.ui.json.table.fixtures.Table;
import org.eclipse.scout.rt.ui.json.testing.JsonTestUtility;
import org.eclipse.scout.testing.client.runner.ScoutClientTestRunner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ScoutClientTestRunner.class)
public class JsonDesktopTableTest {

  /**
   * Tests whether the model row gets correctly selected
   */
  @Test
  public void testSelectionEvent() throws ProcessingException, JSONException {
    Table table = new Table();
    table.fill(5);

    Assert.assertNull(table.getSelectedRow());

    ITableRow row = table.getRow(2);
    JsonDesktopTable jsonDesktopTable = createJsonDesktopTableWithMocks(table);

    JsonEvent event = createJsonSelectedEvent(jsonDesktopTable.getOrCreatedRowId(row));
    jsonDesktopTable.handleUiEvent(event, new JsonResponse());

    Assert.assertTrue(row.isSelected());
  }

  /**
   * Response must not contain the selection event if the selection was triggered by the request
   */
  @Test
  public void testIgnorableSelectionEvent() throws ProcessingException, JSONException {
    Table table = new Table();
    table.fill(5);

    ITableRow row = table.getRow(2);
    JsonDesktopTable jsonDesktopTable = createJsonDesktopTableWithMocks(table);

    JsonEvent event = createJsonSelectedEvent(jsonDesktopTable.getOrCreatedRowId(row));
    jsonDesktopTable.handleUiEvent(event, new JsonResponse());

    List<JSONObject> responseEvents = extractEventsFromResponse(jsonDesktopTable.getJsonSession().currentJsonResponse(), JsonDesktopTable.EVENT_ROWS_SELECTED);
    Assert.assertTrue(responseEvents.size() == 0);
  }

  /**
   * If the selection event triggers the selection of another row, the selection event must not be ignored.
   */
  @Test
  public void testIgnorableSelectionEvent2() throws ProcessingException, JSONException {
    Table table = new Table() {
      @Override
      protected void execRowsSelected(List<? extends ITableRow> rows) throws ProcessingException {
        selectRow(4);
      }
    };
    table.fill(5);

    ITableRow row2 = table.getRow(2);
    ITableRow row4 = table.getRow(4);

    JsonDesktopTable jsonDesktopTable = createJsonDesktopTableWithMocks(table);
    JsonEvent event = createJsonSelectedEvent(jsonDesktopTable.getOrCreatedRowId(row2));

    Assert.assertFalse(row2.isSelected());
    Assert.assertFalse(row4.isSelected());

    jsonDesktopTable.handleUiEvent(event, new JsonResponse());

    Assert.assertFalse(row2.isSelected());
    Assert.assertTrue(row4.isSelected());

    List<JSONObject> responseEvents = extractEventsFromResponse(jsonDesktopTable.getJsonSession().currentJsonResponse(), JsonDesktopTable.EVENT_ROWS_SELECTED);
    Assert.assertTrue(responseEvents.size() == 1);

    List<ITableRow> tableRows = jsonDesktopTable.extractTableRows(responseEvents.get(0));
    Assert.assertEquals(row4, tableRows.get(0));
  }

  public static JsonDesktopTable createJsonDesktopTableWithMocks(ITable table) {
    JsonSessionMock jsonSession = new JsonSessionMock();

    JsonDesktopTable jsonDesktopTable = new JsonDesktopTable(table, jsonSession);
    jsonDesktopTable.init();

    //init treeNode map
    jsonDesktopTable.toJson();

    return jsonDesktopTable;
  }

  public static JsonEvent createJsonSelectedEvent(String rowId) throws JSONException {
    JsonEvent event = JsonTestUtility.createJsonEvent(JsonDesktopTable.EVENT_ROWS_SELECTED);
    JSONArray rowIds = new JSONArray();
    rowIds.put(rowId);
    event.getEventObject().put(JsonDesktopTable.PROP_ROW_IDS, rowIds);
    return event;
  }
}
