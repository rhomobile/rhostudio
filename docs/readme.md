How using Rhomobile Eclipse plugin

1. Create application

You should open application wizard (File->New->Projects->Rhomobile->Rhodes Application)

<a href='https://github.com/rhomobile/rhomobile-eclipse-plugin/tree/master/docs/image/app_wizard/1.jpg'>application wizard</a>

Assign application name and destination folder (by default destination is eclipse workspace folder)

<img src='https://github.com/rhomobile/rhomobile-eclipse-plugin/tree/master/docs/image/app_wizard/2.jpg'>

After press on finish button you can see script output in Rhodes build console

<app_wizard/4.jpg>

2. Create model

For create model you should find the application project in package explorer and was right click on it.

<model_wizard/1.jpg>

After popup menu opened, need select 'Rhomobile->Rhodes model' item. You should see model wizard.
Assign model name and fields of data set separated by comma. 
Whitespace in the middle in name of fields will be replaced on underline chars.

<model_wizard/3.jpg>

After model wizard finished you should see creation log in Rhodes build console.

<model_wizard/4.jpg>
                                                           
3. Edit build.yml

For open build.yml editor you should be double click on 'build.yml' item in project tree. 
After you opened editor  you see two tabs, text editor and WISIWIG editor. 

<yml_editor/1.jpg>

You can make changes in any editors and they appear in both editors.

<yml_editor/3.jpg>

In WISIWIG editor can assign application name, log file name (locate in application folder after run applicatin)
rhodes direcory, and capabilities.

For change capabilities need press Add button and select items in opened dialog. 

<yml_editor/2.jpg>

Selected capabilities appears in text field in editor. 

4. Edit rhobuild.yml

Open preferences (Window->Preferences) and select Rhomobile item.
You see main page and can change JDK path her. 

<preferences/1.jpg>

For android you can change Android SDK and NDK path.

<preferences/2.jpg>

For blackberry you can change path for selected version and add new version

<preferences/3.jpg>

For Windows Mobile you can change path to Cab Wizard utility

<preferences/5.jpg>

5. Build application

For start build process you should create run configuration. 

<configuration/1.jpg>

After open configuration manager and double click on 'Rhodes application' item you 
can edit confiruration for terget platform. 

<configuration/2.jpg>

For android and blackberry platform you can select emulator version.

For android platform you can assign AVD name. 

After all changes are made, you can run the configuration (press Run button). 

Build is started and build output appears in build console.