package BABYLON;

import jsweet.lang.Ambient;
import def.babylonjs.babylon.Vector2;
import def.babylonjs.babylon.Color3;
import def.babylonjs.babylon.Texture;
import def.babylonjs.babylon.Mesh;
import def.babylonjs.babylon.Scene;
import def.babylonjs.babylon.Material;

@Ambient
public class WaterMaterial extends Material{
	public double windForce;
	public double waveHeight;
	public double bumpHeight ;
	public Vector2 windDirection;
	public Color3 waterColor ;
	public double colorBlendFactor ;
	public double waveLength;
	public Texture bumpTexture;
	public native void addToRenderList(Mesh mesh);
	public WaterMaterial(String name,Scene scene){super(name,scene);};

}