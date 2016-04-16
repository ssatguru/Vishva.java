# Vishva 

## about

Vishva - A hindi word for "World"

A simple live scene editor for [BabylonJS](http://www.babylonjs.com/), a 3D HTML Webgl framework.

Developed in Java using [JSweet](http://www.jsweet.org/), a java to javscript transpiler

More info at [http://ssatguru.appspot.com/BabylonJS-Vishva/intro.html](http://ssatguru.appspot.com/BabylonJS-Vishva/intro.html)

## to run

* download project

* cd to "webapp" folder. Open "index.html" in browser. If you are using firefox browser then you can open it directly from disk. For others you will have to serve the file via. some http server due to cross origin requests restrictions. See [here for some helpful information](https://github.com/mrdoob/three.js/wiki/How-to-run-things-locally)

* move your avatar using the "w a s d q e" keys. To run press shift and "w". To jump press and release "Space" key. Right click and drag mouse to look around. To select an item for edit, mouse right click the item. when selected use 1,2 ,3 and F keys to move,rotate,scale and focus on the item. To unslect an item press "esc" key.

## to build

* this project has a dependency on the [EditControl Artifact](https://github.com/ssatguru/BabylonJS-EditControl). Download, build and install that in your local Maven repository.

* download this project, if you haven't already done so.

* cd to the project root folder. This should have the maven "pom.xml" file.

* run command 

```
mvn generate-sources
(This will transpile the java source code files to javascript files and store them in "target/js" folder)
```

## demo
For a demo  see [http://ssatguru.appspot.com/BabylonJS-Vishva/webapp/index.html](http://ssatguru.appspot.com/BabylonJS-Vishva/webapp/index.html)


Note:This is still work in progress.

## build using
* [BabylonJS](http://www.babylonjs.com/)
* [Java](https://www.oracle.com/java/index.html)
* [JSweet](http://www.jsweet.org/)
* [JQuery UI](https://jqueryui.com/)
* [FlexiColorPicker](https://github.com/DavidDurman/FlexiColorPicker)

## 3d assets, animations
* [Eat Sheep , Blend Swap](http://www.blendswap.com/blends/view/25065)
* [Yo Frankie, Blender Institutte](https://apricot.blender.org/download/)
* [Mixamo](https://www.mixamo.com/)
* [SecondLife Internal Animations and Skeleton](http://wiki.secondlife.com/wiki/Internal_Animations)
* 

