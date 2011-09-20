/*******************************************************************************
 *   Copyright (c) 2010 Eteration A.S. and others.
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   which accompanies this distribution, and is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 *  
 *   Contributors:
 *      Naci Dai and Murat Yener, Eteration A.S. - Initial API and implementation
 *******************************************************************************/
package org.eclipse.libra.framework.equinox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.libra.framework.core.FrameworkCorePlugin;
import org.eclipse.libra.framework.core.IOSGIExecutionEnvironment;
import org.eclipse.libra.framework.core.Trace;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.core.plugin.TargetPlatform;
import org.eclipse.pde.core.project.IBundleClasspathEntry;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.internal.core.target.provisional.IResolvedBundle;
import org.eclipse.wst.server.core.IModule;

public class EquinoxHandler implements IEquinoxVersionHandler {

	public IStatus verifyInstallPath(IPath location) {
		boolean isFound = false;
		IPath plugins = location.append("plugins");
		if (plugins.toFile().exists()) {
			File[] files = plugins.toFile().listFiles();
			for (File file : files) {
				if (file.getName().indexOf("org.eclipse.osgi_") > -1) {
					isFound = true;
					break;
				}
			}
			if (isFound) {
				return Status.OK_STATUS;
			}
		}
		return new Status(IStatus.ERROR, EquinoxPlugin.PLUGIN_ID, 0,
				Messages.warningCantReadConfig, null);
	}

	public String getFrameworkClass() {
		return "org.eclipse.core.runtime.adaptor.EclipseStarter";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getFrameworkClasspath(IPath installPath, IPath configPath) {

		List cp = new ArrayList();
		IPath plugins = installPath.append("plugins");

		if (plugins.toFile().exists()) {
			File[] files = plugins.toFile().listFiles();
			for (File file : files) {
				if (file.getName().indexOf("org.eclipse.osgi_") > -1) {
					IPath path = plugins.append(file.getName());
					cp.add(JavaRuntime.newArchiveRuntimeClasspathEntry(path));
				}
			}
		}

		return cp;
	}

	public String[] getFrameworkProgramArguments(IPath configPath,
			boolean debug, boolean starting) {

		ArrayList<String> programArgs = new ArrayList<String>();
		programArgs.add("-dev");
		programArgs.add("file:"+configPath.append("dev.properties").toOSString()); //$NON-NLS-1$
		programArgs.add("-configuration");
		programArgs.add(configPath.makeAbsolute().toOSString()); //$NON-NLS-1$
		if (debug) {
			programArgs.add("-debug"); //$NON-NLS-1$
		}
		programArgs.add("-os"); //$NON-NLS-1$
		programArgs.add(TargetPlatform.getOS());
		programArgs.add("-ws"); //$NON-NLS-1$
		programArgs.add(TargetPlatform.getWS());
		programArgs.add("-arch"); //$NON-NLS-1$
		programArgs.add(TargetPlatform.getOSArch());
		programArgs.add("-consoleLog"); //$NON-NLS-1$
		programArgs.add("-console"); //$NON-NLS-1$
		 
		return (String[]) programArgs.toArray(new String[programArgs.size()]);
	}

	public String[] getExcludedFrameworkProgramArguments(boolean debug,
			boolean starting) {
		return null;
	}

	public String[] getFrameworkVMArguments(IPath installPath,String javaProfileID,
			IPath configPath, IPath deployPath, boolean isTestEnv) {
		
		//String configPathStr = deployPath.makeAbsolute().toOSString();
		String profilePath =  deployPath.append("java.profile").toOSString();
		Properties javaProfileProps = null;
	
		IExecutionEnvironment  environment[] = JavaRuntime.getExecutionEnvironmentsManager().getExecutionEnvironments();
		for(IExecutionEnvironment e: environment){
			if(javaProfileID.equals(e.getId())){
				javaProfileProps = e.getProfileProperties();
				break;
			}
		}
		
		try {
			if(IOSGIExecutionEnvironment.JAVASE6_SERVER.toString().equals(javaProfileID)){
				copyFile(this.getClass().getResourceAsStream("java6-server.profile"), new File(profilePath));				
			}else if(javaProfileProps != null){
				FileOutputStream os = new FileOutputStream(new File(profilePath));
				javaProfileProps.store(os, "THIS FILE IS AUTO GENERATED");
			}
		} catch (IOException e) {
			Trace.trace(Trace.SEVERE, "Could not set equinox VM arguments:"+e.getMessage(), e);
		}
		String vmArgs =""  ;
		
		//If there is a non-default java profile then set it.
		if(javaProfileID != null && !javaProfileID.equals(IOSGIExecutionEnvironment.Default.toString()))
			vmArgs += "-Dosgi.java.profile=file:"+profilePath; //$NON-NLS-1$ //$NON-NLS-2$

		return new String[] {"-Declipse.ignoreApp=true", "-Dosgi.noShutdown=true", vmArgs };
	}

	public IStatus canAddModule(IModule module) {
		String id = module.getModuleType().getId();
		// String version = module.getModuleType().getVersion();
		if ("osgi.bundle".equals(id))
			return Status.OK_STATUS;

		return new Status(IStatus.ERROR, EquinoxPlugin.PLUGIN_ID, 0,
				Messages.errorNotBundle, null);
	}

	public IStatus prepareFrameworkInstanceDirectory(IPath baseDir) {
		return Status.OK_STATUS;
	}

	public IStatus prepareDeployDirectory(IPath deployPath) {

		if (Trace.isTraceEnabled())
			Trace.trace(Trace.FINER, "Creating runtime directory at "
					+ deployPath.toOSString());

		// Prepare a  directory structure
		File temp = deployPath.append("plugins").toFile();
		if (!temp.exists())
			temp.mkdirs();
		temp = deployPath.append("auto").toFile();
		if (!temp.exists())
			temp.mkdirs();
		temp = deployPath.append("cache").toFile();
		if (!temp.exists())
			temp.mkdirs();

		return Status.OK_STATUS;
	}

	public boolean supportsServeModulesWithoutPublish() {
		return true;
	}

	public void prepareFrameworkConfigurationFile(IPath configPath,
			String workspaceBundles, String frameworkJar, IResolvedBundle[] kernelBundles) {
		String[] wsBundleIds =  workspaceBundles.split(" ");

		prepareDevProperties(configPath, wsBundleIds);
		prepareConfigIni(configPath, wsBundleIds, frameworkJar, kernelBundles);
	}

	private void prepareConfigIni(IPath configPath, String[] wsBundleIds, String frameworkJar,
			IResolvedBundle[] krBundles) {
		String propertyInstall = "";
		for (String bundle : wsBundleIds) {
			if (bundle.indexOf("@") != -1)
				bundle = bundle.substring(0, bundle.indexOf("@"));
			IPluginModelBase[] models = PluginRegistry.getWorkspaceModels();
			for (IPluginModelBase iPluginModelBase : models) {
				if (bundle
						.indexOf(iPluginModelBase.getPluginBase().getId()) > -1) {
					String bpath = iPluginModelBase.getInstallLocation();
					if(bpath.endsWith("/"))
						bpath = bpath.substring(0,bpath.length()-1);
					if(iPluginModelBase.isFragmentModel())
						propertyInstall += "reference:file:" + bpath+ ", ";
					else
						propertyInstall += "reference:file:" + bpath+ "@start, ";
				}
			}
		}
		
		Properties properties = new Properties();
		properties.setProperty(
				"osgi.instance.area.default",
				"file:"
						+ configPath.toPortableString().substring(
								0,
								configPath.toPortableString().indexOf(
										".metadata")));

		properties.put("osgi.framework", frameworkJar);
		properties.setProperty("osgi.configuration.cascaded", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		int start = 4;
		properties.put(
				"osgi.bundles.defaultStartLevel", Integer.toString(start)); //$NON-NLS-1$
		for (int i = 0; i < krBundles.length; i++) {
			String targetBundlePath = "reference:file:"+krBundles[i].getBundleInfo().getLocation().getRawPath();
			if (targetBundlePath != null && !(targetBundlePath.trim().equalsIgnoreCase(""))) {
				if (targetBundlePath.indexOf("org.eclipse.osgi_") > -1) 
				  continue;
				
				File file = new File(targetBundlePath.substring(targetBundlePath.indexOf("/")));
				if (file.isFile()) {
					propertyInstall += targetBundlePath;
					if(krBundles[i].isFragment())
						propertyInstall +=  ", ";
					else
						propertyInstall += "@start, ";
				} else {
					for (String string2 : file.list()) {
						if (string2.indexOf(".jar") > -1) {
							propertyInstall += targetBundlePath + string2;
							String fbundleId = getBundleId(string2);
							IPluginModelBase modelBase = PluginRegistry.findModel(fbundleId);
							if(krBundles[i].isFragment())
								propertyInstall +=  ", ";
							else
								propertyInstall += "@start, ";
						}
					}
				}
			}

		}
		
		properties.setProperty("osgi.bundles", propertyInstall);
		properties.put("eclipse.ignoreApp", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		properties.put("osgi.noShutdown", "true"); //$NON-NLS-1$ //$NON-NLS-2$

		try {
			properties.store(
					new FileOutputStream(configPath.append("config.ini")
							.makeAbsolute().toFile()), "## AUTO GENERATED ##");
		} catch (IOException e) {
			Trace.trace(Trace.SEVERE, "Could not create equinox dev.properties configrutaion file:"+e.getMessage(), e);
		}
	}

	private String getBundleId(String targetBundlePath) {
		IPath kbPath = new Path(targetBundlePath);
		String bundleId = kbPath.lastSegment();
		if(bundleId.endsWith(".jar"))
			bundleId = bundleId.substring(0,bundleId.length()-4);
		int vversioNloc = bundleId.indexOf("_");
		if(vversioNloc > 0)
			bundleId = bundleId.substring(0, vversioNloc);
		return bundleId;
	}

	private String[] prepareDevProperties(IPath configPath, String[] wsBundleIds) {
		try {
			// Create file
			FileWriter fstream = new FileWriter(configPath.toPortableString()
					+ "/dev.properties");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("#OSAMI AUTO GENERATED\n");
			for (String bundle : wsBundleIds) {
				if (bundle.indexOf("@") != -1)
					bundle = bundle.substring(0, bundle.indexOf("@"));
				IPluginModelBase[] models = PluginRegistry.getWorkspaceModels();
				String modelId = "";
				for (IPluginModelBase iPluginModelBase : models) {			
					if (bundle.indexOf(iPluginModelBase.getPluginBase().getId()) > -1) {
						IProject pluginProject = iPluginModelBase.getUnderlyingResource().getProject();
						IJavaProject javaProject = JavaCore.create(pluginProject);
						IBundleProjectDescription bundleProjectDescription = FrameworkCorePlugin.getDescription(pluginProject);

						IBundleClasspathEntry[] allCPEntry = bundleProjectDescription.getBundleClasspath();
						modelId = iPluginModelBase.getPluginBase().getId();
						out.write("\n"+modelId + "=");
						for(IBundleClasspathEntry bcpe: allCPEntry){
							if(bcpe.getSourcePath() != null && bcpe.getBinaryPath() == null)
								out.write(" "+javaProject.getOutputLocation().makeRelativeTo(pluginProject.getFullPath()) +",");
							else if(bcpe.getSourcePath() != null && bcpe.getBinaryPath() != null)
								out.write(" "+bcpe.getBinaryPath().toOSString() +",");
							else if(bcpe.getLibrary() != null && bcpe.getLibrary().toOSString().endsWith(".jar") )
								out.write(" "+bcpe.getLibrary().toOSString() +",");
							else 
								out.write(" "+javaProject.getOutputLocation().makeRelativeTo(pluginProject.getFullPath()) +",");
						}
						
					}
				}
			}
			out.close();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not create equinox dev.properties configuration file:"+e.getMessage(), e);
		}
		return wsBundleIds;
	}
	
	
	private  void copyFile(InputStream source, File destFile) throws IOException {


		FileOutputStream destination = null;
		 try {
		  destination = new FileOutputStream(destFile);
		  int c;
		  while((c = source.read()) != -1){
			  destination.write(c);
		  }
		 }
		 finally {
		  if(source != null) {
		   source.close();
		  }
		  if(destination != null) {
		   destination.close();
		  }
		}
	}
}
