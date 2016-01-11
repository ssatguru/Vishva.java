# Vishva 

Vishva - A hindi word for "World"

A small world created using [BabylonJS](http://www.babylonjs.com/) a 3d frameworl and [JSweet](http://www.jsweet.org/)  a java to javscript transpiler

To build
1. download project
2. cd to the project root folder. This should have the maven "pom.xml" file.
3. run command 
```
mvn generate-sources
(This will transpile the java source code files to javascript files and store them in "target/js" folder)
```
To run
1. open "webapp/index.html" in browser. If you are using firefox browser then you can open it directly from disk. For others you will have to serve the file via some http server. Other browsers don ot allow cross origin requests.
