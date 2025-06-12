package com.abapblog.classicOutline.views;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;

@SuppressWarnings("restriction")
public class AbapPageLoadListenerHandler implements IAbapPageLoadListener {

	String destinationId = "";
	private static LinkWithEditorPartListener linkWithEditorPartListener;

	public AbapPageLoadListenerHandler(String destinationId) {
		this.destinationId = destinationId;
	}

	@Override
	public void pageLoaded(IAbapSourcePage sourcePage) {
		if (linkWithEditorPartListener == null) {
			linkWithEditorPartListener = LinkWithEditorPartListener.get();
			final IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			workbenchWindow.getPartService().addPartListener(linkWithEditorPartListener);
		}

		if (sourcePage.getFile().getProject().getName().equals(getDestinationId())) {
			if (View.view != null) {
				if (View.getCurrentTree() == null)
					View.view.reloadOutlineContent(true, true, false);
				if (View.getCurrentTree() != null && View.getCurrentTree().getLinkedObjects().contains(null))
					View.view.reloadOutlineContent(true, true, false);
				AbapPageLoadListener.removeListener(this);
			}
		}
	}

	@Override
	public String getDestinationId() {
		return destinationId;
	}

}
