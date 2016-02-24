# Allow Ensembles Demonstrator

IMPORTANT NOTE: this version have manual dependencies inside lib folder, please read with attention following notes


Demonstrator for Allow Ensembles project, see http://www.allow-ensembles.eu/

![Allow Ensembles Demonstrator](img/img01.png)

## Dependencies

Project dependencies are inside lib folder, please add them to your Eclipse project


## How to build

To build Allow Ensembles Demonstrato you need to install Maven 3.2.x, Java JDK 1.8.x, Eclipse and set
dependencies.


## How to configure

### Mac configuration

In order to run composer component inside Allow Ensembles demonstrator, on system must be configured wine (1.6.2), because runs 
a small executable included inside demonstrator, wsynth.
Please see composer-impl.jar dependency and modifiy configuration.properties inside with following properties (example values)

macWinePath=/opt/local/bin/wine
macwsynthPath=/opt/local/bin/wsynth.exe



# How to run

Main entry point for demonstrator is here:

/demonstrator/src/main/java/eu/allowensembles/Demonstrator.java

when Demonstrator is started, select File-> Open from menu and select scenario file inside repository:

<WORKSPACE>\demonstrator\src\main\resources\storyboard1\Storyboard1-main.xml

To run example process, please press STEP


# License

Please see COPYRIGHT file

