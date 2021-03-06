/**
 * Copyright (c) 2010 WeigleWilczek.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.weiglewilczek.bnd4sbt

import org.specs.SpecificationWithJUnit
import org.specs.mock.Mockito
import sbt.MavenStyleScalaPaths

class BNDPluginPropertiesSpec extends SpecificationWithJUnit with Mockito {

  "Calling bndBundleSymbolicName" should {

    "return organization.name if organization does not end with a sequence name begins with" in {
      new BNDPluginProperties {
        val project = mock[MavenStyleScalaPaths]
        project.organization returns "organization"
        project.name returns "name"
        bndBundleSymbolicName mustEqual "organization.name"
      }
    }

    "return a.b.c.d if organization is a.b.c and name is c.d" in {
      new BNDPluginProperties {
        val project = mock[MavenStyleScalaPaths]
        project.organization returns "a.b.c"
        project.name returns "c.d"
        bndBundleSymbolicName mustEqual "a.b.c.d"
      }
    }

    "return a.b.c.d if organization is a.b.c and name is c-d" in {
      new BNDPluginProperties {
        val project = mock[MavenStyleScalaPaths]
        project.organization returns "a.b.c"
        project.name returns "c-d"
        bndBundleSymbolicName mustEqual "a.b.c.d"
      }
    }

    "return a.b.c.d.e if organization is a.b.c.d and name is c-d.e" in {
      new BNDPluginProperties {
        val project = mock[MavenStyleScalaPaths]
        project.organization returns "a.b.c.d"
        project.name returns "c-d.e"
        bndBundleSymbolicName mustEqual "a.b.c.d.e"
      }
    }

    "return a.b.c.b.c if organization is a-b and name is c.b-c" in {
      new BNDPluginProperties {
        val project = mock[MavenStyleScalaPaths]
        project.organization returns "a-b"
        project.name returns "c.b-c"
        bndBundleSymbolicName mustEqual "a.b.c.b.c"
      }
    }
  }
}
