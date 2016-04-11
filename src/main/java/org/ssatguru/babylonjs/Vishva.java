package org.ssatguru.babylonjs;

import jsweet.util.Globals;

import static jsweet.dom.Globals.alert;
import static jsweet.dom.Globals.console;
import static jsweet.dom.Globals.document;
import static jsweet.dom.Globals.window;
import static jsweet.lang.Globals.isNaN;
import static jsweet.lang.Globals.parseFloat;
import static jsweet.util.Globals.function;

import java.util.function.Supplier;

import org.ssatguru.babylonjs.component.EditControl;

import def.babylonjs.babylon.AbstractMesh;
import def.babylonjs.babylon.Action;
import def.babylonjs.babylon.ActionEvent;
import def.babylonjs.babylon.ActionManager;
import def.babylonjs.babylon.Animatable;
import def.babylonjs.babylon.Animation;
import def.babylonjs.babylon.AnimationRange;
import def.babylonjs.babylon.ArcRotateCamera;
import def.babylonjs.babylon.AssetsManager;
import def.babylonjs.babylon.Axis;
import def.babylonjs.babylon.BaseTexture;
import def.babylonjs.babylon.Bone;
import def.babylonjs.babylon.BoundingInfo;
import def.babylonjs.babylon.Camera;
import def.babylonjs.babylon.Color3;
import def.babylonjs.babylon.CubeTexture;
import def.babylonjs.babylon.DirectionalLight;
import def.babylonjs.babylon.Engine;
import def.babylonjs.babylon.ExecuteCodeAction;
import def.babylonjs.babylon.GroundMesh;
import def.babylonjs.babylon.HemisphericLight;
import def.babylonjs.babylon.IAssetTask;
import def.babylonjs.babylon.IShadowLight;
import def.babylonjs.babylon.InstancedMesh;
import def.babylonjs.babylon.Light;
import def.babylonjs.babylon.Material;
import def.babylonjs.babylon.Matrix;
import def.babylonjs.babylon.Mesh;
import def.babylonjs.babylon.MultiMaterial;
import def.babylonjs.babylon.ParticleSystem;
import def.babylonjs.babylon.PickingInfo;
import def.babylonjs.babylon.Quaternion;
import def.babylonjs.babylon.Scene;
import def.babylonjs.babylon.SceneLoader;
import def.babylonjs.babylon.SceneSerializer;
import def.babylonjs.babylon.ShadowGenerator;
import def.babylonjs.babylon.Skeleton;
import def.babylonjs.babylon.Sound;
import def.babylonjs.babylon.StandardMaterial;
import def.babylonjs.babylon.Tags;
import def.babylonjs.babylon.TextFileAssetTask;
import def.babylonjs.babylon.Texture;
import def.babylonjs.babylon.Vector3;
import BABYLON.WaterMaterial;
//import def.babylonjs.babylon.WaterMaterial;
import jsweet.dom.Event;
import jsweet.dom.File;
import jsweet.dom.FileReader;
import jsweet.dom.HTMLCanvasElement;
import jsweet.dom.HTMLElement;
import jsweet.dom.KeyboardEvent;
import jsweet.dom.PointerEvent;
import jsweet.dom.URL;
import jsweet.lang.Array;
import jsweet.lang.Date;
import jsweet.lang.Function;
import jsweet.lang.Interface;
import jsweet.lang.JSON;
import jsweet.lang.Math;

/**
 * @author satguru
 *
 */

public class Vishva {

	String actuator = "none";

	Scene scene;
	Engine engine;
	HTMLCanvasElement canvas;
	boolean editEnabled;
	jsweet.lang.Array<String> skyboxes;
	jsweet.lang.Object assets;

	// starter assets
	String skyboxTextures = "vishva/internal/textures/skybox-default/default";
	String avatarFolder = "vishva/internal/avatar/";
	String avatarFile = "starterAvatars.babylon";
	// String groundHeightMap = "vishva/internal/ground/heightMap.png";
	String groundTexture = "vishva/internal/textures/ground.jpg";
	String primTexture = "vishva/internal/textures/Birch.jpg";

	HemisphericLight sun;
	DirectionalLight sunDR;
	Mesh skybox;
	Mesh ground;
	Mesh avatar;
	Skeleton avatarSkeleton;
	ArcRotateCamera mainCamera;

	VishvaGUI vishvaGUI;
	boolean editAlreadyOpen = false;
	
	/**
	 * use this to prevent users from switching to another mesh during edit.
	 */
	public boolean switchDisabled = false;

	AnimData walk, walkBack, idle, run, jump, turnLeft, turnRight,strafeLeft,strafeRight;
	AnimData[] anims;
	double avatarSpeed = 0.05;
	AnimData prevAnim = null;

	Key key;
	
	HTMLElement loadingMsg ;

	// options
	boolean showBoundingBox = false;
	boolean cameraCollision = false;

	public Vishva(String scenePath, String sceneFile, String canvasId, boolean editEnabled, jsweet.lang.Object assets ) {
		
		if (!Engine.isSupported()) {
			alert("not supported");
			return;
		}
		
		this.loadingMsg = document.getElementById("loadingMsg");
		
		this.editEnabled=editEnabled;

		this.assets = assets;
		this.key = new Key();
		initAnims();

		this.canvas = (HTMLCanvasElement) document.getElementById(canvasId);
		this.engine = new Engine(canvas, true);
		this.scene = new Scene(engine);

		// add event handlers
		window.addEventListener("resize", this::onWindowResize);
		window.addEventListener("keydown", this::onKeyDown, false);
		window.addEventListener("keyup", this::onKeyUp, false);

		this.scenePath = scenePath;
		this.sceneFile = sceneFile;
		
		//this.engine.hideLoadingUI();
		
		if (sceneFile == null) {
			onSceneLoaded(this.scene);
		} else {
			loadSceneFile(scenePath, sceneFile+".js", this.scene);
		}

	}

	String scenePath;
	String sceneFile;

	private void loadSceneFile(String scenePath, String sceneFile, Scene scene) {
		AssetsManager am = new AssetsManager(scene);
		IAssetTask task = am.addTextFileTask("sceneLoader", scenePath + sceneFile);
		task.onSuccess = this::onTaskSuccess;
		task.onError = this::onTaskFailure;
		am.load();
	}

	SNAserialized[] snas;
	private void onTaskSuccess(Object obj) {
		TextFileAssetTask tfat = (TextFileAssetTask) obj;
		jsweet.lang.Object foo = (jsweet.lang.Object) JSON.parse(tfat.text);
		snas = (SNAserialized[]) foo.$get("VishvaSNA");
		String sceneData = "data:" + tfat.text;
		SceneLoader.ShowLoadingScreen= false;
		SceneLoader.Append(this.scenePath, sceneData, this.scene, this::onSceneLoaded);
	}
	


	private void onTaskFailure(Object obj) {
		alert("scene load failed");

	}

	// private void initAnims() {
	// this.walk = new AnimData("walk", 4, 18, 1);
	// this.walkBack = new AnimData("walkback", 20, 33, 0.5);
	// //this.idle = new AnimData("idle", 1, 2, 0.01);
	// this.idle = new AnimData("idle", 102, 112, 0.1);
	// this.run = new AnimData("run", 35, 49, 1.5);
	// this.jump = new AnimData("jump", 51, 52, 1);
	// this.turnLeft = new AnimData("turnLeft", 54, 76, 1);
	// this.turnRight = new AnimData("turnRight", 78, 100, 1);
	// anims = new AnimData[] { walk, walkBack, idle, run, jump, turnLeft,
	// turnRight };
	// }

	private void initAnims() {
		this.walk = new AnimData("walk", 7, 35, 1);
		this.walkBack = new AnimData("walkBack", 39, 65, 0.5);
		this.idle = new AnimData("idle", 203, 283, 1);
		this.run = new AnimData("run", 69, 95, 1);
		this.jump = new AnimData("jump", 101, 103, 0.5);
		this.turnLeft = new AnimData("turnLeft", 107, 151, 0.5);
		this.turnRight = new AnimData("turnRight", 155, 199, 0.5);
		this.strafeLeft = new AnimData("strafeLeft", 0, 0, 1);
		this.strafeRight = new AnimData("strafeRight", 0, 0, 1);
		anims = new AnimData[] { walk, walkBack, idle, run, jump, turnLeft, turnRight, strafeLeft, strafeRight };
	}

	private void onWindowResize(Event event) {
		engine.resize();
	}

	// esc 27
	// alt 18
	// shift 16
	// ctl 17
	// right 39
	// left 37
	// up 38
	// down 40
	// page up 33
	// page down 34
	// space 32

	// set to false for one time handling
	// set to true for continuous handling
	private void onKeyDown(Event e) {

		KeyboardEvent event = (KeyboardEvent) e;

		if (event.keyCode == 16)
			this.key.shift = true;
		if (event.keyCode == 17)
			this.key.ctl = true;
		if (event.keyCode == 32)
			this.key.jump = false;
		if (event.keyCode == 27)
			this.key.esc = false;

		String chr = jsweet.lang.String.fromCharCode(event.keyCode);
		if ((chr == "W") || (event.keyCode == 38))
			this.key.up = true;
		if ((chr == "A") || (event.keyCode == 37))
			this.key.left = true;
		if ((chr == "D") || (event.keyCode == 39))
			this.key.right = true;
		if ((chr == "S") || (event.keyCode == 40))
			this.key.down = true;

		if (chr == "Q")
			this.key.stepLeft = true;
		if (chr == "E")
			this.key.stepRight = true;

		if (chr == "1")
			this.key.trans = false;
		if (chr == "2")
			this.key.rot = false;
		if (chr == "3")
			this.key.scale = false;
		if (chr == "F")
			this.key.focus = false;
	}

	private void onKeyUp(Event e) {

		KeyboardEvent event = (KeyboardEvent) e;

		if (event.keyCode == 16)
			this.key.shift = false;
		if (event.keyCode == 17)
			this.key.ctl = false;
		if (event.keyCode == 32)
			this.key.jump = true;
		if (event.keyCode == 27)
			this.key.esc = true;

		String chr = jsweet.lang.String.fromCharCode(event.keyCode);

		if ((chr == "W") || (event.keyCode == 38))
			this.key.up = false;
		if ((chr == "A") || (event.keyCode == 37))
			this.key.left = false;
		if ((chr == "D") || (event.keyCode == 39))
			this.key.right = false;
		if ((chr == "S") || (event.keyCode == 40))
			this.key.down = false;
		if (chr == "Q")
			this.key.stepLeft = false;
		if (chr == "E")
			this.key.stepRight = false;

		if (chr == "1")
			this.key.trans = true;
		if (chr == "2")
			this.key.rot = true;
		if (chr == "3")
			this.key.scale = true;
		if (chr == "F")
			this.key.focus = true;
	}

	// *************************************

	/**
	 * material for primitives
	 */
	private StandardMaterial primMaterial;

	private void createPrimMaterial() {
		primMaterial = new StandardMaterial("primMat", this.scene);
		primMaterial.diffuseTexture = new Texture(this.primTexture, this.scene);
		primMaterial.diffuseColor = new Color3(1, 1, 1);
		primMaterial.specularColor = new Color3(0, 0, 0);
	}

	private void setPrimProperties(Mesh mesh) {
		if (this.primMaterial == null)
			createPrimMaterial();
		// place the object in front of the avatar
		double r = mesh.getBoundingInfo().boundingSphere.radiusWorld;
		Vector3 placementLocal = new Vector3(0, r, -(r + 2));
		Vector3 placementGlobal = Vector3.TransformCoordinates(placementLocal, this.avatar.getWorldMatrix());
		mesh.position.addInPlace(placementGlobal);
		//

		mesh.material = primMaterial;
		mesh.checkCollisions = true;
		Globals.array(this.shadowGenerator.getShadowMap().renderList).push(mesh);
		mesh.receiveShadows = true;
		Tags.AddTagsTo(mesh, "Vishva.prim Vishva.internal");
		mesh.id = (new jsweet.lang.Number(Date.now())).toString();
		mesh.name = mesh.id;
	}

	public void addPrim(String primType) {
		if (primType == "plane")
			addPlane();
		else if (primType == "box")
			addBox();
		else if (primType == "sphere")
			addSphere();
		else if (primType == "disc")
			addDisc();
		else if (primType == "cylinder")
			addCylinder();
		else if (primType == "cone")
			addCone();
		else if (primType == "torus")
			addTorus();

	}

	public void addPlane() {
		Mesh mesh = Mesh.CreatePlane("", 1.0, this.scene);
		setPrimProperties(mesh);
	}

	public void addBox() {
		Mesh mesh = Mesh.CreateBox("", 1, this.scene);
		setPrimProperties(mesh);
	}

	public void addSphere() {
		Mesh mesh = Mesh.CreateSphere("", 10, 1, this.scene);
		setPrimProperties(mesh);
	}

	public void addDisc() {
		Mesh mesh = Mesh.CreateDisc("", 0.5, 20, this.scene);
		setPrimProperties(mesh);
	}

	public void addCylinder() {
		Mesh mesh = Mesh.CreateCylinder("", 1, 1, 1, 20, 1, this.scene);
		setPrimProperties(mesh);
	}

	public void addCone() {
		Mesh mesh = Mesh.CreateCylinder("", 1, 0, 1, 20, 1, this.scene);
		setPrimProperties(mesh);
	}

	public void addTorus() {
		Mesh mesh = Mesh.CreateTorus("", 1, 0.25, 20, this.scene);
		setPrimProperties(mesh);
		// mesh.position = Vector3.Zero();
		// mesh.position.addInPlace(this.avatar.position.add(new Vector3(2, 0,
		// 2))).addInPlace(new Vector3(0, 0.25, 0));

	}

	public String switchGround() {
		if (!this.isMeshSelected) {
			return "no mesh selected";
		}
		Tags.RemoveTagsFrom(this.ground, "Vishva.ground");
		this.ground.isPickable = true;
		this.ground = (Mesh) this.meshPicked;
		this.ground.isPickable = false;
		Tags.AddTagsTo(this.ground, "Vishva.ground");

		removeEditControl();
		return null;
	}

	public String instance_mesh() {
		if (!this.isMeshSelected) {
			return "no mesh selected";
		}
		String name = (new jsweet.lang.Number(Date.now())).toString();
		InstancedMesh inst = ((Mesh) this.meshPicked).createInstance(name);
		inst.position = this.meshPicked.position.add(new Vector3(0.1, 0.1, 0.1));
		this.meshPicked = inst;
		swicthEditControl(inst);
		inst.receiveShadows = true;
		Globals.array(this.shadowGenerator.getShadowMap().renderList).push(inst);
		return null;
	}

	public String clone_mesh() {
		if (!this.isMeshSelected) {
			return "no mesh selected";
		}
		String name = (new jsweet.lang.Number(Date.now())).toString();
		AbstractMesh clone = this.meshPicked.clone(name, null, true);
		// cloning copies sensors and actuators key but not value
		// each sensor and actuator can only be attached to one mesh
		// so clean them up
		clone.$delete("sensors");
		clone.$delete("actuators");
		clone.position = this.meshPicked.position.add(new Vector3(0.1, 0.1, 0.1));
		this.meshPicked = clone;
		// this.editControl.switchTo((Mesh) clone);
		swicthEditControl(clone);
		clone.receiveShadows = true;
		Globals.array(this.shadowGenerator.getShadowMap().renderList).push(clone);
		return null;
	}

	public String delete_mesh() {
		if (!this.isMeshSelected) {
			return "no mesh selected";
		}
		removeEditControl();
		
		//remove all sensors and actuators asscoiated with this mesh
		SNAManager.getSNAManager().removeSNAs(this.meshPicked);
		
		//remove this mesh from the shadow generator map 
		Array<AbstractMesh> meshes = Globals.array(this.shadowGenerator.getShadowMap().renderList);
		double i = meshes.indexOf(this.meshPicked);
		if (i >=0){
			meshes.splice(i,1);
		}
		
		this.meshPicked.dispose();
		return null;

	}

	public void setSpaceLocal(Object lcl) {
		if (this.editControl != null)
			this.editControl.setLocal((boolean) lcl);
		return;
	}

	public boolean isSpaceLocal() {
		if (this.editControl != null)
			return this.editControl.isLocal();
		else
			return true;
	}

	public void undo() {
		if (this.editControl != null)
			this.editControl.undo();
		return;
	}

	public void redo() {
		if (this.editControl != null)
			this.editControl.redo();
		return;
	}

	public String[] getSoundFiles() {
		return (String[]) this.assets.$get("sounds");
	}
	
	//selected mesh properties
	
	public boolean anyMeshSelected(){
		return this.isMeshSelected;
	}
	
	public Vector3 getLocation(){
		return this.meshPicked.position;
	}
	
	public Vector3 getRoation(){
		Vector3 euler = this.meshPicked.rotationQuaternion.toEulerAngles();
		double r = 180/Math.PI;
		Vector3 degrees = euler.multiplyByFloats(r, r, r);
		return degrees;
	}
	public Vector3 getScale(){
		return this.meshPicked.scaling;
	}
	//selected mesh skeleton and animations
	public String getSkelName(){
		if (this.meshPicked.skeleton == null ) return null;
		else return this.meshPicked.skeleton.name;
	}
	
	public Skeleton getSkeleton(){
		if (this.meshPicked.skeleton == null ) return null;
		else return this.meshPicked.skeleton;
	}
	
	public AnimationRange[] getAnimationRanges(){
		Skeleton skel = this.meshPicked.skeleton;
		Function getAnimationRanges =  (Function) skel.$get("getAnimationRanges");
		AnimationRange[] ranges = (AnimationRange[]) getAnimationRanges.call(skel);
		return ranges;
	}
	
	public void printAnimCount(Skeleton skel){
		Bone[] bones = skel.bones;
		for(Bone bone:bones){
			console.log(bone.name + "," + bone.animations.length + " , " + bone.animations[0].getHighestFrame() );
			//bone.animations[0].goToFrame(10);
			console.log(bone.animations[0]);
		}
	}
	
	public void playAnimation(String animName, String animRate, boolean loop){
		Skeleton skel = this.meshPicked.skeleton;
		if (skel == null) return;
		double r = parseFloat(animRate);
		if (isNaN(r)) r=1;
		skel.beginAnimation(animName, loop, r);
	}
	
	public void stopAnimation(){
		if (this.meshPicked.skeleton == null) return;
		this.scene.stopAnimation(this.meshPicked.skeleton);
	}

	// ********************************************
	// sensors and actuators
	// ********************************************
	public String[] getSensorList() {
		return SNAManager.getSNAManager().getSensorList();
	}

	public String[] getActuatorList() {
		return SNAManager.getSNAManager().getActuatorList();
	}

	public jsweet.lang.Object getSensorParms(String sensor) {
		return SNAManager.getSNAManager().getSensorParms(sensor);
	}

	public jsweet.lang.Object getActuatorParms(String actuator) {
		return SNAManager.getSNAManager().getActuatorParms(actuator);
	}

	public Array<SensorActuator> getSensors() {
		if (!this.isMeshSelected) {
			return null;
		}
		Array<SensorActuator> sens = (Array<SensorActuator>) this.meshPicked.$get("sensors");
		if (sens == null)
			sens = new Array<SensorActuator>();
		return sens;
	}

	public Array<SensorActuator> getActuators() {
		if (!this.isMeshSelected) {
			return null;
		}

		Array<SensorActuator> acts = (Array<SensorActuator>) this.meshPicked.$get("actuators");
		if (acts == null)
			acts = new Array<SensorActuator>();
		return acts;
	}

	public Sensor addSensorbyName(String sensName) {
		if (!this.isMeshSelected) {
			return null;
		}
		return SNAManager.getSNAManager().createSensorByName(sensName, (Mesh) this.meshPicked, null);
	}

	public Actuator addActuaorByName(String actName) {
		if (!this.isMeshSelected) {
			return null;
		}
		return SNAManager.getSNAManager().createActuatorByName(actName, (Mesh) this.meshPicked, null);
	}

	public String add_sensor(String sensName, SNAproperties prop) {

		if (!this.isMeshSelected) {
			return "no mesh selected";
		}
		if (sensName == "Touch") {
			SensorTouch st = new SensorTouch((Mesh) this.meshPicked, prop);
		} else
			return "No such sensor";

		return null;
	}

	public String addActuator(String actName, SNAproperties parms) {
		if (!this.isMeshSelected) {
			return "no mesh selected";
		}
		Actuator act;
		if (actName == "Rotator") {
			act = new ActuatorRotator((Mesh) this.meshPicked, (ActRotatorParm) parms);
		} else if (actName == "Mover") {
			act = new ActuatorMover((Mesh) this.meshPicked, (ActMoverParm) parms);
		} else
			return "No such actuator";
		return null;
	}

	public String removeSensor(double index) {
		if (!this.isMeshSelected) {
			return "no mesh selected";
		}
		Array<Sensor> sensors = (Array<Sensor>) this.meshPicked.$get("sensors");
		if (sensors != null) {
			Sensor sens = sensors.$get(index);
			if (sens != null) {
				sens.dispose();
			} else
				return "no sensor found";
		} else
			return "no sensor found";
		return null;
	}

	public String removeActuator(double index) {
		if (!this.isMeshSelected) {
			return "no mesh selected";
		}
		Array<Actuator> actuators = (Array<Actuator>) this.meshPicked.$get("actuators");
		if (actuators != null) {
			Actuator act = actuators.$get(index);
			if (act != null) {
				act.dispose();
			} else
				return "no actuator found";
		} else
			return "no actuator found";
		return null;
	}

	public void removeSensorActuator(SensorActuator sa) {
		sa.dispose();
	}

	public void setSunPos(double d) {
		double r = Math.PI * (180 - d) / 180;
		double x = -Math.cos(r);
		double y = -Math.sin(r);
		this.sunDR.direction = new Vector3(x, y, 0);
	}

	public double getSunPos() {
		Vector3 sunDir = this.sunDR.direction;
		double x = sunDir.x;
		double y = sunDir.y;
		double l = Math.sqrt(x * x + y * y);
		double d = Math.acos(x / l);
		return d * 180 / Math.PI;
	}

	public void setLight(double d) {
		this.sun.intensity = d;
		this.sunDR.intensity = d;
	}

	public double getLight() {
		return this.sun.intensity;
	}

	public void setShade(Object dO) {
		double d = (double) dO;
		d = 1 - d;
		this.sun.groundColor = new Color3(d, d, d);
	}

	public double getShade() {
		return (1 - this.sun.groundColor.r);
	}

	public void setFog(Object d) {
		this.scene.fogDensity = (double) d;
	}

	public double getFog() {
		return this.scene.fogDensity;
	}

	public void setFov(Object dO) {
		double d = (double) dO;
		this.mainCamera.fov = (d * 3.14 / 180);
	}

	public double getFov() {
		return this.mainCamera.fov * 180 / 3.14;
	}

	public void setSky(Object sky) {
		StandardMaterial mat = (StandardMaterial) this.skybox.material;
		mat.reflectionTexture.dispose();
		String skyFile = "vishva/assets/skyboxes/" + sky + "/" + sky;
		mat.reflectionTexture = new CubeTexture(skyFile, this.scene);
		mat.reflectionTexture.coordinatesMode = Texture.SKYBOX_MODE;
	}

	public String getSky() {
		StandardMaterial mat = (StandardMaterial) this.skybox.material;
		String skyname = mat.reflectionTexture.name;
		int i = skyname.lastIndexOf("/");
		return skyname.substring(i + 1);
	}

	public void setGroundColor(Object gcolor) {
		double[] ground_color = (double[]) gcolor;
		double r = ground_color[0] / 255;
		double g = ground_color[1] / 255;
		double b = ground_color[2] / 255;
		Color3 color = new Color3(r, g, b);
		StandardMaterial gmat = (StandardMaterial) this.ground.material;
		gmat.diffuseColor = color;
	}

	public double[] getGroundColor() {
		double[] ground_color = new double[3];
		StandardMaterial gmat = (StandardMaterial) this.ground.material;
		if (gmat.diffuseColor != null) {
			ground_color[0] = (gmat.diffuseColor.r * 255);
			ground_color[1] = (gmat.diffuseColor.g * 255);
			ground_color[2] = (gmat.diffuseColor.b * 255);
			return ground_color;
		} else {
			return null;
		}
	}

	public void toggleDebug() {
		if (this.scene.debugLayer.isVisible()) {
			this.scene.debugLayer.hide();
		} else {
			this.scene.debugLayer.show();
		}
	}
	// ******************************
	// save asset to file
	// ******************************

	public String saveAsset() {
		if (!this.isMeshSelected) {
			return null;
		}
		renameWorldTextures();
		// Mesh clone = (Mesh) this.meshPicked.clone("clooney", null,false);
		Mesh clone = (Mesh) this.meshPicked.clone(this.meshPicked.name, null);
		clone.position = Vector3.Zero();
		clone.rotation = Vector3.Zero();
		// Object meshObj = SceneSerializer.SerializeMesh(clone,false,true);
		Object meshObj = SceneSerializer.SerializeMesh(clone, false);
		clone.dispose();
		String meshString = JSON.stringify(meshObj);
		File file = new File(new String[] { meshString }, "AssetFile.babylon");
		return URL.createObjectURL(file);

	}

	// ************************************
	// save world to file
	// ************************************

	public String saveWorld() {
		if (this.editControl != null) {
			alert("cannot save during edit");
			return null;
		}

		cleanupSkels();
		resetSkels(this.scene);

		cleanupMats();
		renameWorldTextures();

		// StandardMaterial sm;
		// String textureName;
		//
		// AbstractMesh[] meshes = this.scene.meshes;
		//
		// // change the location of all internal asset textures
		// for (AbstractMesh mesh : meshes) {
		// if (Tags.HasTags(mesh)) {
		// if (Tags.MatchesQuery(mesh, "Vishva.internal")) {
		// sm = (StandardMaterial) mesh.material;
		// if (sm.diffuseTexture != null) {
		// textureName = sm.diffuseTexture.name;
		// if (textureName.substring(0, 2) != "..") {
		// sm.diffuseTexture.name = "../../../" + textureName;
		// }
		// }
		// }
		// }
		// }
		//
		// sm = (StandardMaterial) this.skybox.material;
		// textureName = sm.reflectionTexture.name;
		// if (textureName.substring(0, 2) != "..") {
		// sm.reflectionTexture.name = "../../../" + textureName;
		// }
		
		jsweet.lang.Object snaObj = SNAManager.getSNAManager().serializeSnAs(this.scene);
		String snaObjStr = JSON.stringify(snaObj);
		
		jsweet.lang.Object sceneObj = (jsweet.lang.Object) SceneSerializer.Serialize(this.scene);
		sceneObj.$set("VishvaSNA", snaObj);
		String sceneString = JSON.stringify(sceneObj);

		File file = new File(new String[] { sceneString }, "WorldFile.babylon");
		return URL.createObjectURL(file);

	}

	/**
	 * resets each skel and assign unique id to each skeleton
	 * 
	 * @param scene
	 */
	private void resetSkels(Scene scene) {
		int i = 0;
		for (Skeleton skel : scene.skeletons) {
			skel.id = (new jsweet.lang.Number(i)).toString();
			i++;
			skel.returnToRest();
			// for (Bone bone : skel.bones) {
			// //bone.updateMatrix(bone.getBaseMatrix());
			// bone.updateMatrix((Matrix) bone.$get("_restPose"));
			//
			// }
		}
	}

	// private void renameTextures() {
	// Material[] mats = this.scene.materials;
	// StandardMaterial sm;
	// String textureName;
	// for (Material mat : mats) {
	// if (mat instanceof StandardMaterial) {
	// sm = (StandardMaterial) mat;
	// if (sm.diffuseTexture != null) {
	// textureName = sm.diffuseTexture.name;
	// if (textureName.substring(0, 2) != "..") {
	// sm.diffuseTexture.name = "../../../../" + textureName;
	// }
	// }
	// if (sm.reflectionTexture != null) {
	// textureName = sm.reflectionTexture.name;
	// if (textureName.substring(0, 2) != "..") {
	// sm.reflectionTexture.name = "../../../../" + textureName;
	// }
	// }
	// if (sm.opacityTexture != null) {
	// textureName = sm.opacityTexture.name;
	// if (textureName.substring(0, 2) != "..") {
	// sm.opacityTexture.name = "../../../../" + textureName;
	// }
	// }
	// if (sm.specularTexture != null) {
	// textureName = sm.specularTexture.name;
	// if (textureName.substring(0, 2) != "..") {
	// sm.specularTexture.name = "../../../../" + textureName;
	// }
	// }
	// if (sm.bumpTexture != null) {
	// textureName = sm.bumpTexture.name;
	// if (textureName.substring(0, 2) != "..") {
	// sm.bumpTexture.name = "../../../../" + textureName;
	// }
	// }
	// }
	// }
	// }

	private void renameWorldTextures() {
		Material[] mats = this.scene.materials;
		renameWorldMaterials(mats);
		MultiMaterial[] mms = this.scene.multiMaterials;
		for (MultiMaterial mm : mms) {
			renameWorldMaterials(mm.subMaterials);
		}
	}

	private void renameWorldMaterials(Material[] mats) {
		StandardMaterial sm;
		for (Material mat : mats) {
			if (mat instanceof StandardMaterial) {
				sm = (StandardMaterial) mat;
				rename(sm.diffuseTexture);
				rename(sm.reflectionTexture);
				rename(sm.opacityTexture);
				rename(sm.specularTexture);
				rename(sm.bumpTexture);
			}
		}
	}

	public void rename(BaseTexture bt) {
		if (bt == null)
			return;
		if (bt.name.substring(0, 2) != "..") {
			bt.name = "../../../../" + bt.name;
		}
	}

	/**
	 * remove all materials not referenced by any mesh
	 * 
	 */
	private void cleanupMats() {
		AbstractMesh[] meshes = this.scene.meshes;
		Array<Material> mats = new Array<Material>();
		Array<MultiMaterial> mms = new Array<MultiMaterial>();

		for (AbstractMesh mesh : meshes) {
			if (mesh.material != null) {
				if (mesh.material instanceof MultiMaterial) {
					MultiMaterial mm = (MultiMaterial) mesh.material;
					mms.push(mm);
					Material[] ms = mm.subMaterials;
					for (Material mat : ms) {
						mats.push(mat);
					}
				} else {
					mats.push(mesh.material);
				}
			}
		}

		// remove all materials not referenced by mesh
		Material[] allMats = this.scene.materials;
		double l = allMats.length;
		// check in reverse - disposing messes up index
		for (double i = l - 1; i >= 0; i--) {
			if (mats.indexOf(allMats[(int) i]) == -1) {
				allMats[(int) i].dispose();
			}
		}
		// remove all multimaterials not referenced by mesh
		MultiMaterial[] allMms = this.scene.multiMaterials;
		l = allMms.length;
		// check in reverse - disposing messes up index
		for (double i = l - 1; i >= 0; i--) {
			if (mms.indexOf(allMms[(int) i]) == -1) {
				allMms[(int) i].dispose();
			}
		}

	}

	/**
	 * remove all skeletons not referenced by any mesh
	 * 
	 */
	private void cleanupSkels() {
		AbstractMesh[] meshes = this.scene.meshes;
		Array<Skeleton> skels = new Array<Skeleton>();

		for (AbstractMesh mesh : meshes) {
			if (mesh.skeleton != null) {
				skels.push(mesh.skeleton);
			}
		}
		Skeleton[] allSkels = this.scene.skeletons;
		double l = allSkels.length;
		for (double i = l - 1; i >= 0; i--) {
			if (skels.indexOf(allSkels[(int) i]) == -1) {
				allSkels[(int) i].dispose();
			}
		}

	}

	// **********************
	// load asset from file
	// **********************

	public void loadAssetFile(File file) {
		String sceneFolderName = file.name.split(".")[0];
		SceneLoader.ImportMesh("", "vishva/assets/" + sceneFolderName + "/", file.name, scene, this::onMeshLoaded);
	}

	String assetType, file;

	public void loadAsset(String assetType, String file) {
		this.assetType = assetType;
		this.file = file;
		SceneLoader.ImportMesh("", "vishva/assets/" + assetType + "/" + file + "/", file + ".babylon", this.scene,
				this::onMeshLoaded);
	}

	private void onMeshLoaded(AbstractMesh[] meshes, ParticleSystem[] particleSystems, Skeleton[] skeletons) {
		double boundingRadius = getBoundingRadius(meshes);
		for (Mesh mesh : (Mesh[]) meshes) {
			mesh.isPickable = true;
			mesh.checkCollisions = true;

			// place the object in front of the avatar
			Vector3 placementLocal = new Vector3(0, 0, -(boundingRadius + 2));
			Vector3 placementGlobal = Vector3.TransformCoordinates(placementLocal, this.avatar.getWorldMatrix());
			mesh.position.addInPlace(placementGlobal);
			Globals.array(this.shadowGenerator.getShadowMap().renderList).push(mesh);
			mesh.receiveShadows = true;

			if (mesh.material instanceof MultiMaterial) {
				MultiMaterial mm = (MultiMaterial) mesh.material;
				Material[] mats = mm.subMaterials;
				for (Material mat : mats) {
					mesh.material.backFaceCulling = false;
					mesh.material.alpha = 1;
					if (mat instanceof StandardMaterial) {
						renameAssetTextures((StandardMaterial) mat);
					}
				}
			} else {
				mesh.material.backFaceCulling = false;
				mesh.material.alpha = 1;
				StandardMaterial sm = (StandardMaterial) mesh.material;
				renameAssetTextures(sm);
			}
			
			if (mesh.skeleton != null){
				fixAnimationRanges(mesh.skeleton);
			}
		}

	}

	private void renameAssetTextures(StandardMaterial sm) {
		renameAssetTexture(sm.diffuseTexture);
		renameAssetTexture(sm.reflectionTexture);
		renameAssetTexture(sm.opacityTexture);
		renameAssetTexture(sm.specularTexture);
		renameAssetTexture(sm.bumpTexture);
	}

	public void renameAssetTexture(BaseTexture bt) {
		if (bt == null)
			return;
		String textureName = bt.name;
		
		//TODO
		//something going on with texture name.
		//if the name of the texture matches the name of  an already laoded texture 
		//then it reuses the already loaded texture - is indexing being doen by texture name?
		//it still loads the new texture though. wonder what happens to that.
		
		//if (textureName.indexOf("vishva/assets/") != 0 && textureName.indexOf("../") != 0) {
		if (textureName.indexOf("vishva/") != 0 && textureName.indexOf("../") != 0) {
			bt.name = "vishva/assets/" + this.assetType + "/" + this.file + "/" + textureName;
			console.log("renamed to " + bt.name);
		}

	}

	/**
	 * finds the bounding sphere radius for a set of meshes. for each mesh gets
	 * bounding radius from the local center. this is the bounding world radius
	 * for that mesh plus the distance from the local center. takes the maximum
	 * of these
	 * 
	 * @param meshes
	 * @return
	 */
	private double getBoundingRadius(AbstractMesh[] meshes) {
		double maxRadius = 0;
		for (AbstractMesh mesh : meshes) {
			BoundingInfo bi = mesh.getBoundingInfo();
			double r = bi.boundingSphere.radiusWorld + mesh.position.length();
			if (maxRadius < r)
				maxRadius = r;
		}
		return maxRadius;
	}

	// **********************
	// load world from file
	// **********************

	private void loadWorldFile(File file) {
		this.sceneFolderName = file.name.split(".")[0];
		FileReader fr = new FileReader();
		fr.onload = this::onSceneFileRead;
		fr.readAsText(file);
	}

	private Object onSceneFileRead(Event e) {
		this.sceneData = "data:" + (String) ((FileReader) e.target).result;
		this.engine.stopRenderLoop();
		this.scene.onDispose = this::onSceneDispose;
		this.scene.dispose();
		return null;
	}

	String sceneFolderName;
	String sceneData;

	private void onSceneDispose() {
		this.scene = null;
		this.avatarSkeleton = null;
		this.avatar = null;
		this.prevAnim = null;
		SceneLoader.Load("worlds/" + this.sceneFolderName + "/", this.sceneData, engine, this::onSceneLoaded);
	}

	ShadowGenerator shadowGenerator;

	private void onSceneLoaded(Scene scene) {

		boolean avFound = false;
		boolean skelFound = false;
		boolean sunFound = false;
		boolean groundFound = false;
		boolean skyFound = false;
		boolean cameraFound = false;

		for (AbstractMesh mesh : scene.meshes) {

			if (Tags.HasTags(mesh)) {
				if (Tags.MatchesQuery(mesh, "Vishva.avatar")) {
					avFound = true;
					this.avatar = (Mesh) mesh;
					// bug?? ellipsoidOffset is not serialized.
					this.avatar.ellipsoidOffset = new Vector3(0, 2, 0);
				} else if (Tags.MatchesQuery(mesh, "Vishva.sky")) {
					skyFound = true;
					this.skybox = (Mesh) mesh;
					this.skybox.isPickable = false;
				} else if (Tags.MatchesQuery(mesh, "Vishva.ground")) {
					groundFound = true;
					this.ground = (Mesh) mesh;
				}

			}
		}

		for (Skeleton skeleton : scene.skeletons) {
			// bug? skeleton tags not supported
			if (Tags.MatchesQuery(skeleton, "Vishva.skeleton") || (skeleton.name == "Vishva.skeleton")) {
				skelFound = true;
				this.avatarSkeleton = skeleton;
			}

		}
		if (!skelFound) {
			console.error("ALARM: No Skeleton found");
		}

		// check for sun
		for (Light light : scene.lights) {
			if (Tags.MatchesQuery(light, "Vishva.sun")) {
				sunFound = true;
				this.sun = (HemisphericLight) light;
			}
		}

		if (!sunFound) {
			console.log("no vishva sun found. creating sun");
			HemisphericLight hl = new HemisphericLight("Vishva.hl01", new Vector3(0, 1, 0), this.scene);
			// goundColor effects shading
			// hl.groundColor = Color3.White();
			hl.groundColor = new Color3(0.5, 0.5, 0.5);
			hl.intensity = 0.4;
			this.sun = hl;
			Tags.AddTagsTo(hl, "Vishva.sun");
			this.sunDR = new DirectionalLight("Vishva.dl01", new Vector3(-1, -1, 0), this.scene);
			this.sunDR.intensity = 0.5;
			IShadowLight sl = (IShadowLight) ((Object) this.sunDR);
			shadowGenerator = new ShadowGenerator(1024, sl);
			// shadowGenerator.useVarianceShadowMap = true;
			this.shadowGenerator.useBlurVarianceShadowMap = true;
			// this.shadowGenerator.usePoissonSampling = true;
			this.shadowGenerator.bias = 0.000001;
		} else {
			for (Light light : scene.lights) {
				if (light.id == "Vishva.dl01") {
					this.sunDR = (DirectionalLight) light;
					this.shadowGenerator = light.getShadowGenerator();
					// the following is not serialized in scene file
					this.shadowGenerator.bias = 0.000001;
					this.shadowGenerator.useBlurVarianceShadowMap = true;
				}
			}
		}

		// check for main camera
		for (Camera camera : scene.cameras) {
			if (Tags.MatchesQuery(camera, "Vishva.camera")) {
				cameraFound = true;
				this.mainCamera = (ArcRotateCamera) camera;

				// this.scene.activeCamera = this.mainCamera;
				// not all settings are serialized need to set them again after
				// load
				setCameraSettings(this.mainCamera);
				this.mainCamera.attachControl(this.canvas, true);

				// workaround for bug in 2.4 02/11/2016
				// fixed in the version of 2.4 as of 03/22/2016
				// if (avFound) {
				// Vector3 pos =
				// this.avatar.position.add(this.mainCamera.position);
				// pos.y = pos.y + 1.5;
				// this.mainCamera.setPosition(pos);
				// }
			}
		}
		if (!cameraFound) {
			console.log("no vishva camera found. creating camera");
			this.mainCamera = createCamera(this.scene, this.canvas);
			this.scene.activeCamera = this.mainCamera;

		}

		if (!groundFound) {
			console.log("no vishva ground found. creating ground");
			this.ground = createGround(this.scene);
		}
		if (!skyFound) {
			console.log("no vishva sky found. creating sky");
			this.skybox = createSkyBox(this.scene);
		}

		if (this.scene.fogMode != Scene.FOGMODE_EXP) {
			this.scene.fogMode = Scene.FOGMODE_EXP;
			this.scene.fogDensity = 0;
		}
		
		if (this.editEnabled){
			this.scene.onPointerDown = this::pickObject;
		}

		// createWater();

		if (!avFound) {
			console.log("no vishva av found. creating av");
			loadAvatar();
		}

		// load the sensors and actuators
		SNAManager.getSNAManager().unMarshal(this.snas, this.scene);
		// garbage collect
		this.snas = null;

		
		render();

	}

	private void createWater() {
		Mesh waterMesh = Mesh.CreateGround("waterMesh", 512, 512, 32, scene, false);
		waterMesh.position.y = 1;
		WaterMaterial water = new WaterMaterial("water", scene);
		water.bumpTexture = new Texture("waterbump.png", this.scene);
		// Water properties
		water.windForce = -5;
		water.waveHeight = 0.5;
		// water.windDirection = new Vector2(1, 1);
		water.waterColor = new Color3(0.1, 0.1, 0.6);
		water.colorBlendFactor = 0;
		water.bumpHeight = 0.1;
		water.waveLength = 0.1;

		// Add skybox and ground to the reflection and refraction
		water.addToRenderList(this.skybox);
		// water.addToRenderList(this.ground);

		// Assign the water material
		waterMesh.material = water;
	}

	// ********************************
	// avatar
	// ********************************

	public String switch_avatar() {
		if (!this.isMeshSelected) {
			return "no mesh selected";
		}

		if (isAvatar((Mesh) this.meshPicked)) {
			this.avatar.isPickable = true;

			Tags.RemoveTagsFrom(this.avatar, "Vishva.avatar");
			Tags.RemoveTagsFrom(this.avatarSkeleton, "Vishva.skeleton");
			this.avatarSkeleton.name = "";

			this.avatar = (Mesh) this.meshPicked;
			this.avatarSkeleton = this.avatar.skeleton;
			Tags.AddTagsTo(this.avatar, "Vishva.avatar");
			Tags.AddTagsTo(this.avatarSkeleton, "Vishva.skeleton");
			this.avatarSkeleton.name = "Vishva.skeleton";
			setAnimationRange(this.avatarSkeleton);

			this.avatar.checkCollisions = true;
			this.avatar.ellipsoid = new Vector3(0.5, 1, 0.5);
			this.avatar.ellipsoidOffset = new Vector3(0, 2, 0);
			this.avatar.isPickable = false;

			// edit control switches the system to rotationQuaternion,
			// switch back to euler
			this.avatar.rotation = this.avatar.rotationQuaternion.toEulerAngles();
			this.avatar.rotationQuaternion = null;

			this.saveAVcameraPos = this.mainCamera.position;
			this.focusOnAv = false;
			removeEditControl();

		} else {
			return "cannot use this as avatar";
		}
		return null;
	}

	private boolean isAvatar(Mesh mesh) {
		if (mesh.skeleton == null) {
			return false;
		}
		return true;
	}

	private void setAvatar(String avName, AbstractMesh[] meshes) {
		Mesh mesh;
		for (AbstractMesh amesh : meshes) {
			mesh = (Mesh) amesh;
			if ((mesh.id == avName)) {
				Vector3 saveRotation, savePosition;
				if (this.avatar != null) {
					saveRotation = this.avatar.rotation;
					savePosition = this.avatar.position;
				} else {
					saveRotation = new Vector3(0, Math.PI, 0);
					savePosition = new Vector3(0, 0, 0);
				}
				this.avatar = mesh;
				this.avatar.rotation = saveRotation;
				this.avatar.position = savePosition;
				this.avatar.visibility = 1;
				this.avatar.skeleton = this.avatarSkeleton;
				this.avatar.checkCollisions = true;
				this.avatar.ellipsoid = new Vector3(0.5, 1, 0.5);
				this.avatar.ellipsoidOffset = new Vector3(0, 2, 0);
				this.avatar.isPickable = false;
			} else {
				mesh.skeleton = null;
				mesh.visibility = 0;
				mesh.checkCollisions = false;
			}
		}

	}

	// ****************************************
	// rendering
	// ****************************************
	private void render() {

		this.scene.registerBeforeRender(this::process);

		// start rendering scene once textures and shaders are loaded
		this.scene.executeWhenReady(this::startRenderLoop);
	}

	private void startRenderLoop() {
		backfaceCulling(this.scene.materials);
		if (this.editEnabled){
			vishvaGUI = new VishvaGUI(this);
		}else{
			vishvaGUI = null;
		}
		this.engine.hideLoadingUI();
		this.loadingMsg.parentNode.removeChild(this.loadingMsg);
		this.engine.runRenderLoop(() -> this.scene.render());
	}

	boolean focusOnAv = true;
	boolean cameraAnimating = false;

	private void process() {

		if (this.cameraAnimating)
			return;

		if (this.mainCamera.radius < 0.75) {
			this.avatar.visibility = 0;
		} else {
			this.avatar.visibility = 1;
		}
		if (this.isMeshSelected) {
			if (this.key.focus) {
				this.key.focus = false;
				if (this.focusOnAv) {
					this.saveAVcameraPos.copyFrom(this.mainCamera.position);
					this.focusOnAv = false;
				}
				focusOnMesh(this.meshPicked, 25);

			}
			if (this.key.esc) {
				this.key.esc = false;
				removeEditControl();
			}
			if (this.key.trans) {
				this.key.trans = false;
				this.editControl.enableTranslation();
			}
			if (this.key.rot) {
				this.key.rot = false;
				this.editControl.enableRotation();
			}
			if (this.key.scale) {
				this.key.scale = false;
				this.editControl.enableScaling();
			}
		}
		// donot do anything if the camera i animating to focus on av
		// if the camera is focused on av then it is ok to move the av
		// if the user hasn't selected anything for editing or if the user has
		// but is not currently moving,translating or scaling an object
		// if the camera is not focused on av and if the user presses up or down
		// key then assume the user
		// wants to refocus on av, and so start the process of refocusing the av

		if (focusOnAv) {
			if (this.editControl == null) {
				moveAvatarCamera();
			} else {
				if (!this.editControl.isEditing()) {
					moveAvatarCamera();
				}
			}
		} else if (this.key.up || this.key.down) {
			if (!this.editControl.isEditing()) {
				switchFocusToAV();
				// moveAvatarCamera();
			}
		}

	}

	private double jumpCycleMax = 25;
	private double jumpCycle = jumpCycleMax;
	private boolean wasJumping = false;

	private void moveAvatarCamera() {
		AnimData anim = idle;
		boolean moving = false;
		double speed = 0, upSpeed = .05, dir = 1;
		Vector3 forward, backwards, stepLeft, stepRight, up;

		if (this.key.up) {
			if (this.key.shift) {
				speed = this.avatarSpeed * 2;
				anim = this.run;
			} else {
				speed = this.avatarSpeed;
				anim = this.walk;
			}
			if (this.key.jump) {
				this.wasJumping = true;
			}
			if (this.wasJumping) {
				upSpeed *= 2;
				if (jumpCycle < jumpCycleMax / 2) {
					dir = 1;
					if (jumpCycle < 0) {
						jumpCycle = jumpCycleMax;
						upSpeed /= 2;
						this.key.jump = false;
						this.wasJumping = false;
					}
				} else {
					anim = this.jump;
					dir = -1;
				}
				jumpCycle--;
			}
//			forward = new Vector3(Math.sin(this.avatar.rotation.y) * speed, upSpeed * dir,
//					Math.cos(this.avatar.rotation.y) * speed);
//			forward = forward.negate();
			forward = this.avatar.calcMovePOV(0,-upSpeed*dir, speed);
			this.avatar.moveWithCollisions(forward);
			moving = true;
		} else if (this.key.down) {
//			backwards = new Vector3(Math.sin(this.avatar.rotation.y) * (this.avatarSpeed / 2), -upSpeed,
//					Math.cos(this.avatar.rotation.y) * (this.avatarSpeed / 2));
			backwards = this.avatar.calcMovePOV(0,-upSpeed*dir, -this.avatarSpeed/2);
			this.avatar.moveWithCollisions(backwards);
			moving = true;
			anim = this.walkBack;
			if (this.key.jump)
				this.key.jump = false;
		} else if (this.key.stepLeft) {
			anim=this.strafeLeft;
			stepLeft = this.avatar.calcMovePOV(-this.avatarSpeed / 2, 0, 0);
			this.avatar.moveWithCollisions(stepLeft);
			moving = true;
		} else if (this.key.stepRight) {
			anim=this.strafeRight;
			stepRight = this.avatar.calcMovePOV(this.avatarSpeed / 2, 0, 0);
			this.avatar.moveWithCollisions(stepRight);
			moving = true;
		}

		if (!moving) {
			if (this.key.jump) {
				this.wasJumping = true;
			}
			if (this.wasJumping) {
				upSpeed *= 2;
				if (jumpCycle < jumpCycleMax / 2) {
					dir = 1;
					if (jumpCycle < 0) {
						jumpCycle = jumpCycleMax;
						upSpeed /= 2;
						this.key.jump = false;
						this.wasJumping = false;
					}
				} else {
					anim = this.jump;
					dir = -1;
				}
				jumpCycle--;
			} else
				dir = dir / 2;

			this.avatar.moveWithCollisions(new Vector3(0, -upSpeed * dir, 0));
		}
		if (!this.key.stepLeft && !this.key.stepRight) {
			if (this.key.left) {

				this.mainCamera.alpha = this.mainCamera.alpha + 0.022;
				if (!moving) {
					this.avatar.rotation.y = -4.69 - this.mainCamera.alpha;
					anim = this.turnLeft;
				}

			} else if (this.key.right) {

				this.mainCamera.alpha = this.mainCamera.alpha - 0.022;
				if (!moving) {
					this.avatar.rotation.y = -4.69 - this.mainCamera.alpha;
					anim = this.turnRight;
				}
			}
		}
		if (moving) {
			this.avatar.rotation.y = -4.69 - this.mainCamera.alpha;

		}

		if (this.prevAnim != anim) {

			this.avatarSkeleton.beginAnimation(anim.name, true, anim.r);
			this.prevAnim = anim;
		}

		this.mainCamera.target = new Vector3(this.avatar.position.x, (this.avatar.position.y + 1.5),
				this.avatar.position.z);

	}

	// *****************************************
	// Editor
	// *****************************************
	private AbstractMesh meshPicked;
	private boolean isMeshSelected = false;
	private Vector3 cameraTargetPos = new Vector3(0, 0, 0);
	private Vector3 saveAVcameraPos = new Vector3(0, 0, 0);
	private EditControl editControl;

	private void pickObject(PointerEvent evt, PickingInfo pickResult) {

		// prevent curosr from changing to a edit caret in Chrome
		evt.preventDefault();

		if (evt.button != 2)
			return;

		if (key.ctl)
			return;

		if (pickResult.hit) {

			if (!this.isMeshSelected) {
				// if none selected hen select the one clicked
				this.isMeshSelected = true;
				this.meshPicked = pickResult.pickedMesh;
				this.meshPicked.showBoundingBox = this.showBoundingBox;
				SNAManager.getSNAManager().disableSnAs((Mesh) this.meshPicked);
				editControl = new EditControl((Mesh) this.meshPicked, this.mainCamera, this.canvas, 0.75);
				editControl.enableTranslation();
				this.editAlreadyOpen = vishvaGUI.showEditMenu();
			} else {
				if (pickResult.pickedMesh == this.meshPicked) {
					//if clicked on already selected then focus on it
					if (this.focusOnAv) {
						this.saveAVcameraPos.copyFrom(this.mainCamera.position);
						this.focusOnAv = false;
					}

					focusOnMesh(this.meshPicked, 50);
				} else {
					// switch to this 
					swicthEditControl(pickResult.pickedMesh);

				}
			}

		}
	}

	
	/**
	 * switch the edit control to the new mesh
	 * @param mesh
	 */
	private void swicthEditControl(AbstractMesh mesh) {
		
		if (this.switchDisabled) return; 

		SNAManager.getSNAManager().enableSnAs(this.meshPicked);

		this.meshPicked.showBoundingBox = false;
		this.meshPicked = mesh;
		this.meshPicked.showBoundingBox = this.showBoundingBox;
		editControl.switchTo((Mesh) this.meshPicked);
		SNAManager.getSNAManager().disableSnAs((Mesh) this.meshPicked);
	}

	private void removeEditControl() {
		this.isMeshSelected = false;
		this.meshPicked.showBoundingBox = false;
		if (!focusOnAv) {
			switchFocusToAV();
		}
		this.editControl.detach();
		this.editControl = null;

		if (!this.editAlreadyOpen)
			this.vishvaGUI.closeEditMenu();
		SNAManager.getSNAManager().enableSnAs(this.meshPicked);
		// SNAManager.getSNAManager().processQueue(this.meshPicked);
	}

	private void switchFocusToAV() {
		Vector3 avTarget = new Vector3(this.avatar.position.x, (this.avatar.position.y + 1.5), this.avatar.position.z);
		// this.mainCamera.target = avTarget;
		// this.mainCamera.setPosition(saveAVcameraPos);
		// this.focusOnAv = true;
		this.mainCamera.detachControl(this.canvas);
		this.frames = 25;
		this.f = this.frames;
		this.delta = saveAVcameraPos.subtract(this.mainCamera.position).scale(1 / this.frames);
		this.delta2 = avTarget.subtract(((Vector3) this.mainCamera.target)).scale(1 / this.frames);
		this.cameraAnimating = true;
		this.scene.registerBeforeRender(animFunc);
	}

	private void focusOnMesh(AbstractMesh mesh, double frames) {
		this.mainCamera.detachControl(this.canvas);
		this.frames = frames;
		this.f = frames;
		this.delta2 = mesh.position.subtract(((Vector3) this.mainCamera.target)).scale(1 / this.frames);
		this.cameraAnimating = true;
		this.scene.registerBeforeRender(animFunc2);
	}

	Runnable animFunc = this::animateCamera;
	Runnable animFunc2 = this::justReFocus;
	double frames;
	double f;
	Vector3 delta;
	Vector3 delta2;

	private void animateCamera() {
		this.mainCamera.setTarget(((Vector3) this.mainCamera.target).add(delta2));
		this.mainCamera.setPosition(this.mainCamera.position.add(delta));
		f--;
		if (f < 0) {
			this.focusOnAv = true;
			this.cameraAnimating = false;
			this.scene.unregisterBeforeRender(animFunc);
			this.mainCamera.attachControl(this.canvas);
		}
	}

	private void justReFocus() {
		this.mainCamera.setTarget(((Vector3) this.mainCamera.target).add(delta2));
		f--;
		if (f < 0) {
			this.cameraAnimating = false;
			this.scene.unregisterBeforeRender(animFunc2);
			this.mainCamera.attachControl(this.canvas);
		}
	}

	private Mesh createGround(Scene scene) {

		StandardMaterial groundMaterial = new StandardMaterial("groundMat", scene);
		groundMaterial.diffuseTexture = new Texture(this.groundTexture, scene);
		((Texture) groundMaterial.diffuseTexture).uScale = 6.0;
		((Texture) groundMaterial.diffuseTexture).vScale = 6.0;
		groundMaterial.diffuseColor = new Color3(0.9, 0.6, 0.4);
		groundMaterial.specularColor = new Color3(0, 0, 0);

		// GroundMesh ground = Mesh.CreateGroundFromHeightMap("ground",
		// this.groundHeightMap, 256, 256, 100, 0, 10, scene,
		// false);
		// Mesh grnd = Mesh.CreateGround("ground", 256, 256, 100, scene);
		Mesh grnd = Mesh.CreateGround("ground", 256, 256, 1, scene);
		grnd.material = groundMaterial;
		grnd.checkCollisions = true;
		grnd.isPickable = false;
		Tags.AddTagsTo(grnd, "Vishva.ground Vishva.internal");
		grnd.freezeWorldMatrix();
		grnd.receiveShadows = true;
		return grnd;
	}

	private Mesh createSkyBox(Scene scene) {
		// First, our box, nothing new, just take notice of the disabled
		// backface culling:
		Mesh skybox = Mesh.CreateBox("skyBox", 1000.0, scene);
		StandardMaterial skyboxMaterial = new StandardMaterial("skyBox", scene);
		skyboxMaterial.backFaceCulling = false;
		skybox.material = skyboxMaterial;
		// The following makes the skybox follow our camera's position.
		skybox.infiniteDistance = true;
		// remove all light reflections on our box (the sun doesn't reflect on
		// the sky!):
		skyboxMaterial.diffuseColor = new Color3(0, 0, 0);
		skyboxMaterial.specularColor = new Color3(0, 0, 0);
		// apply our special sky texture to it.
		skyboxMaterial.reflectionTexture = new CubeTexture(this.skyboxTextures, scene);
		skyboxMaterial.reflectionTexture.coordinatesMode = Texture.SKYBOX_MODE;
		// if you want your skybox to render behind everything else,
		// set the skybox's renderingGroupId to 0, and every other renderable
		// object's renderingGroupId greater than zero
		skybox.renderingGroupId = 0;
		skybox.isPickable = false;
		Tags.AddTagsTo(skybox, "Vishva.sky Vishva.internal");
		return skybox;
	}

	private ArcRotateCamera createCamera(Scene scene, HTMLCanvasElement canvas) {
		ArcRotateCamera camera = new ArcRotateCamera("v.c-camera", 1, 1.4, 4, new Vector3(0, 0, 0), scene);
		setCameraSettings(camera);
		camera.attachControl(canvas, true);
		if (this.avatar != null) {
			camera.target = new Vector3(this.avatar.position.x, this.avatar.position.y + 1.5, this.avatar.position.z);
			camera.alpha = -this.avatar.rotation.y - 4.69;
		} else {
			camera.target = Vector3.Zero();
		}
		camera.checkCollisions = this.cameraCollision;
		Tags.AddTagsTo(camera, "Vishva.camera");
		return camera;
	}

	private void loadAvatar() {
		SceneLoader.ImportMesh("", this.avatarFolder, this.avatarFile, this.scene, this::onAvatarLoaded);
	}

	private void onAvatarLoaded(AbstractMesh[] meshes, ParticleSystem[] particleSystems, Skeleton[] skeletons) {
		// just pick first one for now and dispose of others
		// maybe provide a system to select one later on
		this.avatar = (Mesh) meshes[0];
		Globals.array(this.shadowGenerator.getShadowMap().renderList).push(this.avatar);
		this.avatar.receiveShadows = true;
		int l = meshes.length;
		for (int i = 1; i < l; i++) {
			meshes[i].checkCollisions = false;
			meshes[i].dispose();
		}

		this.avatarSkeleton = skeletons[0];
		l = skeletons.length;
		for (int i = 1; i < l; i++) {
			skeletons[i].dispose();
		}
		
		//setAnimationRange(this.avatarSkeleton);
		fixAnimationRanges(this.avatarSkeleton);
		this.avatar.skeleton = this.avatarSkeleton;

		this.avatar.rotation.y = Math.PI;
		this.avatar.position = new Vector3(0, 0, 0);
		this.avatar.checkCollisions = true;
		this.avatar.ellipsoid = new Vector3(0.5, 1, 0.5);
		this.avatar.ellipsoidOffset = new Vector3(0, 2, 0);
		this.avatar.isPickable = false;

		Tags.AddTagsTo(this.avatar, "Vishva.avatar");
		Tags.AddTagsTo(this.avatarSkeleton, "Vishva.skeleton");
		// looks like skeleton tags are not serialized.
		this.avatarSkeleton.name = "Vishva.skeleton";

		this.mainCamera.target = new Vector3(this.avatar.position.x, this.avatar.position.y + 1.5,
				this.avatar.position.z);
		this.mainCamera.alpha = -this.avatar.rotation.y - 4.69;

		// rename the texture to include the foldername
		StandardMaterial sm = (StandardMaterial) this.avatar.material;
		if (sm.diffuseTexture != null) {
			String textureName = sm.diffuseTexture.name;
			sm.diffuseTexture.name = this.avatarFolder + textureName;
		}

		// render();
	}

	// load default animation range
	private void setAnimationRange(Skeleton skel) {
		for (AnimData anim : anims) {
			skel.createAnimationRange(anim.name, anim.s, anim.e);
		}

	}
	
	/**
	 * workaround for bug in blender exporter 
	 * 4.4.3 animation ranges are off by 1
	 * 4.4.4 issue with actions with just 2 frames -> from = to
	 * 
	 * @param skel
	 */
	private void fixAnimationRanges(Skeleton skel){
		
		Function getAnimationRanges =  (Function) skel.$get("getAnimationRanges");
		AnimationRange[] ranges = (AnimationRange[]) getAnimationRanges.call(skel);
		for (AnimationRange range:ranges){
			//range.from =  range.from +1;
			//range.to= range.to+1;
			if (range.from == range.to){
				range.to++;
			}
		}
		
	}

	// the following camera properties are not serialized
	private void setCameraSettings(ArcRotateCamera camera) {

		// console.log("camera.inertia " + camera.inertia);
		// console.log("camera.angularSensibilityX " +
		// camera.angularSensibilityX);
		// console.log("camera.angularSensibilityY " +
		// camera.angularSensibilityY);
		// console.log("camera.panningSensibility " +
		// camera.panningSensibility);
		// console.log("camera.wheelPrecision " + camera.wheelPrecision);

		// camera.upperBetaLimit=1.5;
		camera.lowerRadiusLimit = 0.25;
		// camera.wheelPrecision = 30;
		camera.keysLeft = new double[] {};
		camera.keysRight = new double[] {};
		camera.keysUp = new double[] {};
		camera.keysDown = new double[] {};
		camera.panningSensibility = 10;
		// camera.inertialPanningX = 100;
		// camera.inertialPanningY = 100;

		camera.inertia = 0.1;
		// default value of angularSensibility.. is 1000,
		// smaller means faster
		camera.angularSensibilityX = 250;
		camera.angularSensibilityY = 250;

	}

	private void backfaceCulling(Material[] mat) {
		int index;
		for (index = 0; index < mat.length; ++index) {
			mat[index].backFaceCulling = false;
		}
	}

}

class Key {
	public boolean up;
	public boolean down;
	public boolean right;
	public boolean left;
	public boolean stepRight;
	public boolean stepLeft;
	public boolean jump;
	public boolean shift;
	public boolean trans;
	public boolean rot;
	public boolean scale;
	public boolean esc;
	public boolean ctl;
	public boolean focus;

}

class AnimData {
	public String name;
	public int s;
	public int e;
	public double r;

	public AnimData(String name, int s, int e, double d) {
		this.name = name;
		this.s = s;
		this.e = e;
		this.r = d;
	}
}

/*
 * sensor and actuators
 */
@Interface
class SNAConfig {

}

class SNAManager {

	jsweet.lang.Object sensors;
	jsweet.lang.Object actuators;
	String[] sensorList = new String[] { "Touch" };
	String[] actuatorList = new String[] { "Mover", "Rotator", "Sound" };

	//
	Array<AbstractMesh> snaDisabledList = new Array();

	// maps signal ids to array of actuators
	jsweet.lang.Object sig2actMap = new jsweet.lang.Object();

	static SNAManager sm;

	protected SNAManager() {

	}

	public static SNAManager getSNAManager() {
		if (sm == null) {
			sm = new SNAManager();
		}
		return sm;
	}

	public void setConfig(jsweet.lang.Object snaConfig) {
		sensors = (jsweet.lang.Object) snaConfig.$get("sensors");
		actuators = (jsweet.lang.Object) snaConfig.$get("actuators");
		sensorList = jsweet.lang.Object.keys(sensors);
		actuatorList = jsweet.lang.Object.keys(actuators);
	}

	public String[] getSensorList() {
		return sensorList;
	}

	public String[] getActuatorList() {
		return actuatorList;
	}

	// should make these ...byName() more generic
	public Sensor createSensorByName(String name, Mesh mesh, SNAproperties prop) {
		if (name == "Touch") {
			if (prop != null)
				return new SensorTouch(mesh, prop);
			else
				return new SensorTouch(mesh, new SenTouchProp());
		}
		return null;
	}

	public Actuator createActuatorByName(String name, Mesh mesh, SNAproperties prop) {
		if (name == "Mover") {
			if (prop != null)
				return new ActuatorMover(mesh, (ActMoverParm) prop);
			else
				return new ActuatorMover(mesh, new ActMoverParm());
		} else if (name == "Rotator") {
			if (prop != null)
				return new ActuatorRotator(mesh, (ActRotatorParm) prop);
			else
				return new ActuatorRotator(mesh, new ActRotatorParm());
		} else if (name == "Sound") {
			if (prop != null)
				return new ActuatorSound(mesh, (ActSoundProp) prop);
			else
				return new ActuatorSound(mesh, new ActSoundProp());
		}
		return null;
	}

	public jsweet.lang.Object getSensorParms(String sensor) {
		jsweet.lang.Object sensorObj = (jsweet.lang.Object) sensors.$get(sensor);
		return (jsweet.lang.Object) sensorObj.$get("parms");
	}

	public jsweet.lang.Object getActuatorParms(String actuator) {
		jsweet.lang.Object actuatorObj = (jsweet.lang.Object) sensors.$get(actuator);
		return (jsweet.lang.Object) actuatorObj.$get("parms");
	}

	public void emitSignal(String signalId) {
		if (signalId.trim()=="") return;
		Object keyValue = sig2actMap.$get(signalId);
		if (keyValue != null) {
			window.setTimeout(function(this::actuate), 0, keyValue);
		}
	}

	private void actuate(Object acts) {
		Actuator[] actuators = (Actuator[]) acts;
		for (Actuator actuator : actuators) {
			// double i = snaDisabledList.indexOf(actuator.getMesh());
			// if (i < 0)
			// actuator.actuate();
			actuator.start();
		}
	};

	/**
	 * this is called to process any signals queued in any of mesh actuators
	 * this could be called after say a user has finished editing a mesh during
	 * edit all actuators are disabled and some events coudl lead to pending
	 * signals one example of such event could be adding a actuator with
	 * "autostart" enabled or enabling an existing actuators "autostart" during
	 * edit.
	 * 
	 * @param mesh
	 */
	public void processQueue(AbstractMesh mesh) {
		Array<Actuator> actuators = (Array<Actuator>) mesh.$get("actuators");
		if (actuators != null) {
			for (Actuator actuator : actuators) {
				actuator.processQueue();
			}
		}
	}

	/**
	 * this temproraily disables all sensors and actuators on a mesh this could
	 * be called for example when editing a mesh
	 * 
	 * @param mesh
	 */
	public void disableSnAs(Mesh mesh) {
		snaDisabledList.push(mesh);
		// stop any running actuator
		Array<ActuatorAbstract> actuators = (Array<ActuatorAbstract>) mesh.$get("actuators");
		if (actuators != null) {
			for (ActuatorAbstract actuator : actuators) {
				if (actuator.actuating)
					actuator.stop();

			}
		}
	}

	public void enableSnAs(AbstractMesh mesh) {
		double i = snaDisabledList.indexOf(mesh);
		if (i != -1) {
			snaDisabledList.splice(i, 1);
		}
		// if actuators are suppose to autosatrt then start them up
		Array<ActuatorAbstract> actuators = (Array<ActuatorAbstract>) mesh.$get("actuators");
		if (actuators != null) {
			for (ActuatorAbstract actuator : actuators) {
				if (actuator.properties.autoStart)
					actuator.start();

			}
		}
	}

	/**
	 * removes all sensors and actuators from a mesh. this would be called when
	 * say disposing off a mesh
	 * 
	 * @param mesh
	 */
	public void removeSNAs(AbstractMesh mesh) {
		Array<Actuator> actuators = (Array<Actuator>) mesh.$get("actuators");
		if (actuators != null) {
			for (Actuator actuator : actuators) {
				actuator.dispose();
			}
		}
		Array<Sensor> sensors = (Array<Sensor>) mesh.$get("sensors");
		if (sensors != null) {
			for (Sensor sensor : sensors) {
				sensor.dispose();
			}
		}

	}

	public void subscribe(Actuator actuator, String signalId) {
		Object keyValue = sig2actMap.$get(signalId);
		if (keyValue == null) {
			Array<Actuator> actuators = new Array<Actuator>();
			actuators.push(actuator);
			sig2actMap.$set(signalId, actuators);
		} else {
			Array<Actuator> actuators = (Array<Actuator>) keyValue;
			actuators.push(actuator);
		}
	}

	public void unSubscribe(Actuator actuator, String signalId) {
		Object keyValue = sig2actMap.$get(signalId);
		if (keyValue != null) {
			Array<Actuator> actuators = (Array<Actuator>) keyValue;
			double i = actuators.indexOf(actuator);
			if (i != -1) {
				actuators.splice(i, 1);
			}
		}
	}

	public void unSubscribeAll() {

	}

	public jsweet.lang.Object serializeSnAs(Scene scene) {
		jsweet.lang.Array<SNAserialized> snas = new jsweet.lang.Array<SNAserialized>();
		SNAserialized sna;
		AbstractMesh[] meshes = scene.meshes;
		String meshId;
		for (AbstractMesh mesh : meshes) {
			meshId = null;
			Array<Actuator> actuators = (Array<Actuator>) mesh.$get("actuators");
			if (actuators != null) {
				meshId = getMeshVishvaUid(mesh);
				for (Actuator actuator : actuators) {
					sna = new SNAserialized();
					sna.name = actuator.getName();
					sna.type = actuator.getType();
					sna.meshId = meshId;
					sna.properties = actuator.getProperties();
					snas.push(sna);
				}
			}
			Array<Sensor> sensors = (Array<Sensor>) mesh.$get("sensors");
			if (sensors != null) {
				if (meshId != null)
					meshId = getMeshVishvaUid(mesh);
				for (Sensor sensor : sensors) {
					sna = new SNAserialized();
					sna.name = sensor.getName();
					sna.type = sensor.getType();
					sna.meshId = meshId;
					sna.properties = sensor.getProperties();
					snas.push(sna);
				}
			}
		}

		return snas;
	}

	public void unMarshal(SNAserialized[] snas, Scene scene) {
		if (snas == null)
			return;
		Actuator act;
		for (SNAserialized sna : snas) {
			Mesh mesh = scene.getMeshesByTags(sna.meshId)[0];
			if (mesh != null) {
				if (sna.type == "SENSOR") {
					createSensorByName(sna.name, mesh, sna.properties);
				} else if (sna.type == "ACTUATOR") {
					if (sna.name == "Sound") {
						ActSoundProp prop = new ActSoundProp();
						prop.unmarshall(sna.properties);
						act = createActuatorByName(sna.name, mesh, prop);
					} else {
						act = createActuatorByName(sna.name, mesh, sna.properties);
					}
				}
			}
		}
	}

	private String getMeshVishvaUid(AbstractMesh mesh) {
		if (Tags.HasTags(mesh)) {
			String[] tags = ((String) Tags.GetTags(mesh, true)).split(" ");
			for (String tag : tags) {
				double i = tag.indexOf("Vishva.uid.");
				if (i >= 0) {
					return tag;
				}
			}
		}
		String uid = "Vishva.uid." + (new jsweet.lang.Number(Date.now())).toString();
		Tags.AddTagsTo(mesh, uid);
		return uid;
	}

}

class SNAserialized {
	String name;
	String type;
	String meshId;
	SNAproperties properties;
}

interface SensorActuator {
	public String getName();

	public String getType();

	public SNAproperties getProperties();

	public void setProperties(SNAproperties properties);

	/**
	 * this is called by the system after the ctuator properties are updated
	 */
	public void processUpdateGeneric();

	/**
	 * called by {@processUpdateGeneric} implementors should do their sensor
	 * actuator specific updates here
	 */
	public void processUpdateSpecific();

	public String getSignalId();

	public void dispose();

	/**
	 * called by dispose() the implementor of the a sensor actuator should do
	 * all cleanup specific to their sensor actuator here
	 */
	public void cleanUp();
}

// interface Sensor extends SensorActuator {
// public void emitSignal(ActionEvent e);
// }
//
// interface Actuator extends SensorActuator {
// public void updateSignalId();
// public Mesh getMesh();
// public void actuate();
// }

interface Sensor extends SensorActuator {
	// ts doesnot do extend ?

	public String getName();

	public String getType();

	public SNAproperties getProperties();

	public void setProperties(SNAproperties properties);

	public void processUpdateGeneric();

	public void processUpdateSpecific();

	public String getSignalId();

	public void dispose();

	public void cleanUp();

	// sensor specific
	public void emitSignal(ActionEvent e);

}

interface Actuator extends SensorActuator {
	// ts doesnot do extend ?
	public String getName();

	public String getType();

	public SNAproperties getProperties();

	public void setProperties(SNAproperties properties);

	public String getSignalId();

	public void processUpdateGeneric();

	public void processUpdateSpecific();

	public void dispose();

	public void cleanUp();

	// TODO swicth start and actuate
	public boolean start();

	public void stop();

	public void actuate();

	public boolean isReady();

	public void processQueue();

	public Mesh getMesh();

}

abstract class SensorAbstract implements Sensor {

	SNAproperties properties;
	Mesh mesh;
	Action action;

	public SensorAbstract(Mesh mesh, SNAproperties properties) {
		this.properties = properties;

		this.mesh = mesh;
		Array<Sensor> sensors = (Array<Sensor>) this.mesh.$get("sensors");
		if (sensors == null) {
			sensors = new Array<Sensor>();
			mesh.$set("sensors", sensors);
		}
		sensors.push(this);

		// if (this.mesh.actionManager == null) {
		// this.mesh.actionManager = new ActionManager(mesh.getScene());
		// }
		// this.action = new ExecuteCodeAction(ActionManager.OnLeftPickTrigger,
		// this::emitSignal);
		// this.mesh.actionManager.registerAction(this.action);

	}

	final public void dispose() {
		Array<Sensor> sensors = (Array<Sensor>) this.mesh.$get("sensors");
		if (sensors != null) {
			double i = sensors.indexOf(this);
			if (i != -1) {
				sensors.splice(i, 1);
			}
		}
		cleanUp();

		// Array<Action> actions =
		// Globals.array(this.mesh.actionManager.actions);
		// double i = actions.indexOf(this.action);
		// actions.splice(i, 1);
		// if (actions.length == 0) {
		// this.mesh.actionManager.dispose();
		// this.mesh.actionManager = null;
		// }
	}

	abstract public void cleanUp();

	public String getSignalId() {
		return this.properties.signalId;
	}

	public void setSignalId(String sid) {
		this.properties.signalId = sid;
	}

	public void emitSignal(ActionEvent e) {
		// donot emit signal if this mesh is on the diabled list
		double i = SNAManager.getSNAManager().snaDisabledList.indexOf(this.mesh);
		if (i >= 0)
			return;
		SNAManager.getSNAManager().emitSignal(this.properties.signalId);
	}

	public abstract String getName();

	public SNAproperties getProperties() {
		return this.properties;
	};

	public void setProperties(SNAproperties prop) {
		this.properties = prop;
	};

	final public void processUpdateGeneric() {
		processUpdateSpecific();
	};

	public abstract void processUpdateSpecific();

	final public String getType() {
		return "SENSOR";
	}

}

class SensorTouch extends SensorAbstract {

	SNAproperties properties;

	public SensorTouch(Mesh mesh, SNAproperties properties) {
		super(mesh, properties);

		if (this.mesh.actionManager == null) {
			this.mesh.actionManager = new ActionManager(mesh.getScene());
		}
		this.action = new ExecuteCodeAction(ActionManager.OnLeftPickTrigger, this::emitSignal);
		this.mesh.actionManager.registerAction(this.action);
	}

	@Override
	public String getName() {
		return "Touch";
	}

	@Override
	public SNAproperties getProperties() {
		return properties;
	}

	@Override
	public void setProperties(SNAproperties properties) {
		this.properties = properties;

	}

	@Override
	public void cleanUp() {
		Array<Action> actions = Globals.array(this.mesh.actionManager.actions);
		double i = actions.indexOf(this.action);
		actions.splice(i, 1);
		if (actions.length == 0) {
			this.mesh.actionManager.dispose();
			this.mesh.actionManager = null;
		}
	}

	@Override
	public void processUpdateSpecific() {
		// TODO Auto-generated method stub

	}

}

abstract class ActuatorAbstract implements Actuator {

	ActProperties properties;
	Mesh mesh;
	String signalId;
	boolean toggle = true;
	boolean actuating = false;
	boolean ready = true;
	int queued = 0;

	public ActuatorAbstract(Mesh mesh, ActProperties prop) {
		this.properties = prop;
		this.mesh = mesh;
		// this.signalId = prop.signalId;
		// SNAManager.getSNAManager().subscribe(this, this.signalId);
		this.processUpdateGeneric();
		Array<Actuator> actuators = (Array<Actuator>) this.mesh.$get("actuators");
		if (actuators == null) {
			actuators = new Array<Actuator>();
			this.mesh.$set("actuators", actuators);
		}
		actuators.push(this);

	}

	final public boolean start() {

		if (!this.ready)
			return false;

		// donot actuate if this mesh is on the disabled list
		double i = SNAManager.getSNAManager().snaDisabledList.indexOf(this.mesh);
		if (i >= 0)
			return false;

		if (actuating) {
			if (!this.properties.loop) {
				queued++;
			}
			return true;
		}
		SNAManager.getSNAManager().emitSignal(this.properties.startSigId);
		actuating = true;
		actuate();
		return true;
	}

	public abstract void actuate();

	public abstract void stop();

	public abstract boolean isReady();

	final public void processQueue() {
		if (queued > 0) {
			queued--;
			start();
		}
	}

	public abstract String getName();

	final public String getType() {
		return "ACTUATOR";
	}

	public Mesh getMesh() {
		return this.mesh;
	}

	public SNAproperties getProperties() {
		return this.properties;
	};

	public void setProperties(SNAproperties prop) {
		this.properties = (ActProperties) prop;
		this.processUpdateGeneric();

	};

	public String getSignalId() {
		return this.properties.signalId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ssatguru.babylonjs.Actuator#processUpdate()
	 */
	final public void processUpdateGeneric() {
		if (this.signalId != null && this.signalId != this.properties.signalId) {
			SNAManager.getSNAManager().unSubscribe(this, this.signalId);
			this.signalId = this.properties.signalId;
			SNAManager.getSNAManager().subscribe(this, this.signalId);
		} else if (this.signalId == null) {
			this.signalId = this.properties.signalId;
			SNAManager.getSNAManager().subscribe(this, this.signalId);
		}
		processUpdateSpecific();

	}

	abstract public void processUpdateSpecific();

	public Object onActuateEnd() {
		SNAManager.getSNAManager().emitSignal(this.properties.endSigId);
		actuating = false;
		if (queued > 0) {
			queued--;
			start();
			return null;
		}
		if (this.properties.loop) {
			start();
			return null;
		}
		return null;
	}

	final public void dispose() {
		SNAManager.getSNAManager().unSubscribe(this, this.properties.signalId);
		Array<Actuator> actuators = (Array<Actuator>) this.mesh.$get("actuators");
		if (actuators != null) {
			this.stop();
			double i = actuators.indexOf(this);
			if (i != -1) {
				actuators.splice(i, 1);
			}
		}
		cleanUp();
	}

	abstract public void cleanUp();

}

class ActuatorRotator extends ActuatorAbstract {

	Animatable a;

	public ActuatorRotator(Mesh mesh, ActRotatorParm parm) {
		super(mesh, parm);
	}

	public void actuate() {
		ActRotatorParm properties = (ActRotatorParm) this.properties;
		Quaternion cPos = mesh.rotationQuaternion.Clone();
		Quaternion nPos;
		Quaternion rotX = Quaternion.RotationAxis(Axis.X, properties.x * Math.PI / 180);
		Quaternion rotY = Quaternion.RotationAxis(Axis.Y, properties.y * Math.PI / 180);
		Quaternion rotZ = Quaternion.RotationAxis(Axis.Z, properties.z * Math.PI / 180);
		Quaternion abc = Quaternion.RotationYawPitchRoll(properties.y * Math.PI / 180, properties.x * Math.PI / 180,
				properties.z * Math.PI / 180);
		if (properties.toggle) {
			if (this.toggle) {
				// nPos = cPos.multiply(rotX).multiply(rotY).multiply(rotZ);
				nPos = cPos.multiply(abc);
			} else {
				// nPos =
				// cPos.multiply(Quaternion.Inverse(rotZ)).multiply(Quaternion.Inverse(rotY)).multiply(Quaternion.Inverse(rotX));
				nPos = cPos.multiply(Quaternion.Inverse(abc));
			}
		} else
			nPos = cPos.multiply(rotX).multiply(rotY).multiply(rotZ);
		this.toggle = !this.toggle;
		double cY = mesh.position.y;
		double nY = mesh.position.y + 5;
		a = Animation.CreateAndStartAnimation("rotate", mesh, "rotationQuaternion", 60, 60 * properties.duration, cPos,
				nPos, 0, null, this::onActuateEnd);
	}

	@Override
	public String getName() {
		return "Rotator";
	}

	@Override
	public void stop() {
		a.stop();
		onActuateEnd();

	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void processUpdateSpecific() {
		if (properties.autoStart) {
			boolean started = this.start();
			// sometime a start maynot be possible example during edit
			// if could not start now then queue it for later start
			// if (!started)
			// this.queued++;
		}

	}

	@Override
	public boolean isReady() {
		return true;
	}

}

class ActuatorMover extends ActuatorAbstract {
	Animatable a;

	public ActuatorMover(Mesh mesh, ActMoverParm parms) {
		super(mesh, parms);
	}

	public void actuate() {
		ActMoverParm props = (ActMoverParm) this.properties;
		Vector3 cPos = mesh.position.Clone();
		Vector3 nPos;
		Vector3 moveBy;

		if (props.local) {
			Matrix meshMatrix = mesh.getWorldMatrix();
			Vector3 localMove = new Vector3(props.x * (1 / mesh.scaling.x), props.y * (1 / mesh.scaling.y),
					props.z * (1 / mesh.scaling.z));
			moveBy = Vector3.TransformCoordinates(localMove, meshMatrix).subtract(mesh.position);
		} else
			moveBy = new Vector3(props.x, props.y, props.z);
		;
		if (props.toggle) {
			if (this.toggle) {
				nPos = cPos.add(moveBy);
			} else {
				nPos = cPos.subtract(moveBy);
			}
			this.toggle = !this.toggle;
		} else {
			nPos = cPos.add(moveBy);
		}
		this.a = Animation.CreateAndStartAnimation("move", mesh, "position", 60, 60 * props.duration, cPos, nPos, 0,
				null, this::onActuateEnd);

	}

	@Override
	public String getName() {
		return "Mover";
	}

	@Override
	public void stop() {
		this.a.stop();
		onActuateEnd();

	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void processUpdateSpecific() {
		if (properties.autoStart) {
			boolean started = this.start();
			// sometime a start maynot be possible example during edit
			// if could not start now then queue it for later start
			// if (!started)
			// this.queued++;
		}

	}

	@Override
	public boolean isReady() {
		return true;
	}

}

class ActuatorSound extends ActuatorAbstract {

	Sound sound;

	public ActuatorSound(Mesh mesh, ActSoundProp prop) {
		super(mesh, prop);
		// this.properties = prop;
	}

	@Override
	public void actuate() {
		sound.play();
	}

	@Override
	public void processUpdateSpecific() {
		ActSoundProp properties = (ActSoundProp) this.properties;
		if (properties.soundFile.value == null)
			return;

		if (this.sound == null || properties.soundFile.value != this.sound.name) {
			if (this.sound != null) {
				stop();
				this.sound.dispose();
			}
			this.ready = false;
			this.sound = new Sound(properties.soundFile.value, "vishva/assets/sounds/" + properties.soundFile.value,
					this.mesh.getScene(), () -> {
						updateSound(properties);
					});
		} else {
			stop();
			updateSound(properties);
		}

	}

	private void updateSound(ActSoundProp properties) {
		this.ready = true;
		if (properties.attachToMesh) {
			this.sound.attachToMesh(this.mesh);
		}

		// this.sound.loop = properties.loop;
		this.sound.onended = this::onActuateEnd;
		this.sound.setVolume(properties.volume.value);
		if (properties.autoStart) {
			boolean started = this.start();
			// sometime a start maynot be possible exmple during edit
			// if could not start now then queue it for later start
			if (!started)
				this.queued++;
		}

	}

	@Override
	public String getName() {
		return "Sound";
	}

	@Override
	public void stop() {
		if (this.sound != null) {
			if (this.sound.isPlaying) {
				sound.stop();
				onActuateEnd();
			}
		}
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isReady() {
		return this.ready;
	}
}

abstract class SNAproperties extends jsweet.lang.Object {
	String signalId = "0";

	public abstract SNAproperties unmarshall(jsweet.lang.Object obj);
}

class SenTouchProp extends SNAproperties {

	@Override
	public SenTouchProp unmarshall(jsweet.lang.Object obj) {

		return (SenTouchProp) obj;
	}

}

abstract class ActProperties extends SNAproperties {
	boolean autoStart = false;
	boolean loop = false;
	boolean toggle = true;
	String startSigId = "";
	String endSigId = "";

	@Override
	public abstract ActProperties unmarshall(jsweet.lang.Object obj);
}

class ActRotatorParm extends ActProperties {
	double x = 0;
	double y = 90;
	double z = 0;
	double duration = 1;

	//
	// TODO:always loacl for now. provide a way to do global rotate
	// boolean local = false;
	@Override
	public ActRotatorParm unmarshall(jsweet.lang.Object obj) {
		// TODO Auto-generated method stub
		return (ActRotatorParm) obj;
	}
}

class ActMoverParm extends ActProperties {
	double x = 1;
	double y = 1;
	double z = 1;
	double duration = 1;
	boolean local = false;

	@Override
	public ActMoverParm unmarshall(jsweet.lang.Object obj) {
		// TODO Auto-generated method stub
		return (ActMoverParm) obj;
	}
}

class ActSoundProp extends ActProperties {
	SelectType soundFile = new SelectType();
	boolean attachToMesh = false;
	Range volume = new Range(0.0, 1.0, 1.0, 0.1);

	public ActSoundProp unmarshall(jsweet.lang.Object obj) {
		ActSoundProp inObj = (ActSoundProp) obj;
		ActSoundProp out = this;
		out.attachToMesh = inObj.attachToMesh;
		out.autoStart = inObj.autoStart;
		out.loop = inObj.loop;
		out.signalId = inObj.signalId;
		out.endSigId = inObj.endSigId;
		out.startSigId = inObj.startSigId;
		out.toggle = inObj.toggle;
		out.soundFile.value = inObj.soundFile.value;
		out.soundFile.values = inObj.soundFile.values;
		out.volume.max = inObj.volume.max;
		out.volume.min = out.volume.min;
		out.volume.value = inObj.volume.value;
		out.volume.step = inObj.volume.step;
		return out;
	}
}
