package com.abapblog.classicOutline.api.Rest;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;

import com.abapblog.classicOutline.Activator;
import com.abapblog.classicOutline.api.IApiCaller;
import com.abapblog.classicOutline.preferences.PreferenceConstants;
import com.abapblog.classicOutline.tree.ObjectTree;
import com.abapblog.classicOutline.tree.SourceNode;
import com.abapblog.classicOutline.tree.TreeNode;
import com.abapblog.classicOutline.utils.AbapRelease;
import com.abapblog.classicOutline.utils.BackendComponentVersion;
import com.abapblog.classicOutline.utils.ProjectUtility;
import com.abapblog.classicOutline.views.LinkedObject;

import com.sap.adt.destinations.ui.logon.AdtLogonServiceUIFactory;
import com.sap.adt.project.IAdtCoreProject;
import java.nio.charset.Charset;
import com.sap.adt.communication.message.IHeaders;
import com.sap.adt.communication.message.IMessageBody;
import com.sap.adt.communication.message.IResponse;
import com.sap.adt.communication.message.IResponse.IErrorInfo;
import com.sap.adt.communication.message.ByteArrayMessageBody;
import com.sap.adt.communication.message.HeadersFactory;
import com.sap.adt.communication.resources.AdtRestResourceFactory;
import java.net.URI;
import com.sap.adt.communication.resources.IRestResource;
import com.sap.adt.communication.resources.IQueryParameter;
import com.sap.adt.communication.resources.QueryParameter;
import java.net.HttpURLConnection;

public class RestCaller implements IApiCaller {
	private static IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	private static HashMap<String, Boolean> RestAuthorityForProject = new HashMap<String, Boolean>();

	@Override
	public ObjectTree getObjectTree(LinkedObject linkedObject, boolean forceRefresh) {

		ObjectTree restObjectTree = getObjectTree(linkedObject);
		return (forceRefresh || restObjectTree == null) ? getNewObjectTree(linkedObject) : restObjectTree;

	}

	@Override
	public ObjectTree getNewObjectTree(LinkedObject linkedObject) {
		try {
			String destinationId = ProjectUtility.getDestinationID(linkedObject.getProject());
			/*
			JCoDestination destination = JCoDestinationManager.getDestination(destinationId);
			String fm = "Z_ADTCO_GET_OBJECT_TREE";

			if (RestAuthorityForProject.containsKey(destinationId)) {
				if (RestAuthorityForProject.get(destinationId).booleanValue() == false) {
					return createDummyObjectTree(linkedObject);
				}
			}
			JCoFunction function = destination.getRepository().getFunction(fm);
			if (function == null) {
				return createDummyObjectTree(linkedObject);
			}
			function.getImportParameterList().getField("OBJECT_NAME").setValue(linkedObject.getParentName());
			function.getImportParameterList().getField("OBJECT_TYPE").setValue(linkedObject.getParentType());

			addParametersForTreeCall(function);

			try {
				function.execute(destination);
				JCoTable objectTree = function.getExportParameterList().getTable("TREE");
				return RestObjectTreeContentHandler.deserialize(linkedObject, objectTree);
			} catch (AbapException e) {
				System.out.println(e.toString());
				return createDummyObjectTree(linkedObject);
			}
		} catch (

		JCoException e) {
			e.printStackTrace();
			return createDummyObjectTree(linkedObject);
		}*/
		return createDummyObjectTree(linkedObject);
	}

	private ObjectTree createDummyObjectTree(LinkedObject linkedObject) {
		ObjectTree newObjectTree = new ObjectTree(linkedObject);
		SourceNode sourceNode = new SourceNode(1);
		sourceNode.setName("DummyNode");
		sourceNode.setText1("Z_ADTCO_GET_OBJECT_TREE not found or you don't have Rest authorizations for it.");
		sourceNode.setType("CO");
		newObjectTree.addChild(sourceNode);

		sourceNode = new SourceNode(2);
		sourceNode.setName("DummyNode");
		sourceNode.setText1("This plugin needs a ABAP Backend components that have to be installed in the system.");
		sourceNode.setType("CO");
		newObjectTree.addChild(sourceNode);

		sourceNode = new SourceNode(3);
		sourceNode.setName("DummyNode");
		sourceNode.setText1(
				"Use abapGit to install repository from https://github.com/fidley/ADT-Classic-Outline-Backend");
		sourceNode.setType("CO");
		newObjectTree.addChild(sourceNode);

		if (!RestAuthorityForProject.containsKey(ProjectUtility.getDestinationID(linkedObject.getProject())))
			RestAuthorityForProject.put(linkedObject.getProject().getName(), false);

		return newObjectTree;
	}

	private void addParametersForTreeCall(JCoFunction function) {
		try {
			JCoTable parametersTable = function.getImportParameterList().getTable("PARAMETERS");
			addBoolParameter(parametersTable, PreferenceConstants.P_FETCH_METHOD_REDEFINITIONS);
			addBoolParameter(parametersTable, PreferenceConstants.P_LOAD_ALL_LEVELS_OF_SUBCLASSES);
			addBoolParameter(parametersTable, PreferenceConstants.P_LOAD_ALL_LEVELS_OF_REDEFINITIONS);

		} catch (Exception e) {
			e.getLocalizedMessage();
		}
	}

	private void addBoolParameter(JCoTable parametersTable, String parameterName) {
		parametersTable.appendRow();
		parametersTable.setValue("NAME", parameterName);
		parametersTable.setValue("VALUE", parseToAbapBool(store.getBoolean(parameterName)));
	}

	private String parseToAbapBool(boolean booleanValue) {
		return booleanValue ? "X" : "";
	}

	@Override
	public ObjectTree getObjectTree(LinkedObject linkedObject) {
		return objectsList.stream()
				.filter(restObjectTree -> restObjectTree.getLinkedObject().getName().equals(linkedObject.getName())
						&& restObjectTree.getLinkedObject().getProject().getName()
								.equals(linkedObject.getProject().getName())
						&& restObjectTree.getLinkedObject().getType().equals(linkedObject.getType()))
				.findFirst().orElse(null);
	}

	@Override
	public String getUriForTreeNode(TreeNode treeNode) {
		LinkedObject linkedObject = treeNode.getLinkedObject();
		String destinationId = ProjectUtility.getDestinationID(linkedObject.getProject());
		try {
			JCoDestination destination = JCoDestinationManager.getDestination(destinationId);
			JCoFunction function = destination.getRepository().getFunction("Z_ADTCO_GET_URI_FOR_TREE_NODE");
			if (function == null)
				throw new RuntimeException("Z_ADTCO_GET_URI_FOR_TREE_NODE not found."
						+ "This plugin needs a ABAP Backend components that have to be installed in the system in order to use it."
						+ "Use abapGit to install repository from https://github.com/fidley/ADT-Classic-Outline-Backend");

			function.getImportParameterList().getField("OBJECT_NAME").setValue(linkedObject.getParentName());
			function.getImportParameterList().getField("OBJECT_TYPE").setValue(linkedObject.getParentType());
			RestObjectNodeContentHandler.serialize(treeNode.getSourceNode(),
					function.getImportParameterList().getStructure("NODE"));

			try {
				function.execute(destination);
				return function.getExportParameterList().getString("URI");

			} catch (AbapException e) {
				System.out.println(e.toString());
				return "";
			}
		} catch (JCoException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public String getMasterProgramForInclude(LinkedObject linkedObject) {
		String destinationId = ProjectUtility.getDestinationID(linkedObject.getProject());
		try {
			JCoDestination destination = JCoDestinationManager.getDestination(destinationId);
			JCoFunction function = destination.getRepository().getFunction("Z_ADTCO_GET_INC_MASTER_PROGRAM");
			if (function == null) {
				System.out.println("Z_ADTCO_GET_INC_MASTER_PROGRAM not found."
						+ "This plugin needs a lastest ABAP Backend components that have to be installed in the system in order to use it."
						+ "Use abapGit to install repository from https://github.com/fidley/ADT-Classic-Outline-Backend");
				return "";
			}

			function.getImportParameterList().getField("INCLUDE").setValue(linkedObject.getName());
			try {
				function.execute(destination);
				String masterType = function.getExportParameterList().getString("MASTER_TYPE");
				if (!masterType.isEmpty()) {
					linkedObject.setParentType(masterType);
				}
				return function.getExportParameterList().getString("MASTER");

			} catch (AbapException e) {
				System.out.println(e.toString());
				return "";
			}
		} catch (JCoException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public AbapRelease getAbapRelease(IProject project) {
		String destinationId = ProjectUtility.getDestinationID(project);
		try {
			JCoDestination destination = JCoDestinationManager.getDestination(destinationId);
			JCoFunction function = destination.getRepository().getFunction("DELIVERY_GET_COMPONENT_RELEASE");
			if (function == null) {
				System.out.println("DELIVERY_GET_COMPONENT_RELEASE not found."
						+ "This plugin needs a lastest ABAP Backend components that have to be installed in the system in order to use it."
						+ "Use abapGit to install repository from https://github.com/fidley/ADT-Classic-Outline-Backend");
				return null;
			}

			String BASIS_COMPONENT_NAME = "SAP_BASIS";
			function.getImportParameterList().getField("IV_COMPNAME").setValue(BASIS_COMPONENT_NAME);
			try {
				function.execute(destination);

				String version = function.getExportParameterList().getString("EV_COMPVERS");
				String patchLevel = function.getExportParameterList().getString("EV_PATCHLVL");
				AbapRelease abapRelease = new AbapRelease(version, patchLevel, project);
				return abapRelease;

			} catch (AbapException e) {
				System.out.println(e.toString());
				return null;
			}
		} catch (JCoException e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public BackendComponentVersion getBackendComponentVersion(IProject project) {
		String destinationId = ProjectUtility.getDestinationID(project);
		try {
			JCoDestination destination = JCoDestinationManager.getDestination(destinationId);
			JCoFunction function = destination.getRepository().getFunction("Z_ADTCO_GET_BACKEND_COMP_VER");
			if (function == null) {
				System.out.println("Z_ADTCO_GET_BACKEND_COMP_VER not found."
						+ "This plugin needs a lastest ABAP Backend components that have to be installed in the system in order to use it."
						+ "Use abapGit to install repository from https://github.com/fidley/ADT-Classic-Outline-Backend");
				return new BackendComponentVersion(0, project);
			}

			try {
				function.execute(destination);

				Integer version = function.getExportParameterList().getInt("VERSION");
				return new BackendComponentVersion(version, project);

			} catch (AbapException e) {
				System.out.println(e.toString());
				return null;
			}
		} catch (JCoException e) {
			e.printStackTrace();
		}
		return new BackendComponentVersion(0, project);

	}

}
