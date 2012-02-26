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

<img src='http://rhodocs.s3.amazonaws.com/eclipse-plugin/install/add-update-site.png'></img>

Check Rhomobile (or RhoStudio) and press Next. Follow instructions.

<img src='http://rhodocs.s3.amazonaws.com/eclipse-plugin/install/install.png'></img>

## Generate Rhodes Application

You should open application wizard. In the menu: File->New->Projects->Rhomobile->Rhodes Application

Select Rhodes Application wizard:

<img src='http://rhodocs.s3.amazonaws.com/eclipse-plugin/app_wizard/1.jpg'></img>

Assign application name and destination folder (by default destination is eclipse workspace folder)

<img src='http://rhodocs.s3.amazonaws.com/eclipse-plugin/app_wizard/2.jpg'></img>

After pressing Finish button you'll see Rhodes generator script output in the output console (Rhodes build log console).

<img src='http://rhodocs.s3.amazonaws.com/eclipse-plugin/app_wizard/4.jpg'></img>

## Generate Rhodes Model

To generate Rhodes model and associated Controller and View templates you should right click on the application project in package explorer and open project popup menu.

Project popup menu:<br/>

<img src='http://rhodocs.s3.amazonaws.com/eclipse-plugin/model_wizard/1.jpg'></img>

In the project popup menu select 'Rhomobile->Rhodes Model' item to open Rhodes Model wizard. Use this wizard to assign model name and specify model fields as a coma separated string. (Keep in mind, whitespaces at the field name begining and end will be trimmed and whitespaces in the middle of the field name will be replaced with underscore character.)

<img src='http://rhodocs.s3.amazonaws.com/eclipse-plugin/model_wizard/3.jpg'></img>

After pressing Finish button you'll see Rhodes model generator script output in the output console (Rhodes build log console).

<img src='http://rhodocs.s3.amazonaws.com/eclipse-plugin/model_wizard/4.jpg'></img>
                                                           
## Edit build.yml

You should edit build.yml to manage Rhodes build time configuration. 
Double click on 'build.yml' item in project tree to open build.yml editor. 
In the editor you'll see two tabs: text editor and WISIWIG editor. 

WISIWIG editor:<br/>
<img src='http://rhodocs.s3.amazonaws.com/eclipse-plugin/yml_editor/1.jpg'></img>

Text editor:<br/>
<img src='http://rhodocs.s3.amazonaws.com/eclipse-plugin/yml_editor/3.jpg'></img>

Make changes on ant editor tabs and they appear in another.

Use WISIWIG editor to assign application name, application capabilities, log file name (locate in application folder after application run), and location of Rhodes (useful in case you have more then one Rhodes gem installed or build your app using Rhodes source code).

To change application capabilities press Add button and select items in the popup dialog. 

Application capabilities dialog:<br/>
<img src='http://rhodocs.s3.amazonaws.com/eclipse-plugin/yml_editor/2.jpg'></img>

Selected application capabilities will appear in text field in the WISIWIG editor. 

## Edit rhobuild.yml

Use rhobuild.yml to manage location(s) of platform SDK-s/JDK-s used to build Rhodes application; rhobuild.yml located in the Rhodes gem folder (or in the Rhodes source code folder). 

To edit rhobuild.yml open Preferences (Window->Preferences) and select Rhomobile item.

Main preference page:<br/>
<img src='http://rhodocs.s3.amazonaws.com/eclipse-plugin/preferences/1.jpg'></img>

Android preference page:<br/>

<img src='http://rhodocs.s3.amazonaws.com/eclipse-plugin/preferences/2.jpg'></img>

Use this page to set Android SDK and NDK path.

Blackberry preference page:<br/>

<img src='http://rhodocs.s3.amazonaws.com/eclipse-plugin/preferences/3.jpg'></img>

Use this page to set path to BlackBerry JDK-s, path to MDS server, and device simulator name

Windows Mobile preference page:<br/>

<img src='http://rhodocs.s3.amazonaws.com/eclipse-plugin/preferences/5.jpg'></img>

Use this page to set path to Cab Wizard utility used by build script to build cab file. 

## Build application

To start build process you should create Run Configuration. 

Run configuration dialog:<br/>

<img src='http://rhodocs.s3.amazonaws.com/eclipse-plugin/configuration/1.jpg'></img>

To edit configuration for the target platform open Run Configuration manager and double click on 'Rhodes application'. 

Rhodes configuration:<br/>

<img src='http://rhodocs.s3.amazonaws.com/eclipse-plugin/configuration/2.jpg'></img>

For Android and BlackBerry platform you may select emulator version. For Android platform you may assign AVD name. 

Press Run button to build and run application. Build output will appear in the Rhodes build output console. Application log will be available in the Rhodes application output console.

## Steps to build RhoStudio

- download latest JDK
- download and install [Eclipse for RCP and RAP Developers](http://www.eclipse.org/downloads/packages/eclipse-rcp-and-rap-developers/heliossr2)
- install DLTK (http://download.eclipse.org/technology/dltk/updates/)
- clone RhoStudio sources from git repository
- mport project into Eclipse workspace (menu item File/Import)
- build project (menu item Project/Build). If this menu item disabled then uncheck 'build automatically' located below
- export jar package: 
   open export dialog (menu item File/Export); 
   in open window select tree item 'Plugin Development/Deployable plugin and fragments';
   in wizard you can assign destination directory for plugin jar package and his file name
- copy created plugin into you eclipse (in subfolder /plugins/) and reboot Eclipse
