package rhogenwizard.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

public class RhostudioPerspective implements IPerspectiveFactory 
{
	public static final String PERSPECTIVE_ID = "rhogenwizard.perspectives.RhostudioPerspective";
	
	private IPageLayout m_factory;

	public RhostudioPerspective()
	{
		super();
	}

	public void createInitialLayout(IPageLayout factory) 
	{
		this.m_factory = factory;
		
		addViews();
		addActionSets();
		addNewWizardShortcuts();
		addPerspectiveShortcuts();
		addViewShortcuts();
	}

	private void addViews() 
	{
		IFolderLayout bottom = m_factory.createFolder("bottomRight", IPageLayout.BOTTOM, 0.75f, m_factory.getEditorArea());
		
		bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		bottom.addView(IPageLayout.ID_PROGRESS_VIEW);

		IFolderLayout topLeft = m_factory.createFolder("topLeft", IPageLayout.LEFT, 0.25f, m_factory.getEditorArea());
		
		topLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);
	}

	private void addActionSets() 
	{
		m_factory.addActionSet("org.eclipse.debug.ui.launchActionSet"); //NON-NLS-1
		m_factory.addActionSet("org.eclipse.debug.ui.debugActionSet"); //NON-NLS-1
		m_factory.addActionSet("org.eclipse.debug.ui.profileActionSet"); //NON-NLS-1
	}

	private void addPerspectiveShortcuts()
	{
		m_factory.addPerspectiveShortcut("org.eclipse.team.ui.TeamSynchronizingPerspective"); //NON-NLS-1
		m_factory.addPerspectiveShortcut("org.eclipse.team.cvs.ui.cvsPerspective"); //NON-NLS-1
	}

	private void addNewWizardShortcuts() 
	{
		m_factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");//NON-NLS-1
		m_factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");//NON-NLS-1
		m_factory.addNewWizardShortcut("rhogenwizard.wizards.ModelWizard");//NON-NLS-1
		m_factory.addNewWizardShortcut("rhogenwizard.wizards.ExtensionWizard");//NON-NLS-1
		m_factory.addNewWizardShortcut("rhogenwizard.wizards.SourceAdapterWizard");//NON-NLS-1
	}

	private void addViewShortcuts() 
	{
//		factory.addShowViewShortcut("org.eclipse.ant.ui.views.AntView"); //NON-NLS-1
//		factory.addShowViewShortcut("org.eclipse.team.ccvs.ui.AnnotateView"); //NON-NLS-1
//		factory.addShowViewShortcut("org.eclipse.pde.ui.DependenciesView"); //NON-NLS-1
//		factory.addShowViewShortcut("org.eclipse.jdt.junit.ResultView"); //NON-NLS-1
//		factory.addShowViewShortcut("org.eclipse.team.ui.GenericHistoryView"); //NON-NLS-1
//		factory.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
//		factory.addShowViewShortcut(JavaUI.ID_PACKAGES);
//		factory.addShowViewShortcut(IPageLayout.ID_RES_NAV);
//		factory.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
//		factory.addShowViewShortcut(IPageLayout.ID_OUTLINE);
	}
}
