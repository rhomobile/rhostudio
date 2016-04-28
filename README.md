# RhoStudio
RhoStudio is Eclipse plugin to facilitate development of mobile applications.

Use RhoStudio to:

- Generate Rhodes application(s)
- Generate Rhodes Model(s) and associated Controllers and View templates
- Manage build configuration (build.yml and rhobuild.yml) using Eclipse UI
- Build and run on Rhodes application on iPhone, Android, Windows Mobile, and BlackBerry simulators and devices
- See build and application execution logs in Eclipse output console

## Install RhoStudio in Eclipse

You will need Rhodes 2.4 (or later) gem or source code installed for RhoStudio to work. See [here](http://docs.rhomobile.com/rhodes/install) how to install Rhodes. 

Open menu Help->Install New Software... 

In Install popup add RhoStudio update site:

- Enter name: RhoStudio
- Enter location: http://rhostudio.rhomobile.com



Check Rhomobile (or RhoStudio) and press Next. Follow instructions.



## Generate Rhodes Application

You should open application wizard. In the menu: File->New->Projects->Rhomobile->Rhodes Application

Select Rhodes Application wizard:



Assign application name and destination folder (by default destination is eclipse workspace folder)



After pressing Finish button you'll see Rhodes generator script output in the output console (Rhodes build log console).



## Generate Rhodes Model

To generate Rhodes model and associated Controller and View templates you should right click on the application project in package explorer and open project popup menu.

Project popup menu:<br/>



In the project popup menu select 'Rhomobile->Rhodes Model' item to open Rhodes Model wizard. Use this wizard to assign model name and specify model fields as a coma separated string. (Keep in mind, whitespaces at the field name begining and end will be trimmed and whitespaces in the middle of the field name will be replaced with underscore character.)



After pressing Finish button you'll see Rhodes model generator script output in the output console (Rhodes build log console).


                                                           
## Edit build.yml

You should edit build.yml to manage Rhodes build time configuration. 
Double click on 'build.yml' item in project tree to open build.yml editor. 
In the editor you'll see two tabs: text editor and WISIWIG editor. 

WISIWIG editor:<br/>


Text editor:<br/>


Make changes on ant editor tabs and they appear in another.

Use WISIWIG editor to assign application name, application capabilities, log file name (locate in application folder after application run), and location of Rhodes (useful in case you have more then one Rhodes gem installed or build your app using Rhodes source code).

To change application capabilities press Add button and select items in the popup dialog. 

Application capabilities dialog:<br/>


Selected application capabilities will appear in text field in the WISIWIG editor. 

## Edit rhobuild.yml

Use rhobuild.yml to manage location(s) of platform SDK-s/JDK-s used to build Rhodes application; rhobuild.yml located in the Rhodes gem folder (or in the Rhodes source code folder). 

To edit rhobuild.yml open Preferences (Window->Preferences) and select Rhomobile item.

Main preference page:<br/>


Android preference page:<br/>



Use this page to set Android SDK and NDK path.

Blackberry preference page:<br/>



Use this page to set path to BlackBerry JDK-s, path to MDS server, and device simulator name

Windows Mobile preference page:<br/>



Use this page to set path to Cab Wizard utility used by build script to build cab file. 

## Build application

To start build process you should create Run Configuration. 

Run configuration dialog:<br/>



To edit configuration for the target platform open Run Configuration manager and double click on 'Rhodes application'. 

Rhodes configuration:<br/>



For Android and BlackBerry platform you may select emulator version. For Android platform you may assign AVD name. 

Press Run button to build and run application. Build output will appear in the Rhodes build output console. Application log will be available in the Rhodes application output console.

## Steps to build RhoStudio 

###Prerequisite Downloads
- download Java Development Kit (JDK)1.7   [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
- download [Eclipse 3.7.2 (Indigo SR2) for RCP and RAP Developers ](http://www.eclipse.org/downloads/packages/eclipse-rcp-and-rap-developers/indigosr2)
- download [Eclipse 3.7.2 delta pack](http://archive.eclipse.org/eclipse/downloads/drops/R-3.7.2-201202080800/download.php?dropFile=eclipse-3.7.2-delta-pack.zip)  
- download [DLTK SDK 4.0 core for Eclipse](http://www.eclipse.org/downloads/download.php?file=/technology/dltk/downloads/drops/R4.0/R-4.0-201206120903/dltk-core-sdk-R-4.0-201206120903.zip&mirror_id=1135)
- download [DLTK SDK 4.0 Ruby for Eclipse ](http://www.eclipse.org/downloads/download.php?file=/technology/dltk/downloads/drops/R4.0/R-4.0-201206120903/dltk-ruby-sdk-R-4.0-201206120903.zip) 

###Configure
- Install JDK(make sure its present in program files->java). 
- Extract Eclipse
- Install DeltaPack in eclipse by follwing below steps.
 - How to add it to your target platform
 - Download the DeltaPack as described above
 - Unzip it to a location .e.g. "C:\eclipse-3.7.2-delta-pack"
 - Open Window/Preferences.
 - Select PDE/Target Platform
 - Select your (active) target platform
 - Click Edit
 - Click Add
 - Select "Directory"
 - Click Next
 - In "Location" type: "C:\eclipse-3.7.2-delta-pack\eclipse"
 - Press Next
 - Press Finish
 - Press Finish
 - Press OK
- Clone RhoStudio sources from git repository
- install DLTK SDK 4.0 core into Eclipse(go to Help->Install New software->Add->Archieve->dltk-core-sdk-R-4.0-201206120903.zip) 
- install DLTK-ruby-SDK-R-4.0  into Eclipse(go to Help->Install New software->Add->Archieve->dltk-ruby-sdk-R-4.0-201206120903.zip) 
- import rhostudio project into workspace
- build project (menu item Project/Build). If this menu item disabled then uncheck 'build automatically' located below
- export jar package: 
   open export dialog (menu item File/Export); 
   in open window select tree item 'Plugin Development/Deployable plugin and fragments';
   in wizard you can assign destination directory for plugin jar package and his file name
- copy created plugin into you eclipse (in subfolder /plugins/) and reboot Eclipse

