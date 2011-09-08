/*******************************************************************************
 * Copyright (c) 2010, 2011 SAP AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Dimov (SAP AG) - initial API and implementation
 *     Kaloyan Raev (SAP AG)
 *******************************************************************************/
package org.eclipse.libra.facet.test;

import static org.eclipse.libra.facet.OSGiBundleFacetUtils.VIRTUAL_COMPONENT_PATH;
import static org.eclipse.libra.facet.OSGiBundleFacetUtils.WEB_CONTEXT_PATH_HEADER;
import static org.eclipse.libra.facet.OSGiBundleFacetUtils.getContextRootFromPDEModel;
import static org.eclipse.libra.facet.OSGiBundleFacetUtils.getContextRootFromWTPModel;
import static org.eclipse.libra.facet.OSGiBundleFacetUtils.setContextRootInPDEModel;
import static org.eclipse.libra.facet.OSGiBundleFacetUtils.setContextRootInWTPModel;
import static org.eclipse.wst.common.tests.OperationTestCase.deleteAllProjects;
import static org.eclipse.wst.common.tests.OperationTestCase.waitOnJobs;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class WebContextRootSynchronizerTest {
	
	private static final String APPEND = "Changed";	
	
	private static int MAX_ATTEMPTS = 20;
	
	private String expectedWebContextRoot;
	private IProject wabProject;
	
	@Before
	public void importProject() throws Exception {
		// clean up the workspace
		deleteAllProjects();
		waitOnJobs();
		// create new WAB project
		String projectName = "TestWAB" + System.currentTimeMillis();
		expectedWebContextRoot = '/' + projectName + APPEND;
		wabProject = createWabProject(projectName);
		waitOnJobs();
	}
	
	@After
	public void waitJobs() throws Exception {
		// Wait for all validation jobs to end before ending test....
		waitOnJobs();
	}
	
	@Test
	public void testPDEChangeLeadsToWTPChange() throws Exception {		
    	String newPDEWebContextPath = getContextRootFromPDEModel(wabProject) + APPEND;
    	setContextRootInPDEModel(wabProject, newPDEWebContextPath, null);
    	checkModels(wabProject, expectedWebContextRoot);
	}
	
	@Test
	public void testWTPChangeLeadsToPDEChange() throws Exception {
    	String newWTPWebContextPath = getContextRootFromWTPModel(wabProject) + APPEND;
    	setContextRootInWTPModel(wabProject, newWTPWebContextPath);        	
    	checkModels(wabProject, expectedWebContextRoot);
	}
	
	@Test
	public void testSettingsFileChangeLeadsToModelChange() throws Exception {
		String newWTPWebContextPath = getContextRootFromWTPModel(wabProject) + APPEND;
		setWebContextRootInSettings(wabProject, newWTPWebContextPath);
		checkModels(wabProject, expectedWebContextRoot);
	}
	
	@Test
	public void testManifetsFileChangeLeadsToModelChange() throws Exception {
		String newPDEWebContextPath = getContextRootFromPDEModel(wabProject) + APPEND;
		setWebContextRootInManifest(wabProject, newPDEWebContextPath);
	    checkModels(wabProject, expectedWebContextRoot);
	} 
	
	@Test
	public void testRemoveWebContextRootFromPDEModel() throws Exception {
		setContextRootInPDEModel(wabProject, null, null);
	    checkModels(wabProject, null);
	} 
	
	@Test
	public void testRemoveWebContextRootFromWTPModel() throws Exception {
		setContextRootInWTPModel(wabProject, null);
	    checkModels(wabProject, null);
	} 
	
	
	// ------------------------------ private helper methods ----------------------------------------------
	
	private IProject createWabProject(String projectName) throws CoreException {
		IFacetedProjectWorkingCopy wc = FacetedProjectFramework.createNewProject();
		wc.setProjectName(projectName);
		wc.setSelectedPreset("osgi.web.bundle.preset");
		wc.commitChanges(null);
		return wc.getProject();
	}
	
	private boolean areModelsEqualToTheExpectedValue(IProject project, String expectedWebContextRoot) throws CoreException {
		String pdeWebContextPath = getContextRootFromPDEModel(project);
		String wtpWebContextPath = getContextRootFromWTPModel(project);
		
		if (expectedWebContextRoot == null) {
			return pdeWebContextPath == null && wtpWebContextPath == null;
		} else {
			return expectedWebContextRoot.equals(pdeWebContextPath) && expectedWebContextRoot.equals(wtpWebContextPath); 
		}
	} 
	
	private void checkModels(IProject wabProject, String expectedWebContextRoot) throws CoreException {
		boolean equal = false;
    	
    	for (int attempt = 0; attempt < MAX_ATTEMPTS && !equal; attempt++) {    		
    		equal = areModelsEqualToTheExpectedValue(wabProject, expectedWebContextRoot);
    		if (!equal) {
    			// we need to wait for the other model to refresh
    			wabProject.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    			try {
    				Thread.sleep(1000);// 1 second
    			} catch (InterruptedException iexc) {
    				System.out.println("Interrupted exception caught. Checking once more.");
    				wabProject.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
    				equal = areModelsEqualToTheExpectedValue(wabProject, expectedWebContextRoot);
    			}
    		}
    	}  	       	

    	// check that both models are set to the new context root
    	Assert.assertEquals(expectedWebContextRoot, getContextRootFromPDEModel(wabProject));
    	Assert.assertEquals(expectedWebContextRoot, getContextRootFromWTPModel(wabProject));
	}
	
	private void setWebContextRootInSettings(IProject wabProject, String newValue) throws ParserConfigurationException, SAXException, IOException, TransformerException, CoreException {
		IFile settingsFile = wabProject.getFile(VIRTUAL_COMPONENT_PATH);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		Document xmlDocument = db.parse(settingsFile.getContents());
		changeWebContextRootFromSettings(xmlDocument, newValue);
				
		InputStream is = new ByteArrayInputStream(saveDomToBytes(xmlDocument));
		settingsFile.setContents(is, IResource.KEEP_HISTORY | IResource.FORCE, null);
	}

	private Element getWebModuleElement(Document xmlDocument) {
		Element root = xmlDocument.getDocumentElement();
		NodeList children = root.getElementsByTagName("wb-module");
		Element webModuleElem = (Element)children.item(0);
		return webModuleElem;
	}

	private Element getContextRootProperty(Element webModuleElement) {
		NodeList webModuleElementProperties = webModuleElement.getElementsByTagName("property");
		for (int i = 0; i < webModuleElementProperties.getLength(); i++) {
			Element property = (Element) webModuleElementProperties.item(i);
			NamedNodeMap attributes = property.getAttributes();
			Attr nameAttribute = (Attr) attributes.getNamedItem("name");
			if (nameAttribute.getNodeValue().equals("context-root")) {
				return property;
			}
		}
		return null;
	}

	private Attr getValueAttribute(Element contextRootProperty) {
		NamedNodeMap attributes = contextRootProperty.getAttributes();
		Attr valueAttribute = (Attr) attributes.getNamedItem("value");
		if (valueAttribute != null) {
			return valueAttribute;
		}
		return null;
	}

	private void changeWebContextRootFromSettings(Document xmlDocument, String newValue) {
		Element webModuleElement = getWebModuleElement(xmlDocument);
		Element contextRootProperty = getContextRootProperty(webModuleElement);
		Attr valueAttribute = getValueAttribute(contextRootProperty);
		valueAttribute.setNodeValue(newValue);
	}
	
	private byte[] saveDomToBytes(Document xmlDocument) throws TransformerException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.transform(new DOMSource(xmlDocument), new StreamResult(os));
		
		return os.toByteArray();
	}

	private void setWebContextRootInManifest(IProject wabProject, String newContextRoot) throws CoreException, IOException {
		IFile manifestFile = wabProject.getFile(new Path("WebContent/META-INF/MANIFEST.MF"));
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(os);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(manifestFile.getContents()));
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith(WEB_CONTEXT_PATH_HEADER)) {
				pw.println(WEB_CONTEXT_PATH_HEADER + ": " + newContextRoot);
			} else {
				pw.println(line);
			}
		}
		
		pw.flush();
		
		InputStream is = new ByteArrayInputStream(os.toByteArray());
		manifestFile.setContents(is, IResource.KEEP_HISTORY | IResource.FORCE, null);
	}

}
