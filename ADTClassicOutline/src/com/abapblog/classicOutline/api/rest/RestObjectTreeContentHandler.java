package com.abapblog.classicOutline.api.rest;

import com.abapblog.classicOutline.tree.ObjectTree;
import com.abapblog.classicOutline.tree.SourceNode;
import com.abapblog.classicOutline.views.LinkedObject;
import com.sap.conn.jco.JCoTable;

public class RestObjectTreeContentHandler {

	public static ObjectTree deserialize(LinkedObject linkedObject, JCoTable restTable) {
		int definitionStartId = 0;
		ObjectTree newObjectTree = new ObjectTree(linkedObject);
		if (restTable == null)
			return null;
		for (int i = 0; i < restTable.getNumRows(); i++) {
			restTable.setRow(i);
			SourceNode sourceNode = new SourceNode(restTable.getInt(SourceNode.fieldNameId));
			sourceNode.setChild(restTable.getInt(SourceNode.fieldNameChild));
			sourceNode.setParent(restTable.getInt(SourceNode.fieldNameParent));
			sourceNode.setType(restTable.getString(SourceNode.fieldNameType));
			sourceNode.setName(restTable.getString(SourceNode.fieldNameName));
			sourceNode.setText1(restTable.getString(SourceNode.fieldNameText1));
			sourceNode.setText2(restTable.getString(SourceNode.fieldNameText2));
			sourceNode.setText8(restTable.getString(SourceNode.fieldNameText8));
			sourceNode.setText9(restTable.getString(SourceNode.fieldNameText9));
			sourceNode.setKind5(restTable.getInt(SourceNode.fieldNameKind5));
			try {
				sourceNode.setKind4(Integer.parseInt(restTable.getString(SourceNode.fieldNameKind4)));
			} catch (NumberFormatException e) {

			}

			sourceNode.setKind6(restTable.getString(SourceNode.fieldNameKind6).equals("X"));
			sourceNode.setKind7(restTable.getString(SourceNode.fieldNameKind7).equals("X"));
			sourceNode.setKind8(restTable.getString(SourceNode.fieldNameKind8).equals("X"));
			sourceNode.setKind3(restTable.getString(SourceNode.fieldNameKind3).equals("X"));
			try {
				sourceNode.setKind9(Integer.parseInt(restTable.getString(SourceNode.fieldNameKind9)));
			} catch (NumberFormatException e) {

			}
			sourceNode.setIndex(i);
			if (sourceNode.getType().equals("COLD")) {
				definitionStartId = restTable.getInt(SourceNode.fieldNameNext);
			} else if (sourceNode.getType().equals("OOLD")) {
				sourceNode.setDefinitionStartId(definitionStartId);
			} else {
				definitionStartId = 0;
			}
			newObjectTree.addChild(sourceNode);
		}
		return newObjectTree;
	}

}
