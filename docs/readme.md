How using Rhomobile Eclipse plugin

1. Create application

You should open application wizard (File->New->Projects->Rhomobile->Rhodes Application)

<a href='http://rhodocs.s3.amazonaws.com/eclipse-plugin/app_wizard/1.jpg'>Select wizzard</a>

Assign application name and destination folder (by default destination is eclipse workspace folder)

<a href='http://rhodocs.s3.amazonaws.com/eclipse-plugin/app_wizard/2.jpg'>Application wizard</a>

After press on finish button you can see script output in Rhodes build console

<a href='http://rhodocs.s3.amazonaws.com/eclipse-plugin/app_wizard/4.jpg'>Build console</a>

2. Create model

For create model you should find the application project in package explorer and was right click on it.

<a href='http://rhodocs.s3.amazonaws.com/eclipse-plugin/model_wizard/1.jpg'>Project popup menu</a>

After popup menu opened, need select 'Rhomobile->Rhodes model' item. You should see model wizard.
Assign model name and fields of data set separated by comma. 
Whitespace in the middle in name of fields will be replaced on underline chars.

<a href='http://rhodocs.s3.amazonaws.com/eclipse-plugin/model_wizard/3.jpg'>Model wizard</a>

After model wizard finished you should see creation log in Rhodes build console.

<a href='http://rhodocs.s3.amazonaws.com/eclipse-plugin/model_wizard/4.jpg'>Build console</a>
                                                           
3. Edit build.yml

For open build.yml editor you should be double click on 'build.yml' item in project tree. 
After you opened editor  you see two tabs, text editor and WISIWIG editor. 

<a href='http://rhodocs.s3.amazonaws.com/eclipse-plugin/yml_editor/1.jpg'>WISIWIG editor</a>

You can make changes in any editors and they appear in both editors.

<a href='http://rhodocs.s3.amazonaws.com/eclipse-plugin/yml_editor/3.jpg'>Text editor</a>

In WISIWIG editor can assign application name, log file name (locate in application folder after run applicatin)
rhodes direcory, and capabilities.

For change capabilities need press Add button and select items in opened dialog. 

<a href='http://rhodocs.s3.amazonaws.com/eclipse-plugin/yml_editor/2.jpg'>Capabilities dialog</a>

Selected capabilities appears in text field in editor. 

4. Edit rhobuild.yml

Open preferences (Window->Preferences) and select Rhomobile item.
You see main page and can change JDK path her. 

<a href='http://rhodocs.s3.amazonaws.com/eclipse-plugin/preferences/1.jpg'>Main preference page</a>

For android you can change Android SDK and NDK path.

<a href='http://rhodocs.s3.amazonaws.com/eclipse-plugin/preferences/2.jpg'>Android preference page</a>

For blackberry you can change path for selected version and add new version

<a href='http://rhodocs.s3.amazonaws.com/eclipse-plugin/preferences/3.jpg'>Blackberry preference page</a>

For Windows Mobile you can change path to Cab Wizard utility

<a href='http://rhodocs.s3.amazonaws.com/eclipse-plugin/preferences/5.jpg'>Windows Mobile preference page</a>

5. Build application

For start build process you should create run configuration. 

<a href='http://rhodocs.s3.amazonaws.com/eclipse-plugin/configuration/1.jpg'>Commin configuration dialog</a>

After open configuration manager and double click on 'Rhodes application' item you 
can edit confiruration for terget platform. 

<a href='http://rhodocs.s3.amazonaws.com/eclipse-plugin/configuration/2.jpg'>Rhodes configuration</a>

For android and blackberry platform you can select emulator version.

For android platform you can assign AVD name. 

After all changes are made, you can run the configuration (press Run button). 

Build is started and build output appears in build console.