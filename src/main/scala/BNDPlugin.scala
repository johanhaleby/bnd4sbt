/**
 * Copyright (c) 2010 WeigleWilczek.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.weiglewilczek.bnd4sbt

import aQute.lib.osgi.Builder
import java.util.Properties
import sbt.DefaultProject
import java.io.FileOutputStream
import java.util.jar.Attributes
import java.util.List
import java.util.LinkedList
import java.util.jar.Manifest

trait BNDPlugin extends DefaultProject with BNDPluginProperties {
  protected val project = this

  override def packageOptions: Seq[PackageOption] = {
    log info "JOhan!!!"
    val mainAttributes = createManifest().getMainAttributes()
    val manifestEntries = (new scala.collection.immutable.HashSet() ++ new scala.collection.jcl.HashSet[java.util.Map.Entry[Attributes.Name,String]](mainAttributes.entrySet().asInstanceOf[java.util.HashSet[java.util.Map.Entry[Attributes.Name,String]]]))
    val osgiManifestAttributes = manifestEntries.map(entry => ManifestAttributes( (entry.getKey, entry.getValue) )).toList
	val manifestInfo = getMainClass(false).map(MainClass(_)).toList ::: osgiManifestAttributes //manifestClassPath.map(cp => ManifestAttributes( (Attributes.Name.CLASS_PATH, cp) )).toList :::
    manifestInfo
  }
  
 private def createManifest() : Manifest = {
    val builder = new Builder
    builder setProperties properties
    builder setClasspath Array(bndClasspath.absolutePath)
    val jar = builder.build
    val manifest =jar.getManifest
    manifest
  }

  private def properties = {
    val properties = new Properties
    properties.setProperty("Bundle-SymbolicName", bndBundleSymbolicName)
    properties.setProperty("Bundle-Version", bndBundleVersion)
    properties.setProperty("Bundle-Name", bndBundleName)
    properties.setProperty("Private-Package", bndPrivatePackage mkString ",")
    properties.setProperty("Export-Package", bndExportPackage mkString ",")
    properties.setProperty("Import-Package", bndImportPackage mkString ",")
    properties
  }
}
