/**
 * Polyfill for IE9 providing a simple implementation of FormData (as used by FileChooser.js)
 *
 * Only supports the append() and the toString() method. Does not work in modern browsers, because
 * it uses obsolete File.getAsBinary (https://developer.mozilla.org/de/docs/Web/API/File/getAsBinary).
 *
 * Rules for generating the boundary:
 * http://stackoverflow.com/a/4656646
 *
 * How to use this polyfill with jQuery:
 * http://stackoverflow.com/a/5976031
 *
 */
(function(window) {
  if (window.FormData) {
    // Nothing to do
    return;
  }

  window.FormData = function() {
    this.polyfill = true; // Marker
    this._boundary = 'ScoutFormData-' + scout.numbers.randomId(50);
    this._data = [];
    this._crlf = '\r\n';
  };

  window.FormData.prototype.append = function(key, value, filename) {
    var element = [key, value, filename];
    this._data.push(element);
  };

  window.FormData.prototype.getContent = function() {
    var result = '';
    this._data.forEach(function(element) {
      var key = element[0];
      var value = element[1];
      var filename = element[2];

      result += '--' + this._boundary + this._crlf;
      if (value instanceof window.File) {
        // File
        var file = value;
        filename = filename || file.name || key;
        result += 'Content-Disposition: form-data; name="' + key + '"; filename="' + filename + '"' + this._crlf;
        result += 'Content-Type: ' + file.type + this._crlf;
        result += this._crlf;
        result += (file.getAsBinary ? file.getAsBinary() : 'UNKNOWN CONTENT') + this._crlf;
      } else {
        // Everything else
        result += 'Content-Disposition: form-data; name="' + key + '";' + this._crlf;
        result += this._crlf;
        result += value + this._crlf;
      }
    }.bind(this));
    result += '--' + this._boundary + '--';
    return result;
  };

  window.FormData.prototype.applyToAjaxOptions = function(ajaxOpts) {
    if (!ajaxOpts) {
      return;
    }
    // Make sure no text encoding stuff is done by xhr
    ajaxOpts.xhr = function() {
      var xhr = jQuery.ajaxSettings.xhr();
      xhr.send = xhr.sendAsBinary || xhr.send; // sendAsBinary only exists in older browsers
      return xhr;
    };
    // Manually set content type (with boundary)
    ajaxOpts.contentType = 'multipart/form-data; boundary=' + this._boundary;
    // Generate content
    ajaxOpts.data = this.getContent();
  };
})(window);
