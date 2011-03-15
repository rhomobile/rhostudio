Description

Rhodes plugin for easy create and editing rhodes application and model.

Steps of build plugin

1. Download latest JDK.

2. Download and install 'Eclipse for RCP and RAP Developers'
http://www.eclipse.org/downloads/packages/eclipse-rcp-and-rap-developers/heliossr2

3. Download plugin sourses from git repositary.

4. Import project into Eclipse workspace (menu item File/Import)

5. Build project (menu item Project/Build). If this menu item greyed then uncheck 'build automaticaly' located below.

6. Export jar package. 
   Open export dialog (menu item File/Export). In open window select tree item 'Plugin Development/Deployable plugin and fragments'. 
   In wizard you can assign destination directory for plugin jar package and his file name.

7. Copy created plugin into you eclipse (in subfolder /plugins/) and reboot his.