package org.ssatguru.babylonjs;

import static def.jquery.Globals.$;
import static jsweet.dom.Globals.alert;
import static jsweet.dom.Globals.console;
import static jsweet.dom.Globals.document;
import static jsweet.dom.Globals.window;
import static jsweet.lang.Globals.isNaN;
import static jsweet.lang.Globals.parseFloat;
import static jsweet.util.Globals.union;
import static jsweet.util.Globals.function;
import static jsweet.util.Globals.typeof;

import def.babylonjs.babylon.AnimationRange;
import def.babylonjs.babylon.Skeleton;
import def.babylonjs.babylon.Vector3;
import def.jquery.JQueryEventObject;
import def.jqueryui.JQuery;
import def.jqueryui.jqueryui.DialogButtonOptions;
import def.jqueryui.jqueryui.DialogOptions;
import def.jqueryui.jqueryui.JQueryPositionOptions;
import def.jqueryui.jqueryui.SliderOptions;
import def.jqueryui.jqueryui.SliderUIParams;
import jsweet.dom.CSSStyleDeclaration;
import jsweet.dom.Event;
import jsweet.dom.EventListener;
import jsweet.dom.File;
import jsweet.dom.FileList;
import jsweet.dom.Element;
import jsweet.dom.HTMLAnchorElement;
import jsweet.dom.HTMLButtonElement;
import jsweet.dom.HTMLCollection;
import jsweet.dom.HTMLDivElement;
import jsweet.dom.HTMLElement;
import jsweet.dom.HTMLFormElement;
import jsweet.dom.HTMLImageElement;
import jsweet.dom.HTMLInputElement;
import jsweet.dom.HTMLLIElement;
import jsweet.dom.HTMLLabelElement;
import jsweet.dom.HTMLOptionElement;
import jsweet.dom.HTMLSelectElement;
import jsweet.dom.HTMLTableCellElement;
import jsweet.dom.HTMLTableElement;
import jsweet.dom.HTMLTableRowElement;
import jsweet.dom.HTMLUListElement;
import jsweet.dom.MouseEvent;
import jsweet.dom.Node;
import jsweet.dom.NodeList;
import jsweet.lang.Ambient;
import jsweet.lang.Array;
import jsweet.lang.Function;
import jsweet.lang.Globals;
import jsweet.lang.JSON;
import jsweet.lang.Number;
import jsweet.util.StringTypes;
import jsweet.util.function.TriConsumer;
import jsweet.util.union.Union;


public class VishvaGUI {

	private Vishva vishva;

	boolean local = true;

	HTMLAnchorElement downloadLink;

	private static final String LARGE_ICON_SIZE = "width:128px;height:128px;";
	private static final String SMALL_ICON_SIZE = "width:64px;height:64px;";

	private boolean menuBarOn = false;

	public VishvaGUI(Vishva vishva) {

		this.vishva = vishva;

		HTMLButtonElement showMenu = (HTMLButtonElement) document.getElementById("showMenu");
		showMenu.style.visibility = "visible";
		
		
		document.getElementById("menubar").style.visibility ="visible";
		JQuery menuBar = (JQuery) ((Object) $("#menubar"));
		JQueryPositionOptions jpo = new JQueryPositionOptions() {
			{
				my = "left center";
				at = "right center";
				of = showMenu;
			}
		};
		menuBar.position(jpo);
		menuBar.hide(null);
		showMenu.onclick = (e) -> {
			if (menuBarOn) {
				menuBar.hide("slide");
			} else {
				menuBar.show("slide");
			}
			menuBarOn = !menuBarOn;
			return true;
		};

		createJPOs();
		updateAddMenu();
		setNavMenu();

		// edit dialog
		createEditDiag();

		// environment dialog
		createEnvDiag();

		// skyboxes dialog
		// createSkyBoxesDiag();

		// download file dialog
		createDownloadDiag();

		// upload file dialog
		createUploadDiag();

		// help dialog
		createHelpDiag();

		// alert diag
		createAlertDiag();

		// sensors and actuators dialog
		create_sNaDiag();

		// add sensors dialog
		createEditSensDiag();

		// add actuator dialog
		createEditActDiag();

		// add properties dialog
		createPropsDiag();

		window.addEventListener("resize", this::onWindowResize);
	}

	private JQueryPositionOptions centerBottom;
	private JQueryPositionOptions leftCenter;
	private JQueryPositionOptions rightCenter;

	private void createJPOs() {
		centerBottom = new JQueryPositionOptions() {
			{
				at = "center bottom";
				my = "center bottom";
				of = window;
				// within =this.vishva.canvas;
			}
		};
		leftCenter = new JQueryPositionOptions() {
			{
				at = "left center";
				my = "left center";
				of = window;
				// within =this.vishva.canvas;
			}
		};
		rightCenter = new JQueryPositionOptions() {
			{
				at = "right center";
				my = "right center";
				of = window;
				// within =this.vishva.canvas;
			}
		};

	}

	/**
	 * this array will be used store all dialogs whose position needs to be
	 * reset on window resize
	 */
	private Array<JQuery> dialogs = new Array<JQuery>();

	/**
	 * resposition all dialogs to their original default postions 
	 * without this, a window resize could end up moving some dialogs outside the window 
	 * and thus make them disappear
	 * 
	 * @param evt
	 */
	private void onWindowResize(Event evt) {
		for (JQuery jq : dialogs) {
			JQueryPositionOptions jpo = (JQueryPositionOptions) jq.$get("jpo");
			if (jpo != null) {
				jq.dialog("option", "position", jpo);
				boolean open = (boolean) (Object) jq.dialog("isOpen");
				if (open) {
					jq.dialog("close");
					jq.dialog("open");
				}
			}
		}
	}

	// skyboxes dialog
	JQuery skyboxesDiag;

	private void updateAddMenu() {
		String[] assetTypes = jsweet.lang.Object.keys(this.vishva.assets);
		HTMLUListElement addMenu = (HTMLUListElement) document.getElementById("AddMenu");
		java.util.function.Function<MouseEvent, Object> f = this::onAddMenuItemClick;
		for (String assetType : assetTypes) {

			if (assetType == "sounds") {
				continue;
			}
			HTMLLIElement li = document.createElement(StringTypes.li);
			li.id = "add-" + assetType;
			li.innerText = assetType;
			li.onclick = f;
			addMenu.appendChild(li);
		}
	}

	private Object onAddMenuItemClick(MouseEvent e) {
		// lazy load the asset tables
		HTMLLIElement li = (HTMLLIElement) e.target;
		JQuery jq = (JQuery) li.$get("diag");
		if (jq == null) {
			String assetType = li.innerHTML;
			jq = createAssetDiag(assetType);
			li.$set("diag", jq);
		}
		jq.dialog("open");
		return true;
	}

	private JQuery createAssetDiag(String assetType) {

		// a div for creating dialog for the asset type
		HTMLDivElement div = document.createElement(StringTypes.div);
		div.id = assetType + "Div";
		div.setAttribute("title", assetType);

		// a table to hold icons for each item in the asset type
		HTMLTableElement table = document.createElement(StringTypes.table);
		table.id = assetType + "Tbl";
		Array<String> items = (Array<String>) this.vishva.assets.$get(assetType);
		updateAssetTable(table, assetType, items);

		div.appendChild(table);
		document.body.appendChild(div);

		JQuery jq = (JQuery) ((Object) $("#" + div.id));
		DialogOptions dos = new DialogOptions() {
			{
				autoOpen = false;
				resizable = true;
				position = centerBottom;
				// width=800;
				width = union("100%");
				height = union("auto");
			}
		};

		jq.dialog(dos);
		jq.$set("jpo", centerBottom);
		dialogs.push(jq);
		return jq;
	}

	private void updateAssetTable(HTMLTableElement tbl, String assetType, Array<String> items) {
		// check if already created
		if (tbl.rows.length > 0) {
			return;
		}
		java.util.function.Function<MouseEvent, Object> f = this::onAssetImgClick;
		HTMLTableRowElement row = (HTMLTableRowElement) tbl.insertRow();
		for (String item : items) {
			HTMLImageElement img = document.createElement(StringTypes.img);
			img.id = item;
			img.src = "vishva/assets/" + assetType + "/" + item + "/" + item + ".jpg";
			// need to set the image size to help dialog figure out its size
			// before image is loaded.
			img.setAttribute("style", SMALL_ICON_SIZE + "cursor:pointer;");
			//img.setAttribute("alt", "Icon");
			img.className = assetType;
			img.onclick = f;

			HTMLTableCellElement cell = (HTMLTableCellElement) row.insertCell();
			cell.appendChild(img);
		}
		HTMLTableRowElement row2 = (HTMLTableRowElement) tbl.insertRow();
		for (String item : items) {
			HTMLTableCellElement cell = (HTMLTableCellElement) row2.insertCell();
			cell.innerText = item;
		}
	}

	private Object onAssetImgClick(Event e) {
		HTMLImageElement i = (HTMLImageElement) e.target;
		if (i.className == "skyboxes") {
			this.vishva.setSky(i.id);
		} else if (i.className == "primitives") {
			this.vishva.addPrim(i.id);
		} else {
			this.vishva.loadAsset(i.className, i.id);
		}
		return true;
	}

	// Edit dialog
	JQuery editDialog;

	private void createEditDiag() {
		JQuery editMenu = (JQuery) ((Object) $("#editMenu"));
		editMenu.menu();
		def.jquery.JQuery em = (def.jquery.JQuery) (Object) editMenu;
		em.unbind("keydown");

		editDialog = (JQuery) ((Object) $("#editDiv"));
//		JQueryPositionOptions jpo = new JQueryPositionOptions() {
//		};
//		jpo.at = "left center";
//		jpo.my = "left center";

		DialogOptions dos = new DialogOptions() {
			{
				autoOpen = false;
				resizable = false;
				position = leftCenter;
				width = union("auto");
				height = union("auto");
			}
		};
		editDialog.dialog(dos);

		editDialog.$set("jpo", leftCenter);
		dialogs.push(editDialog);
	}

	HTMLElement localAxis = document.getElementById("local");

	public boolean showEditMenu() {
		boolean alreadyOpen = (boolean) ((Object) editDialog.dialog("isOpen"));
		if (alreadyOpen)
			return alreadyOpen;
		if (this.vishva.isSpaceLocal()) {
			this.local = true;
			localAxis.innerHTML = "Switch to Global Axis";
		} else {
			this.local = false;
			localAxis.innerHTML = "Switch to Local Axis";
		}

		editDialog.dialog("open");
		return false;
	}

	public void closeEditMenu() {
		editDialog.dialog("close");
	}

	// Environment Dialog
	JQuery envDiag;

	private void createEnvDiag() {
		JQuery sunPos = (JQuery) ((Object) $("#sunPos"));
		JQuery light = (JQuery) ((Object) $("#light"));
		JQuery shade = (JQuery) ((Object) $("#shade"));
		JQuery fog = (JQuery) ((Object) $("#fog"));
		JQuery fov = (JQuery) ((Object) $("#fov"));

		sunPos.slider(sliderOptions(0, 180, this.vishva.getSunPos()));
		light.slider(sliderOptions(0, 100, 100 * this.vishva.getLight()));
		shade.slider(sliderOptions(0, 100, 100 * this.vishva.getShade()));
		fog.slider(sliderOptions(0, 100, 1000 * this.vishva.getFog()));
		fov.slider(sliderOptions(0, 180, this.vishva.getFov()));

		HTMLButtonElement skyButton = (HTMLButtonElement) document.getElementById("skyButton");
		skyButton.onclick = (e) -> {
			HTMLElement foo = document.getElementById("add-skyboxes");
			foo.click();
			return true;
		};
		
		HTMLButtonElement trnButton = (HTMLButtonElement) document.getElementById("trnButton");
		trnButton.onclick = (e) -> {
			showAlertDiag("Sorry. To be implemneted soon");
			return true;
		};

		HTMLElement colorEle = document.getElementById("color-picker");
		ColorPicker cp = new ColorPicker(colorEle, this::colorPickerHandler);
		Function setRGB = (Function) cp.$get("setRgb");
		double[] color = this.vishva.getGroundColor();
		if (color != null) {
			RGB rgb = new RGB();
			rgb.r = color[0];
			rgb.g = color[1];
			rgb.b = color[2];
			// setRGB.call(cp, rgb);
			cp.setRgb(rgb);
		}

		envDiag = (JQuery) ((Object) $("#envDiv"));

		DialogOptions dos1 = new DialogOptions() {
			{
				autoOpen = false;
				resizable = false;
				position = rightCenter;
				minWidth = 350;
				height = union("auto");
			}
		};
		envDiag.dialog(dos1);

		envDiag.$set("jpo", rightCenter);
		dialogs.push(envDiag);

	}

	// download file dialog
	JQuery downloadDialog;

	private void createDownloadDiag() {
		this.downloadLink = (HTMLAnchorElement) document.getElementById("downloadLink");
		this.downloadDialog = (JQuery) ((Object) $("#saveDiv"));
		this.downloadDialog.dialog();
		this.downloadDialog.dialog("close");

	}

	// Upload dialog
	JQuery loadDialog;

	private void createUploadDiag() {
		HTMLInputElement loadFileInput = (HTMLInputElement) document.getElementById("loadFileInput");
		HTMLButtonElement loadFileOk = (HTMLButtonElement) document.getElementById("loadFileOk");
		loadFileOk.onclick = (e) -> {
			FileList fl = loadFileInput.files;
			if (fl.length == 0) {
				alert("no file slected");
				return null;
			}
			File file = null;
			for (File f : fl) {
				file = f;
			}
			this.vishva.loadAssetFile(file);
			this.loadDialog.dialog("close");
			return true;
		};
		this.loadDialog = (JQuery) ((Object) $("#loadDiv"));
		this.loadDialog.dialog();
		this.loadDialog.dialog("close");

	}

	// help dialog
	JQuery helpDiag;

	private void createHelpDiag() {
		Object obj = $("#helpDiv");
		helpDiag = (JQuery) obj;
		DialogOptions dos = new DialogOptions() {
			{
				autoOpen = false;
				resizable = false;
				width = 500;
			}
		};
		helpDiag.dialog(dos);
	}

	JQuery sNaDialog;
	HTMLSelectElement sensSel;
	HTMLSelectElement actSel;
	HTMLTableElement sensTbl;
	HTMLTableElement actTbl;

	private void create_sNaDiag() {

		JQuery sNaDetails = (JQuery) ((Object) $("#sNaDetails"));
		sNaDetails.tabs();

		this.sNaDialog = (JQuery) ((Object) $("#sNaDiag"));
		DialogOptions dos = new DialogOptions() {
		};
		dos.autoOpen = false;
		dos.modal = false;
		dos.resizable = false;
		dos.width = "auto";
		dos.title = "Sensors and Actuators";
		dos.close = (e, ui) -> {
			this.vishva.switchDisabled = false;
		};
		this.sNaDialog.dialog(dos);

		sensSel = (HTMLSelectElement) document.getElementById("sensSel");
		actSel = (HTMLSelectElement) document.getElementById("actSel");
		String[] sensors = this.vishva.getSensorList();
		String[] actuators = this.vishva.getActuatorList();
		for (String sensor : sensors) {
			HTMLOptionElement opt = (HTMLOptionElement) document.createElement(StringTypes.option);
			opt.value = sensor;
			opt.innerHTML = sensor;
			sensSel.add(opt);
		}
		for (String actuator : actuators) {
			HTMLOptionElement opt = (HTMLOptionElement) document.createElement(StringTypes.option);
			opt.value = actuator;
			opt.innerHTML = actuator;
			actSel.add(opt);
		}
		this.sensTbl = (HTMLTableElement) document.getElementById("sensTbl");
		this.actTbl = (HTMLTableElement) document.getElementById("actTbl");
	}

	private void show_sNaDiag() {
		// console.log($("#sNaDiag").parent());
		// $("#sNaDiag").parent().css("width","auto !important");
		Array<SensorActuator> sens = (Array<SensorActuator>) this.vishva.getSensors();
		if (sens == null) {
			showAlertDiag("no mesh selected");
			return;
		}
		Array<SensorActuator> acts = this.vishva.getActuators();
		if (acts == null) {
			showAlertDiag("no mesh selected");
			return;
		}

		this.vishva.switchDisabled = true;
		updateSensActTbl(sens, this.sensTbl);
		updateSensActTbl(acts, this.actTbl);

		HTMLElement addSens = document.getElementById("addSens");
		addSens.onclick = (e) -> {
			HTMLOptionElement s = (HTMLOptionElement) sensSel.item(sensSel.selectedIndex);
			String sensor = s.value;
			this.vishva.addSensorbyName(sensor);
			updateSensActTbl(this.vishva.getSensors(), this.sensTbl);
			// close and open to reset width
			this.sNaDialog.dialog("close");
			this.sNaDialog.dialog("open");

			return true;
		};

		HTMLElement addAct = document.getElementById("addAct");
		addAct.onclick = (e) -> {
			HTMLOptionElement a = (HTMLOptionElement) actSel.item(actSel.selectedIndex);
			String actuator = a.value;
			this.vishva.addActuaorByName(actuator);
			updateSensActTbl(this.vishva.getActuators(), this.actTbl);
			// close and open to reset width
			this.sNaDialog.dialog("close");
			this.sNaDialog.dialog("open");

			return true;
		};
		this.sNaDialog.dialog("open");
	}

	private void updateSensActTbl(Array<SensorActuator> sensAct, HTMLTableElement tbl) {
		// delete all rows
		double l = tbl.rows.length;
		for (double i = l - 1; i > 0; i--) {
			tbl.deleteRow(i);
		}
		// add all sensors
		l = sensAct.length;

		for (double i = 0; i < l; i++) {
			HTMLTableRowElement row = (HTMLTableRowElement) tbl.insertRow();
			HTMLTableCellElement cell = (HTMLTableCellElement) row.insertCell();
			cell.innerHTML = sensAct.$get(i).getName();

			cell = (HTMLTableCellElement) row.insertCell();
			cell.innerHTML = sensAct.$get(i).getProperties().signalId;

			cell = (HTMLTableCellElement) row.insertCell();
			HTMLButtonElement editBut = (HTMLButtonElement) document.createElement("BUTTON");
			editBut.innerHTML = "edit";
			// editBut.className = "w3-btn w3-cyan w3-ripple w3-round-large";
			JQuery jq = (JQuery) ((Object) $(editBut));
			jq.button();
			Double d = i;
			editBut.id = d.toString();
			editBut.$set("sa", sensAct.$get(i));
			cell.appendChild(editBut);
			editBut.onclick = (e) -> {
				HTMLElement el = (HTMLElement) e.currentTarget;
				SensorActuator sa = (SensorActuator) el.$get("sa");
				if (sa.getType() == "SENSOR") {
					showEditSensDiag((Sensor) sa);
				} else {
					showEditActDiag((Actuator) sa);
				}
				return true;
			};

			cell = (HTMLTableCellElement) row.insertCell();
			HTMLButtonElement delBut = (HTMLButtonElement) document.createElement("BUTTON");
			delBut.innerHTML = "del";
			// delBut.className = "w3-btn w3-cyan w3-ripple w3-round-large";
			JQuery jq2 = (JQuery) ((Object) $(delBut));
			jq2.button();
			delBut.id = d.toString();
			delBut.$set("row", row);
			delBut.$set("sa", sensAct.$get(i));
			cell.appendChild(delBut);
			delBut.onclick = (e) -> {
				HTMLElement el = (HTMLElement) e.currentTarget;
				HTMLTableRowElement r = (HTMLTableRowElement) el.$get("row");
				tbl.deleteRow(r.rowIndex);
				this.vishva.removeSensorActuator((SensorActuator) el.$get("sa"));
				return true;
			};
		}
	}

	private void createEditSensDiag() {
		JQuery editSensDiag = (JQuery) ((Object) $("#editSensDiag"));

		DialogOptions dos = new DialogOptions() {
		};
		dos.autoOpen = false;
		dos.modal = true;
		dos.resizable = false;
		dos.width = "auto";
		dos.title = "Edit Sensor";

		editSensDiag.dialog(dos);
	}

	private void showEditSensDiag(Sensor sensor) {
		HTMLLabelElement sensNameEle = (HTMLLabelElement) document.getElementById("editSensDiag.sensName");
		sensNameEle.innerHTML = sensor.getName();
		JQuery editSensDiag = (JQuery) ((Object) $("#editSensDiag"));
		editSensDiag.dialog("open");

		HTMLElement parmDiv = document.getElementById("editSensDiag.parms");
		Node node = parmDiv.firstChild;
		if (node != null)
			parmDiv.removeChild(node);
		HTMLTableElement tbl = createForm(sensor.getProperties(), parmDiv.id);
		parmDiv.appendChild(tbl);

		DialogButtonOptions dbo = new DialogButtonOptions() {
		};
		dbo.text = "save";
		dbo.click = (e) -> {
			readForm(sensor.getProperties(), parmDiv.id);
			updateSensActTbl(this.vishva.getSensors(), this.sensTbl);
			editSensDiag.dialog("close");
			return true;
		};
		DialogButtonOptions[] dbos = new DialogButtonOptions[] { dbo };
		editSensDiag.dialog("option", "buttons", dbos);

	}

	private void createEditActDiag() {
		JQuery editActDiag = (JQuery) ((Object) $("#editActDiag"));
		DialogOptions dos = new DialogOptions() {
		};
		dos.autoOpen = false;
		dos.modal = true;
		dos.resizable = false;
		dos.width = "auto";
		dos.title = "Edit Actuator";
		editActDiag.dialog(dos);
	}

	private void showEditActDiag(Actuator actuator) {
		HTMLLabelElement actNameEle = (HTMLLabelElement) document.getElementById("editActDiag.actName");
		actNameEle.innerHTML = actuator.getName();
		JQuery editActDiag = (JQuery) ((Object) $("#editActDiag"));
		editActDiag.dialog("open");

		HTMLElement parmDiv = document.getElementById("editActDiag.parms");
		Node node = parmDiv.firstChild;
		if (node != null) {
			parmDiv.removeChild(node);
		}
		// TODO : need a better way to initialize sound actuator
		if (actuator.getName() == "Sound") {
			ActSoundProp prop = (ActSoundProp) actuator.getProperties();
			prop.soundFile.values = this.vishva.getSoundFiles();
			// prop.soundFile.value = prop.soundFile.values[0];
		}
		HTMLTableElement tbl = createForm(actuator.getProperties(), parmDiv.id);
		parmDiv.appendChild(tbl);

		DialogButtonOptions dbo = new DialogButtonOptions() {
		};
		dbo.text = "save";
		dbo.click = (e) -> {
			readForm(actuator.getProperties(), parmDiv.id);
			actuator.processUpdateGeneric();
			updateSensActTbl(this.vishva.getActuators(), this.actTbl);
			editActDiag.dialog("close");
			return true;
		};
		DialogButtonOptions[] dbos = new DialogButtonOptions[] { dbo };
		editActDiag.dialog("option", "buttons", dbos);
	}

	// TODO handle arrays and objects
	private HTMLTableElement createForm(SNAproperties snap, String idPrefix) {
		idPrefix = idPrefix + ".";
		HTMLTableElement tbl = document.createElement(StringTypes.table);
		String[] keys = jsweet.lang.Object.keys(snap);
		for (String key : keys) {
			HTMLTableRowElement row = (HTMLTableRowElement) tbl.insertRow();
			HTMLTableCellElement cell = (HTMLTableCellElement) row.insertCell();
			cell.innerHTML = key;
			cell = (HTMLTableCellElement) row.insertCell();

			// String t = (String) jsweet.lang.Globals.eval("typeof snap[key]");
			String t = typeof(snap.$get(key));
			//if ((t == "object") && (snap.$get(key) instanceof SelectType)) {
			if ((t == "object") && (((jsweet.lang.Object)snap.$get(key)).$get("type")=="SelectType")) {
				
				console.log("is of type SelectType");
				SelectType keyValue = (SelectType) snap.$get(key);
				String[] options = keyValue.values;
				HTMLSelectElement sel = document.createElement(StringTypes.select);
				sel.id = idPrefix + key;

				for (String option : options) {
					HTMLOptionElement opt = document.createElement(StringTypes.option);
					if (option == keyValue.value) {
						opt.selected = true;
					}
					opt.innerText = option;
					sel.add(opt);
				}
				cell.appendChild(sel);

			} else {
				HTMLInputElement inp = document.createElement(StringTypes.input);
				inp.id = idPrefix + key;
				inp.className = "ui-widget-content ui-corner-all";
				inp.value = (String) snap.$get(key);
				//if ((t == "object") && (snap.$get(key)) instanceof Range) {
				if ((t == "object") && (((jsweet.lang.Object)snap.$get(key)).$get("type")=="Range")) {
					Range r = (Range) snap.$get(key);
					inp.type = "range";
					inp.max = (new jsweet.lang.Number(r.max)).toString();
					inp.min = (new jsweet.lang.Number(r.min)).toString();
					inp.step = (new jsweet.lang.Number(r.step)).toString();
					inp.value = (new jsweet.lang.Number(r.value)).toString();
				} else if ((t == "string") || (t == "number")) {
					inp.type = "text";
					inp.value = (String) snap.$get(key);
				} else if (t == "boolean") {
					boolean check = (boolean) snap.$get(key);
					inp.type = "checkbox";
					if (check)
						inp.setAttribute("checked", "true");
				}
				cell.appendChild(inp);
			}

		}
		return tbl;
	}

	private void readForm(SNAproperties snap, String idPrefix) {
		idPrefix = idPrefix + ".";
		String[] keys = jsweet.lang.Object.keys(snap);
		for (String key : keys) {
			// String t = (String) jsweet.lang.Globals.eval("typeof snap[key]");
			String t = typeof(snap.$get(key));
			if ((t == "object") && (((jsweet.lang.Object)snap.$get(key)).$get("type")=="SelectType")) {
			//if ((t == "object") && (snap.$get(key) instanceof SelectType)) {
				SelectType s = (SelectType) snap.$get(key);
				HTMLSelectElement sel = (HTMLSelectElement) document.getElementById(idPrefix + key);
				s.value = sel.value;
			} else {
				HTMLInputElement ie = (HTMLInputElement) document.getElementById(idPrefix + key);
				if ((t == "object") && (((jsweet.lang.Object)snap.$get(key)).$get("type")=="Range")) {
				//if ((t == "object") && (snap.$get(key) instanceof Range)) {
					Range r = (Range) snap.$get(key);
					r.value = parseFloat(ie.value);
				} else if ((t == "string") || (t == "number")) {
					if (t == "number") {
						double v = parseFloat(ie.value);
						if (isNaN(v))
							snap.$set(key, 0);
						else
							snap.$set(key, v);
					} else {
						snap.$set(key, ie.value);
					}

				} else if (t == "boolean") {
					snap.$set(key, ie.checked);
				}
			}
		}
	}

	// add sensor dialog
	// private void createAddSensDiag() {
	// // JQuery sensMenu = (JQuery) ((Object) $("#sensMenu"));
	// // sensMenu.menu();
	//
	// JQuery addSensDiag = (JQuery) ((Object) $("#addSensDiag"));
	// DialogOptions dos = new DialogOptions() {
	// };
	// dos.autoOpen = false;
	// dos.modal = true;
	// dos.resizable = false;
	// dos.width = "auto";
	// dos.title = "Add Sensors";
	// DialogButtonOptions dbo = new DialogButtonOptions() {
	// };
	// dbo.text = "save";
	// dbo.click = (e) -> {
	// HTMLInputElement ele = (HTMLInputElement)
	// document.getElementById("touchSigId");
	// SNAproperties prop = new SNAproperties();
	// prop.signalId = ele.value;
	// String msg = this.vishva.add_sensor("Touch", prop);
	// if (msg != null) {
	// showAlertDiag(msg);
	// addSensDiag.dialog("close");
	// return true;
	// }
	// Array<SensorActuator> s = this.vishva.getSensors();
	// updateSensActTbl(s, this.sensTbl);
	// // refresh dialog to resize
	// this.sNaDialog.dialog("close");
	// this.sNaDialog.dialog("open");
	// //
	// addSensDiag.dialog("close");
	// return true;
	// };
	// DialogButtonOptions[] dbos = new DialogButtonOptions[] { dbo };
	// dos.buttons = union(dbos);
	//
	// addSensDiag.dialog(dos);
	// }

	// add sensor dialog
	// private void createAddActDiag() {
	// // JQuery sensMenu = (JQuery) ((Object) $("#sensMenu"));
	// // sensMenu.menu();
	//
	// JQuery addActDiag = (JQuery) ((Object) $("#addActDiag"));
	// DialogOptions dos = new DialogOptions() {
	// };
	// dos.autoOpen = false;
	// dos.modal = true;
	// dos.resizable = false;
	// dos.width = "auto";
	// dos.title = "Add Actuator";
	// DialogButtonOptions dbo = new DialogButtonOptions() {
	// };
	// dbo.text = "save";
	// dbo.click = (e) -> {
	// ActMoverParm parm = new ActMoverParm();
	// parm.signalId = ((HTMLInputElement)
	// document.getElementById("moveSigId")).value;
	// parm.x = Globals.parseFloat(((HTMLInputElement)
	// document.getElementById("moveX")).value);
	// parm.y = Globals.parseFloat(((HTMLInputElement)
	// document.getElementById("moveY")).value);
	// parm.z = Globals.parseFloat(((HTMLInputElement)
	// document.getElementById("moveZ")).value);
	// parm.duration = Globals.parseFloat(((HTMLInputElement)
	// document.getElementById("moveDuration")).value);
	// parm.local = ((HTMLInputElement)
	// document.getElementById("moveLocal")).checked;
	// parm.toggle = ((HTMLInputElement)
	// document.getElementById("moveToggle")).checked;
	// parm.startSigId = ((HTMLInputElement)
	// document.getElementById("moveSSigId")).value;
	// parm.endSigId = ((HTMLInputElement)
	// document.getElementById("moveESigId")).value;
	// String msg = this.vishva.addActuator("Mover", parm);
	// if (msg != null) {
	// showAlertDiag(msg);
	// addActDiag.dialog("close");
	// return true;
	// }
	// Array<SensorActuator> a = this.vishva.getActuators();
	// updateSensActTbl(a, this.actTbl);
	// // refresh dialog to resize
	// this.sNaDialog.dialog("close");
	// this.sNaDialog.dialog("open");
	// //
	// addActDiag.dialog("close");
	// return true;
	// };
	// DialogButtonOptions[] dbos = new DialogButtonOptions[] { dbo };
	// dos.buttons = union(dbos);
	//
	// addActDiag.dialog(dos);
	// }

	// properties dialog
	JQuery meshPropsDiag;
	HTMLSelectElement animSelect;
	HTMLInputElement animRate;
	HTMLInputElement animLoop;
	Skeleton skel;

	private void createPropsDiag() {
		JQuery meshPropstTab = (JQuery) ((Object) $("#meshPropsTab"));
		meshPropstTab.tabs();

		animSelect = (HTMLSelectElement) document.getElementById("animList");
		animSelect.onchange = (e) -> {
			String animName = animSelect.value;
			if (animName != null) {
				AnimationRange range = skel.getAnimationRange(animName);
				document.getElementById("animFrom").innerText = (new Number(range.from)).toString();
				document.getElementById("animTo").innerText = (new Number(range.to)).toString();
			}
			return true;
		};

		animRate = (HTMLInputElement) document.getElementById("animRate");
		animLoop = (HTMLInputElement) document.getElementById("animLoop");

		document.getElementById("playAnim").onclick = (e) -> {
			if (this.skel == null)
				return true;
			String animName = animSelect.value;
			String rate = animRate.value;
			if (animName != null) {
				this.vishva.playAnimation(animName, rate, animLoop.checked);
			}
			return true;
		};

		document.getElementById("stopAnim").onclick = (e) -> {
			if (this.skel == null)
				return true;
			this.vishva.stopAnimation();
			return true;
		};

		meshPropsDiag = (JQuery) ((Object) $("#meshPropsDiag"));
		DialogOptions dos = new DialogOptions() {
		};
		dos.autoOpen = false;
		dos.modal = false;
		dos.resizable = false;
		dos.width = 450;
		dos.height=union("300");
		dos.title = "Mesh Properties";
		dos.close = (e, ui) -> {
			this.vishva.switchDisabled = false;
		};
		meshPropsDiag.dialog(dos);
	}

	private void updateTransform() {
		Vector3 loc = this.vishva.getLocation();
		Vector3 rot = this.vishva.getRoation();
		Vector3 scl = this.vishva.getScale();
		document.getElementById("loc.x").innerText = toString(loc.x);
		document.getElementById("loc.y").innerText = toString(loc.y);
		document.getElementById("loc.z").innerText = toString(loc.z);

		document.getElementById("rot.x").innerText = toString(rot.x);
		document.getElementById("rot.y").innerText = toString(rot.y);
		document.getElementById("rot.z").innerText = toString(rot.z);

		document.getElementById("scl.x").innerText = toString(scl.x);
		document.getElementById("scl.y").innerText = toString(scl.y);
		document.getElementById("scl.z").innerText = toString(scl.z);
	}

	private void updateAnimations() {
		this.skel = this.vishva.getSkeleton();

		String skelName;
		if (this.skel == null) {
			document.getElementById("skelName").innerText = "no skeleton";
			return;
		} else {
			skelName = this.skel.name;
			if (skelName.trim() == "")
				skelName = "no name";
		}
		document.getElementById("skelName").innerText = skelName;

		HTMLCollection childs = animSelect.children;
		int l = (int) childs.length;
		for (int i = l - 1; i >= 0; i--) {
			childs.$get(i).remove();
		}
		if (skelName != null) {
			AnimationRange[] range = this.vishva.getAnimationRanges();
			HTMLOptionElement animOpt;
			for (AnimationRange ar : range) {
				animOpt = document.createElement(StringTypes.option);
				animOpt.value = ar.name;
				animOpt.innerText = ar.name;
				animSelect.appendChild(animOpt);
			}
			if (range[0] != null) {
				document.getElementById("animFrom").innerText = (new Number(range[0].from)).toString();
				document.getElementById("animTo").innerText = (new Number(range[0].to)).toString();
			}
		}
	}

	private String toString(double d) {
		return (new jsweet.lang.Number(d)).toFixed(2).toString();
	}

	// alert dialog
	JQuery alertDialog;
	HTMLElement alertDiv;

	private void createAlertDiag() {
		this.alertDiv = document.getElementById("alertDiv");
		this.alertDialog = (JQuery) ((Object) $("#alertDiv"));
		DialogOptions dos = new DialogOptions() {
			{
				title = "Error";
				autoOpen = false;
			}
		};
		this.alertDialog.dialog(dos);
	}

	private void showAlertDiag(String msg) {
		this.alertDiv.innerHTML = msg;
		this.alertDialog.dialog("open");
	}

	private SliderOptions sliderOptions(double min, double max, double value) {
		SliderOptions so = new SliderOptions() {
		};
		so.min = min;
		so.max = max;
		so.value = value;
		so.slide = this::handleSlide;
		return so;
	}

	private boolean handleSlide(Event e, SliderUIParams ui) {
		String slider = ((HTMLElement) e.target).id;
		if (slider == "fov") {
			this.vishva.setFov(ui.value);
		} else if (slider == "sunPos") {
			this.vishva.setSunPos(ui.value);
		} else {

			double v = ui.value / 100;
			if (slider == "light") {
				this.vishva.setLight(v);
			} else if (slider == "shade") {
				this.vishva.setShade(v);
			} else if (slider == "fog") {
				this.vishva.setFog(v / 10);
			}
		}
		return true;
	}

	private void colorPickerHandler(Object hex, Object hsv, RGB rgb) {
		double[] colors = new double[] { rgb.r, rgb.g, rgb.b };
		this.vishva.setGroundColor(colors);
	}

	boolean firstTime = true;
	boolean addMenuOn = false;

	private void setNavMenu() {
		Object slideDown = JSON.parse("{\"direction\":\"up\"}");
		HTMLElement navAdd = document.getElementById("navAdd");
		JQuery addMenu = (JQuery) ((Object) $("#AddMenu"));
		addMenu.menu();
		addMenu.hide(null);
		navAdd.onclick = (e) -> {
			if (firstTime) {
				JQueryPositionOptions jpo = new JQueryPositionOptions() {
					{
						my = "left top";
						at = "left bottom";
						of = navAdd;
					}
				};
				addMenu.menu().position(jpo);
				firstTime = false;
			}
			if (addMenuOn) {
				addMenu.menu().hide("slide", slideDown);
			} else {
				addMenu.show("slide", slideDown);
			}
			addMenuOn=!addMenuOn;
			// lets hide the menu the moment we click somewhere in the document
			$(document).one("click", (jqe) -> {
				if (addMenuOn){
					addMenu.menu().hide("slide", slideDown);
					addMenuOn=false;
				}
				return true;
			});
			// lets prevent this click from bubbling up to document and
			// triggering the "one" above
			e.cancelBubble = true;

			return true;
		};

		// navWorld
		HTMLElement downWorld = document.getElementById("downWorld");
		downWorld.onclick = (e) -> {
			// worldsMenu.menu().hide(null);
			String downloadURL = this.vishva.saveWorld();
			if (downloadURL == null)
				return true;

			downloadLink.href = downloadURL;
			this.downloadDialog.dialog("open");
			return false;

		};

		// navAsset

		// HTMLFormElement fileLoadForm = (HTMLFormElement)
		// document.getElementById("fileLoadForm");
		//
		// HTMLElement insAsset = document.getElementById("insAsset");
		// insAsset.onclick = (e) -> {
		// assetsMenu.menu().hide(null);
		// fileLoadForm.reset();
		// this.loadDialog.dialog("open");
		// return true;
		//
		// };

		// navEnv
		HTMLElement navEnv = document.getElementById("navEnv");
		navEnv.onclick = (e) -> {
			envDiag = (JQuery) ((Object) $("#envDiv"));
			envDiag.dialog("open");
			return false;

		};

		// navEdit
		HTMLElement navEdit = document.getElementById("navEdit");
		navEdit.onclick = (e) -> {
			showEditMenu();
			return true;
		};

		// help
		HTMLElement helpLink = document.getElementById("helpLink");
		helpLink.onclick = (e) -> {
			this.helpDiag.dialog("open");
			return true;
		};
		// debug
		HTMLElement debugLink = document.getElementById("debugLink");
		debugLink.onclick = (e) -> {
			this.vishva.toggleDebug();
			return true;
		};

		// edit menu
		HTMLElement swAv = document.getElementById("swAv");
		HTMLElement swGnd = document.getElementById("swGnd");
		// HTMLElement instMesh = document.getElementById("instMesh");
		HTMLElement parentMesh = document.getElementById("parentMesh");
		HTMLElement cloneMesh = document.getElementById("cloneMesh");
		HTMLElement delMesh = document.getElementById("delMesh");
		HTMLElement undo = document.getElementById("undo");
		HTMLElement redo = document.getElementById("redo");
		HTMLElement sNa = document.getElementById("sNa");
		HTMLElement meshProps = document.getElementById("meshProps");

		swGnd.onclick = (e) -> {
			String err = this.vishva.switchGround();
			if (err != null) {
				this.showAlertDiag(err);
			}
			return true;
		};

		swAv.onclick = (e) -> {
			String err = this.vishva.switch_avatar();
			if (err != null) {
				this.showAlertDiag(err);
			}
			return true;
		};

		HTMLElement downAsset = document.getElementById("downMesh");
		downAsset.onclick = (e) -> {
			// assetsMenu.menu().hide(null);
			String downloadURL = this.vishva.saveAsset();
			if (downloadURL == null) {
				this.showAlertDiag("No Mesh Selected");
				return true;
			}

			downloadLink.href = downloadURL;
			JQuery env = (JQuery) ((Object) $("#saveDiv"));
			env.dialog("open");
			return false;

		};
		
		parentMesh.onclick = (e) -> {
			String err = this.vishva.makeParent();
			if (err != null) {
				this.showAlertDiag(err);
			}
			return false;
		};
		
		
		
		/*
		 * instMesh.onclick = (e) -> { String err = this.vishva.instance_mesh();
		 * if (err != null) { this.showAlertDiag(err); } return false; };
		 */
		cloneMesh.onclick = (e) -> {
			String err = this.vishva.clone_mesh();
			if (err != null) {
				this.showAlertDiag(err);
			}
			return false;
		};
		delMesh.onclick = (e) -> {
			String err = this.vishva.delete_mesh();
			if (err != null) {
				this.showAlertDiag(err);
			}
			return false;
		};
		undo.onclick = (e) -> {
			this.vishva.undo();
			return false;
		};
		redo.onclick = (e) -> {
			this.vishva.redo();
			return false;
		};

		// ????
		localAxis.onclick = (MouseEvent e) -> {
			this.local = !this.local;
			if (this.local) {
				((HTMLElement) e.currentTarget).innerHTML = "Switch to Global Axis";
			} else {
				((HTMLElement) e.currentTarget).innerHTML = "Switch to Local Axis";
			}
			this.vishva.setSpaceLocal(local);
			return true;
		};

		sNa.onclick = (e) -> {
			show_sNaDiag();
			return true;
		};

		meshProps.onclick = (e) -> {
			if (!this.vishva.anyMeshSelected()) {
				this.showAlertDiag("no mesh selected");
				return true;
			}
			this.vishva.switchDisabled = true;
			updateTransform();
			updateAnimations();
			this.meshPropsDiag.dialog("open");
			return true;
		};

	}

}

/*
 * color pickers
 * http://www.jqueryrain.com/demo/jquery-color-picker/
 * https://github.com/PitPik/tinyColorPicker
 * http://www.abeautifulsite.net/jquery-minicolors-a-color-selector-for-input-controls/
 * https://github.com/DavidDurman/FlexiColorPicker
 * 
 * 
 */
@Ambient
class ColorPicker extends jsweet.lang.Object {
	public ColorPicker(HTMLElement e, TriConsumer<Object, Object, RGB> f) {
	};

	public native void setRgb(RGB rgb);
}

class RGB {
	double r;
	double g;
	double b;

}

class Range {
	public final String type="Range";
	public double min;
	public double max;
	public double value;
	public double step;

	public Range(double min, double max, double value, double step) {
		this.min = min;
		this.max = max;
		this.value = value;
		this.step = step;
	}
}

class SelectType {
	public final String type="SelectType";
	public String[] values;
	public String value;
}
