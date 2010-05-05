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
import sbt.Path
import sbt.PathFinder
import sbt.FileUtilities
import java.util.jar.Manifest

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
  
  override protected def packageAction = {
    def myPackageTask(sources: PathFinder, jarPath: => Path, options: => Seq[PackageOption]): Task =
		fileTask("package", jarPath from sources)
		{
		    log info "In myPackageTask with options %s".format(options.toString)
			import com.weiglewilczek.bnd4sbt.test.{MutableMapWrapper,Wrappers}
			/** Copies the mappings in a2 to a1, mutating a1. */
			def mergeAttributes(a1: Attributes, a2: Attributes)
			{
				for( (key, value) <- Wrappers.toList(a2))
					a1.put(key, value)
			}

			val manifest = new Manifest
			var recursive = false
			for(option <- options)
			{
				option match
				{
					case JarManifest(mergeManifest) =>
					{
						mergeAttributes(manifest.getMainAttributes, mergeManifest.getMainAttributes)
						val entryMap = new MutableMapWrapper(manifest.getEntries)
						for((key, value) <- Wrappers.toList(mergeManifest.getEntries))
						{
							entryMap.get(key) match
							{
								case Some(attributes) => mergeAttributes(attributes, value)
								case None => entryMap += (key, value)
							}
						}
					}
					case Recursive => recursive = true
					case MainClass(mainClassName) =>
						manifest.getMainAttributes.put(Attributes.Name.MAIN_CLASS, mainClassName)
					case ManifestAttributes(attributes @ _*) =>
						val main = manifest.getMainAttributes
						for( (name, value) <- attributes) {
							println("Adding %s : %s to manifest".format(name, value))
							main.put(name, value)
						}
					case _ => log.warn("Ignored unknown package option " + option)
				}
			}
			val jarPathLocal = jarPath
			FileUtilities.clean(jarPathLocal :: Nil, log) orElse
			FileUtilities.jar(sources.get, jarPathLocal, manifest, recursive, log)
		}
    	log info "Before my package task"
    	val returnVal = myPackageTask(packagePaths, jarPath, packageOptions).dependsOn(compile) describedAs "PackageDescription"
        log info "After my package task"
        returnVal
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
