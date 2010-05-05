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

trait BNDPlugin extends DefaultProject with BNDPluginProperties {
  protected val project = this

  override def packageOptions: Seq[PackageOption] = {
    val props = properties; 
    val osgiManifestAttributes = props.map(entry => ManifestAttributes( (new Attributes.Name(entry._1), entry._2) )).toList
	val manifestInfo = manifestClassPath.map(cp => ManifestAttributes( (Attributes.Name.CLASS_PATH, cp) )).toList :::
	        getMainClass(false).map(MainClass(_)).toList ::: osgiManifestAttributes
   log info "Created manifest info: %s".format(manifestInfo)
   manifestInfo
  }
  
  private def properties  = {
    val properties = Map(
    		"Bundle-SymbolicName"-> bndBundleSymbolicName,
    		"Bundle-Version" -> bndBundleVersion,
    		"Bundle-Name" -> bndBundleName,
    		"Private-Package" -> (bndPrivatePackage mkString ","),
    		"Export-Package" -> (bndExportPackage mkString ","),
    		"Import-Package"-> (bndImportPackage mkString ",")
    	)
    properties
  }
}
