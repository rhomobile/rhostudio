package rhogenwizard.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

public class RhostudioPerspective implements IPerspectiveFactory 
{
	public static final String idEGitRepositaryView = "org.eclipse.egit.ui.RepositoriesView";
	public static final String idEGitActopnSet = "org.eclipse.egit.ui.gitaction";
	public static final String idDltkRubyPerspective = "org.eclipse.dltk.ruby.ui.RubyPerspective";
    public static final String idDltkRubyBrowsePerspective = "org.eclipse.dltk.ruby.ui.RubyBrowsingPerspective";
	public static final String idPerspective = "rhogenwizard.perspectives.RhostudioPerspective";
	
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
	}

	private void addViews() 
	{
		IFolderLayout bottom = m_factory.createFolder("bottomRight", IPageLayout.BOTTOM, 0.75f, m_factory.getEditorArea());
		
		bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		bottom.addView(IPageLayout.ID_PROGRESS_VIEW);
		bottom.addView(idEGitRepositaryView);

		IFolderLayout topLeft = m_factory.createFolder("topLeft", IPageLayout.LEFT, 0.25f, m_factory.getEditorArea());
		
		topLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);
	}

	private void addActionSets() 
	{
		m_factory.addActionSet("org.eclipse.debug.ui.launchActionSet"); //NON-NLS-1
		m_factory.addActionSet("org.eclipse.debug.ui.debugActionSet"); //NON-NLS-1
		m_factory.addActionSet("org.eclipse.debug.ui.profileActionSet"); //NON-NLS-1
		m_factory.addActionSet(idEGitActopnSet); //NON-NLS-1
	}

	private void addPerspectiveShortcuts()
	{
		m_factory.addPerspectiveShortcut(idDltkRubyPerspective); //NON-NLS-1
		m_factory.addPerspectiveShortcut(idDltkRubyBrowsePerspective); //NON-NLS-1
	}

	private void addNewWizardShortcuts() 
	{
		m_factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");//NON-NLS-1
		m_factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");//NON-NLS-1
		m_factory.addNewWizardShortcut("rhogenwizard.wizards.ModelWizard");//NON-NLS-1
		m_factory.addNewWizardShortcut("rhogenwizard.wizards.ExtensionWizard");//NON-NLS-1
		m_factory.addNewWizardShortcut("rhogenwizard.wizards.SpecWizard");//NON-NLS-1
		m_factory.addNewWizardShortcut("rhogenwizard.wizards.SourceAdapterWizard");//NON-NLS-1
	}
}
