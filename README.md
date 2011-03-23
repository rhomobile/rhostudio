rhomobile-eclipse-plugin
---

An eclipse plugin for easily generating and developing rhodes applications.

Steps to build the plugin:

1. Download latest JDK.

2. Download and install 'Eclipse for RCP and RAP Developers'
http://www.eclipse.org/downloads/packages/eclipse-rcp-and-rap-developers/heliossr2

3. Download plugin sources from git repository.

4. Import project into Eclipse workspace (menu item File/Import)

5. Build project (menu item Project/Build). If this menu item disabled then uncheck 'build automatically' located below.

6. Export jar package. 
   Open export dialog (menu item File/Export). In open window select tree item 'Plugin Development/Deployable plugin and fragments'. 
   In wizard you can assign destination directory for plugin jar package and his file name.

7. Copy created plugin into you eclipse (in subfolder /plugins/) and reboot his.