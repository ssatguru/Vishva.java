"Generated from Java with JSweet 1.1.0-SNAPSHOT - http://www.jsweet.org";
var org;
(function (org) {
    var ssatguru;
    (function (ssatguru) {
        var babylonjs;
        (function (babylonjs) {
            var VishvaGUI = (function () {
                function VishvaGUI(vishva) {
                    var _this = this;
                    this.local = true;
                    this.menuBarOn = false;
                    /**
                     * this array will be used store all dialogs whose position needs to be
                     * reset on window resize
                     */
                    this.dialogs = new Array();
                    this.localAxis = document.getElementById("local");
                    this.firstTime = true;
                    this.addMenuOn = false;
                    this.vishva = vishva;
                    var showMenu = document.getElementById("showMenu");
                    showMenu.style.visibility = "visible";
                    document.getElementById("menubar").style.visibility = "visible";
                    var menuBar = $("#menubar");
                    var jpo = Object.defineProperty({
                        my: "left center",
                        at: "right center",
                        of: showMenu
                    }, '__interfaces', { configurable: true, value: ["def.jqueryui.jqueryui.JQueryPositionOptions"] });
                    menuBar.position(jpo);
                    menuBar.hide(null);
                    showMenu.onclick = (function (menuBar) {
                        return function (e) {
                            if (_this.menuBarOn) {
                                menuBar.hide("slide");
                            }
                            else {
                                menuBar.show("slide");
                            }
                            _this.menuBarOn = !_this.menuBarOn;
                            return true;
                        };
                    })(menuBar);
                    this.createJPOs();
                    this.updateAddMenu();
                    this.setNavMenu();
                    this.createEditDiag();
                    this.createEnvDiag();
                    this.createDownloadDiag();
                    this.createUploadDiag();
                    this.createHelpDiag();
                    this.createAlertDiag();
                    this.create_sNaDiag();
                    this.createEditSensDiag();
                    this.createEditActDiag();
                    this.createPropsDiag();
                    window.addEventListener("resize", function (evt) { return _this.onWindowResize(evt); });
                }
                VishvaGUI.prototype.createJPOs = function () {
                    this.centerBottom = Object.defineProperty({
                        at: "center bottom",
                        my: "center bottom",
                        of: window
                    }, '__interfaces', { configurable: true, value: ["def.jqueryui.jqueryui.JQueryPositionOptions"] });
                    this.leftCenter = Object.defineProperty({
                        at: "left center",
                        my: "left center",
                        of: window
                    }, '__interfaces', { configurable: true, value: ["def.jqueryui.jqueryui.JQueryPositionOptions"] });
                    this.rightCenter = Object.defineProperty({
                        at: "right center",
                        my: "right center",
                        of: window
                    }, '__interfaces', { configurable: true, value: ["def.jqueryui.jqueryui.JQueryPositionOptions"] });
                };
                /**
                 * resposition all dialogs to their original default postions
                 * without this, a window resize could end up moving some dialogs outside the window
                 * and thus make them disappear
                 *
                 * @param evt
                 */
                VishvaGUI.prototype.onWindowResize = function (evt) {
                    for (var index157 = 0; index157 < this.dialogs.length; index157++) {
                        var jq = this.dialogs[index157];
                        {
                            var jpo = jq["jpo"];
                            if (jpo != null) {
                                jq.dialog("option", "position", jpo);
                                var open = jq.dialog("isOpen");
                                if (open) {
                                    jq.dialog("close");
                                    jq.dialog("open");
                                }
                            }
                        }
                    }
                };
                VishvaGUI.prototype.updateAddMenu = function () {
                    var _this = this;
                    var assetTypes = Object.keys(this.vishva.assets);
                    var addMenu = document.getElementById("AddMenu");
                    var f = function (e) { return _this.onAddMenuItemClick(e); };
                    for (var index158 = 0; index158 < assetTypes.length; index158++) {
                        var assetType = assetTypes[index158];
                        {
                            if (assetType == "sounds") {
                                continue;
                            }
                            var li = document.createElement("li");
                            li.id = "add-" + assetType;
                            li.innerText = assetType;
                            li.onclick = f;
                            addMenu.appendChild(li);
                        }
                    }
                };
                VishvaGUI.prototype.onAddMenuItemClick = function (e) {
                    var li = e.target;
                    var jq = li["diag"];
                    if (jq == null) {
                        var assetType = li.innerHTML;
                        jq = this.createAssetDiag(assetType);
                        li["diag"] = jq;
                    }
                    jq.dialog("open");
                    return true;
                };
                VishvaGUI.prototype.createAssetDiag = function (assetType) {
                    var div = document.createElement("div");
                    div.id = assetType + "Div";
                    div.setAttribute("title", assetType);
                    var table = document.createElement("table");
                    table.id = assetType + "Tbl";
                    var items = this.vishva.assets[assetType];
                    this.updateAssetTable(table, assetType, items);
                    div.appendChild(table);
                    document.body.appendChild(div);
                    var jq = $("#" + div.id);
                    var dos = Object.defineProperty({
                        autoOpen: false,
                        resizable: true,
                        position: this.centerBottom,
                        width: "100%",
                        height: "auto"
                    }, '__interfaces', { configurable: true, value: ["def.jqueryui.jqueryui.DialogEvents", "def.jqueryui.jqueryui.DialogOptions"] });
                    jq.dialog(dos);
                    jq["jpo"] = this.centerBottom;
                    this.dialogs.push(jq);
                    return jq;
                };
                VishvaGUI.prototype.updateAssetTable = function (tbl, assetType, items) {
                    var _this = this;
                    if (tbl.rows.length > 0) {
                        return;
                    }
                    var f = function (e) { return _this.onAssetImgClick(e); };
                    var row = tbl.insertRow();
                    for (var index159 = 0; index159 < items.length; index159++) {
                        var item = items[index159];
                        {
                            var img = document.createElement("img");
                            img.id = item;
                            img.src = "vishva/assets/" + assetType + "/" + item + "/" + item + ".jpg";
                            img.setAttribute("style", VishvaGUI.SMALL_ICON_SIZE + "cursor:pointer;");
                            img.className = assetType;
                            img.onclick = f;
                            var cell = row.insertCell();
                            cell.appendChild(img);
                        }
                    }
                    var row2 = tbl.insertRow();
                    for (var index160 = 0; index160 < items.length; index160++) {
                        var item = items[index160];
                        {
                            var cell = row2.insertCell();
                            cell.innerText = item;
                        }
                    }
                };
                VishvaGUI.prototype.onAssetImgClick = function (e) {
                    var i = e.target;
                    if (i.className == "skyboxes") {
                        this.vishva.setSky(i.id);
                    }
                    else if (i.className == "primitives") {
                        this.vishva.addPrim(i.id);
                    }
                    else {
                        this.vishva.loadAsset(i.className, i.id);
                    }
                    return true;
                };
                VishvaGUI.prototype.createEditDiag = function () {
                    var editMenu = $("#editMenu");
                    editMenu.menu();
                    var em = editMenu;
                    em.unbind("keydown");
                    this.editDialog = $("#editDiv");
                    var dos = Object.defineProperty({
                        autoOpen: false,
                        resizable: false,
                        position: this.leftCenter,
                        width: "auto",
                        height: "auto"
                    }, '__interfaces', { configurable: true, value: ["def.jqueryui.jqueryui.DialogEvents", "def.jqueryui.jqueryui.DialogOptions"] });
                    this.editDialog.dialog(dos);
                    this.editDialog["jpo"] = this.leftCenter;
                    this.dialogs.push(this.editDialog);
                };
                VishvaGUI.prototype.showEditMenu = function () {
                    var alreadyOpen = this.editDialog.dialog("isOpen");
                    if (alreadyOpen)
                        return alreadyOpen;
                    if (this.vishva.isSpaceLocal()) {
                        this.local = true;
                        this.localAxis.innerHTML = "Switch to Global Axis";
                    }
                    else {
                        this.local = false;
                        this.localAxis.innerHTML = "Switch to Local Axis";
                    }
                    this.editDialog.dialog("open");
                    return false;
                };
                VishvaGUI.prototype.closeEditMenu = function () {
                    this.editDialog.dialog("close");
                };
                VishvaGUI.prototype.createEnvDiag = function () {
                    var _this = this;
                    var sunPos = $("#sunPos");
                    var light = $("#light");
                    var shade = $("#shade");
                    var fog = $("#fog");
                    var fov = $("#fov");
                    sunPos.slider(this.sliderOptions(0, 180, this.vishva.getSunPos()));
                    light.slider(this.sliderOptions(0, 100, 100 * this.vishva.getLight()));
                    shade.slider(this.sliderOptions(0, 100, 100 * this.vishva.getShade()));
                    fog.slider(this.sliderOptions(0, 100, 1000 * this.vishva.getFog()));
                    fov.slider(this.sliderOptions(0, 180, this.vishva.getFov()));
                    var skyButton = document.getElementById("skyButton");
                    skyButton.onclick = function (e) {
                        var foo = document.getElementById("add-skyboxes");
                        foo.click();
                        return true;
                    };
                    var trnButton = document.getElementById("trnButton");
                    trnButton.onclick = function (e) {
                        _this.showAlertDiag("Sorry. To be implemneted soon");
                        return true;
                    };
                    var colorEle = document.getElementById("color-picker");
                    var cp = new ColorPicker(colorEle, function (hex, hsv, rgb) { return _this.colorPickerHandler(hex, hsv, rgb); });
                    var setRGB = cp["setRgb"];
                    var color = this.vishva.getGroundColor();
                    if (color != null) {
                        var rgb = new RGB();
                        rgb.r = color[0];
                        rgb.g = color[1];
                        rgb.b = color[2];
                        cp.setRgb(rgb);
                    }
                    this.envDiag = $("#envDiv");
                    var dos1 = Object.defineProperty({
                        autoOpen: false,
                        resizable: false,
                        position: this.rightCenter,
                        minWidth: 350,
                        height: "auto"
                    }, '__interfaces', { configurable: true, value: ["def.jqueryui.jqueryui.DialogEvents", "def.jqueryui.jqueryui.DialogOptions"] });
                    this.envDiag.dialog(dos1);
                    this.envDiag["jpo"] = this.rightCenter;
                    this.dialogs.push(this.envDiag);
                };
                VishvaGUI.prototype.createDownloadDiag = function () {
                    this.downloadLink = document.getElementById("downloadLink");
                    this.downloadDialog = $("#saveDiv");
                    this.downloadDialog.dialog();
                    this.downloadDialog.dialog("close");
                };
                VishvaGUI.prototype.createUploadDiag = function () {
                    var _this = this;
                    var loadFileInput = document.getElementById("loadFileInput");
                    var loadFileOk = document.getElementById("loadFileOk");
                    loadFileOk.onclick = (function (loadFileInput) {
                        return function (e) {
                            var fl = loadFileInput.files;
                            if (fl.length == 0) {
                                alert("no file slected");
                                return null;
                            }
                            var file = null;
                            for (var index161 = 0; index161 < fl.length; index161++) {
                                var f = fl[index161];
                                {
                                    file = f;
                                }
                            }
                            _this.vishva.loadAssetFile(file);
                            _this.loadDialog.dialog("close");
                            return true;
                        };
                    })(loadFileInput);
                    this.loadDialog = $("#loadDiv");
                    this.loadDialog.dialog();
                    this.loadDialog.dialog("close");
                };
                VishvaGUI.prototype.createHelpDiag = function () {
                    var obj = $("#helpDiv");
                    this.helpDiag = obj;
                    var dos = Object.defineProperty({
                        autoOpen: false,
                        resizable: false,
                        width: 500
                    }, '__interfaces', { configurable: true, value: ["def.jqueryui.jqueryui.DialogEvents", "def.jqueryui.jqueryui.DialogOptions"] });
                    this.helpDiag.dialog(dos);
                };
                VishvaGUI.prototype.create_sNaDiag = function () {
                    var _this = this;
                    var sNaDetails = $("#sNaDetails");
                    sNaDetails.tabs();
                    this.sNaDialog = $("#sNaDiag");
                    var dos = Object.defineProperty({}, '__interfaces', { configurable: true, value: ["def.jqueryui.jqueryui.DialogEvents", "def.jqueryui.jqueryui.DialogOptions"] });
                    dos.autoOpen = false;
                    dos.modal = false;
                    dos.resizable = false;
                    dos.width = "auto";
                    dos.title = "Sensors and Actuators";
                    dos.close = function (e, ui) {
                        _this.vishva.switchDisabled = false;
                    };
                    this.sNaDialog.dialog(dos);
                    this.sensSel = document.getElementById("sensSel");
                    this.actSel = document.getElementById("actSel");
                    var sensors = this.vishva.getSensorList();
                    var actuators = this.vishva.getActuatorList();
                    for (var index162 = 0; index162 < sensors.length; index162++) {
                        var sensor = sensors[index162];
                        {
                            var opt = document.createElement("option");
                            opt.value = sensor;
                            opt.innerHTML = sensor;
                            this.sensSel.add(opt);
                        }
                    }
                    for (var index163 = 0; index163 < actuators.length; index163++) {
                        var actuator = actuators[index163];
                        {
                            var opt = document.createElement("option");
                            opt.value = actuator;
                            opt.innerHTML = actuator;
                            this.actSel.add(opt);
                        }
                    }
                    this.sensTbl = document.getElementById("sensTbl");
                    this.actTbl = document.getElementById("actTbl");
                };
                VishvaGUI.prototype.show_sNaDiag = function () {
                    var _this = this;
                    var sens = this.vishva.getSensors();
                    if (sens == null) {
                        this.showAlertDiag("no mesh selected");
                        return;
                    }
                    var acts = this.vishva.getActuators();
                    if (acts == null) {
                        this.showAlertDiag("no mesh selected");
                        return;
                    }
                    this.vishva.switchDisabled = true;
                    this.updateSensActTbl(sens, this.sensTbl);
                    this.updateSensActTbl(acts, this.actTbl);
                    var addSens = document.getElementById("addSens");
                    addSens.onclick = function (e) {
                        var s = _this.sensSel.item(_this.sensSel.selectedIndex);
                        var sensor = s.value;
                        _this.vishva.addSensorbyName(sensor);
                        _this.updateSensActTbl(_this.vishva.getSensors(), _this.sensTbl);
                        _this.sNaDialog.dialog("close");
                        _this.sNaDialog.dialog("open");
                        return true;
                    };
                    var addAct = document.getElementById("addAct");
                    addAct.onclick = function (e) {
                        var a = _this.actSel.item(_this.actSel.selectedIndex);
                        var actuator = a.value;
                        _this.vishva.addActuaorByName(actuator);
                        _this.updateSensActTbl(_this.vishva.getActuators(), _this.actTbl);
                        _this.sNaDialog.dialog("close");
                        _this.sNaDialog.dialog("open");
                        return true;
                    };
                    this.sNaDialog.dialog("open");
                };
                VishvaGUI.prototype.updateSensActTbl = function (sensAct, tbl) {
                    var _this = this;
                    var l = tbl.rows.length;
                    for (var i = l - 1; i > 0; i--) {
                        tbl.deleteRow(i);
                    }
                    l = sensAct.length;
                    for (var i = 0; i < l; i++) {
                        var row = tbl.insertRow();
                        var cell = row.insertCell();
                        cell.innerHTML = sensAct[i].getName();
                        cell = row.insertCell();
                        cell.innerHTML = sensAct[i].getProperties().signalId;
                        cell = row.insertCell();
                        var editBut = document.createElement("BUTTON");
                        editBut.innerHTML = "edit";
                        var jq = $(editBut);
                        jq.button();
                        var d = i;
                        editBut.id = d.toString();
                        editBut["sa"] = sensAct[i];
                        cell.appendChild(editBut);
                        editBut.onclick = function (e) {
                            var el = e.currentTarget;
                            var sa = el["sa"];
                            if (sa.getType() == "SENSOR") {
                                _this.showEditSensDiag(sa);
                            }
                            else {
                                _this.showEditActDiag(sa);
                            }
                            return true;
                        };
                        cell = row.insertCell();
                        var delBut = document.createElement("BUTTON");
                        delBut.innerHTML = "del";
                        var jq2 = $(delBut);
                        jq2.button();
                        delBut.id = d.toString();
                        delBut["row"] = row;
                        delBut["sa"] = sensAct[i];
                        cell.appendChild(delBut);
                        delBut.onclick = function (e) {
                            var el = e.currentTarget;
                            var r = el["row"];
                            tbl.deleteRow(r.rowIndex);
                            _this.vishva.removeSensorActuator(el["sa"]);
                            return true;
                        };
                    }
                };
                VishvaGUI.prototype.createEditSensDiag = function () {
                    var editSensDiag = $("#editSensDiag");
                    var dos = Object.defineProperty({}, '__interfaces', { configurable: true, value: ["def.jqueryui.jqueryui.DialogEvents", "def.jqueryui.jqueryui.DialogOptions"] });
                    dos.autoOpen = false;
                    dos.modal = true;
                    dos.resizable = false;
                    dos.width = "auto";
                    dos.title = "Edit Sensor";
                    editSensDiag.dialog(dos);
                };
                VishvaGUI.prototype.showEditSensDiag = function (sensor) {
                    var _this = this;
                    var sensNameEle = document.getElementById("editSensDiag.sensName");
                    sensNameEle.innerHTML = sensor.getName();
                    var editSensDiag = $("#editSensDiag");
                    editSensDiag.dialog("open");
                    var parmDiv = document.getElementById("editSensDiag.parms");
                    var node = parmDiv.firstChild;
                    if (node != null)
                        parmDiv.removeChild(node);
                    var tbl = this.createForm(sensor.getProperties(), parmDiv.id);
                    parmDiv.appendChild(tbl);
                    var dbo = Object.defineProperty({}, '__interfaces', { configurable: true, value: ["def.jqueryui.jqueryui.DialogButtonOptions"] });
                    dbo.text = "save";
                    dbo.click = (function (editSensDiag, parmDiv) {
                        return function (e) {
                            _this.readForm(sensor.getProperties(), parmDiv.id);
                            _this.updateSensActTbl(_this.vishva.getSensors(), _this.sensTbl);
                            editSensDiag.dialog("close");
                            return true;
                        };
                    })(editSensDiag, parmDiv);
                    var dbos = [dbo];
                    editSensDiag.dialog("option", "buttons", dbos);
                };
                VishvaGUI.prototype.createEditActDiag = function () {
                    var editActDiag = $("#editActDiag");
                    var dos = Object.defineProperty({}, '__interfaces', { configurable: true, value: ["def.jqueryui.jqueryui.DialogEvents", "def.jqueryui.jqueryui.DialogOptions"] });
                    dos.autoOpen = false;
                    dos.modal = true;
                    dos.resizable = false;
                    dos.width = "auto";
                    dos.title = "Edit Actuator";
                    editActDiag.dialog(dos);
                };
                VishvaGUI.prototype.showEditActDiag = function (actuator) {
                    var _this = this;
                    var actNameEle = document.getElementById("editActDiag.actName");
                    actNameEle.innerHTML = actuator.getName();
                    var editActDiag = $("#editActDiag");
                    editActDiag.dialog("open");
                    var parmDiv = document.getElementById("editActDiag.parms");
                    var node = parmDiv.firstChild;
                    if (node != null) {
                        parmDiv.removeChild(node);
                    }
                    if (actuator.getName() == "Sound") {
                        var prop = actuator.getProperties();
                        prop.soundFile.values = this.vishva.getSoundFiles();
                    }
                    var tbl = this.createForm(actuator.getProperties(), parmDiv.id);
                    parmDiv.appendChild(tbl);
                    var dbo = Object.defineProperty({}, '__interfaces', { configurable: true, value: ["def.jqueryui.jqueryui.DialogButtonOptions"] });
                    dbo.text = "save";
                    dbo.click = (function (parmDiv, editActDiag) {
                        return function (e) {
                            _this.readForm(actuator.getProperties(), parmDiv.id);
                            actuator.processUpdateGeneric();
                            _this.updateSensActTbl(_this.vishva.getActuators(), _this.actTbl);
                            editActDiag.dialog("close");
                            return true;
                        };
                    })(parmDiv, editActDiag);
                    var dbos = [dbo];
                    editActDiag.dialog("option", "buttons", dbos);
                };
                VishvaGUI.prototype.createForm = function (snap, idPrefix) {
                    idPrefix = idPrefix + ".";
                    var tbl = document.createElement("table");
                    var keys = Object.keys(snap);
                    for (var index164 = 0; index164 < keys.length; index164++) {
                        var key = keys[index164];
                        {
                            var row = tbl.insertRow();
                            var cell = row.insertCell();
                            cell.innerHTML = key;
                            cell = row.insertCell();
                            var t = typeof snap[key];
                            if ((t == "object") && (snap[key]["type"] == "SelectType")) {
                                console.log("is of type SelectType");
                                var keyValue = snap[key];
                                var options = keyValue.values;
                                var sel = document.createElement("select");
                                sel.id = idPrefix + key;
                                for (var index165 = 0; index165 < options.length; index165++) {
                                    var option = options[index165];
                                    {
                                        var opt = document.createElement("option");
                                        if (option == keyValue.value) {
                                            opt.selected = true;
                                        }
                                        opt.innerText = option;
                                        sel.add(opt);
                                    }
                                }
                                cell.appendChild(sel);
                            }
                            else {
                                var inp = document.createElement("input");
                                inp.id = idPrefix + key;
                                inp.className = "ui-widget-content ui-corner-all";
                                inp.value = snap[key];
                                if ((t == "object") && (snap[key]["type"] == "Range")) {
                                    var r = snap[key];
                                    inp.type = "range";
                                    inp.max = (new Number(r.max)).toString();
                                    inp.min = (new Number(r.min)).toString();
                                    inp.step = (new Number(r.step)).toString();
                                    inp.value = (new Number(r.value)).toString();
                                }
                                else if ((t == "string") || (t == "number")) {
                                    inp.type = "text";
                                    inp.value = snap[key];
                                }
                                else if (t == "boolean") {
                                    var check = snap[key];
                                    inp.type = "checkbox";
                                    if (check)
                                        inp.setAttribute("checked", "true");
                                }
                                cell.appendChild(inp);
                            }
                        }
                    }
                    return tbl;
                };
                VishvaGUI.prototype.readForm = function (snap, idPrefix) {
                    idPrefix = idPrefix + ".";
                    var keys = Object.keys(snap);
                    for (var index166 = 0; index166 < keys.length; index166++) {
                        var key = keys[index166];
                        {
                            var t = typeof snap[key];
                            if ((t == "object") && (snap[key]["type"] == "SelectType")) {
                                var s = snap[key];
                                var sel = document.getElementById(idPrefix + key);
                                s.value = sel.value;
                            }
                            else {
                                var ie = document.getElementById(idPrefix + key);
                                if ((t == "object") && (snap[key]["type"] == "Range")) {
                                    var r = snap[key];
                                    r.value = parseFloat(ie.value);
                                }
                                else if ((t == "string") || (t == "number")) {
                                    if (t == "number") {
                                        var v = parseFloat(ie.value);
                                        if (isNaN(v))
                                            snap[key] = 0;
                                        else
                                            snap[key] = v;
                                    }
                                    else {
                                        snap[key] = ie.value;
                                    }
                                }
                                else if (t == "boolean") {
                                    snap[key] = ie.checked;
                                }
                            }
                        }
                    }
                };
                VishvaGUI.prototype.createPropsDiag = function () {
                    var _this = this;
                    var meshPropstTab = $("#meshPropsTab");
                    meshPropstTab.tabs();
                    this.animSelect = document.getElementById("animList");
                    this.animSelect.onchange = function (e) {
                        var animName = _this.animSelect.value;
                        if (animName != null) {
                            var range = _this.skel.getAnimationRange(animName);
                            document.getElementById("animFrom").innerText = (new Number(range.from)).toString();
                            document.getElementById("animTo").innerText = (new Number(range.to)).toString();
                        }
                        return true;
                    };
                    this.animRate = document.getElementById("animRate");
                    this.animLoop = document.getElementById("animLoop");
                    document.getElementById("playAnim").onclick = function (e) {
                        if (_this.skel == null)
                            return true;
                        var animName = _this.animSelect.value;
                        var rate = _this.animRate.value;
                        if (animName != null) {
                            _this.vishva.playAnimation(animName, rate, _this.animLoop.checked);
                        }
                        return true;
                    };
                    document.getElementById("stopAnim").onclick = function (e) {
                        if (_this.skel == null)
                            return true;
                        _this.vishva.stopAnimation();
                        return true;
                    };
                    this.meshPropsDiag = $("#meshPropsDiag");
                    var dos = Object.defineProperty({}, '__interfaces', { configurable: true, value: ["def.jqueryui.jqueryui.DialogEvents", "def.jqueryui.jqueryui.DialogOptions"] });
                    dos.autoOpen = false;
                    dos.modal = false;
                    dos.resizable = false;
                    dos.width = 450;
                    dos.height = "300";
                    dos.title = "Mesh Properties";
                    dos.close = function (e, ui) {
                        _this.vishva.switchDisabled = false;
                    };
                    this.meshPropsDiag.dialog(dos);
                };
                VishvaGUI.prototype.updateTransform = function () {
                    var loc = this.vishva.getLocation();
                    var rot = this.vishva.getRoation();
                    var scl = this.vishva.getScale();
                    document.getElementById("loc.x").innerText = this.toString(loc.x);
                    document.getElementById("loc.y").innerText = this.toString(loc.y);
                    document.getElementById("loc.z").innerText = this.toString(loc.z);
                    document.getElementById("rot.x").innerText = this.toString(rot.x);
                    document.getElementById("rot.y").innerText = this.toString(rot.y);
                    document.getElementById("rot.z").innerText = this.toString(rot.z);
                    document.getElementById("scl.x").innerText = this.toString(scl.x);
                    document.getElementById("scl.y").innerText = this.toString(scl.y);
                    document.getElementById("scl.z").innerText = this.toString(scl.z);
                };
                VishvaGUI.prototype.updateAnimations = function () {
                    this.skel = this.vishva.getSkeleton();
                    var skelName;
                    if (this.skel == null) {
                        document.getElementById("skelName").innerText = "no skeleton";
                        return;
                    }
                    else {
                        skelName = this.skel.name;
                        if (skelName.trim() == "")
                            skelName = "no name";
                    }
                    document.getElementById("skelName").innerText = skelName;
                    var childs = this.animSelect.children;
                    var l = (childs.length | 0);
                    for (var i = l - 1; i >= 0; i--) {
                        childs[i].remove();
                    }
                    if (skelName != null) {
                        var range = this.vishva.getAnimationRanges();
                        var animOpt;
                        for (var index167 = 0; index167 < range.length; index167++) {
                            var ar = range[index167];
                            {
                                animOpt = document.createElement("option");
                                animOpt.value = ar.name;
                                animOpt.innerText = ar.name;
                                this.animSelect.appendChild(animOpt);
                            }
                        }
                        if (range[0] != null) {
                            document.getElementById("animFrom").innerText = (new Number(range[0].from)).toString();
                            document.getElementById("animTo").innerText = (new Number(range[0].to)).toString();
                        }
                    }
                };
                VishvaGUI.prototype.toString = function (d) {
                    return (new Number(d)).toFixed(2).toString();
                };
                VishvaGUI.prototype.createAlertDiag = function () {
                    this.alertDiv = document.getElementById("alertDiv");
                    this.alertDialog = $("#alertDiv");
                    var dos = Object.defineProperty({
                        title: "Information",
                        autoOpen: false,
                        width: "auto",
                        height: "auto"
                    }, '__interfaces', { configurable: true, value: ["def.jqueryui.jqueryui.DialogEvents", "def.jqueryui.jqueryui.DialogOptions"] });
                    this.alertDialog.dialog(dos);
                };
                VishvaGUI.prototype.showAlertDiag = function (msg) {
                    this.alertDiv.innerHTML = msg;
                    this.alertDialog.dialog("open");
                };
                VishvaGUI.prototype.sliderOptions = function (min, max, value) {
                    var _this = this;
                    var so = Object.defineProperty({}, '__interfaces', { configurable: true, value: ["def.jqueryui.jqueryui.SliderEvents", "def.jqueryui.jqueryui.SliderOptions"] });
                    so.min = min;
                    so.max = max;
                    so.value = value;
                    so.slide = function (e, ui) { return _this.handleSlide(e, ui); };
                    return so;
                };
                VishvaGUI.prototype.handleSlide = function (e, ui) {
                    var slider = e.target.id;
                    if (slider == "fov") {
                        this.vishva.setFov(ui.value);
                    }
                    else if (slider == "sunPos") {
                        this.vishva.setSunPos(ui.value);
                    }
                    else {
                        var v = ui.value / 100;
                        if (slider == "light") {
                            this.vishva.setLight(v);
                        }
                        else if (slider == "shade") {
                            this.vishva.setShade(v);
                        }
                        else if (slider == "fog") {
                            this.vishva.setFog(v / 10);
                        }
                    }
                    return true;
                };
                VishvaGUI.prototype.colorPickerHandler = function (hex, hsv, rgb) {
                    var colors = [rgb.r, rgb.g, rgb.b];
                    this.vishva.setGroundColor(colors);
                };
                VishvaGUI.prototype.setNavMenu = function () {
                    var _this = this;
                    var slideDown = JSON.parse("{\"direction\":\"up\"}");
                    var navAdd = document.getElementById("navAdd");
                    var addMenu = $("#AddMenu");
                    addMenu.menu();
                    addMenu.hide(null);
                    navAdd.onclick = (function (addMenu, navAdd, slideDown) {
                        return function (e) {
                            if (_this.firstTime) {
                                var jpo = Object.defineProperty({
                                    my: "left top",
                                    at: "left bottom",
                                    of: navAdd
                                }, '__interfaces', { configurable: true, value: ["def.jqueryui.jqueryui.JQueryPositionOptions"] });
                                addMenu.menu().position(jpo);
                                _this.firstTime = false;
                            }
                            if (_this.addMenuOn) {
                                addMenu.menu().hide("slide", slideDown);
                            }
                            else {
                                addMenu.show("slide", slideDown);
                            }
                            _this.addMenuOn = !_this.addMenuOn;
                            $(document).one("click", function (jqe) {
                                if (_this.addMenuOn) {
                                    addMenu.menu().hide("slide", slideDown);
                                    _this.addMenuOn = false;
                                }
                                return true;
                            });
                            e.cancelBubble = true;
                            return true;
                        };
                    })(addMenu, navAdd, slideDown);
                    var downWorld = document.getElementById("downWorld");
                    downWorld.onclick = function (e) {
                        var downloadURL = _this.vishva.saveWorld();
                        if (downloadURL == null)
                            return true;
                        _this.downloadLink.href = downloadURL;
                        _this.downloadDialog.dialog("open");
                        return false;
                    };
                    var navEnv = document.getElementById("navEnv");
                    navEnv.onclick = function (e) {
                        _this.envDiag = $("#envDiv");
                        _this.envDiag.dialog("open");
                        return false;
                    };
                    var navEdit = document.getElementById("navEdit");
                    navEdit.onclick = function (e) {
                        _this.showEditMenu();
                        return true;
                    };
                    var helpLink = document.getElementById("helpLink");
                    helpLink.onclick = function (e) {
                        _this.helpDiag.dialog("open");
                        return true;
                    };
                    var debugLink = document.getElementById("debugLink");
                    debugLink.onclick = function (e) {
                        _this.vishva.toggleDebug();
                        return true;
                    };
                    var swAv = document.getElementById("swAv");
                    var swGnd = document.getElementById("swGnd");
                    var parentMesh = document.getElementById("parentMesh");
                    var removeParent = document.getElementById("removeParent");
                    var removeChildren = document.getElementById("removeChildren");
                    var cloneMesh = document.getElementById("cloneMesh");
                    var delMesh = document.getElementById("delMesh");
                    var undo = document.getElementById("undo");
                    var redo = document.getElementById("redo");
                    var sNa = document.getElementById("sNa");
                    var meshProps = document.getElementById("meshProps");
                    swGnd.onclick = function (e) {
                        var err = _this.vishva.switchGround();
                        if (err != null) {
                            _this.showAlertDiag(err);
                        }
                        return true;
                    };
                    swAv.onclick = function (e) {
                        var err = _this.vishva.switch_avatar();
                        if (err != null) {
                            _this.showAlertDiag(err);
                        }
                        return true;
                    };
                    var downAsset = document.getElementById("downMesh");
                    downAsset.onclick = function (e) {
                        var downloadURL = _this.vishva.saveAsset();
                        if (downloadURL == null) {
                            _this.showAlertDiag("No Mesh Selected");
                            return true;
                        }
                        _this.downloadLink.href = downloadURL;
                        var env = $("#saveDiv");
                        env.dialog("open");
                        return false;
                    };
                    parentMesh.onclick = function (e) {
                        var err = _this.vishva.makeParent();
                        if (err != null) {
                            _this.showAlertDiag(err);
                        }
                        return false;
                    };
                    removeParent.onclick = function (e) {
                        var err = _this.vishva.removeParent();
                        if (err != null) {
                            _this.showAlertDiag(err);
                        }
                        return false;
                    };
                    removeChildren.onclick = function (e) {
                        var err = _this.vishva.removeChildren();
                        if (err != null) {
                            _this.showAlertDiag(err);
                        }
                        return false;
                    };
                    cloneMesh.onclick = function (e) {
                        var err = _this.vishva.clone_mesh();
                        if (err != null) {
                            _this.showAlertDiag(err);
                        }
                        return false;
                    };
                    delMesh.onclick = function (e) {
                        var err = _this.vishva.delete_mesh();
                        if (err != null) {
                            _this.showAlertDiag(err);
                        }
                        return false;
                    };
                    undo.onclick = function (e) {
                        _this.vishva.undo();
                        return false;
                    };
                    redo.onclick = function (e) {
                        _this.vishva.redo();
                        return false;
                    };
                    this.localAxis.onclick = function (e) {
                        _this.local = !_this.local;
                        if (_this.local) {
                            e.currentTarget.innerHTML = "Switch to Global Axis";
                        }
                        else {
                            e.currentTarget.innerHTML = "Switch to Local Axis";
                        }
                        _this.vishva.setSpaceLocal(_this.local);
                        return true;
                    };
                    sNa.onclick = function (e) {
                        _this.show_sNaDiag();
                        return true;
                    };
                    meshProps.onclick = function (e) {
                        if (!_this.vishva.anyMeshSelected()) {
                            _this.showAlertDiag("no mesh selected");
                            return true;
                        }
                        _this.vishva.switchDisabled = true;
                        _this.updateTransform();
                        _this.updateAnimations();
                        _this.meshPropsDiag.dialog("open");
                        return true;
                    };
                };
                VishvaGUI.LARGE_ICON_SIZE = "width:128px;height:128px;";
                VishvaGUI.SMALL_ICON_SIZE = "width:64px;height:64px;";
                return VishvaGUI;
            })();
            babylonjs.VishvaGUI = VishvaGUI;
            var RGB = (function () {
                function RGB() {
                }
                return RGB;
            })();
            babylonjs.RGB = RGB;
            var Range = (function () {
                function Range(min, max, value, step) {
                    this.type = "Range";
                    this.min = min;
                    this.max = max;
                    this.value = value;
                    this.step = step;
                }
                return Range;
            })();
            babylonjs.Range = Range;
            var SelectType = (function () {
                function SelectType() {
                    this.type = "SelectType";
                }
                return SelectType;
            })();
            babylonjs.SelectType = SelectType;
        })(babylonjs = ssatguru.babylonjs || (ssatguru.babylonjs = {}));
    })(ssatguru = org.ssatguru || (org.ssatguru = {}));
})(org || (org = {}));
