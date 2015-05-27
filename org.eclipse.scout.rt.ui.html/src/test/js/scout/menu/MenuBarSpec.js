/* global MenuSpecHelper */
describe("MenuBar", function() {
  var helper, session;

  beforeEach(function() {
    setFixtures(sandbox());
    session = sandboxSession();
    helper = new MenuSpecHelper(session);
  });

  describe('updateItems', function() {

    it('prefers menu type for the left location if menu types for multiple locations are specified', function() {
      var menus, menu1,
        menuBar = new scout.MenuBar(session, new scout.GroupBoxMenuItemsOrder());

      menu1 = helper.createMenu(helper.createModel('foo'));
      menu1.menuTypes = ['Table.EmptySpace', 'Table.SingleSelection'];
      menus = [menu1];

      menuBar.render(session.$entryPoint);
      menuBar.updateItems(menus);

      expect(menuBar.menuItems.length).toBe(1);
      expect(menuBar.menuItems[0]).toBe(menu1);

      // FIXME AWE: (menu) add more tests to MenuBarSpec.js
    });

  });

});
