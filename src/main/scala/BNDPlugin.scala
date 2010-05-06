/**
 * Copyright (c) 2010 WeigleWilczek.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.weiglewilczek.bnd4sbt

import aQute.lib.osgi.{Builder, Constants}
import java.util.Properties
import sbt.{DefaultProject, Path, ScalaPaths}

trait BNDPlugin extends DefaultProject with BNDPluginProperties {

  /** Creates an OSGi bundle out of this project by using BND. */
  lazy val bndBundle = bndBundleAction

  protected def bndBundleAction =
    task {
      try {
        createBundle()
        log info "Created OSGi bundle at %s.".format(bndOutput)
        None
      } catch { case e =>
        log error "Error when trying to create OSGi bundle: %s.".format(e.getMessage)
        Some(e.getMessage)
      }
    } dependsOn compile describedAs "Creates an OSGi bundle out of this project by using BND."

  protected val project = this

  override protected def packageAction = bndBundle
  
  def allDependencyJars = Path.lazyPathFinder { 
    topologicalSort.flatMap { 
      case p: ScalaPaths => p.jarPath.getFiles.map(Path.fromFile); 
      case _ => Set() 
    } 
  }

  //def proguardInJars = runClasspath --- proguardExclude
  def proguardInJars = (((compileClasspath +++ allDependencyJars) ** "*.jar") getPaths) mkString(",")
  def getSbtResources = (mainResources getPaths) mkString(",")

  private def createBundle() {
    val builder = new Builder
    builder setProperties properties
    builder setClasspath Array(bndClasspath.absolutePath)
    val jar = builder.build
    jar write bndOutput.absolutePath
  }

  private def properties = {
    val properties = new Properties
    properties.setProperty("Bundle-SymbolicName", bndBundleSymbolicName)
    properties.setProperty("Bundle-Version", bndBundleVersion)
    properties.setProperty("Bundle-Name", bndBundleName)
    properties.setProperty("Private-Package", bndPrivatePackage mkString ",")
    properties.setProperty("Export-Package", bndExportPackage mkString ",")
    properties.setProperty("Import-Package", bndImportPackage mkString ",")
    properties.setProperty(Constants.INCLUDE_RESOURCE, getSbtResources + proguardInJars)
    properties
  }
}
