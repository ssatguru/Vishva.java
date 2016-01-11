package org.ssatguru.babylonjs;

import static jsweet.dom.Globals.alert;
import static jsweet.dom.Globals.console;
import static jsweet.dom.Globals.document;
import static jsweet.dom.Globals.window;

import def.babylonjs.babylon.AbstractMesh;
import def.babylonjs.babylon.ArcRotateCamera;
import def.babylonjs.babylon.Color3;
import def.babylonjs.babylon.CubeTexture;
import def.babylonjs.babylon.Engine;
import def.babylonjs.babylon.Material;
import def.babylonjs.babylon.Mesh;
import def.babylonjs.babylon.ParticleSystem;
import def.babylonjs.babylon.Scene;
import def.babylonjs.babylon.SceneLoader;
import def.babylonjs.babylon.Skeleton;
import def.babylonjs.babylon.Sound;
import def.babylonjs.babylon.StandardMaterial;
import def.babylonjs.babylon.Texture;
import def.babylonjs.babylon.Vector3;

import jsweet.dom.Event;
import jsweet.dom.HTMLButtonElement;
import jsweet.dom.HTMLCanvasElement;
import jsweet.dom.HTMLElement;
import jsweet.dom.KeyboardEvent;
import jsweet.dom.MouseEvent;
import jsweet.lang.Math;

public class Vishva {

	Engine engine;
	HTMLCanvasElement canvas;
	HTMLButtonElement vincent;
	HTMLButtonElement joan;
	Scene scene;
	String skyboxFile;
	String avatars;
	Mesh avatar;
	AbstractMesh[] avMeshes;
	Skeleton avatarSkeleton;
	String avatarFile;
	String avatarName = "Vincent";
	ArcRotateCamera mainCamera;

	gAnim walk, walkBack, idle, run;
	double avatarSpeed = 0.05;
	gAnim prevAnim;

	Keys navKeys;

	public static void main(String[] args) {
		new Vishva("a", "main_scene.babylon", "skybox/desert", "avatars.babylon");

	}

	public Vishva(String canvasId, String sceneFile, String skyboxFile, String avatarFile) {
		if (!Engine.isSupported()) {
			alert("not supported");
			return;
		}

		this.skyboxFile = skyboxFile;
		this.avatarFile = avatarFile;
		this.navKeys = new Keys();
		initAnims();

		canvas = (HTMLCanvasElement) document.getElementById("a");
		vincent = (HTMLButtonElement) document.getElementById("Vincent");
		joan = (HTMLButtonElement) document.getElementById("Joan");

		
		vincent.onclick = this::changeAV;
		joan.onclick = this::changeAV;

		
		engine = new Engine(canvas, true);

		// add event handlers
		window.addEventListener("resize", this::onWindowResize);
		window.addEventListener("keydown", this::onKeyDown, false);
		window.addEventListener("keyup", this::onKeyUp, false);

		SceneLoader.Load("scene/", sceneFile, engine, this::onSceneLoaded);

	}

	private void initAnims() {
		// gAnims={walk:{s:1,e:15,r:1},
		// walkback:{s:16,e:28,r:0.5},idle:{s:30,e:31,r:0.01},run:{s:33,e:47,r:1.5}};
		this.walk = new gAnim("walk", 1, 15, 1);
		this.walkBack = new gAnim("walkback", 16, 28, 0.5);
		this.idle = new gAnim("idle", 30, 31, 0.01);
		this.run = new gAnim("run", 33, 47, 1.5);
		this.prevAnim = new gAnim("", 0, 0, 0);

	}

//	private void onKeyDown(Event e) {
//		KeyboardEvent event = (KeyboardEvent) e;
//		double keyCode = event.keyCode;
//		if (keyCode == 16)
//			this.navKeys.shift = true;
//		// works only on firefox
//		// jsweet.lang.String ch = new
//		// jsweet.lang.String(event.key.toUpperCase());
//		String chr = jsweet.lang.String.fromCharCode(event.keyCode);
//		jsweet.lang.String ch = new jsweet.lang.String(chr);
//		// console.log("key code " + ch + ":" + keyCode);
//		if (ch.localeCompare("W") == 0)
//			this.navKeys.up = true;
//		if (ch.localeCompare("A") == 0)
//			this.navKeys.left = true;
//		if (ch.localeCompare("D") == 0)
//			this.navKeys.right = true;
//		if (ch.localeCompare("S") == 0)
//			this.navKeys.down = true;
//	}
	private void onKeyDown(Event e) {
		KeyboardEvent event = (KeyboardEvent) e;
		if (event.keyCode == 16)
			this.navKeys.shift = true;
		String chr = jsweet.lang.String.fromCharCode(event.keyCode);
		if (chr == "W")
			this.navKeys.up = true;
		if (chr == "A")
			this.navKeys.left = true;
		if (chr == "D")
			this.navKeys.right = true;
		if (chr =="S")
			this.navKeys.down = true;
	}
	
	private void onKeyUp(Event e) {
		KeyboardEvent event = (KeyboardEvent) e;
		if (event.keyCode == 16)
			this.navKeys.shift = false;
		String chr = jsweet.lang.String.fromCharCode(event.keyCode);
		if (chr == "W")
			this.navKeys.up = false;
		if (chr == "A")
			this.navKeys.left = false;
		if (chr == "D")
			this.navKeys.right = false;
		if (chr =="S")
			this.navKeys.down = false;
	}

	private void onSceneLoaded(Scene s) {
		this.scene = s;
		backfaceCulling(this.scene.materials);
		createSkyBox(this.scene);

		// Sound sound = new
		// Sound("background","Kalapani-Rafi-HumBekhudiMein.mp3",this.scene);
		// sound.loop = true;
		// sound.autoplay = true;

		// load avatar
		SceneLoader.ImportMesh("", "avatars/", this.avatarFile, scene, this::onAvatarLoaded);
	}

	private void createSkyBox(Scene scene) {

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
		skyboxMaterial.reflectionTexture = new CubeTexture(this.skyboxFile, scene);
		skyboxMaterial.reflectionTexture.coordinatesMode = Texture.SKYBOX_MODE;
		// if you want your skybox to render behind everything else,
		// set the skybox's renderingGroupId to 0, and every other renderable
		// object's renderingGroupId greater than zero
		skybox.renderingGroupId = 0;

		skybox.isPickable = false;
	}

	private void onAvatarLoaded(AbstractMesh[] meshes, ParticleSystem[] particleSystems, Skeleton[] skeletons) {
		this.avatarSkeleton = skeletons[0];
		this.avMeshes = meshes;
		setAvatar(this.avatarName);

		this.avatar.rotation.y = Math.PI;
		this.avatar.position = new Vector3(0, 0, 0);

		// create camera
		this.mainCamera = createCamera(this.scene, this.canvas);
		// this.mainCamera.checkCollisions = true;
		// this.mainCamera.ellipsoid = new Vector3(0.1, 0.1, 0.1);
		mainCamera.alpha = -this.avatar.rotation.y - 4.69;

		this.scene.activeCamera = this.mainCamera;

		// set pick
		// this.scene.onPointerDown = this::fPick;
		render();

	}

	private Object changeAV(MouseEvent evt) {
		HTMLElement target = (HTMLElement) evt.target;
		setAvatar(target.id);
		return null;

	}


	private void setAvatar(String avName) {
		AbstractMesh[] meshes = this.avMeshes;
		Mesh mesh;
		for (AbstractMesh amesh : meshes) {
			mesh = (Mesh) amesh;
			if (mesh.id == avName) {
				console.log("match found");
				Vector3 saveRotation, savePosition;
				if (this.avatar != null) {
					saveRotation = this.avatar.rotation;
					savePosition = this.avatar.position;
				}else{
					saveRotation = new Vector3(0,Math.PI,0);
					savePosition =new Vector3(0, 0, 0);
				}
				this.avatar = mesh;
				this.avatar.rotation = saveRotation;
				this.avatar.position = savePosition;
				this.avatar.visibility = 1;
				this.avatar.skeleton = this.avatarSkeleton;
				this.avatar.checkCollisions = true;
				this.avatar.ellipsoid = new Vector3(0.5, 1, 0.5);
				this.avatar.ellipsoidOffset = new Vector3(0, 2, 0);
				// this.avatar.applyGravity = true;
			} else {
				mesh.skeleton = null;
				mesh.visibility = 0;
				mesh.checkCollisions = false;
			}
		}

	}

	private void render() {
		this.scene.registerBeforeRender(() -> moveAvatarCamera());
		// start rendering scene once textures and shaders are loaded
		this.scene.executeWhenReady(this::startRenderLoop);
	}

	private void startRenderLoop() {
		Vishva.listMeshes(this.scene);
		backfaceCulling(this.scene.materials);
		this.engine.runRenderLoop(() -> this.scene.render());

	}

	private void moveAvatarCamera() {
		gAnim anim;
		boolean moving;
		double speed;
		Vector3 forward;
		Vector3 backwards;
		if (this.navKeys.up) {
			if (this.navKeys.shift) {
				speed = this.avatarSpeed * 2;
				anim = this.run;
			} else {
				speed = this.avatarSpeed;
				anim = this.walk;
			}
			forward = new Vector3(Math.sin(this.avatar.rotation.y) * speed, 0.5,
					Math.cos(this.avatar.rotation.y) * speed);
			forward = forward.negate();
			this.avatar.moveWithCollisions(forward);
			moving = true;
		} else if (this.navKeys.down) {
			backwards = new Vector3(Math.sin(this.avatar.rotation.y) * (this.avatarSpeed / 2), -0.5,
					Math.cos(this.avatar.rotation.y) * (this.avatarSpeed / 2));
			this.avatar.moveWithCollisions(backwards);
			moving = true;
			anim = this.walkBack;
		} else {
			moving = false;
			anim = this.idle;
		}
		if (moving) {
			this.avatar.rotation.y = -4.69 - this.mainCamera.alpha;
		}
		if (this.prevAnim.name != anim.name) {
			this.scene.beginAnimation(this.avatarSkeleton, anim.s, anim.e, true, anim.r);
		}
		this.prevAnim = anim;

		Vector3 pos = new Vector3(this.avatar.position.x, (this.avatar.position.y + 1.5), this.avatar.position.z);

		this.mainCamera.target = pos;

	}

	private ArcRotateCamera createCamera(Scene scene, HTMLCanvasElement canvas) {
		ArcRotateCamera camera = new ArcRotateCamera("ArcRotateCamera", 1, 1.4, 4, new Vector3(0, 0, 0), scene);
		// camera.upperBetaLimit=1.5;
		camera.lowerRadiusLimit = 1;
		camera.target = new Vector3(this.avatar.position.x, this.avatar.position.y + 1.5, this.avatar.position.z);
		camera.wheelPrecision = 15;
		camera.attachControl(canvas, true);
		camera.keysLeft = new double[] { 68, 39 };
		camera.keysRight = new double[] { 65, 37 };
		return camera;
	}

	private void onWindowResize(Event event) {
		engine.resize();
	}

	private void backfaceCulling(Material[] mat) {
		int index;
		for (index = 0; index < mat.length; ++index) {
			mat[index].backFaceCulling = false;
		}
	}

	private static void listMeshes(Scene scene) {
		AbstractMesh[] meshes = scene.meshes;

		for (AbstractMesh mesh : meshes) {
			console.log("mesh id : " + mesh.id);
		}

	}

}

class Keys {
	boolean up;
	boolean down;
	boolean right;
	boolean left;
	boolean shift;
}

class gAnim {
	String name;
	int s;
	int e;
	double r;

	gAnim(String name, int s, int e, double d) {
		this.name = name;
		this.s = s;
		this.e = e;
		this.r = d;
	}
}
