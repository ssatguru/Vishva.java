# Vishva 

## about

Vishva - A hindi word for "World"

A simple live scene editor for [BabylonJS](http://www.babylonjs.com/), a 3D HTML Webgl framework.

Developed in Java using [JSweet](http://www.jsweet.org/), a java to javscript transpiler

For intro and demo see [http://ssatguru.appspot.com/BabylonJS-Vishva/intro.html](http://ssatguru.appspot.com/BabylonJS-Vishva/intro.html)

For details see [wiki at https://github.com/ssatguru/Vishva/wiki](https://github.com/ssatguru/Vishva/wiki)

## to run

* download project

* cd to "webapp" folder. Open "index.html" in browser. If you are using firefox browser then you can open it directly from disk. For others you will have to serve the file via. some http server due to cross origin requests restrictions. See [here for some helpful information](https://github.com/mrdoob/three.js/wiki/How-to-run-things-locally)

* for more help see [wiki](https://github.com/ssatguru/Vishva/wiki)

## to build

#### Java developers

* this is a standard java maven project

* source under "src" folder

* this project has a dependency on the [EditControl Artifact](https://github.com/ssatguru/BabylonJS-EditControl.java). Download, build and install that in your local Maven repository.

* to build, run following command, from the project root folder (this has the "pom.xml" file)
```
mvn generate-sources
```
* this will generate and copy all the javascript and typescript files to "webapp/js" folder

* to clean
```
mvn clean jsweet:clean
```

####  JavaScript and TypeScript developers

No build required

Source under "webapp/js" folder

Dependencies under "webapp/lib" folder

## demo
For a demo  see [http://ssatguru.appspot.com/BabylonJS-Vishva/webapp/index.html](http://ssatguru.appspot.com/BabylonJS-Vishva/webapp/index.html)

## built using
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


