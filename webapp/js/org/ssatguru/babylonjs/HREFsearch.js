"Generated from Java with JSweet 1.1.0-SNAPSHOT - http://www.jsweet.org";
var org;
(function (org) {
    var ssatguru;
    (function (ssatguru) {
        var babylonjs;
        (function (babylonjs) {
            var HREFsearch = (function () {
                function HREFsearch() {
                    this.names = new Array();
                    this.values = new Array();
                    var search = window.location.search;
                    search = search.substring(1);
                    var parms = search.split("&");
                    for (var index121 = 0; index121 < parms.length; index121++) {
                        var parm = parms[index121];
                        {
                            var nameValues = parm.split("=");
                            if (nameValues.length == 2) {
                                var name = nameValues[0];
                                var value = nameValues[1];
                                this.names.push(name);
                                this.values.push(value);
                            }
                        }
                    }
                }
                HREFsearch.prototype.getParm = function (parm) {
                    var i = this.names.indexOf(parm);
                    if (i != -1) {
                        return this.values[i];
                    }
                    return null;
                };
                return HREFsearch;
            }());
            babylonjs.HREFsearch = HREFsearch;
        })(babylonjs = ssatguru.babylonjs || (ssatguru.babylonjs = {}));
    })(ssatguru = org.ssatguru || (org.ssatguru = {}));
})(org || (org = {}));
