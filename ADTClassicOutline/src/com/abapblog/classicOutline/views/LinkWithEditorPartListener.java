package com.abapblog.classicOutline.views;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.abapblog.classicOutline.Activator;
import com.abapblog.classicOutline.preferences.PreferenceConstants;
import com.sap.adt.tools.core.ui.editors.IAdtFormEditor;

@SuppressWarnings("restriction")
public class LinkWithEditorPartListener implements IPartListener2 {
	private static final String CLASSIC_OUTLINE_VIEW_ID = "com.abapblog.classicOutline.view";
	private static final String ECLIPSE_STANDARD_OUTLINE_VIEW_ID = "org.eclipse.ui.views.ContentOutline";
	private final ILinkedWithEditorView view;
	private static IEditorPart previousEditor = null;
	// Check preference before opening views
	private static final IPreferenceStore store = Activator.getDefault().getPreferenceStore();

	public LinkWithEditorPartListener(ILinkedWithEditorView view) {
		this.view = view;
	}

	@Override
	public void partActivated(IWorkbenchPartReference ref) {
		if (ref.getPart(true) instanceof IEditorPart) {
			IEditorPart editor = view.getViewSite().getPage().getActiveEditor();
			if (editor == previousEditor) {
				return;
			}
			previousEditor = editor;
			view.editorActivated(editor);

			boolean activateCorrectOutline = store.getBoolean(PreferenceConstants.P_ACTIVATE_CORRECT_OUTLINE);
			if (!activateCorrectOutline) {
				return;
			}

			if (editor instanceof IAdtFormEditor) {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IViewReference viewRef = page.findViewReference(CLASSIC_OUTLINE_VIEW_ID);
					boolean isOpened = viewRef != null;
					boolean isVisible = isOpened && page.isPartVisible(viewRef.getView(false));
					if (!isOpened || isVisible) {
						return;
					}
					page.showView(CLASSIC_OUTLINE_VIEW_ID, null, IWorkbenchPage.VIEW_VISIBLE);
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IViewReference viewRef = page.findViewReference(ECLIPSE_STANDARD_OUTLINE_VIEW_ID);
					boolean isOpened = viewRef != null;
					boolean isVisible = isOpened && page.isPartVisible(viewRef.getView(false));
					if (!isOpened || isVisible) {
						return;
					}
					page.showView(ECLIPSE_STANDARD_OUTLINE_VIEW_ID, null, IWorkbenchPage.VIEW_VISIBLE);
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference ref) {
		if (ref.getPart(true) == view) {
			view.editorActivated(view.getViewSite().getPage().getActiveEditor());
		}
	}

	@Override
	public void partOpened(IWorkbenchPartReference ref) {
		if (ref.getPart(true) == view) {
			view.editorActivated(view.getViewSite().getPage().getActiveEditor());
		}
	}

	@Override
	public void partVisible(IWorkbenchPartReference ref) {
		if (ref.getPart(true) == view) {
			IEditorPart editor = view.getViewSite().getPage().getActiveEditor();
			if (editor != null) {
				view.editorActivated(editor);
			}
		}
	}

	@Override
	public void partClosed(IWorkbenchPartReference ref) {
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference ref) {
	}

	@Override
	public void partHidden(IWorkbenchPartReference ref) {
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference ref) {
	}
}