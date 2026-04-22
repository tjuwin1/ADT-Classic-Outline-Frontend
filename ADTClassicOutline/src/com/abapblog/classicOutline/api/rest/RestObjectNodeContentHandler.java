package com.abapblog.classicOutline.api.rest;

import com.abapblog.classicOutline.tree.SourceNode;
import com.sap.conn.jco.JCoStructure;

public class RestObjectNodeContentHandler {

	public static JCoStructure serialize(SourceNode sourceNode, JCoStructure RestStructure) {

		restStructure.setValue(SourceNode.fieldNameId, sourceNode.getId());
		restStructure.setValue(SourceNode.fieldNameChild, sourceNode.getChild());
		restStructure.setValue(SourceNode.fieldNameParent, sourceNode.getParent());
		restStructure.setValue(SourceNode.fieldNameType, sourceNode.getType());
		restStructure.setValue(SourceNode.fieldNameName, sourceNode.getName());
		restStructure.setValue(SourceNode.fieldNameText1, sourceNode.getText1());
		restStructure.setValue(SourceNode.fieldNameText2, sourceNode.getText2());
		restStructure.setValue(SourceNode.fieldNameText8, sourceNode.getText8());
		restStructure.setValue(SourceNode.fieldNameText9, sourceNode.getText9());
		return restStructure;

	}

}
