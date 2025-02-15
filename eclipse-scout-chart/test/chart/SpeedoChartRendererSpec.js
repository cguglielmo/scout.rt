/*
 * Copyright (c) 2010-2022 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
/* global sandboxSession, createSimpleModel*/

import {Chart, SpeedoChartRenderer} from '../../src/index';
import {scout} from '@eclipse-scout/core';
import {LocaleSpecHelper} from '@eclipse-scout/core/src/testing/index';

describe('SpeedoChartRenderer', () => {
  let locale, helper, session;

  beforeEach(() => {
    setFixtures(sandbox());
    session = sandboxSession();
    helper = new LocaleSpecHelper();
    locale = helper.createLocale(LocaleSpecHelper.DEFAULT_LOCALE);
  });

  describe('_formatValue', () => {
    it('should display compact labels for large values', () => {
      let speedo = new SpeedoChartRenderer({});
      speedo.session = {
        locale: locale
      };
      expect(speedo._formatValue(1)).toBe('1');
      expect(speedo._formatValue(999)).toBe('999');
      expect(speedo._formatValue(1000)).toBe('1\'000');
      expect(speedo._formatValue(9999)).toBe('9\'999');
      expect(speedo._formatValue(10000)).toBe('10k');
      expect(speedo._formatValue(999000)).toBe('999k');
      expect(speedo._formatValue(1000000)).toBe('1M');
      expect(speedo._formatValue(1200000)).toBe('1.2M');
      expect(speedo._formatValue(1230000)).toBe('1.23M');
    });
  });

  describe('click handling', () => {
    it('should handle click', () => {
      let chart = scout.create('Chart', {
        parent: session.desktop,
        data: {
          axes: [],
          chartValueGroups: [{
            clickable: true,
            colorHexValue: '#ffee00',
            groupName: 'Group0',
            values: [
              1, 5, 10
            ]
          }]
        },
        config: {
          type: Chart.Type.SPEEDO,
          clickable: true,
          options: {
            autoColor: true,
            clickable: true,
            tooltips: {
              enabled: true
            },
            legend: {
              display: true,
              position: Chart.Position.RIGHT
            }
          },
          speedo: {
            greenAreaPosition: SpeedoChartRenderer.Position.CENTER
          }
        }
      });
      chart.render();
      chart.revalidateLayout();

      let event = null;
      chart.on('valueClick', event0 => {
        event = event0;
      });
      let $svg = session.desktop.$container.find('svg');
      $svg.click();
      expect(event.data).toEqual({
        xIndex: null,
        dataIndex: null,
        datasetIndex: null
      });
    });
  });

  describe('calculations', () => {
    it('rounded segments are always in the correct part', () => {
      let speedo = new SpeedoChartRenderer({});

      speedo.parts = SpeedoChartRenderer.NUM_PARTS_GREEN_EDGE;
      speedo.numSegmentsPerPart = 8;
      testSegmentToBeInPart(speedo);

      speedo.parts = SpeedoChartRenderer.NUM_PARTS_GREEN_CENTER;
      speedo.numSegmentsPerPart = 5;
      testSegmentToBeInPart(speedo);
    });

    function testSegmentToBeInPart(speedo) {
      let multiplier = 100,
        modifiers = [0, 1, 5, 10];

      let minValue = 1,
        maxValue = speedo.parts * multiplier;

      for (let i = 0; i < speedo.parts + 1; i++) {
        modifiers.forEach(modifier => {
          if (i > 0 && modifier > 0) {
            let value = i * multiplier - modifier;
            expectSegmentToBeInPart(speedo, value, minValue, maxValue);
          }
          if (i < speedo.parts + 1) {
            let value = i * multiplier + modifier;
            expectSegmentToBeInPart(speedo, value, minValue, maxValue);
          }
        });
      }
    }

    function expectSegmentToBeInPart(speedo, value, minValue, maxValue) {
      let numTotalSegments = speedo.parts * speedo.numSegmentsPerPart,
        valuePercentage = speedo._getValuePercentage(value, minValue, maxValue),
        segmentToPointAt = speedo._getSegmentToPointAt(valuePercentage, numTotalSegments),
        segmentToPointAtPercentage = segmentToPointAt / numTotalSegments,
        partForValue = speedo._getPartForValue(valuePercentage),
        partForSegmentToPointAt = speedo._getPartForValue(segmentToPointAtPercentage);

      expect(partForValue)
        .withContext(`Parts are not equal for value=${value}, minValue=${minValue}, maxValue=${maxValue}. The part for the value is ${partForValue} while the part for the segment is ${partForSegmentToPointAt}.`)
        .toBe(partForSegmentToPointAt);
    }
  });
});
